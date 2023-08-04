package com.backend.external.nft.client;

import com.backend.external.nft.dto.TransferDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://kip17-api.klaytnapi.com", name="tokenTransfer")
public interface NftTransferClient {

    @PostMapping(value = "/v2contract/{contractAlias}/token/{tokenId}", consumes = "application/json")
    TransferDto.Response transferToken(@RequestHeader("x-chain-id") String chainId,
                                       @RequestHeader("Authorization") String authorizationHeader,
                                       @PathVariable String contractAlias,
                                       @PathVariable String tokenId);
}