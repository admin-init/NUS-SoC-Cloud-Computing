<template>
    <el-form class="form" :model="registerForm" ref="registerFormRef" :rules="rules">
        <el-form-item label="ID No.:" prop="idCard">
            <el-input v-model="registerForm.idCard" placeholder="Enter 10-digit ID number"></el-input>
        </el-form-item>
        <el-form-item label="Name:" prop="name">
            <el-input v-model="registerForm.name" placeholder="Enter your name"></el-input>
        </el-form-item>
        <el-form-item label="Password:" prop="password">
            <el-input type="password" v-model="registerForm.password" placeholder="Enter password (letters and numbers only)"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="handleRegister" :loading="loading">Register</el-button>
        </el-form-item>
    </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register as authRegister } from '../../stores/auth.js'

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref()

// 表单数据
const registerForm = reactive({
    idCard: '',
    name: '',
    password: ''
})

// 身份证号验证规则 - 必须是10位数字
const validateIdCard = (rule, value, callback) => {
    if (!value) {
        callback(new Error('Please enter your ID card number'))
    } else if (!/^\d{10}$/.test(value)) {
        callback(new Error('ID card number must be exactly 10 digits'))
    } else {
        callback()
    }
}

// 密码验证规则 - 必须由字母和数字组成，6-20位
const validatePassword = (rule, value, callback) => {
    if (!value) {
        callback(new Error('Please enter your password'))
    } else if (value.length < 6 || value.length > 20) {
        callback(new Error('Password length must be between 6 and 20 characters'))
    } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
        callback(new Error('Password can only contain letters and numbers'))
    } else if (!/[a-zA-Z]/.test(value) || !/[0-9]/.test(value)) {
        callback(new Error('Password must contain both letters and numbers'))
    } else {
        callback()
    }
}

// 表单验证规则
const rules = {
    idCard: [
        { validator: validateIdCard, trigger: 'blur' }
    ],
    name: [
        { required: true, message: 'Please enter your name', trigger: 'blur' },
        { min: 2, max: 10, message: 'Name length must be between 2 and 10 characters', trigger: 'blur' }
    ],
    password: [
        { validator: validatePassword, trigger: 'blur' }
    ]
}

// 注册处理函数
const handleRegister = () => {
    registerFormRef.value.validate((valid) => {
        if (valid) {
            performRegister()
        } else {
            ElMessage.error('Please fill in all fields correctly.')
        }
    })
}

const performRegister = async () => {
    loading.value = true
    
    try {
        // 准备注册数据
        const registerData = {
            idCard: registerForm.idCard.trim(),
            name: registerForm.name.trim(),
            password: registerForm.password.trim()
        }

        // 调用auth store的注册函数
        const registerFlag = authRegister(registerData)
        
        if (registerFlag) {
            ElMessage.success('Registration successful')
            // 清空表单
            registerForm.idCard = ''
            registerForm.name = ''
            registerForm.password = ''

            registerFormRef.value.resetFields()

            const userList = JSON.parse(localStorage.getItem('userList')) || []
            userList.push(registerData)
            localStorage.setItem('userList', JSON.stringify(userList))

            const orderList = JSON.parse(localStorage.getItem('orders')) || {}
            orderList[registerData.idCard] = [] 
            localStorage.setItem('orders', JSON.stringify(orderList))

            // 跳转到登录页面
            router.push({ name: 'login' })
        } else {
            // authRegister内部已经显示了错误消息
        }
        
    } catch (error) {
        console.error('Register error:', error)
        ElMessage.error('Registration failed.')
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
