package com.example.demo.dto;

import java.util.List;

public record ChatRequest(String model, List<ChatMessage> messages) {
}
