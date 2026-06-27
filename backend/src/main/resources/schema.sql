CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS documents (

	id			BIGSERIAL PRIMARY KEY,
	name		VARCHAR(255) NOT NULL,
	content		TEXT NOT NULL,
	file_type	VARCHAR(50),
	created_at	TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS document_chunks (
	id				BIGSERIAL PRIMARY KEY,

	document_id	BIGINT NOT NULL
	    REFERENCES documents(id) ON DELETE CASCADE,
	chunk_index	INT NOT NULL,
	content			TEXT NOT NULL,
	embedding		VECTOR(1024) NOT NULL,
	created_at		TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(document_id, chunk_index)
);

CREATE INDEX IF NOT EXISTS idx_chunks_embedding
	ON document_chunks
    USING hnsw (embedding vector_cosine_ops);

CREATE TABLE IF NOT EXISTS chat_sessions (
	id			BIGSERIAL PRIMARY KEY,
	title			VARCHAR(255),
	created_at	TIMESTAMPTZ DEFAULT NOW(),
	updated_at	TIMESTAMPTZ DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS chat_messages(
	id			BIGSERIAL PRIMARY KEY,
	session_id	BIGINT NOT NULL
	    REFERENCES chat_sessions(id) ON DELETE CASCADE,
	role			VARCHAR(20) NOT NULL,
        CHECK (role IN('system', 'user', 'assistant')),
	content		TEXT NOT NULL,
	created_at	TIMESTAMPTZ DEFAULT NOW()
);


CREATE INDEX IF NOT EXISTS idx_message_session
ON chat_messages(session_id, id);

CREATE INDEX IF NOT EXISTS idx_document_chunks_document
ON document_chunks(document_id, chunk_index);