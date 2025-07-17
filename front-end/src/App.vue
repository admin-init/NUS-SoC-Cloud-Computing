<template>
    <el-container class="app-container">
        <el-header class="webhead">
            <WebHead />
        </el-header>
        <router-view />
    </el-container>
</template>

<script setup>
import WebHead from './components/WebHead.vue'

// import HomePage from './components/HomePage.vue';
// import TicketPage from './components/TicketPage.vue';
// import MyPage from './components/MyPage.vue';
// import LoginPage from './components/LoginPage.vue';

</script>

<style>
.app-container {
  height: 95vh;
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
</style>
