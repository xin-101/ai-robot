# AI Robot

An intelligent conversational robot system based on Spring AI + Vue 3, supporting AI capabilities such as web search, RAG (Retrieval-Augmented Generation), and multimodal generation.

## Project Architecture

This project consists of four core modules:

| Module | Description | Technology Stack |
|--------|-------------|------------------|
| ai-robot-client | Main conversational client | Spring Boot 3 + Spring AI + OpenAI |
| ai-robot-rag-client | RAG vector retrieval module | Spring Boot 3 + Spring AI + Vector Store |
| mcp-server | MCP tool service | Spring Boot 3 + MCP Protocol |
| ai-robot-front | Frontend interface | Vue 3 + Vite |

## Features

### 🤖 Intelligent Conversation
- Supports streaming output (SSE)
- Multi-model support (text generation, image generation, speech synthesis, video generation)
- Conversation memory functionality

### 🌐 Web Search
- Integrated SearXNG search engine
- AI-powered summarization of search results
- Concurrent web content retrieval

### 📚 RAG Retrieval-Augmented Generation
- Supports multiple document formats: TXT, JSON, Markdown, HTML, PDF, Word, PPT
- Vector storage and similarity-based retrieval
- Customizable text splitter

### 🔧 MCP Toolset
- **Date Tool**: Get current time with support for multiple time zones
- **Email Tool**: Send email notifications
- **Weather Tool**: City search and weather query

### 🎨 Multimodal Capabilities
- Text-to-image generation
- Text-to-speech generation
- Text-to-video generation

## Quick Start

### Environment Requirements

- JDK 17+
- Maven 3.8+
- Node.js 18+
- OpenAI API Key

### Backend Startup

```bash
# Start main client
cd ai-robot-client
mvn spring-boot:run

# Start RAG client (optional)
cd ai-robot-rag-client
mvn spring-boot:run

# Start MCP service (optional)
cd mcp-server
mvn spring-boot:run
```

### Frontend Startup

```bash
cd ai-robot-front
npm install
npm run dev
```

### Configuration

Configure required API keys in `application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key
```

## API Endpoints

### Conversation Endpoint

```
GET /test/chatFlux?msg=Hello
```

### Web Search

```
GET /network/search?query=keyword
GET /network/chat?msg=question
```

### RAG Query

```
GET /vector/query?query=question
GET /vector/rag?query=question
```

## Project Structure

```
ai-robot/
├── ai-robot-client/          # Main client
│   ├── src/main/java/
│   │   └── io/github/zh/
│   │       ├── config/        # Configuration classes
│   │       ├── controller/    # Controllers
│   │       ├── service/       # Service layer
│   │       ├── advisor/       # AI advisor
│   │       └── utils/         # Utility classes
│   └── src/main/resources/
│       └── prompts/           # Prompt templates
│
├── ai-robot-rag-client/      # RAG module
│   └── src/main/java/
│       └── io/github/zh/
│           ├── reader/        # Document readers
│           ├── controller/    # Controllers
│           └── runner/        # Initialization runner
│
├── mcp-server/               # MCP tool service
│   └── src/main/java/
│       └── io/github/zh/
│           └── tools/         # Tool implementations
│
└── ai-robot-front/           # Frontend project
    └── src/
        ├── views/            # Page views
        ├── components/       # Components
        └── router/           # Routing configuration
```

## Technical Highlights

1. **Spring AI Integration**: Leverages Spring AI's model abstraction layer
2. **Reactive Programming**: Uses WebFlux for non-blocking streaming responses
3. **Asynchronous Processing**: Employs CompletableFuture for high-concurrency content retrieval
4. **SSE Real-time Push**: Implements Server-Sent Events for real-time AI responses
5. **AOP Logging**: Uses aspect-oriented programming to log service execution times

## License

MIT License