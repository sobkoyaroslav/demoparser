package com.example.demo.service;

import com.example.demo.model.Review;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ReviewService reviewService;

    public DataInitializer(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public void run(String ... args) throws Exception {
        if (!reviewService.getAllReviews().isEmpty()) {
            System.out.println(">>> База данных уже содержит отзывы. Пропускаем авто-импорт.");
            return;
        }
        System.out.println(">>> Начало авто-импорта тестовых отзывов...");

        List<Review> testReviews = new ArrayList<>();

        // 1. Естественные отзывы
        testReviews.add(createRawReview("Ozon", "Иван К.", "Отличные кроссовки, размер подошел идеально! Доставка за 2 дня.", 5));
        testReviews.add(createRawReview("Wildberries", "Мария", "Платье красивое, но ткань немного просвечивает. На лето пойдет.", 4));
        testReviews.add(createRawReview("Ozon", "ТехноГик", "Телефон супер, экран 120Гц радует глаз. Батарею держит весь день.", 5));
        testReviews.add(createRawReview("AliExpress", "User99", "Все пришло в целости. Коробка немного помята, но девайс рабочий.", 4));

        // 2. Спам / Реклама (должны быть помечены как фейк)
        testReviews.add(createRawReview("Wildberries", "Бот1", "Зарабатывай от 5000р в день! Переходи по ссылке в профиле!", 5));
        testReviews.add(createRawReview("Ozon", "Скупщик", "КУПИТЕ дешево качественные отзывы! Наш телеграм @fake_reviews", 5));
        testReviews.add(createRawReview("Wildberries", "Акционер", "Внимание! Срочная акция на все товары в магазине, успей купить!", 5));

        // 3. Слишком короткие (должны быть помечены как фейк)
        testReviews.add(createRawReview("Ozon", "Лень", "Ок", 5));
        testReviews.add(createRawReview("Wildberries", "Аноним", "Норм", 3));
        testReviews.add(createRawReview("AliExpress", "Клиент", "Ужас", 1));

        for (Review r : testReviews) {
            try {
                System.out.println("Отправляем на анализ отзыв от: " + r.getAuthor());
                reviewService.analyzeReview(r);
                System.out.println("Успешно проанализирован отзыв от: " + r.getAuthor());

                // 👇 Ждем 2 секунды перед следующим запросом к ИИ
                Thread.sleep(5000);

            } catch (Exception e) {
                System.err.println("Ошибка при импорте отзыва: " + e.getMessage());
            }
        }
        System.out.println(">>> Авто-импорт успешно завершен!");
        }
        private Review createRawReview(String platform, String author, String text, int rating) {
            Review review = new Review();
            review.setPlatform(platform);
            review.setAuthor(author);
            review.setText(text);
            review.setRating(rating);
            return review;

        }
    }

