<script setup>
defineProps({
  apps: {
    type: Array,
    required: true,
  },
})

defineEmits(['open-app'])
</script>

<template>
  <section class="home-page">
    <div class="home-page__mast">
      <div class="brand-lockup" aria-label="OfferPilot Console">
        <span class="brand-lockup__mark">OP</span>
        <span class="brand-lockup__name">OfferPilot Console</span>
      </div>

      <div class="home-page__headline">
        <p class="eyebrow">AI 应用工作台</p>
        <h1>选择一个应用，直接开始对话。</h1>
        <p>
          面向 OfferPilot 与 AI 超级智能体的统一前端入口，保留独立上下文，并实时接收后端 SSE 输出。
        </p>
      </div>
    </div>

    <div class="app-grid" aria-label="应用列表">
      <article
        v-for="app in apps"
        :key="app.id"
        class="app-card"
        :style="{ '--accent': app.accent, '--accent-soft': app.accentSoft }"
      >
        <div class="app-card__topline">
          <span class="app-card__badge">{{ app.shortName }}</span>
          <span class="app-card__signal">{{ app.signal }}</span>
        </div>

        <div>
          <h2>{{ app.name }}</h2>
          <p class="app-card__subtitle">{{ app.subtitle }}</p>
          <p>{{ app.description }}</p>
        </div>

        <button class="app-card__button" type="button" @click="$emit('open-app', app.id)">
          <span>进入应用</span>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M5 12h13m-5-5 5 5-5 5" />
          </svg>
        </button>
      </article>
    </div>
  </section>
</template>
