<template>
  <el-main class="main-container">
    <HomePageCarousel />
    <!-- <HomePageDecoration /> -->
  </el-main>
  
  
</template>

<script setup>
// import HomePageDecoration from './HomePage/HomePageDecoration.vue';
import HomePageCarousel from './HomePage/HomePageCarousel.vue';
import { ref, onMounted } from 'vue';

const webHeadRef = ref();

// 组件挂载后设置首页为活跃状态
onMounted(() => {
    webHeadRef.value?.setActiveNav('home');
});

</script>

<style scoped>
.main-container {
    height: 95vh;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}
</style>
