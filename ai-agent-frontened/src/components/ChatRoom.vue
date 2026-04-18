<script setup>
import { nextTick, ref, watch } from 'vue'
import { useChatSession } from '../composables/useChatSession'

const props = defineProps({
  appConfig: {
    type: Object,
    required: true,
  },
})

defineEmits(['back'])

const messageList = ref(null)
const textarea = ref(null)

const {
  messages,
  draft,
  error,
  isStreaming,
  canSend,
  chatId,
  sendMessage,
  stopStreaming,
  clearMessages,
} = useChatSession(props.appConfig)

function submitMessage() {
  sendMessage()
  nextTick(() => {
    textarea.value?.focus()
  })
}

function scrollToBottom() {
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

function formatTime(value) {
  return new Intl.DateTimeFormat('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

watch(messages, scrollToBottom, { deep: true, immediate: true })
</script>

<template>
  <section
    class="chat-page"
    :style="{ '--accent': appConfig.accent, '--accent-soft': appConfig.accentSoft }"
  >
    <header class="chat-header">
      <button class="icon-button" type="button" title="返回主页" @click="$emit('back')">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M15 6 9 12l6 6" />
        </svg>
      </button>

      <div class="chat-header__identity">
        <span class="chat-header__mark">{{ appConfig.shortName }}</span>
        <div>
          <h1>{{ appConfig.name }}</h1>
          <p>{{ appConfig.subtitle }}</p>
        </div>
      </div>

      <div class="chat-header__actions">
        <span class="status-pill" :class="{ 'status-pill--live': isStreaming }">
          {{ isStreaming ? '生成中' : '就绪' }}
        </span>
        <button class="icon-button" type="button" title="清空对话" @click="clearMessages">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 7h16M9 7V5h6v2m-8 0 1 13h8l1-13" />
          </svg>
        </button>
      </div>
    </header>

    <div ref="messageList" class="message-list" aria-live="polite">
      <article
        v-for="message in messages"
        :key="message.id"
        class="message-row"
        :class="`message-row--${message.role}`"
      >
        <div class="message-avatar" aria-hidden="true">
          {{ message.role === 'user' ? '我' : appConfig.shortName }}
        </div>

        <div class="message-bubble">
          <div class="message-meta">
            <span>{{ message.role === 'user' ? '你' : appConfig.name }}</span>
            <time :datetime="message.createdAt">{{ formatTime(message.createdAt) }}</time>
          </div>

          <p v-if="message.content" class="message-content">{{ message.content }}</p>
          <div v-else class="typing-dots" aria-label="正在生成">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </article>
    </div>

    <p v-if="error" class="chat-error">{{ error }}</p>

    <form class="composer" @submit.prevent="submitMessage">
      <div class="composer__field">
        <textarea
          ref="textarea"
          v-model="draft"
          rows="1"
          :placeholder="appConfig.prompt"
          :disabled="isStreaming"
          @keydown.enter.exact.prevent="submitMessage"
        ></textarea>
        <span v-if="appConfig.includeChatId" class="composer__chat-id">
          {{ chatId }}
        </span>
      </div>

      <button
        v-if="!isStreaming"
        class="send-button"
        type="submit"
        title="发送"
        :disabled="!canSend"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M4 12 20 5l-5 14-3-6-8-1Z" />
        </svg>
      </button>

      <button
        v-else
        class="send-button send-button--stop"
        type="button"
        title="停止"
        @click="stopStreaming('stopped')"
      >
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M8 8h8v8H8z" />
        </svg>
      </button>
    </form>
  </section>
</template>
