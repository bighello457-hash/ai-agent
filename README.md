# ai-agent

一个基于 Spring Boot 3、Spring AI 与 Vue 3 的 AI Agent 项目。项目包含面向求职场景的 OfferPilot 对话助手、多工具调用 Agent、RAG 检索增强、文件型会话记忆、MySQL 会话存储能力，以及一个独立的图片搜索 MCP 服务模块。

## 项目功能

- **OfferPilot 求职助手**：围绕简历优化、面试准备、Offer 判断、职业选择等场景提供结构化建议。
- **FxsManus 多工具智能体**：基于 ReAct / Tool Calling 思路，支持任务拆解、工具调用与流式过程输出。
- **RAG 检索增强**：支持本地 Markdown 文档加载、向量化检索、查询改写、多查询扩展等能力。
- **会话记忆**：支持基于 Kryo 的本地文件会话记忆，并预留 MySQL 持久化存储实现。
- **MCP 扩展**：集成 Spring AI MCP Client / Server，并包含 `image-search-mcp` 图片搜索 MCP 子项目。
- **前端聊天界面**：基于 Vue 3 + Vite，支持 SSE 流式响应、会话缓存和多应用入口。
- **接口文档**：集成 Springdoc OpenAPI 与 Knife4j，便于调试后端接口。

## 技术栈

### 后端

- Java 21
- Spring Boot 3.4.4
- Spring AI / Spring AI Alibaba
- DashScope / 通义千问模型
- LangChain4j DashScope
- MyBatis-Plus
- MySQL
- Hutool
- iText PDF
- Jsoup
- Knife4j / Springdoc OpenAPI

### 前端

- Vue 3
- Vite
- Axios
- EventSource / SSE

### 子模块

- `image-search-mcp`：基于 Spring Boot 3.5.x 与 Spring AI MCP Server 的图片搜索 MCP 服务。

## 项目结构

```text
ai-agent
├── src/main/java/com/fxs/aiagent
│   ├── agent              # Agent 抽象、ReAct Agent、ToolCall Agent、FxsManus
│   ├── app                # OfferPilot
│   ├── advisor            # ChatClient Advisor 扩展
│   ├── chatMemory         # 文件 / MySQL 会话记忆
│   ├── config             # CORS、工具注册、MCP 工具聚合等配置
│   ├── controller         # REST / SSE 接口
│   ├── demo               # 模型调用与 RAG 示例
│   ├── rag                # 文档加载、向量库、RAG Advisor 配置
│   ├── tools              # 文件、终端、网页抓取、PDF、资源下载等工具
│   └── prompt             # 系统提示词与模板服务
├── src/main/resources
│   ├── application.yml    # 主配置
│   ├── mapper             # MyBatis Mapper XML
│   ├── prompts            # Freemarker / 文本提示词模板
│   └── mcp-servers.json   # MCP Server 配置示例
├── ai-agent-frontened     # Vue 3 前端项目
├── image-search-mcp       # 图片搜索 MCP 服务
├── chat-memory            # 本地会话记忆文件目录
├── Dockerfile             # 后端 Docker 构建文件
└── pom.xml                # 后端 Maven 配置
```

## 环境要求

- JDK 21+
- Maven 3.9+，也可以直接使用项目自带的 `mvnw`
- Node.js 18+
- MySQL 8.x
- 可用的 DashScope API Key

## 配置说明

项目默认读取 Spring 配置文件中的模型、数据库和搜索服务配置。建议在本地或生产环境中通过环境变量覆盖敏感信息，不要把真实密钥提交到仓库。

常用配置项：

```bash
SPRING_PROFILES_ACTIVE=local
SPRING_AI_DASHSCOPE_API_KEY=your_dashscope_api_key
SPRING_AI_DASHSCOPE_CHAT_OPTIONS_MODEL=qwen-max
SEARCH_API_API_KEY=your_search_api_key
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ai_agent?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_mysql_password
```

前端默认请求后端地址：

```bash
VITE_API_BASE_URL=http://localhost:8123/api
```

可以在 `ai-agent-frontened/.env.development` 或启动环境中覆盖该值。

## 后端启动

在项目根目录执行：

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Windows PowerShell 可使用：

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

服务默认启动在：

```text
http://localhost:8123/api
```

健康检查：

```text
GET http://localhost:8123/api/health
```

接口文档：

```text
http://localhost:8123/api/doc.html
http://localhost:8123/api/swagger-ui.html
```

## 前端启动

进入前端目录：

```bash
cd ai-agent-frontened
npm install
npm run dev
```

前端默认启动在：

```text
http://localhost:5173
```

构建生产包：

```bash
npm run build
```

## 主要接口

### OfferPilot 同步对话

```text
GET /api/ai/love_app/chat/sync?message=你好&chatId=demo-chat
```

### OfferPilot SSE 流式对话

```text
GET /api/ai/love_app/chat/sse?message=帮我优化自我介绍&chatId=demo-chat
```

### OfferPilot Server-Sent Event 包装响应

```text
GET /api/ai/love_app/chat/sse/server?message=帮我模拟面试&chatId=demo-chat
```

### FxsManus 多工具 Agent

```text
GET /api/ai/manus/chat?message=帮我抓取网页并总结重点
```

## MCP 图片搜索服务

`image-search-mcp` 是独立的 MCP Server 子项目。可单独构建：

```bash
cd image-search-mcp
./mvnw clean package
```

Windows PowerShell：

```powershell
cd image-search-mcp
.\mvnw.cmd clean package
```

主项目中的 `src/main/resources/mcp-servers.json` 提供了 MCP Server 接入示例，可根据实际 API Key、Jar 路径和启动方式调整。

## Docker 部署

后端镜像构建：

```bash
docker build -t ai-agent-backend .
```

运行容器：

```bash
docker run -p 8123:8123 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_AI_DASHSCOPE_API_KEY=your_dashscope_api_key \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/ai_agent?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_mysql_password \
  ai-agent-backend
```

前端目录中也提供了独立的 `Dockerfile` 和 `nginx.conf`，可在 `ai-agent-frontened` 目录下构建前端镜像。

## 开发建议

- 真实 API Key、数据库密码、地图服务 Key 等敏感配置建议放入环境变量或部署平台密钥管理中。
- `chat-memory` 会保存本地会话文件，生产环境可按需切换到 MySQL 或其他持久化方案。
- 如果启用 MCP stdio 服务，请先确认对应子项目 Jar 已完成构建，并且 `mcp-servers.json` 中的路径与运行环境一致。
- 前后端联调时，请确保后端 `server.servlet.context-path` 与前端 `VITE_API_BASE_URL` 保持一致。

## License

该项目当前未声明开源许可证。如需公开使用或协作开发，建议补充合适的 License 文件。
