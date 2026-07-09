# 🤖 AI-Powered Contextual Chat Assistant (RAG)

A Retrieval-Augmented Generation (RAG) chatbot that provides context-aware responses by retrieving relevant document chunks before generating answers with an LLM.

The application consists of an Angular frontend, a Spring Boot REST API backend, and PostgreSQL for persistent storage. It supports document ingestion, semantic retrieval, chat history, and contextual conversations.

---

## ✨ Features

- 📄 Document ingestion and indexing
- 🔍 Retrieval-Augmented Generation (RAG)
- 💬 Context-aware multi-turn conversations
- 🧠 LLM integration via Anthropic API
- 📚 Automatic document chunking
- 🗂 Chat session management
- 📝 Persistent chat history
- 🐳 Docker & Docker Compose support
- ☁️ Cloud deployment ready

---

# Architecture

```
                    +----------------------+
                    |      End Users       |
                    +----------+-----------+
                               |
                        HTTPS (CloudFront)
                               |
                     +---------v---------+
                     |  Angular Frontend |
                     |      (S3)         |
                     +---------+---------+
                               |
                          REST API
                               |
                     +---------v---------+
                     | Spring Boot API   |
                     |  DigitalOcean VPS |
                     +---------+---------+
                               |
          +--------------------+--------------------+
          |                                         |
+---------v---------+                    +----------v----------+
|   PostgreSQL      |                    | Anthropic Claude API|
|  Chat + Documents |                    |      LLM            |
+-------------------+                    +---------------------+
```

---

# Tech Stack

## Frontend

- Angular
- TypeScript
- HTML/CSS
- RxJS

## Backend

- Spring Boot
- Java
- Spring Web
- Spring Data JDBC/JPA
- Maven

## Database

- PostgreSQL

## AI / RAG

- Anthropic Claude API
- Document Chunking
- Context Retrieval

## Infrastructure

- Docker
- Docker Compose
- Nginx
- AWS S3
- AWS CloudFront
- DigitalOcean Droplet (VPS)

---

# Repository Structure

```
.
├── backend/                  # Spring Boot REST API
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   └── config/
│
├── frontend/                 # Angular SPA
│   ├── src/
│   │   ├── components/
│   │   ├── services/
│   │   └── models/
│
├── docker-compose.yml
├── docker-compose.prod.yml
└── README.md
```

---

# Backend Components

### Controllers

- `ChatController`
    - Chat API endpoints
- `DocumentController`
    - Document upload & ingestion
- `GlobalExceptionHandler`
    - Global API exception handling

### Services

- `RagChatService`
    - Main RAG workflow
- `DocumentIngestionService`
    - Document processing
- `ChunkingService`
    - Text chunk generation
- `AnthropicApiClient`
    - Claude API integration

### Repositories

- Chat persistence
- Document persistence
- Chunk retrieval
- Semantic search

---

# Frontend

The Angular application provides:

- Chat interface
- Conversation history
- Streaming AI responses (if enabled)
- API communication
- Responsive UI

Main component:

```
ChatComponent
```

Communicates with:

```
ApiService
```

---

# RAG Workflow

```
          Upload Document
                  │
                  ▼
         Document Ingestion
                  │
                  ▼
          Text Chunking
                  │
                  ▼
      Store Chunks in PostgreSQL
                  │
                  ▼
           User asks question
                  │
                  ▼
       Retrieve relevant chunks
                  │
                  ▼
      Build contextual prompt
                  │
                  ▼
        Anthropic Claude API
                  │
                  ▼
         AI-generated response
```

---

# Deployment Architecture

## Frontend

- Angular production build
- Hosted on **AWS S3**
- Served globally through **AWS CloudFront**

Benefits:

- Low latency
- CDN caching
- HTTPS
- Scalable static hosting

---

## Backend

Hosted on a **DigitalOcean Droplet (VPS)** using Docker Compose.

Runs:

- Spring Boot API
- PostgreSQL
- Reverse proxy (optional)

---

# Docker

Development

```bash
docker-compose up --build
```

Production

```bash
docker compose -f docker-compose.prod.yml up -d
```

---

# Configuration

The backend uses `application.properties`.

Typical environment variables include:

```properties
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

ANTHROPIC_API_KEY=
```

---

# API Overview

## Chat

```
POST /api/chat
```

Generate a contextual AI response.

---

## Documents

```
POST /api/documents
```

Upload and ingest documents.

---

## Health

```
GET /
```

Basic application status.

---

# Data Model

Core entities:

- ChatSession
- ChatMessage
- Document
- DocumentChunk
- Chat

---

# Running Locally

## Clone

```bash
git clone https://github.com/<username>/<repository>.git

cd <repository>
```

## Backend

```bash
cd backend

mvn spring-boot:run
```

## Frontend

```bash
cd frontend

npm install

ng serve
```

Open

```
http://localhost:4200
```

---

# Production Infrastructure

| Component | Technology |
|------------|------------|
| Frontend | Angular |
| Static Hosting | AWS S3 |
| CDN | AWS CloudFront |
| Backend | Spring Boot |
| Database | PostgreSQL |
| VPS | DigitalOcean Droplet |
| Containerization | Docker |
| Build Tool | Maven |
| Package Manager | npm |

---

# Future Improvements

- Vector database integration
- Embedding generation service
- Streaming responses
- Authentication & authorization
- Multiple document collections
- Citations with source highlighting
- Conversation export
- Role-based access control
- Redis caching
- Kubernetes deployment
- CI/CD pipeline
- Monitoring & observability

---

# License

This project is licensed under the MIT License.

---

# Author

Built as a Retrieval-Augmented Generation (RAG) assistant using Angular, Spring Boot, PostgreSQL, and Anthropic Claude.