package com.ragassistant.service;
import com.ragassistant.model.Document;
import com.ragassistant.model.DocumentChunk;

import com.ragassistant.repository.DocumentChunkRepository;
import com.ragassistant.repository.DocumentRepository;
import org.apache.tika.Tika;

import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pgvector.PGvector;

@Service
public class DocumentIngestionService {
    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;

    private final ChunkingService chunkingService;
    private final AnthropicApiClient apiClient;
    private final Tika tika = new Tika();

    public DocumentIngestionService(DocumentRepository documentRepository,
                                    DocumentChunkRepository chunkRepository,
                                    ChunkingService chunkingService,
                                    AnthropicApiClient apiClient) {
        this.documentRepository = documentRepository;

        this.chunkRepository    = chunkRepository;
        this.chunkingService    = chunkingService;
        this.apiClient          = apiClient;
    }

    @Transactional
    public Document ingest(MultipartFile file) throws IOException, TikaException {
        log.info("Ingesting document: {}", file.getOriginalFilename());

        String rawText;
        try {
            rawText = tika.parseToString(file.getInputStream());
        } catch (TikaException e) {
            throw new IOException("Failed to extract text from file: " + e.getMessage(), e);
        }

        Document doc = new Document();
        doc.setName(file.getOriginalFilename());
        doc.setContent(rawText);
        doc.setFileType(file.getContentType());
        documentRepository.save(doc);

        List<String> chunkTexts = chunkingService.chunk(rawText);
        log.info("Document split into {} chunks", chunkTexts.size());
        List<DocumentChunk> chunks = new ArrayList<>();

        for (int i = 0; i< chunkTexts.size(); i++) {
            String chunkText = chunkTexts.get(i);
            float[] embedding = apiClient.embed(chunkText);

            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocument(doc);
            chunk.setChunkIndex(i);
            chunk.setContent(chunkText);
            chunk.setEmbedding(embedding);

            chunks.add(chunk);
        }
        chunkRepository.saveAll(chunks);
        log.info("Saved {} chunks for document {}", chunks.size(), doc.getId());

        return doc;
    }

    @Transactional

    public void deleteDocument(Long documentId) {
        documentRepository.deleteById(documentId);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByCreatedAtDesc();
    }
}