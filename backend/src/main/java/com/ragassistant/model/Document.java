package com.ragassistant.model;
import jakarta.persistence.*;
import lombok.Data;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documents")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)

	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "created_at")

	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentChunk> chunks;
}