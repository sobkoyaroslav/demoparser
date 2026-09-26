package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity // 1. Говорит Hibernate: "Этот класс — это таблица в базе данных"
@Table(name = "reviews") // 2. Задает имя таблице в PostgreSQL. Если не написать, таблица назовется просто review
@Data // 3. Магия Lombok! Она сама сгенерирует геттеры, сеттеры, equals и toString под капотом. Код чистый и аккуратный!
public class Review {

    @Id // 4. Говорит, что это поле — Primary Key (первичный ключ, уникальный ID строки)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 5. База данных сама будет увеличивать ID на 1 при добавлении новой строки (1, 2, 3...)
    private Long id;

    private String platform; // Название площадки (Ozon, WB, Yandex)

    @Column(columnDefinition = "TEXT") // 6. По умолчанию Hibernate создает тип VARCHAR(255). Но отзывы бывают длинными, поэтому мы явно говорим: "Сделай тип TEXT"
    private String text;

    private String author; // Имя того, кто оставил отзыв
    private Integer rating; // Оценка товара (от 1 до 5)

    // --- Поля, которые заполнит наш ИИ после анализа ---
    private Boolean isFake; // Вердикт ИИ: true (бот), false (человек)
    private Integer confidence; // На сколько процентов ИИ уверен в своем ответе (0-100)
    @Column(columnDefinition = "TEXT")
    private String aiReason; // Объяснение от ИИ, почему он так решил

    private LocalDateTime createdAt = LocalDateTime.now(); // Дата и время добавления отзыва в нашу систему
}