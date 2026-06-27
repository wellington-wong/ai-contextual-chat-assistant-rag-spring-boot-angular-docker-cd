package com.ragassistant.dto;
import com.ragassistant.model.ChatMessage;
import com.ragassistant.model.ChatSession;

import com.ragassistant.model.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class Dtos {
    public record CreateSessionRequest(String title) {}

    public record ChatRequest(@NotBlank String message) {}

    public record SessionResponse(
            Long id, String title,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        public static SessionResponse from (ChatSession s) {
            return new SessionResponse(s.getId(), s.getTitle(), s.getCreatedAt(), s.getUpdatedAt());
        }
    }


    public record MessageResponse(
            Long id, String role, String content, LocalDateTime createdAt
    ) {
        public static MessageResponse from (ChatMessage m) {
            return new MessageResponse(m.getId(), m.getRole(), m.getContent(), m.getCreatedAt());
        }
    }

    public record DocumentResponse(

            Long id, String name, String filetype, LocalDateTime createdAt
    ) {
        public static DocumentResponse from (Document d) {
            return new DocumentResponse(d.getId(), d.getName(), d.getFileType(), d.getCreatedAt());
        }
    }

    public record ApiError(String message) {}
}