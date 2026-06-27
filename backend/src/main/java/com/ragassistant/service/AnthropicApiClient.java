package com.ragassistant.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AnthropicApiClient {
    private static final Logger log = LoggerFactory.getLogger(AnthropicApiClient.class);
    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String VOYAGE_API_URL = "https://api.voyageai.com/v1/embeddings";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api-key}")
    private String apiKey;

    @Value("${voyage.api-key}")
    private String voyageApiKey;

    @Value("${anthropic.chat-model}")
    private String chatModel;

    @Value("${anthropic.embedding-model}")
    private String embeddingModel;

    @Value("${anthropic.max-tokens}")
    private int maxTokens;

    public AnthropicApiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()

                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

                .build();
    }

    public float[] embed(String text) throws IOException {
        ObjectNode body = objectMapper.createObjectNode();

        body.put("model", embeddingModel);
        body.put("input", text);

        String responseBody = post(VOYAGE_API_URL, body.toString(), true);
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode embeddingArray = root.path("data").get(0).path("embedding");

        float[] vector = new float[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++){

            vector[i] = (float) embeddingArray.get(i).asDouble();
        }
        return vector;
    }

    public String chat(String systemPrompt, List<MessagePair> messages) throws IOException {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", chatModel);
        body.put("max_tokens", maxTokens);

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            body.put("system", systemPrompt);
        }

        ArrayNode msgs = body.putArray("messages");
        for (MessagePair mp : messages) {
            ObjectNode msg = msgs.addObject();
            msg.put("role", mp.role());
            msg.put("content", mp.content());
        }

        String responseBody = post(CLAUDE_API_URL, body.toString(), false);
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("content").get(0).path("text").asText();
    }

    private String post(String url, String jsonBody, boolean isVoyage) throws IOException {
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request.Builder builder = new Request.Builder()

                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json");

        if (isVoyage) {
            builder.addHeader("Authorization", "Bearer " + voyageApiKey);
        } else {
            builder.addHeader("x-api-key", apiKey)
                    .addHeader("anthropic-version", "2023-06-01");

        }

        try (Response response = httpClient.newCall(builder.build()).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                log.error("API error {}: {}", response.code(), body);
                throw new IOException("API request failed with status " + response.code() + ": " + body);
            }
            return body;

        }
    }

    public record MessagePair(String role, String content) {}
}