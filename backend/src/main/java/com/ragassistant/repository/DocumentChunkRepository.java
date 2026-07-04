package com.ragassistant.repository;
import com.ragassistant.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {
    @Query(value = """
            
            SELECT * FROM document_chunks
            ORDER BY embedding <=> CAST(:embedding as vector)
            LIMIT :k
            """, nativeQuery = true)
    List<DocumentChunk> findTopKSimilar(@Param("embedding") String embedding, @Param("k") int k);

    void deleteByDocumentId(Long documentId);
}