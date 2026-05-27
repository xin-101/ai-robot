# AI Robot 项目接口文档

## 概述
AI Robot 是一个基于 Spring AI 和 Vue 3 构建的全栈 AI 应用项目，提供多种 AI 能力，包括文本对话、多模态交互、网络搜索增强、RAG 知识库问答以及 MCP 工具集成。

**技术栈**:
- 后端: Spring Boot 3.5.6 + Spring AI 1.0.0 + DashScope 2.20.8
- 前端: Vue 3 + Vite
- 基础路径: 
  - 客户端 (Client): `http://localhost:8888`
  - RAG 客户端: `http://localhost:8889` (假设端口)

---

## 一、AI 对话接口 (ai-robot-client)

### 1.1 基础对话接口

#### 1.1.1 简单对话测试
- **URL**: `/test/hello`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 用户消息
- **响应类型**: `String` (普通文本)
- **示例**: 
  ```
  GET http://localhost:8888/test/hello?msg=你好
  ```

#### 1.1.2 流式对话测试
- **URL**: `/test/Flux`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 用户消息
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **示例**: 
  ```
  GET http://localhost:8888/test/Flux?msg=你好
  ```

#### 1.1.3 流式对话
- **URL**: `/test/chatFlux`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 用户消息
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **示例**: 
  ```
  GET http://localhost:8888/test/chatFlux?msg=请介绍一下你自己
  ```

#### 1.1.4 带记忆的流式对话
- **URL**: `/test/chatFluxMemory`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 用户消息
  - `chatId` (String, 必填): 会话 ID，用于维持上下文记忆
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **示例**: 
  ```
  GET http://localhost:8888/test/chatFluxMemory?msg=我们刚才说了什么&chatId=user123
  ```

#### 1.1.5 记忆对话
- **URL**: `/test/chatMemory`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 用户消息
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **示例**: 
  ```
  GET http://localhost:8888/test/chatMemory?msg=你好
  ```

#### 1.1.6 SSE 标准流式对话
- **URL**: `/test/chatFluxSSE`
- **方法**: `GET`
- **Content-Type**: `text/event-stream`
- **参数**: 
  - `msg` (String, 必填): 用户消息
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **示例**: 
  ```
  GET http://localhost:8888/test/chatFluxSSE?msg=请讲个故事
  ```

#### 1.1.7 SSE 结构化响应
- **URL**: `/test/chatFluxSSEAIResponse`
- **方法**: `GET`
- **Content-Type**: `text/event-stream`
- **参数**: 
  - `msg` (String, 必填): 用户消息
- **响应类型**: `Flux<AIResponse>` (SSE 流式响应，JSON 格式)
- **响应体示例**:
  ```json
  {
    "content": "AI 回复的内容"
  }
  ```
- **示例**: 
  ```
  GET http://localhost:8888/test/chatFluxSSEAIResponse?msg=你好
  ```

---

### 1.2 多模态接口 (Multi-Model)

#### 1.2.1 视觉模型对话
- **URL**: `/multiModel/viewChat`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 用户消息（针对内置图片提问）
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **说明**: 使用内置图片 `/images/1.png` 进行视觉问答
- **示例**: 
  ```
  GET http://localhost:8888/multiModel/viewChat?msg=这张图片中有什么？
  ```

#### 1.2.2 文生图
- **URL**: `/multiModel/text2Img`
- **方法**: `GET`
- **Content-Type**: `text/event-stream`
- **参数**: 
  - `msg` (String, 必填): 图片描述文本
- **响应类型**: `String` (JSON 格式)
- **模型**: `wan2.2-t2i-flash`
- **图片尺寸**: `1024*1024`
- **响应示例**:
  ```json
  {
    "output": {
      "results": [
        {
          "url": "生成的图片 URL"
        }
      ]
    },
    "request_id": "请求 ID"
  }
  ```
- **示例**: 
  ```
  GET http://localhost:8888/multiModel/text2Img?msg=一只可爱的猫咪在草地上玩耍
  ```

#### 1.2.3 文生音频
- **URL**: `/multiModel/text2Audio`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 要转换为音频的文本
- **响应类型**: 文件下载 (MP3)
- **模型**: `cosyvoice-v2`
- **音色**: `longxiaochun_v2`
- **说明**: 生成的音频文件保存到服务器 `D:\JavaProjects\ai-robot\output.mp3`
- **示例**: 
  ```
  GET http://localhost:8888/multiModel/text2Audio?msg=你好，欢迎使用 AI Robot
  ```

#### 1.2.4 文生视频
- **URL**: `/multiModel/text2Video`
- **方法**: `GET`
- **参数**: 
  - `msg` (String, 必填): 视频描述文本
- **响应类型**: `String` (JSON 格式)
- **模型**: `wan2.5-t2v-preview`
- **视频尺寸**: `1920*1080`
- **响应示例**:
  ```json
  {
    "output": {
      "video_url": "生成的视频 URL"
    },
    "request_id": "请求 ID"
  }
  ```
- **示例**: 
  ```
  GET http://localhost:8888/multiModel/text2Video?msg=一只小鸟在天空中飞翔
  ```

---

### 1.3 网络搜索接口 (Network Search)

#### 1.3.1 网络搜索
- **URL**: `/network/search`
- **方法**: `GET`
- **参数**: 
  - `query` (String, 必填): 搜索关键词
- **响应类型**: `List<SearchResult>` (JSON 数组)
- **说明**: 调用 SearXNG 服务进行网络搜索
- **响应体示例**:
  ```json
  [
    {
      "title": "搜索结果标题",
      "url": "https://example.com",
      "content": "搜索结果摘要内容"
    }
  ]
  ```
- **示例**: 
  ```
  GET http://localhost:8888/network/search?query=Spring AI 教程
  ```

#### 1.3.2 AI 增强搜索
- **URL**: `/network/searchToAi`
- **方法**: `GET`
- **参数**: 
  - `query` (String, 必填): 搜索关键词
- **响应类型**: `List<SearchResult>` (JSON 数组)
- **说明**: 搜索并处理结果，10 秒超时，适合 AI 后续处理
- **示例**: 
  ```
  GET http://localhost:8888/network/searchToAi?query=最新 AI 技术
  ```

#### 1.3.3 网络搜索对话
- **URL**: `/network/chat`
- **方法**: `GET`
- **Content-Type**: `text/event-stream`
- **参数**: 
  - `msg` (String, 必填): 用户问题
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **说明**: 结合网络搜索结果进行智能对话，自动搜索相关信息后回答
- **示例**: 
  ```
  GET http://localhost:8888/network/chat?msg=2024年最新的AI技术有哪些？
  ```

---

## 二、RAG 知识库接口 (ai-robot-rag-client)

### 2.1 向量数据库查询

#### 2.1.1 查询向量数据库
- **URL**: `/vector/query`
- **方法**: `GET`
- **参数**: 
  - `query` (String, 必填): 查询关键词
- **响应类型**: `List<Document>` (JSON 数组)
- **说明**: 从向量数据库中搜索相似文档，返回 Top 2 结果
- **响应体示例**:
  ```json
  [
    {
      "id": "文档 ID",
      "text": "文档内容",
      "metadata": {
        "source": "文档来源"
      },
      "similarity": 0.95
    }
  ]
  ```
- **示例**: 
  ```
  GET http://localhost:8889/vector/query?query=Java 编程基础
  ```

#### 2.1.2 RAG 知识库对话
- **URL**: `/vector/rag`
- **方法**: `GET`
- **Content-Type**: `text/event-stream`
- **参数**: 
  - `query` (String, 必填): 用户问题
- **响应类型**: `Flux<String>` (SSE 流式响应)
- **说明**: 基于内部知识库进行问答，如果未找到相关资料会回复"我不会"
- **示例**: 
  ```
  GET http://localhost:8889/vector/rag?query=如何学习 Java？
  ```

---

### 2.2 文档读取测试接口

> 以下接口用于测试各类文档读取功能，返回解析后的文档列表

#### 2.2.1 TXT 文档读取
- **URL**: `/test/txt1`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 TXT 文档（不分割）

#### 2.2.2 TXT 文档读取并分割
- **URL**: `/test/txt2`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 TXT 文档并分割成小块

#### 2.2.3 JSON 文档读取
- **URL**: `/test/json1`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 JSON 文档（不分割）

#### 2.2.4 JSON 文档读取并分割
- **URL**: `/test/json2`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 JSON 文档并分割成小块

#### 2.2.5 Markdown 文档读取
- **URL**: `/test/md`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 Markdown 文档

#### 2.2.6 HTML 文档读取
- **URL**: `/test/html1`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 HTML 文档（不分割）

#### 2.2.7 HTML 文档读取并分割
- **URL**: `/test/html2`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 HTML 文档并分割成小块

#### 2.2.8 PDF 文档读取
- **URL**: `/test/pdf`
- **方法**: `GET/POST`
- **响应类型**: `List<Document>`
- **说明**: 读取 PDF 文档

---

## 三、MCP 工具接口

MCP (Model Context Protocol) 服务器提供工具调用能力，通过 Spring AI 集成到对话系统中，不直接对外暴露 HTTP 接口。

### 3.1 可用工具

#### 3.1.1 日期工具 (DateTool)
- **功能**: 获取当前日期时间
- **工具名**: `get_current_date`
- **参数**: 无
- **返回**: 当前日期时间字符串

#### 3.1.2 邮件工具 (EmailTool)
- **功能**: 发送邮件
- **工具名**: `send_email`
- **参数**:
  ```json
  {
    "to": "收件人邮箱",
    "subject": "邮件主题",
    "content": "邮件内容"
  }
  ```
- **返回**: 发送结果状态

#### 3.1.3 天气工具 (WeatherTool)
- **功能**: 查询天气信息
- **工具名**: `get_weather`
- **参数**:
  ```json
  {
    "city": "城市名称"
  }
  ```
- **返回**: 天气信息，包括温度、天气状况等

---

## 四、前端接口调用示例

### 4.1 SSE 流式响应处理 (Vue 3)

```javascript
// 使用 EventSource 处理 SSE 流式响应
const eventSource = new EventSource(`http://localhost:8888/test/chatFluxSSE?msg=${encodeURIComponent(message)}`);

eventSource.onmessage = (event) => {
  console.log('收到消息:', event.data);
  // 更新 UI
};

eventSource.onerror = (error) => {
  console.error('SSE 错误:', error);
  eventSource.close();
};
```

### 4.2 Fetch API 调用示例

```javascript
// 调用网络搜索对话
const response = await fetch(`http://localhost:8888/network/chat?msg=${encodeURIComponent(query)}`);
const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
  const { done, value } = await reader.read();
  if (done) break;
  const text = decoder.decode(value);
  console.log('流式内容:', text);
}
```

---

## 五、通用说明

### 5.1 响应格式

#### 普通响应
```json
{
  "content": "响应内容"
}
```

#### SSE 流式响应
```
data: 第一段内容

data: 第二段内容

data: [DONE]
```

### 5.2 错误处理

- **400 Bad Request**: 参数错误或缺失
- **404 Not Found**: 接口不存在
- **500 Internal Server Error**: 服务器内部错误
- **网络异常**: 前端需实现超时和重连机制

### 5.3 跨域配置

项目已配置 CORS，允许前端跨域访问。配置详见 `CorsConfig.java`。

### 5.4 字符编码

所有接口统一使用 UTF-8 编码。

### 5.5 注意事项

1. **流式响应**: 所有返回 `Flux<String>` 的接口都使用 SSE (Server-Sent Events) 协议
2. **会话记忆**: 使用 `chatId` 参数维持多轮对话上下文
3. **超时处理**: 网络搜索接口设置了 10 秒超时
4. **文件路径**: 文生音频功能会将文件保存到服务器本地
5. **API Key**: 多模态接口需要配置 DashScope API Key

---

## 六、配置说明

### 6.1 环境变量

在 `.env` 文件中配置：
```env
# DashScope API Key
DASHSCOPE_API_KEY=your_api_key

# SearXNG 服务地址
SEARXNG_URL=http://localhost:8080
```

### 6.2 应用配置

在 `application.yml` 中配置：
```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}
      chat:
        options:
          model: qwen-turbo
```

---

## 七、更新日志

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-04-09 | v1.0 | 初始版本，包含所有核心接口文档 |

---

## 八、联系方式

如有问题，请联系开发团队。

**文档生成时间**: 2026-04-09
