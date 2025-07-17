<template>
    <el-form class="form" :model="loginForm" ref="loginFormRef" :rules="rules">
        <el-form-item label="ID No.:" prop="idCard">
            <el-input v-model="loginForm.idCard"></el-input>
        </el-form-item>
        <el-form-item label="Name:" prop="name">
            <el-input v-model="loginForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Password:" prop="password">
            <el-input type="password" v-model="loginForm.password"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="handleLogin" :loading="loading">Login</el-button>
        </el-form-item>
    </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as authLogin } from '../../stores/auth.js'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref()

// 表单数据
const loginForm = reactive({
    idCard: '',
    name: '',
    password: ''
})

// 表单验证规则
const rules = {
    idCard: [
        { required: true, message: 'Please enter your ID card number', trigger: 'blur' }
    ],
    name: [
        { required: true, message: 'Please enter your name', trigger: 'blur' }
    ],
    password: [
        { required: true, message: 'Please enter your password', trigger: 'blur' }
    ]
}

// 登录处理函数
const handleLogin = () => {
    loginFormRef.value.validate((valid) => {
        if (valid) {
            performLogin()
        } else {
            ElMessage.error('Please fill in all fields.')
        }
    })
}

const performLogin = async () => {
    // 检查所有字段是否非空
    if (!loginForm.idCard.trim() || !loginForm.name.trim() || !loginForm.password.trim()) {
        ElMessage.error('Please fill in all fields.')
        return
    }
    
    loading.value = true
    
    try {
        // 准备登录数据
        const loginData = {
            idCard: loginForm.idCard.trim(),
            name: loginForm.name.trim(),
            password: loginForm.password.trim()
        }

        // 调用auth store的登录函数，生成一个简单的token
        const mockToken = `token_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
        const loginFlag = authLogin(loginData, mockToken)
        
        if (loginFlag) {
            ElMessage.success('Login successful')
            router.push({ name: 'my' })
        } else {
            // authLogin内部已经显示了错误消息
        }
        
    } catch (error) {
        console.error('Login error:', error)
        ElMessage.error('Login failed.')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.form {
    width: 300px;
    margin: 50px auto;
}
.form .el-form-item {
    margin-bottom: 30px;
}
</style>
