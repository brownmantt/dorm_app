<template>
  <div class="page-header">
    <div class="page-header-main">
      <p v-if="subtitle" class="page-header-sub">{{ subtitle }}</p>
      <h2 class="page-title">{{ title }}</h2>
    </div>
    <div v-if="$slots.extra" class="page-header-extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--cnc-primary);
}

.page-header-main {
  position: relative;
  padding-left: 12px;
}

.page-header-main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  bottom: 4px;
  width: 4px;
  background: var(--cnc-accent);
}

.page-header-sub {
  margin: 0 0 4px;
  font-family: var(--cnc-font-en);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.12em;
  color: var(--cnc-primary);
  text-transform: uppercase;
}

.page-header-extra {
  display: flex;
  gap: 8px;
  padding-bottom: 2px;
}
</style>
