<template>
    <el-main class="main-container">
        <TicketPageSelectFir @search-results="handleSearchResults" @search-start="handleSearchStart" />
        <TicketPageTable :tickets="tickets" :loading="loading" />
    </el-main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import TicketPageSelectFir from './TicketPage/TicketPageSelectFir.vue'
import TicketPageTable from './TicketPage/TicketPageTable.vue'

const route = useRoute()
const tickets = ref([])
const loading = ref(false)

// 处理搜索结果
const handleSearchResults = (searchResults) => {
    tickets.value = searchResults
    loading.value = false
}

// 开始搜索时显示加载状态
const handleSearchStart = () => {
    loading.value = true
}

// 组件挂载时获取搜索结果
onMounted(() => {
    // 从sessionStorage获取搜索结果
    const searchResults = sessionStorage.getItem('searchResults')
    if (searchResults) {
        try {
            const parsedResults = JSON.parse(searchResults)
            tickets.value = parsedResults
            // 清除sessionStorage中的数据（可选）
            sessionStorage.removeItem('searchResults')
        } catch (error) {
            console.error('解析搜索结果失败:', error)
        }
    }
    
    // 如果有路由查询参数，可以用于重新搜索
    const { startPos, stopPos, date } = route.query
    if (startPos && stopPos && date && !searchResults) {
        // 如果没有缓存的搜索结果，但有查询参数，可以重新搜索
        console.log('路由参数:', { startPos, stopPos, date })
    }
})
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.webhead {
    width: 100%;
    height: 60px;
    background-color: rgb(155, 203, 255);
    display: flex;
    justify-content: center;
    align-items: center;
}
.main-container {
    width: 1150px;
    padding: 20px;
}
</style>
