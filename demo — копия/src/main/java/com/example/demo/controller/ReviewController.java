package com.example.demo.controller;

import com.example.demo.model.Review;
import com.example.demo.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.ReviewService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*") // 1. Говорит Спрингу: "Этот класс принимает HTTP-запросы и возвращает данные (обычно JSON)"
@RequestMapping("/api/reviews") // 2. Задает базовый адрес. Все запросы к этому контроллеру будут начинаться с http://localhost:8080/api/reviews
public class ReviewController {

        private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 5. GET-запрос для проверки связи и чтения данных из базы
    @GetMapping("/hello")
    public String testServer() {
        return "Сервер на связи! База подключена.";
    }

    // 6. GET-запрос, который достанет ВСЕ отзывы из базы данных в виде списка
    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // 7. GET-запрос для добавления ТЕСТОВОГО отзыва прямо из строки браузера
    @PostMapping("/add")
    public Review addAndAnalyzeReview(@RequestBody Review review) {
        return reviewService.analyzeReview(review);
    }
    @PostMapping("/import-external")
    public ResponseEntity<List<Review>> importExternalReviews(@RequestBody List<Review> externalReviews) {
        List<Review> savedReviews = new ArrayList<>();

        for (Review review : externalReviews) {
            // Ставим платформу по умолчанию, если не пришла
            if (review.getPlatform() == null || review.getPlatform().isEmpty()) {
                review.setPlatform("Ozon");
            }
            // Прогоняем через ИИ и сохраняем в базу
            Review analyzed = reviewService.analyzeReview(review);
            savedReviews.add(analyzed);
        }

        return ResponseEntity.ok(savedReviews);
    }
}