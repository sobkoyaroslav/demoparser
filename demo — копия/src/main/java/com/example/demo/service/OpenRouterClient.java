package com.example.demo.service;

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenRouterClient {
    @Value("${mistral.api.key}")
    private String apiKey;

    @Value("${mistral.api.url}")
    private String apiUrl;

    @Value("${mistral.api.model}")
    private String model;

    // Создаем современный HTTP-клиент с таймаутом на подключение и чтение в 4 секунды
    private final RestClient restClient = RestClient.builder()
            .requestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                setConnectTimeout(4000); // 4 секунды на установку связи
                setReadTimeout(4000);    // 4 секунды на ожидание ответа
            }})
            .build();

    public String analyzeWithAI(String reviewText) {
        try {
            String systemPrompt = "Ты — профессиональный ИИ-аналитик отзывов на маркетплейсах. " +
                    "Твоя задача — проанализировать текст отзыва и определить, является ли он фейковым/накрученным/спамом. " +
                    "Ответь СТРОГО в формате JSON без какого-либо другого текста, разметки markdown или пояснений снаружи JSON. " +
                    "Формат ответа должен быть точно таким:\n" +
                    "{\n" +
                    "  \"isFake\": true/false,\n" +
                    "  \"confidence\": число от 0 до 100,\n" +
                    "  \"reason\": \"короткое объяснение твоего решения на русском языке\"\n" +
                    "}";

            ChatRequest request = new ChatRequest(
                    model,
                    List.of(
                            new ChatMessage("system", systemPrompt),
                            new ChatMessage("user", "Проанализируй этот отзыв: " + reviewText)
                    )
            );
            Map response = restClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request) // Спринг сам превратит наш ChatRequest в JSON!
                    .retrieve()
                    .body(Map.class); // Получаем ответ сразу в виде Map

            if (response != null) {
                List<?> choices = (List<?>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при запросе к ИИ: " + e.getMessage());
        }

        // Дефолтный ответ, если что-то пошло не так
        return "{\"isFake\": false, \"confidence\": 50, \"reason\": \"Не удалось связаться с ИИ.\"}";
        }
    }

