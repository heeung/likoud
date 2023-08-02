package com.backend.api.nft.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.backend.api.drawing.service.DrawingViewService;
import com.backend.domain.drawing.entity.Drawing;
import com.backend.domain.member.entity.Member;
import com.backend.domain.nft.Dto.TokenMetaData;
import com.backend.domain.nft.entity.Nft;
import com.backend.domain.nft.repository.NftRepository;
import com.backend.external.nft.client.AllTokenListClient;
import com.backend.external.nft.client.MemberNftListClient;
import com.backend.external.nft.client.NftTokenClient;
import com.backend.external.nft.client.WalletClient;
import com.backend.external.nft.dto.NftTokenDto;
import com.backend.external.nft.dto.TokenListDto;
import com.backend.external.nft.dto.WalletDto;
import com.backend.global.error.ErrorCode;
import com.backend.global.error.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NftService {

    private final WalletClient walletClient;
    private final NftTokenClient nftTokenClient;
    private final MemberNftListClient memberNftListClient;
    private final StringEncryptor stringEncryptor;
    private final DrawingViewService drawingViewService;
    private final NftRepository nftRepository;
    private final AllTokenListClient allTokenListClient;
    private final AmazonS3Client amazonS3Client;

    @Value("${KAS.client.authorization}")
    private String Authorization;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public WalletDto.Response createWallet(Member member){
        // 지갑생성
        WalletDto.Response response =  walletClient.createWallet("1001", Authorization);

        // 멤버 필드 업데이트
        member.setWallet(response.getAddress());
        // 개인키 암호화
        member.setKeyId(stringEncryptor.encrypt(response.getKeyId()));
        return response;
    }

    // s3에 메타데이터 업로드, 엔티티 생성 및 토큰 발행
    @Transactional
    public Nft uploadNftAndCreateToken(Long drawingId, Member member){
        // 지갑이 없는 경우
        String wallet = member.getWallet();
        if (wallet == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_WALLET);
        }
        Drawing drawing = drawingViewService.findDrawingById(drawingId);
        // 본인 그림이 아닌 경우
        if (!drawing.getMember().getMemberId().equals(member.getMemberId())){
            throw new BusinessException(ErrorCode.UNAUTHORIZED_NFT);
        }
        // 이미 발행된 경우
        Optional<Nft> optionalNft = nftRepository.findNftByDrawingDrawingId(drawingId);
        if (optionalNft.isPresent()){
            throw new BusinessException(ErrorCode.ALREADY_PUBLISHED_TOKEN);
        }
        log.info("토큰 발행 시작");
        NftTokenDto.Response responseToken = this.createToken(drawing, member);
        return this.uploadNFT(drawing, member);
    }

    // 토큰 발행
    @Transactional
    public NftTokenDto.Response createToken(Drawing drawing, Member member) {
        NftTokenDto.Request requestDto = NftTokenDto.Request.builder()
                        .to(member.getWallet())
                        .id("0x" + Long.toHexString(Math.abs(UUID.randomUUID().getLeastSignificantBits())))
                        .uri(drawing.getImageUrl())
                        .build();
        log.info(requestDto.getTo());
        log.info(requestDto.getId());
        log.info(requestDto.getUri());
        NftTokenDto.Response response = nftTokenClient.createToken("1001", Authorization, "i-likloud", requestDto);
        log.info("발급됨");

        return response;
    }

    // S3에 메타데이터 업로드
    private void uploadMetadataToS3(String metadataJson, String key) {
        byte[] contentAsBytes = metadataJson.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream contentsAsStream = new ByteArrayInputStream(contentAsBytes);
        ObjectMetadata md = new ObjectMetadata();
        md.setContentLength(contentAsBytes.length);
        amazonS3Client.putObject(bucket, key, contentsAsStream, md);
    }

    // 업로드 후 엔티티 생성
    @Transactional
    public Nft uploadNFT(Drawing drawing, Member member) {
        TokenMetaData metaData = new TokenMetaData(drawing.getTitle(), drawing.getContent(), drawing.getImageUrl(), member.getWallet());
        // 메타데이터 JSON 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMetaData;
        try {
            jsonMetaData = objectMapper.writeValueAsString(metaData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert metadata to JSON", e);
        }
        // S3에 메타데이터 업로드
        String key = "nft-metadata/" + UUID.randomUUID().toString() + ".json";
        uploadMetadataToS3(jsonMetaData, key);

        // 메타데이터 URL
        String metadataUrl = amazonS3Client.getUrl(bucket, key).toString();

        // NFT 엔티티 생성
        String contract = "0x6825676316690642c9351feb9b87ea0d23b9ce03";
        Nft nft = Nft.builder()
                .nftImageUrl(drawing.getImageUrl())
                .nftMetadata(metadataUrl)
                .contract(contract)
                .member(member)
                .drawing(drawing)
                .build();

        // NFT 발행 표시
        drawing.setNftYn(true);
        drawing.setNft(nft);

        // NFT 엔티티 저장
        nftRepository.save(nft);

        return nft;
    }

    // 특정 사용자 NFT 토큰 조회
    public TokenListDto.Response getTokenList(Member member) {
        log.info("찾기");
        return memberNftListClient.getTokenList("1001", Authorization, "i-likloud", member.getWallet());
    }

    // 모든 토큰 조회
    public TokenListDto.Response getAllTokenList(){
        return allTokenListClient.getAllTokenList("1001", Authorization, "i-likloud");
    }
}
