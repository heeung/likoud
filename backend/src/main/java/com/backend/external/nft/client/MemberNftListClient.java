package com.backend.external.nft.client;

import com.backend.external.nft.dto.TokenListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://kip17-api.klaytnapi.com", name="getTokenList")
public interface MemberNftListClient {

    @GetMapping(value = "/v2/contract/{contractAlias}/owner/{address}", consumes = "application/json")
    TokenListDto.Response getTokenList(@RequestHeader("x-chain-id") String chainId,
                                      @RequestHeader("Authorization") String authorizationHeader,
                                      @PathVariable String contractAlias,
                                      @PathVariable String address);

}
