

# AI Robot

一个基于 Spring AI + Vue 3 的智能对话机器人系统，支持网络搜索、RAG（检索增强生成）、多模态生成等多种 AI 能力。

## 项目架构

本项目由四个核心模块组成：

| 模块 | 描述 | 技术栈 |
|------|------|--------|
| ai-robot-client | 主对话客户端 | Spring Boot 3 + Spring AI + OpenAI |
| ai-robot-rag-client | RAG 向量检索模块 | Spring Boot 3 + Spring AI + Vector Store |
| mcp-server | MCP 工具服务 | Spring Boot 3 + MCP Protocol |
| ai-robot-front | 前端界面 | Vue 3 + Vite |

## 功能特性

### 🤖 智能对话
- 支持流式输出（SSE）
- 多模型支持（文本生成、图像生成、语音合成、视频生成）
- 对话记忆功能

### 🌐 网络搜索
- 集成 SearXNG 搜索引擎
- AI 智能总结搜索结果
- 并发获取网页内容

### 📚 RAG 检索增强
- 支持多种文档格式：TXT、JSON、Markdown、HTML、PDF、Word、PPT
- 向量存储与相似度检索
- 自定义文本分割器

### 🔧 MCP 工具集
- **日期工具**：获取当前时间、支持不同时区
- **邮件工具**：发送邮件通知
- **天气工具**：城市搜索、天气查询

### 🎨 多模态能力
- 文本生成图像
- 文本生成语音
- 文本生成视频

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- OpenAI API Key

### 后端启动

```bash
# 启动主客户端
cd ai-robot-client
mvn spring-boot:run

# 启动 RAG 客户端（可选）
cd ai-robot-rag-client
mvn spring-boot:run

# 启动 MCP 服务（可选）
cd mcp-server
mvn spring-boot:run
```

### 前端启动

```bash
cd ai-robot-front
npm install
npm run dev
```

### 配置说明

在 `application.yml` 中配置必要的 API Key：

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key
```

## API 接口

### 对话接口

```
GET /test/chatFlux?msg=你好
```

### 网络搜索

```
GET /network/search?query=关键词
GET /network/chat?msg=问题
```

### RAG 查询

```
GET /vector/query?query=问题
GET /vector/rag?query=问题
```

## 项目结构

```
ai-robot/
├── ai-robot-client/          # 主客户端
│   ├── src/main/java/
│   │   └── io/github/zh/
│   │       ├── config/        # 配置类
│   │       ├── controller/    # 控制器
│   │       ├── service/      # 服务层
│   │       ├── advisor/      # AI 顾问
│   │       └── utils/        # 工具类
│   └── src/main/resources/
│       └── prompts/           # 提示词模板
│
├── ai-robot-rag-client/      # RAG 模块
│   └── src/main/java/
│       └── io/github/zh/
│           ├── reader/        # 文档读取器
│           ├── controller/   # 控制器
│           └── runner/       # 初始化_runner
│
├── mcp-server/               # MCP 工具服务
│   └── src/main/java/
│       └── io/github/zh/
│           └── tools/        # 工具实现
│
└── ai-robot-front/           # 前端项目
    └── src/
        ├── views/            # 页面视图
        ├── components/       # 组件
        └── router/           # 路由配置
```

## 技术亮点

1. **Spring AI 集成**：充分利用 Spring AI 的 AI 模型抽象层
2. **响应式编程**：使用 WebFlux 实现非阻塞流式响应
3. **异步处理**：CompletableFuture 实现高并发内容获取
4. **SSE 实时推送**：Server-Sent Events 实现实时 AI 响应
5. **AOP 日志**：切面编程记录服务执行时间

## 许可证

MIT License