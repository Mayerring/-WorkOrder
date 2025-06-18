<script setup>
import Login from './views/Login.vue'
</script>

<template>
  <router-view />
</template>

<style>
/* 重置一些默认样式 */
html,
body {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  height: 100vh;
  overflow: hidden;
}

body {
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

#app {
  height: 100vh;
  overflow: hidden;
}

/* 确保路由视图占满整个容器 */
.router-view {
  height: 100%;
  overflow: auto;
}
</style>
