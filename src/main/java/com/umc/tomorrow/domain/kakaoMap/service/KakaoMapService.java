package com.umc.tomorrow.domain.kakaoMap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class KakaoMapService {

    @Value("${KAKAO_REST_API_KEY}")
    private String kakaoApiKey;

    public String getAddressFromCoord(BigDecimal lat, BigDecimal lng) {
        WebClient webClient = WebClient.create();

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/geo/coord2address.json")
                        .queryParam("x", lng.toPlainString())
                        .queryParam("y", lat.toPlainString())
                        .build())
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = new ObjectMapper().readTree(response);
            JsonNode document = root.path("documents").get(0);
            if (document.hasNonNull("road_address")) {
                return document.path("road_address").path("address_name").asText(); // 도로명 주소 우선
            }
            return document.path("address").path("address_name").asText(); // 없으면 지번 주소
        } catch (Exception e) {
            return "주소 없음";
        }
    }



}