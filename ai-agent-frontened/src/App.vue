<script setup>
import { computed, ref } from 'vue'
import ChatRoom from './components/ChatRoom.vue'
import HomeView from './components/HomeView.vue'
import { appCatalog } from './data/apps'

const activeAppId = ref('home')

const activeApp = computed(() => {
  return appCatalog.find((item) => item.id === activeAppId.value) ?? null
})

function openApp(appId) {
  activeAppId.value = appId
}

function backHome() {
  activeAppId.value = 'home'
}
</script>

<template>
  <main class="app-shell" :class="{ 'app-shell--chat': activeApp }">
    <HomeView v-if="!activeApp" :apps="appCatalog" @open-app="openApp" />
    <ChatRoom v-else :app-config="activeApp" @back="backHome" />
  </main>
</template>
