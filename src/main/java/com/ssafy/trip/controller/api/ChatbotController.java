package com.ssafy.trip.controller.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.trip.ai.ChatbotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Tag(name = "챗봇", description = "AI 챗봇 관련 API")
public class ChatbotController {
    
    private final ChatbotService chatbotService;
    
    @PostMapping("/message")
    @Operation(summary = "챗봇 메시지 전송", description = "사용자 메시지를 챗봇에게 전송하고 응답을 받습니다.")
    @ApiResponse(responseCode = "200", description = "챗봇 응답 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "사용자 메시지", required = true)
            @RequestBody Map<String, String> request) {
        
        try {
            String userMessage = request.get("message");
            
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "메시지가 비어있습니다."));
            }
            
            // 메시지 길이 제한
            if (userMessage.length() > 500) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "메시지가 너무 깁니다. (최대 500자)"));
            }
            
            log.info("챗봇 메시지 요청: {}", userMessage);
            
            // 챗봇 응답 생성
            String response = chatbotService.generateResponse(userMessage.trim());
            
            log.info("챗봇 응답 생성 완료");
            
            return ResponseEntity.ok(Map.of(
                "response", response,
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            log.error("챗봇 메시지 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "챗봇 응답 생성 중 오류가 발생했습니다."));
        }
    }
}