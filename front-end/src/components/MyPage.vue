<template>
  
    <el-main class="main-container">
      <el-menu
        :default-active="activeTab"
        class="menu"
        @select="handleSelect"
      >
        <el-menu-item index="info">
          <span style="font-size: 16px;">My Information</span>
        </el-menu-item>
        <el-menu-item index="order">
          <span style="font-size: 16px;">My Orders</span>
        </el-menu-item>
      </el-menu>
      <component :is="currentComponent" :key="activeTab" />
    </el-main>
</template>

<script setup>
import { ref, onMounted, shallowRef } from 'vue';
import MyPageInfo from './MyPage/MyPageInfo.vue';
import MyPagOrder from './MyPage/MyPagOrder.vue';

const activeTab = ref('info'); 
const currentComponent = shallowRef(MyPageInfo);

const componentsMap = {
  info: MyPageInfo,
  order: MyPagOrder
}

const handleSelect = (index) => {
  activeTab.value = index;
  currentComponent.value = null;
  setTimeout(() => {
    currentComponent.value = componentsMap[index];
  }, 0);
}

const webHeadRef = ref();
onMounted(() => {
    webHeadRef.value?.setActiveNav('my');
});
</script>

<style scoped>
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
.main-container {
  width: 1150px;
  height: 700px;
  margin-top: 20px;
  display: flex;
  flex-direction: row;
}
.menu {
  width: 200px;
  height: 700px;
  background-color: #e5e5e5;
  border-right: 2px solid #7e8baa;
}

</style>
