package com.example.demo.service;

import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OpenRouterClient openRouterClient; // Внедряем наш ИИ-клиент
    private final ObjectMapper objectMapper = new ObjectMapper(); // Инструмент для чтения JSON

    public ReviewService(ReviewRepository reviewRepository, OpenRouterClient openRouterClient) {
        this.reviewRepository = reviewRepository;
        this.openRouterClient = openRouterClient;
    }

    public Review analyzeReview(Review review) {
        // 1. Отправляем отзыв в ИИ и получаем текстовый JSON-ответ
        String aiJsonResponse = openRouterClient.analyzeWithAI(review.getText());

        try {
            // 👇 ДОБАВЛЯЕМ ОЧИСТКУ ОТ MARKDOWN-РАЗМЕТКИ
            if (aiJsonResponse != null) {
                // Убираем возможные пробелы по краям
                aiJsonResponse = aiJsonResponse.trim();

                // Если ответ начинается с ```json, отрезаем его
                if (aiJsonResponse.startsWith("```json")) {
                    aiJsonResponse = aiJsonResponse.substring(7);
                } else if (aiJsonResponse.startsWith("```")) {
                    aiJsonResponse = aiJsonResponse.substring(3);
                }

                // Если ответ заканчивается на ```, отрезаем его с конца
                if (aiJsonResponse.endsWith("```")) {
                    aiJsonResponse = aiJsonResponse.substring(0, aiJsonResponse.length() - 3);
                }

                // Снова убираем пробелы и переносы строк, которые могли остаться
                aiJsonResponse = aiJsonResponse.trim();
            }

            // 2. Теперь парсим чистую JSON-строку
            Map<?, ?> aiResult = objectMapper.readValue(aiJsonResponse, Map.class);

            // 3. Заполняем поля нашего отзыва данными, которые вернул ИИ
            review.setIsFake((Boolean) aiResult.get("isFake"));

            Number confidence = (Number) aiResult.get("confidence");
            review.setConfidence(confidence != null ? confidence.intValue() : 50);

            review.setAiReason((String) aiResult.get("reason"));

        } catch (Exception e) {
            System.err.println("Не удалось распарсить ответ от ИИ: " + e.getMessage());
            System.err.println("Сырой ответ ИИ был: " + aiJsonResponse); // Добавим вывод сырого ответа для отладки
            review.setIsFake(false);
            review.setConfidence(50);
            review.setAiReason("Ошибка анализа (сбой разбора ответа ИИ).");
        }

        // 4. Сохраняем уже проанализированный отзыв в базу данных
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}

