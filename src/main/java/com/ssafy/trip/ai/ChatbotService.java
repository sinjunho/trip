package com.ssafy.trip.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotService {

    @Value("${openai.api.key}")
    private String apiKey;
    
    private final WebClient webClient;
    
    public String generateResponse(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", "당신은 여행 도우미 챗봇입니다. 사용자에게 여행 정보를 제공해주세요."),
            Map.of("role", "user", "content", userMessage)
        });
        
        try {
            Map<String, Object> response = webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
                
            // 응답에서 메시지 추출
            return extractMessageFromResponse(response);
        } catch (Exception e) {
            log.error("챗봇 응답 생성 중 오류 발생", e);
            return "죄송합니다. 응답을 생성하는 데 문제가 발생했습니다.";
        }
    }
    
    private String extractMessageFromResponse(Map<String, Object> response) {
        try {
            var choices = (java.util.List) response.get("choices");
            var firstChoice = (Map<String, Object>) choices.get(0);
            var message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("응답 파싱 중 오류 발생", e);
            return "응답 처리 중 오류가 발생했습니다.";
        }
    }
}