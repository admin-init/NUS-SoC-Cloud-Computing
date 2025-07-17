import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const apiOperations = axios.create({
  baseURL: 'http://operations-management-service.default.svc.cluster.local:8080', 
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token等
apiOperations.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
apiOperations.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    const message = error.response?.data?.message || 'Request failed'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

const apiOrder = axios.create({
  baseURL: 'http://order-management-service.default.svc.cluster.local:8081', 
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token等
apiOrder.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
apiOrder.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    const message = error.response?.data?.message || 'Request failed'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default { apiOperations, apiOrder };