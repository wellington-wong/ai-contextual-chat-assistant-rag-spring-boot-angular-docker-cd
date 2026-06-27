package com.ragassistant.controller;
import com.ragassistant.dto.Dtos;
import com.ragassistant.service.RagChatService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")

public class ChatController {
    private final RagChatService chatService;

    public ChatController(RagChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public List<Dtos.SessionResponse> listSessions() {

        return chatService.getAllSessions()
                .stream().map(Dtos.SessionResponse::from).toList();
    }

    @PostMapping
    public Dtos.SessionResponse createSession(@RequestBody Dtos.CreateSessionRequest req) {
        return Dtos.SessionResponse.from(chatService.createSession(req.title()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        chatService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/messages")
    public List<Dtos.MessageResponse> getMessages(@PathVariable Long id) {
        return chatService.getMessages(id).stream().map(Dtos.MessageResponse::from).toList();
    }

    @PostMapping("/{id}/chat")
    public Dtos.MessageResponse chat(@PathVariable Long id,
                                     @Valid @RequestBody Dtos.ChatRequest req) throws IOException {
        return Dtos.MessageResponse.from(chatService.chat(id, req.message()));
    }
}