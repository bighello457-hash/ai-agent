import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { createSseConnection } from '../services/sse'

function createId(prefix = 'msg') {
  if (crypto?.randomUUID) {
    return `${prefix}-${crypto.randomUUID()}`
  }

  return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}`
}

function readJson(key, fallback) {
  try {
    const raw = localStorage.getItem(key)
    return raw ? JSON.parse(raw) : fallback
  } catch {
    return fallback
  }
}

function createGreeting(appConfig) {
  return {
    id: createId('assistant'),
    role: 'assistant',
    content: appConfig.greeting,
    createdAt: new Date().toISOString(),
    status: 'done',
  }
}

function getOrCreateChatId(appConfig) {
  if (!appConfig.includeChatId) {
    return ''
  }

  const cached = localStorage.getItem(appConfig.chatIdKey)

  if (cached) {
    return cached
  }

  const nextId = createId('chat')
  localStorage.setItem(appConfig.chatIdKey, nextId)
  return nextId
}

function createAssistantMessage(content = '', status = 'streaming') {
  return {
    id: createId('assistant'),
    role: 'assistant',
    content,
    createdAt: new Date().toISOString(),
    status,
  }
}

function parseToolPayload(payload) {
  const text = payload.trim()

  if (!text) {
    return ''
  }

  try {
    const parsed = JSON.parse(text)

    if (Array.isArray(parsed)) {
      return parsed
        .map((item) => parseToolPayload(item.text ?? item.content ?? JSON.stringify(item)))
        .filter(Boolean)
        .join('\n')
    }

    if (typeof parsed === 'string') {
      return parseToolPayload(parsed)
    }

    if (parsed && typeof parsed === 'object') {
      return JSON.stringify(parsed, null, 2)
    }
  } catch {
    return text
      .replace(/\\n/g, '\n')
      .replace(/\\"/g, '"')
      .replace(/https?:\/\//g, '\n$&')
      .replace(/,+\s*/g, '\n')
      .trim()
  }

  return text
}

function formatToolLine(line) {
  const match = line.match(/^工具(.+?)完成了它的任务([\s\S]*)$/)

  if (!match) {
    return line.trim()
  }

  const [, toolName, payload] = match
  const result = parseToolPayload(payload)

  return `工具 ${toolName.trim()} 完成了它的任务！${result ? `\n结果：${result}` : ''}`
}

function formatStepChunk(chunk) {
  const text = chunk.trim()
  const stepMatch = text.match(/^Step\s*(\d+)\s*:\s*([\s\S]*)$/i)
  const stepLabel = stepMatch ? `Step ${stepMatch[1]}：` : ''
  const body = stepMatch ? stepMatch[2] : text
  const lines = body
    .split(/\n(?=工具)/)
    .map(formatToolLine)
    .filter(Boolean)

  return [stepLabel, ...lines].filter(Boolean).join('\n')
}

export function useChatSession(appConfig) {
  const messages = ref(
    readJson(appConfig.storageKey, [createGreeting(appConfig)]),
  )
  const draft = ref('')
  const error = ref('')
  const isStreaming = ref(false)
  const activeConnection = ref(null)
  const chatId = ref(getOrCreateChatId(appConfig))

  const canSend = computed(() => {
    return draft.value.trim().length > 0 && !isStreaming.value
  })

  watch(
    messages,
    (value) => {
      localStorage.setItem(appConfig.storageKey, JSON.stringify(value))
    },
    { deep: true },
  )

  function setAssistantStatus(id, status) {
    const target = messages.value.find((message) => message.id === id)
    if (target) {
      target.status = status
    }
  }

  function appendAssistantContent(id, chunk) {
    const target = messages.value.find((message) => message.id === id)
    if (target) {
      target.content += chunk
    }
  }

  function replaceAssistantContent(id, chunk) {
    const target = messages.value.find((message) => message.id === id)
    if (target) {
      target.content = chunk
    }
  }

  function stopStreaming(status = 'stopped') {
    activeConnection.value?.close()
    activeConnection.value = null
    isStreaming.value = false

    const current = [...messages.value]
      .reverse()
      .find((message) => message.role === 'assistant' && message.status === 'streaming')

    if (current) {
      current.status = status
      if (!current.content) {
        current.content = status === 'error' ? '连接失败，请稍后重试。' : '已停止生成。'
      }
    }
  }

  function sendMessage() {
    const content = draft.value.trim()

    if (!content || isStreaming.value) {
      return
    }

    error.value = ''
    draft.value = ''

    const userMessage = {
      id: createId('user'),
      role: 'user',
      content,
      createdAt: new Date().toISOString(),
      status: 'done',
    }

    const assistantMessage = createAssistantMessage()
    const stepMode = appConfig.responseMode === 'steps'
    let firstStepMessageId = assistantMessage.id

    messages.value.push(userMessage, assistantMessage)
    isStreaming.value = true

    const params = {
      message: content,
    }

    if (appConfig.includeChatId) {
      params.chatId = chatId.value
    }

    activeConnection.value = createSseConnection({
      path: appConfig.endpoint,
      params,
      onChunk: (chunk) => {
        if (!stepMode) {
          appendAssistantContent(assistantMessage.id, chunk)
          return
        }

        const stepContent = formatStepChunk(chunk)

        if (firstStepMessageId) {
          replaceAssistantContent(firstStepMessageId, stepContent)
          setAssistantStatus(firstStepMessageId, 'done')
          firstStepMessageId = ''
          return
        }

        messages.value.push(createAssistantMessage(stepContent, 'done'))
      },
      onDone: () => {
        setAssistantStatus(assistantMessage.id, 'done')
        isStreaming.value = false
        activeConnection.value = null
      },
      onError: () => {
        error.value = '无法连接到后端 SSE 服务，请确认 http://localhost:8123/api 已启动。'
        setAssistantStatus(assistantMessage.id, 'error')
        appendAssistantContent(assistantMessage.id, '连接失败，请检查后端服务或稍后重试。')
        isStreaming.value = false
        activeConnection.value = null
      },
    })
  }

  function clearMessages() {
    stopStreaming('stopped')
    messages.value = [createGreeting(appConfig)]
    localStorage.setItem(appConfig.storageKey, JSON.stringify(messages.value))
  }

  onBeforeUnmount(() => {
    stopStreaming('stopped')
  })

  return {
    messages,
    draft,
    error,
    isStreaming,
    canSend,
    chatId,
    sendMessage,
    stopStreaming,
    clearMessages,
  }
}
