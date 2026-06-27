package com.ragassistant.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ChunkingService {

    @Value("${rag.chunk-size}")
    private int chunkSize;

    @Value("${rag.chunk-overlap}")
    private int chunkOverlap;

    public List<String> chunk(String text) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");

        int start = 0;
        while (start < words.length) {

            int end = Math.min(start + chunkSize, words.length);
            String chunk = String.join(" ", Arrays.copyOfRange(words, start ,end)).trim();
            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }
            if (end >= words.length) break;
            start += chunkSize - chunkOverlap;
        }
        return chunks;
    }
}