package com.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("전체조회")
                .pathsToMatch("/**")
                .build();
    }
    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("소셜로그인")
                .pathsToMatch("/api/oauth/**")
                .build();
    }
    @Bean
    public GroupedOpenApi mypageApi() {
        return GroupedOpenApi.builder()
                .group("마이페이지")
                .pathsToMatch("/api/mypage/**")
                .build();
    }
    @Bean
    public GroupedOpenApi tokenApi() {
        return GroupedOpenApi.builder()
                .group("토큰")
                .pathsToMatch("/api/accounts/**")
                .build();
    }
    @Bean
    public GroupedOpenApi drawingsApi() {
        return GroupedOpenApi.builder()
                .group("그림")
                .pathsToMatch("/api/drawings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi photoApi() {
        return GroupedOpenApi.builder()
                .group("사진")
                .pathsToMatch("/api/photo/**")
                .build();
    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Spring Boot API")
                        .description("연봉 1조")
                        .version("v0.0.1"));
    }
}