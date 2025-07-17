<template>
    <el-main class="main-container">
        <div class="card">
            <el-menu
                :default-active="activeIndex"
                class="menu"
                mode="horizontal"
                @select="handleSelect"
                ellipsis=false
            >
                <el-menu-item index="login" class="menu-item">Login</el-menu-item>
                <el-menu-item index="register" class="menu-item">Register</el-menu-item>
            </el-menu>
            <component :is="currentComponent" :key="activeIndex" />
        </div>
    </el-main>
</template>

<script setup>
import LoginPageLogin from './LoginPage/LoginPageLogin.vue';
import LoginPageRegister from './LoginPage/LoginPageRegister.vue';
import { ref, onMounted , shallowRef } from 'vue';

const webHeadRef = ref();
onMounted(() => {
    webHeadRef.value?.setActiveNav('my');
});

const activeIndex = ref('login'); 
const currentComponent = shallowRef(LoginPageLogin);
const componentsMap = {
  login: LoginPageLogin,
  register: LoginPageRegister
}
const handleSelect = (index) => {
  activeIndex.value = index;
  currentComponent.value = null;
  setTimeout(() => {
    currentComponent.value = componentsMap[index];
  }, 0);
}

</script>

<style scoped>
.main-container {
  width: 1150px;
  height: 700px;
  margin-top: 20px;
  display: flex;
    flex-direction: row;    
    justify-content: center;
}
.card{
    width: 400px;
    height: 500px;
    margin-top: 50px;
    border-radius: 10px;
    background-color: #f5f5f5;
    box-shadow: 0 4px 4px rgba(0, 0, 0, 0.1);
}
.menu {
  width: 100%;
  height: 60px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #e4e7ed;
}
.menu-item {
  width: 40%;
  font-size: 18px;
  color: #409eff;
  text-align: center;
}

</style>
