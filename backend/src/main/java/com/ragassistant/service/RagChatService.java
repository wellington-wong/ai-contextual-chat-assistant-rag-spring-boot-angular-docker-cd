package com.ragassistant.service;
import com.ragassistant.model.ChatMessage;
import com.ragassistant.model.ChatSession;

import com.ragassistant.model.DocumentChunk;
import com.ragassistant.repository.ChatMessageRepository;
import com.ragassistant.repository.ChatSessionRepository;
import com.ragassistant.repository.DocumentChunkRepository;
import com.ragassistant.repository.ChunkSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class RagChatService {
    private static final Logger log = LoggerFactory.getLogger(RagChatService.class);

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final DocumentChunkRepository chunkRepository;

    private final AnthropicApiClient apiClient;

    @Value("${rag.top-k}")
    private int topK;
    public RagChatService(ChatSessionRepository sessionRepository,
                          ChatMessageRepository messageRepository,
                          DocumentChunkRepository chunkRepository,
                          AnthropicApiClient apiClient) {
        this.sessionRepository  = sessionRepository;
        this.messageRepository  = messageRepository;
        this.chunkRepository    = chunkRepository;
        this.apiClient          = apiClient;
    }
    @Transactional
    public ChatSession createSession(String title) {

        ChatSession session = new ChatSession();
        session.setTitle(title == null || title.isBlank() ? "New Chat" : title);
        return sessionRepository.save(session);
    }

    public List<ChatSession> getAllSessions() {
        return sessionRepository.findAllByOrderByUpdatedAtDesc();
    }


    public ChatSession getSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
    }

    @Transactional
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    public List<ChatMessage> getMessages(Long sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Transactional
    public ChatMessage chat(Long sessionId, String userText) throws IOException {
        ChatSession session = getSession(sessionId);
        // 1. Persist user turn
        ChatMessage userMsg = saveMessage(session, "user", userText);

        // 2. Embed the query
        float[] queryEmbedding = apiClient.embed(userText);
        String pgVectorLiteral = toPgVectorLiteral(queryEmbedding);

        // 3. Retrieve top-K similar chunks
        List<ChunkSearchResult> chunks = chunkRepository.findTopKSimilar(pgVectorLiteral, topK);
        log.info("Retrieved {} context chunks for query", chunks.size());

        // 4. Build augmented system prompt
        String systemPrompt = buildSystemPrompt(chunks);

        // 5. Build conversation history
        List<ChatMessage> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        List<AnthropicApiClient.MessagePair> messages = history.stream()
                .map(m -> new AnthropicApiClient.MessagePair(m.getRole(), m.getContent()))
                .collect(Collectors.toList());

        // 6. Call Claude
        String reply = apiClient.chat(systemPrompt, messages);


        // 7. Persist assistant turn
        ChatMessage assistantMsg = saveMessage(session, "assistant", reply);

        // 8. Update session
        session.setUpdatedAt(LocalDateTime.now());
        if ("New Chat".equals(session.getTitle()) && history.size() <= 1) {
            session.setTitle(truncate(userText, 60));
        }
        sessionRepository.save(session);

        return assistantMsg;
    }

    private ChatMessage saveMessage(ChatSession session, String role, String content) {
        ChatMessage msg = new ChatMessage();
        msg.setSession(session);
        msg.setRole(role);
        msg.setContent(content);
        return messageRepository.save(msg);
    }

    private String buildSystemPrompt(List<ChunkSearchResult> chunks) {
        if (chunks.isEmpty()) {
            return """
                    You are a helpful assistant. Answer the user's questions clearly and accurately as possible.
                    If you don't know the answer, say so honestly.
                    """;
        }


        String contextBlock = chunks.stream()
                .map(ChunkSearchResult::getContent)

                //.map(c -> "---\n" + c.getContent())
                .collect(Collectors.joining("\n"));

        return """
                You are a helpful assistant that answers questions based on the provided context.
                
                CONTEXT (retrieved from the knowledge base):
                %s
                
                INSTRUCTIONS:
                - Answer the user's question using ONLY the information in the context above.
                - If the context does not contain enough information to answer, say so clearly.
                - Be concise, accurate, and cite which part of the context supports your answer when relevant.
                - Do not make up information not present in the context.
                - Explain as clearly as possible.
                """.formatted(contextBlock);
    }


    private String toPgVectorLiteral(float[] vec) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vec.length; i++) {
            sb.append(vec[i]);
            if (i < vec.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
