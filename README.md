# 🚀 DevPilot

An **AI-powered codebase assistant** built with Spring Boot and Next.js, designed to help developers understand and interact with their GitHub repositories using Retrieval-Augmented Generation (RAG).

Connect your GitHub account, select a repository, index its codebase, and ask questions about your code. DevPilot retrieves relevant code snippets and uses them as context to generate meaningful answers with citations.

## 🛠️ Tech Stack

- **Java 21 & Spring Boot** — Backend application
- **Spring AI** — AI integration and RAG pipeline
- **Spring Security & OAuth2** — GitHub authentication
- **Spring Data JPA** — Database access and persistence
- **PostgreSQL & PGVector** — Relational and vector storage
- **OpenAI API** — Chat completions and embedding generation
- **Next.js, React & TypeScript** — Frontend application
- **GitHub REST API** — Repository synchronization and code retrieval
- **Docker & Docker Compose** — Database containerization
- **Server-Sent Events (SSE)** — Real-time AI response streaming
- **Maven & npm** — Dependency management and build tools

## 🏗️ Architecture

DevPilot follows a **Retrieval-Augmented Generation (RAG)** architecture to generate answers grounded in the actual contents of a repository.

1. **GitHub Authentication** — Users sign in using GitHub OAuth2.
2. **Repository Synchronization** — Fetches the user's GitHub repositories.
3. **Code Indexing** — Retrieves repository files, filters them, and splits them into smaller chunks.
4. **Embedding Generation** — Converts code chunks into vector embeddings using OpenAI.
5. **Vector Storage** — Stores embeddings and metadata in PostgreSQL using PGVector.
6. **Similarity Search** — Retrieves relevant code chunks based on the user's question.
7. **AI Response Generation** — Passes the retrieved context to the OpenAI chat model to generate contextual answers.
8. **Response Streaming** — Streams generated responses to the frontend using SSE.

## 🌐 Services & Ports

| Service | Port |
|---|---:|
| Backend (Spring Boot) | `8080` |
| Frontend (Next.js) | `3000` |
| PostgreSQL (Host) | `5433` |
| PostgreSQL (Container) | `5432` |

## ✨ Highlights

- **GitHub OAuth2 authentication** for secure user login
- **GitHub repository synchronization** to fetch and manage repositories
- **Automated code indexing** with file filtering and chunking
- **RAG-based codebase Q&A** using relevant repository context
- **Vector similarity search** powered by PostgreSQL and PGVector
- **OpenAI integration** for embeddings and contextual AI responses
- **Asynchronous repository indexing** to avoid blocking API requests
- **Real-time response streaming** using Server-Sent Events
- **Chat sessions and message history** for repository-specific conversations
- **Source citations** to identify the code snippets used in AI responses

## ⚙️ Getting Started

### Prerequisites

- Java 21 or later
- Maven
- Node.js and npm
- Docker & Docker Compose
- Git

### Clone the Repository

```bash
git clone https://github.com/NitinBhattar/DevPilot-FML.git
cd DevPilot-FML
```

### Environment Configuration

Configure the required environment variables before starting the application.

Check the `.env` files in the following locations:

- Project root (`/`)
- Frontend (`/client`)
- Backend (`/backend`)

Make sure the required values are configured in their respective files.

### Database Setup

DevPilot uses PostgreSQL with the PGVector extension, configured through Docker Compose.

From the repository root, run:

```bash
docker compose up -d
```

PostgreSQL runs inside the container on port `5432`, mapped to host port `5433`.

### Run the Backend

Run these commands from the repository root:

```bash
cd ./backend
mvn clean install
mvn spring-boot:run
```

Backend will be available at `http://localhost:8080`.

### Run the Frontend

Open another terminal from the repository root:

```bash
cd ./client
npm install
npm run dev
```

Frontend will be available at `http://localhost:3000`.

> Make sure Docker is running and the database is ready before starting the backend. Configure the required environment variables and GitHub OAuth application credentials before launching DevPilot.

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for details.

---

<p align="center">Built with ☕ Java, 🍀 Spring Boot and ▲ Next.js</p>