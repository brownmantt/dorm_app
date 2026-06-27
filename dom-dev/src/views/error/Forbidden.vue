<template>
  <div class="page-container">
    <PageHeader title="403 - 権限エラー" subtitle="Forbidden" />
    <el-result icon="warning" title="アクセス権限がありません" sub-title="管理者にお問い合わせください。">
      <template #extra>
        <el-button type="primary" @click="$router.push('/allocation')">寮割カレンダーへ</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import PageHeader from '@/components/PageHeader.vue'
</script>
