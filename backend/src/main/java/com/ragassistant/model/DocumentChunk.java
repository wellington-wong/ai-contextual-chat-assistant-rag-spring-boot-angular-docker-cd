package com.ragassistant.model;
import jakarta.persistence.*;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunks")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunk {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id", nullable = false)
	private Document document;

	@Column(name = "chunk_index", nullable = false)
	private Integer chunkIndex;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(columnDefinition = "vector(1024)")
	@JdbcTypeCode(SqlTypes.ARRAY)
	private float[] embedding;

	@Column(name = "created_at")
	private LocalDateTime createAt = LocalDateTime.now();
}