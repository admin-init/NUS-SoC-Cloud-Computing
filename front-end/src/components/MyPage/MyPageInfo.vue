<template>
    <div class="appcontainer">
        <el-row style="height: 50px;"></el-row>
        <el-row class="info-row" gutter="20">
            <el-col :span="7" class="info-title">Name:</el-col>
            <el-col :span="16" class="info-content">{{ userInfo.name  }}</el-col>
        </el-row>
        <el-row class="info-row" gutter="20">
            <el-col :span="7" class="info-title">ID No.:</el-col>
            <el-col :span="16" class="info-content">{{ userInfo.idCard }}</el-col>
        </el-row>
        <el-row class="info-row" gutter="20">
            <el-col :span="7" class="info-title">Account Balance:</el-col>
            <el-col :span="16" class="info-content">999999999</el-col>
        </el-row>
        <el-row class="info-row">
            <el-col :span="6" :offset="5" >
                <el-button type="primary" @click="handleLogout">Logout</el-button>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { user, logout as authLogout } from '../../stores/auth.js'

const router = useRouter()
const logoutLoading = ref(false)

const userInfo = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : user.value


// 注销处理函数
const handleLogout = async () => {
    try {
        // 显示确认对话框
        await ElMessageBox.confirm(
            'Are you sure you want to log out? You will be redirected to the homepage.',
            'Confirm Logout',
            {
                confirmButtonText: 'Sure',
                cancelButtonText: 'Cancel',
                type: 'warning',
            }
        )
        
        logoutLoading.value = true
        
        // 模拟注销延迟
        setTimeout(() => {

            authLogout()

            ElMessage.success('Logout successful')
            router.push({ name: 'home' })
            
            logoutLoading.value = false

        }, 500)
        
    } catch (error) {
        // 用户取消注销
        if (error === 'cancel') {
            ElMessage.info('Logout cancelled')
        } else {
            console.error('Logout error:', error)
            ElMessage.error('Logout failed, please try again')
            logoutLoading.value = false
        }
    }
}

// 组件挂载时检查用户信息
onMounted(() => {
    if (!user.value) {
        ElMessage.warning('User information not found, please log in again.')
        router.push({ name: 'login' })
    }
})
</script>

<style scoped>
.appcontainer {
    width: 860px;
    height: 700px;
    background-color: #f5f5f5;
    direction: vertical;
}
.info-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    padding: 12px 0;
    text-align: right;
}
.info-content {
    font-size: 18px;
    color: #333;
    padding: 12px 0;
}
.info-row {
    margin: 15px;
    width: 700px;
    height: 50px;
}

</style>
