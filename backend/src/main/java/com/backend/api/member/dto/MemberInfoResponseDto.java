package com.backend.api.member.dto;

import com.backend.domain.member.constant.Role;
import com.backend.domain.member.constant.SocialType;
import com.backend.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoResponseDto {
    // 회원 정보 조회 Dto

    private Long memberId;

    private String email;

    private String nickname;

    private int profileFace;
    private int profileColor;
    private int profileAccessory;

    private int goldCoin;
    private int silverCoin;

    private SocialType socialType;

    private Role role;

    public static MemberInfoResponseDto of(Member member) {
        return MemberInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileFace(member.getProfileFace())
                .profileColor(member.getProfileColor())
                .profileAccessory(member.getProfileAccessory())
                .role(member.getRole())
                .socialType(member.getSocialType())
                .build();
    }
}
