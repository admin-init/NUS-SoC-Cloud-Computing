<template>
    <el-row class="makerogether">
        <el-col :span="0.8"><img src="@/assets/logo.png" id="headicon" /></el-col>
        <el-col :span="5" id="headtitle">Rail Ticket Web</el-col>
        <el-col :span="2" :offset="1">
            <div :class="['navi', { 'navi-active': activeNav === 'home' }]" @click="navigateTo('home')">Home</div> 
        </el-col>
        <el-col :span="2">
            <div :class="['navi', { 'navi-active': activeNav === 'ticket' }]" @click="navigateTo('ticket')">Ticket</div> 
        </el-col>
        <el-col :span="2">
            <div :class="['navi', { 'navi-active': activeNav === 'my' || activeNav === 'login' }]" @click="navigateTo('my')">My</div> 
        </el-col>
        <el-col :span="8" :offset="2" class="search-form">
            <el-form class="">
                <el-form-item>
                    <el-input placeholder="This search box is fake."></el-input>
                </el-form-item>
                 <el-button type="primary" @click="onSubmit">Search</el-button>
            </el-form>
        </el-col>
    </el-row>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isLoggedIn } from '../stores/auth.js'

const router = useRouter()
const route = useRoute()

let activeNav = ref('home')

// 导航点击处理
const navigateTo = (navType) => {
    if (navType === 'my') {
        if (isLoggedIn.value) {
            activeNav.value = 'my'
            router.push({ name: 'my' })
        } else {
            activeNav.value = 'login'
            router.push({ name: 'login' })
        }
    } else {
        activeNav.value = navType
        router.push({ name: navType })
    }
}

// 根据当前路由设置活跃状态
const updateActiveNav = () => {
    const routeName = route.name
    if (routeName) {
        // 如果在登录页面，将"我的"按钮设为活跃状态
        if (routeName === 'login') {
            activeNav.value = 'login'
        } else {
            activeNav.value = routeName
        }
    }
}

// 搜索提交处理
const onSubmit = () => {
    // 搜索逻辑
    console.log('搜索功能待实现')
}

// 监听路由变化，自动更新活跃状态
watch(() => route.name, () => {
    updateActiveNav()
}, { immediate: true })

// 监听登录状态变化
watch(isLoggedIn, (newLoginStatus) => {
    // 如果用户登录成功且当前在登录页面，跳转到我的页面
    if (newLoginStatus && route.name === 'login') {
        router.push({ name: 'my' })
    }
})

// 组件挂载时更新活跃状态
onMounted(() => {
    updateActiveNav()
})

// 暴露方法供外部调用
defineExpose({
    setActiveNav: (navType) => {
        activeNav.value = navType
    }
})
</script>

<style scoped>
.el-header {
    width: 100%;
    height: 60px;
    background-color: rgb(155, 203, 255);
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 2px 8px rgba(0, 21, 41, 0.08);
}
#headicon {
    width: 40px;
    height: 40px;
}
#headtitle {
    font-size: 26px;
    color: #000000;
    margin-left: 10px;
    line-height: 60px;
    font-weight: bold;
}
.makerogether {
    align-items: center;
    height: 100%;
    width: 1200px;
    min-width: 900px;
    display: flex;
}
.navi {
    font-size: 18px;
    color: #000000;
    line-height: 35px;
    text-align: center;
    justify-content: center; 
    align-items: center;
    height: 34px;
    width: 60px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
}
.navi:hover {
    background-color: rgba(255, 255, 255, 0.7);
}
.navi-active {
    background-color: white;
}
.search-form {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
}
.search-form .el-form {
    width: 100%;
    display: flex;
    align-items: center;
    height: 100%;
}
.search-form .el-form-item {
    margin: 0 !important;
    width: 100%;
    display: flex;
    align-items: center;
    height: 100%;
}
.search-form :deep(.el-form-item__content) {
    margin: 0 !important;
    display: flex;
    align-items: center;
    height: 100%;
}
</style>