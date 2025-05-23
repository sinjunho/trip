package com.ssafy.trip.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.ssafy.trip.model.dto.Attraction;
import com.ssafy.trip.model.service.AttractionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotService {

    @Value("${openai.api.key:}")
    private String apiKey;
    
    private final WebClient webClient;
    private final AttractionService attractionService;
    private final Random random = new Random();
    
    public String generateResponse(String userMessage) {
        String message = userMessage.toLowerCase().trim();
        
        try {
            // 1. 키워드 기반 로컬 응답 먼저 시도
            String localResponse = generateLocalResponse(message);
            if (localResponse != null) {
                return localResponse;
            }
            
            // 2. OpenAI API 사용 (API 키가 있는 경우)
            if (StringUtils.hasText(apiKey)) {
                return generateAIResponse(userMessage);
            }
            
            // 3. 기본 응답
            return generateDefaultResponse();
            
        } catch (Exception e) {
            log.error("챗봇 응답 생성 중 오류 발생", e);
            return "죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요. 🙏";
        }
    }
    
    private String generateLocalResponse(String message) {
        try {
            // 관광지 추천 요청
            if (containsKeywords(message, "관광지", "추천", "여행지", "명소", "가볼만한")) {
                return generateAttractionRecommendation(message);
            }
            
            // 여행 계획 관련
            if (containsKeywords(message, "계획", "일정", "짜기", "세우기")) {
                return generateTravelPlanAdvice();
            }
            
            // 맛집 관련
            if (containsKeywords(message, "맛집", "음식", "식당", "먹을거리")) {
                return generateFoodRecommendation();
            }
            
            // 숙박 관련
            if (containsKeywords(message, "숙박", "호텔", "펜션", "게스트하우스", "머물", "잠자리")) {
                return generateAccommodationAdvice();
            }
            
            // 교통 관련
            if (containsKeywords(message, "교통", "버스", "지하철", "택시", "기차", "비행기", "가는법")) {
                return generateTransportationAdvice();
            }
            
            // 날씨 관련
            if (containsKeywords(message, "날씨", "기온", "비", "눈", "옷차림")) {
                return generateWeatherAdvice();
            }
            
            // 예산 관련
            if (containsKeywords(message, "예산", "비용", "돈", "가격", "얼마")) {
                return generateBudgetAdvice();
            }
            
            // 인사말
            if (containsKeywords(message, "안녕", "하이", "헬로", "hello", "hi")) {
                return generateGreeting();
            }
            
            // 감사 인사
            if (containsKeywords(message, "고마워", "감사", "thank")) {
                return generateThankYouResponse();
            }
            
        } catch (Exception e) {
            log.error("로컬 응답 생성 중 오류", e);
        }
        
        return null;
    }
    
    private String generateAttractionRecommendation(String message) {
        try {
            List<Attraction> attractions = attractionService.getRandomAttractions(3);
            if (attractions == null || attractions.isEmpty()) {
                return "죄송합니다. 현재 추천할 수 있는 관광지 정보가 없습니다. 😅";
            }
            
            StringBuilder response = new StringBuilder();
            response.append("🌟 추천 관광지를 소개해드릴게요!\n\n");
            
            for (int i = 0; i < attractions.size(); i++) {
                Attraction attraction = attractions.get(i);
                response.append(String.format("🏞️ **%s**\n", attraction.getTitle()));
                response.append(String.format("📍 %s %s\n", attraction.getSido(), attraction.getGugun()));
                if (i < attractions.size() - 1) {
                    response.append("\n");
                }
            }
            
            response.append("\n자세한 정보는 관광지 검색 페이지에서 확인해보세요! 🔍");
            return response.toString();
            
        } catch (Exception e) {
            log.error("관광지 추천 생성 중 오류", e);
            return "관광지 추천 정보를 가져오는 중 문제가 발생했습니다. 관광지 검색 페이지를 이용해주세요! 🏞️";
        }
    }
    
    private String generateTravelPlanAdvice() {
        String[] advices = {
            "여행 계획을 세울 때 이런 순서로 해보세요! ✈️\n\n1. 여행 목적과 테마 정하기\n2. 예산 설정하기\n3. 날짜와 기간 정하기\n4. 교통편과 숙소 예약\n5. 관광지와 맛집 리스트 작성\n6. 일정표 만들기\n\n저희 사이트의 여행 계획 기능을 활용해보세요! 📝",
            "효율적인 여행 계획 팁을 알려드릴게요! 🗺️\n\n• 너무 빡빡한 일정은 피하세요\n• 날씨와 계절을 고려하세요\n• 현지 교통편을 미리 확인하세요\n• 비상 연락처와 중요 서류를 준비하세요\n• 여행 보험 가입을 고려해보세요\n\n즐거운 여행 되세요! ✨"
        };
        return advices[random.nextInt(advices.length)];
    }
    
    private String generateFoodRecommendation() {
        String[] responses = {
            "맛집 찾기 팁을 알려드릴게요! 🍽️\n\n• 현지인들이 많이 가는 곳\n• 줄이 길게 서 있는 식당\n• SNS에서 핫한 맛집\n• 전통 시장의 먹거리\n• 지역 특산물 요리\n\n각 지역마다 특색있는 음식이 있으니 여행 전에 미리 조사해보세요! 🥘",
            "여행지별 대표 음식을 소개해드릴게요! 🍜\n\n🏙️ 서울: 명동 만두, 광장시장 빈대떡\n🌊 부산: 밀면, 돼지국밥, 씨앗호떡\n🏝️ 제주: 흑돼지, 갈치조림, 한라봉\n🏛️ 경주: 황남빵, 쌈밥정식\n\n현지 맛집은 블로그나 리뷰를 참고해보세요! 😋"
        };
        return responses[random.nextInt(responses.length)];
    }
    
    private String generateAccommodationAdvice() {
        return "숙박 선택 가이드를 알려드릴게요! 🏨\n\n🏨 **호텔**: 편의시설이 좋고 서비스가 우수\n🏡 **펜션**: 가족/단체 여행에 적합, 요리 가능\n🏠 **게스트하우스**: 저렴하고 다른 여행자와 소통 가능\n🏢 **모텔**: 경제적이고 접근성이 좋음\n\n💡 **선택 팁**\n• 위치 우선 (교통편, 관광지 접근성)\n• 예산에 맞는 시설 선택\n• 리뷰와 평점 확인\n• 취소 정책 꼭 확인하기";
    }
    
    private String generateTransportationAdvice() {
        return "교통수단별 이용 팁을 알려드릴게요! 🚌\n\n🚌 **버스**: 시외버스, 고속버스 - 경제적\n🚇 **지하철**: 도시 내 이동 - 빠르고 정확\n🚕 **택시**: 편리하지만 비용 부담\n🚄 **KTX**: 장거리 이동 - 빠르고 편안\n✈️ **항공**: 제주도, 해외 - 시간 절약\n\n교통카드를 미리 준비하시면 편리해요! 💳";
    }
    
    private String generateWeatherAdvice() {
        return "날씨별 여행 준비 가이드예요! 🌤️\n\n☀️ **맑은 날**: 자외선 차단제, 모자 필수\n🌧️ **비 오는 날**: 우산, 방수 신발 준비\n❄️ **추운 날**: 따뜻한 옷, 핫팩 준비\n🌸 **봄**: 얇은 겉옷, 꽃가루 알레르기 주의\n🏖️ **여름**: 시원한 옷, 충분한 수분 섭취\n🍂 **가을**: 일교차 대비 겉옷 준비\n⛄ **겨울**: 두꺼운 패딩, 미끄럼 방지 신발\n\n여행 전 날씨 예보를 꼭 확인하세요! 📱";
    }
    
    private String generateBudgetAdvice() {
        return "여행 예산 계획 가이드입니다! 💰\n\n📊 **예산 분배 비율**\n• 교통비: 30-40%\n• 숙박비: 25-35%\n• 식비: 20-25%\n• 관광/쇼핑: 15-20%\n• 기타/비상금: 5-10%\n\n💡 **절약 팁**\n• 미리 예약하면 할인 혜택\n• 현지 대중교통 이용\n• 로컬 맛집 이용\n• 무료 관광지 활용\n• 여행자 할인 혜택 확인\n\n예산을 미리 세워두시면 안심하고 여행할 수 있어요! ✨";
    }
    
    private String generateGreeting() {
        String[] greetings = {
            "안녕하세요! 여행 도우미입니다! 🌟\n어떤 여행 정보가 필요하신가요?",
            "반갑습니다! 🙋‍♀️\n여행 계획에 도움이 필요하시면 언제든 말씀해주세요!",
            "안녕하세요! 즐거운 여행을 계획 중이신가요? ✈️\n궁금한 것이 있으시면 편하게 물어보세요!"
        };
        return greetings[random.nextInt(greetings.length)];
    }
    
    private String generateThankYouResponse() {
        String[] responses = {
            "천만에요! 도움이 되어서 기뻐요! 😊\n다른 궁금한 것이 있으시면 언제든 말씀해주세요!",
            "별말씀을요! 즐거운 여행 되세요! 🌟",
            "도움이 되어서 다행이에요! 🥰\n좋은 여행 기억 만드시길 바라요!"
        };
        return responses[random.nextInt(responses.length)];
    }
    
    private String generateDefaultResponse() {
        String[] responses = {
            "죄송해요, 잘 이해하지 못했어요. 😅\n다시 한번 말씀해주시거나 다음 중에서 선택해주세요:\n\n• 관광지 추천\n• 여행 계획 세우기\n• 맛집 정보\n• 숙박 정보\n• 교통 정보",
            "조금 더 구체적으로 말씀해주시면 더 정확한 답변을 드릴 수 있어요! 🤔\n\n예를 들어:\n• '부산 관광지 추천해줘'\n• '제주도 맛집 알려줘'\n• '여행 예산 어떻게 짜지?'",
            "어떤 도움이 필요하신지 잘 모르겠어요. 🙋‍♀️\n\n이런 것들을 물어보실 수 있어요:\n• 여행지 추천\n• 여행 계획 방법\n• 음식 정보\n• 교통 수단\n• 날씨 정보"
        };
        return responses[random.nextInt(responses.length)];
    }
    
    private String generateAIResponse(String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", 
                "당신은 한국 여행 전문 도우미입니다. 친근하고 도움이 되는 톤으로 답변해주세요. " +
                "여행 관련 질문에 대해서만 답변하고, 한국의 관광지, 음식, 문화에 대한 정보를 제공해주세요. " +
                "답변은 간결하고 실용적으로 해주세요."),
            Map.of("role", "user", "content", userMessage)
        });
        requestBody.put("max_tokens", 300);
        requestBody.put("temperature", 0.7);
        
        try {
            Map<String, Object> response = webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
                
            return extractMessageFromResponse(response);
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            return generateDefaultResponse();
        }
    }
    
    private String extractMessageFromResponse(Map<String, Object> response) {
        try {
            @SuppressWarnings("unchecked")
            var choices = (java.util.List<Map<String, Object>>) response.get("choices");
            var firstChoice = choices.get(0);
            @SuppressWarnings("unchecked")
            var message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("응답 파싱 중 오류 발생", e);
            return "응답 처리 중 오류가 발생했습니다. 다시 시도해주세요.";
        }
    }
    
    private boolean containsKeywords(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    }