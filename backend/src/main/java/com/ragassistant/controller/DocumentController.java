package com.ragassistant.controller;
import com.ragassistant.dto.Dtos;
import com.ragassistant.service.DocumentIngestionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.apache.tika.exception.TikaException;

@RestController
@RequestMapping("/api/documents")

public class DocumentController {
    private final DocumentIngestionService ingestionService;

    public DocumentController (DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @GetMapping
    public List<Dtos.DocumentResponse> listDocuments() {

        return ingestionService.getAllDocuments()
                .stream().map(Dtos.DocumentResponse::from).toList();
    }

    @PostMapping("/upload")
    public Dtos.DocumentResponse upload(@RequestParam("file") MultipartFile file) throws IOException, TikaException {
        return Dtos.DocumentResponse.from(ingestionService.ingest(file));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingestionService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}