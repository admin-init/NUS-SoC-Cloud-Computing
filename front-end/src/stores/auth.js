import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const user = ref(null)
const token = ref(null)

// 从 localStorage 初始化状态
const initAuth = () => {
  const savedUser = localStorage.getItem('user')
  const savedToken = localStorage.getItem('token')
  
  if (savedUser) {
    try {
      user.value = JSON.parse(savedUser)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('user')
    }
  }
  if (savedToken) {
    token.value = savedToken
  }
}

// 登录函数
const login = (userInfo, userToken) => {
  const userList = JSON.parse(localStorage.getItem('userList') || '[]')
  
  // 查找用户
  const foundUser = userList.find(u => u.idCard === userInfo.idCard)
  
  if (!foundUser) {
    ElMessage.error('ID card not found. Please register first.')
    return false
  }
  
  // 验证密码和姓名
  if (foundUser.password !== userInfo.password || foundUser.name !== userInfo.name) {
    ElMessage.error('Incorrect information.')
    return false
  }
  
  // 登录成功
  console.log('登录成功:', userInfo)
  user.value = userInfo
  token.value = userToken
  localStorage.setItem('user', JSON.stringify(userInfo))
  localStorage.setItem('token', userToken)
  
  return true
}

// 登出函数
const logout = () => {
  user.value = null
  token.value = null
  console.log('用户已登出')
  // 清除 localStorage
  localStorage.removeItem('user')
  localStorage.removeItem('token')
}

// 注册函数
const register = (userInfo) => {
  console.log('注册用户:', userInfo)
  
  // 数据验证
  if (!userInfo.idCard || !userInfo.name || !userInfo.password) {
    ElMessage.error('Please fill in all fields.')
    return false
  }
  
  // 验证身份证号 - 必须是10位数字
  if (!/^\d{10}$/.test(userInfo.idCard)) {
    ElMessage.error('ID card number must be exactly 10 digits.')
    return false
  }
  
  // 验证密码长度 - 6到20位
  if (userInfo.password.length < 6 || userInfo.password.length > 20) {
    ElMessage.error('Password length must be between 6 and 20 characters.')
    return false
  }
  
  // 验证密码格式 - 只能包含字母和数字
  if (!/^[a-zA-Z0-9]+$/.test(userInfo.password)) {
    ElMessage.error('Password can only contain letters and numbers.')
    return false
  }
  
  // 验证密码必须同时包含字母和数字
  if (!/[a-zA-Z]/.test(userInfo.password) || !/[0-9]/.test(userInfo.password)) {
    ElMessage.error('Password must contain both letters and numbers.')
    return false
  }
  
  const userList = JSON.parse(localStorage.getItem('userList') || '[]')
  
  // 检查身份证号是否已存在
  const existingUser = userList.find(u => u.idCard === userInfo.idCard)
  if (existingUser) {
    ElMessage.error('This ID card number is already registered.')
    return false
  }
  
  // 注册成功，保存用户信息
  const newUser = {
    idCard: userInfo.idCard,
    name: userInfo.name,
    password: userInfo.password,
    registeredAt: new Date().toISOString()
  }
  
  userList.push(newUser)
  localStorage.setItem('userList', JSON.stringify(userList))
  
  return true
}

// 获取当前用户
const getUser = () => {
  return user.value
}

// 获取当前token
const getToken = () => {
  return token.value
}

// 移除token
const removeToken = () => {
  logout()
}

// 计算属性：是否已登录
const isLoggedIn = computed(() => {
  return !!user.value && !!token.value
})

// 检查用户是否存在
const checkUserExists = (idCard) => {
  const userList = JSON.parse(localStorage.getItem('userList') || '[]')
  return userList.some(u => u.idCard === idCard)
}

// 获取所有用户（仅用于调试，生产环境中不应该有这个功能）
const getAllUsers = () => {
  return JSON.parse(localStorage.getItem('userList') || '[]')
}

// 初始化
initAuth()

export {
  user,
  token,
  isLoggedIn,
  login,
  logout,
  register,
  initAuth,
  getUser,
  getToken,
  removeToken,
  checkUserExists,
  getAllUsers
}