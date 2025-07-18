<template>
    <el-card class="card">
        <el-form>
            <el-row>
                <el-col :span="6">
                    <el-form-item label="Departure Zone:">
                        <el-select v-model="form.startPos" placeholder="Select Departure Station">
                            <el-option label="Beijing Railway Station" value="1" />
                            <el-option label="Beijing West Railway Station" value="2" />
                            <el-option label="Shanghai Railway Station" value="3" />
                            <el-option label="Shanghai Hongqiao Railway Station" value="4" />
                            <el-option label="Guangzhou Railway Station" value="5" />
                            <el-option label="Guangzhou South Railway Station" value="6" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="6" :offset="1">
                    <el-form-item label="Arrival Zone:">
                        <el-select v-model="form.stopPos" placeholder="Select Arrival Station">
                            <el-option label="Beijing Railway Station" value="1" />
                            <el-option label="Beijing West Railway Station" value="2" />
                            <el-option label="Shanghai Railway Station" value="3" />
                            <el-option label="Shanghai Hongqiao Railway Station" value="4" />
                            <el-option label="Guangzhou Railway Station" value="5" />
                            <el-option label="Guangzhou South Railway Station" value="6" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="6" :offset="1">
                    <el-form-item label="Departure Date:">
                        <el-date-picker
                            v-model="form.date"
                            type="date"
                            placeholder="Select Departure Date"
                            style="width: 100%"
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="3" :offset="1">
                    <el-button 
                        type="primary" 
                        @click="onSubmit"
                        :loading="searching"
                    >
                        {{ searching ? 'Searching...' : 'Search' }}
                    </el-button>
                </el-col>
            </el-row>
        </el-form>
    </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ticketAPI } from '@/api/ticket.js'

const route = useRoute()
const searching = ref(false)

// 定义 emit 事件，用于向父组件传递搜索结果
const emit = defineEmits(['search-results'])

const form = reactive({
    startPos: '',
    stopPos: '',
    date: ''
})

// 组件挂载时从路由参数中获取搜索条件
onMounted(() => {
    if (route.query.startPos) {
        form.startPos = route.query.startPos
    }
    if (route.query.stopPos) {
        form.stopPos = route.query.stopPos
    }
    if (route.query.date) {
        form.date = route.query.date
    }
    
})

function myIsString(value) {
  return typeof value === "string" || value instanceof String;
}

const onSubmit = async () => {
    // 表单验证
    if (!form.startPos || !form.stopPos || !form.date) {
        ElMessage.warning('Please fill in all fields.')
        return
    }
    if (form.startPos === form.stopPos) {
        ElMessage.warning('Departure and arrival locations cannot be the same.')
        return
    }

    searching.value = true
    
    try {
        // 调用API搜索车票
        const response = await ticketAPI.searchTickets({
            startPos: form.startPos,
            stopPos: form.stopPos,
            date: myIsString(form.date) ? form.date : (form.date.getFullYear() + '-' + ((form.date.getMonth() + 1)<10 ? '0' : '')
                     +  (form.date.getMonth() + 1) + '-' + (form.date.getDate()<10 ? '0' : '') + form.date.getDate())
        })
        
        for (let i = 0; i < response.length; i++) {
                
            response[i].departureTime = response[i].departureTime.slice(0, 10) + ' ' + response[i].departureTime.slice(11, 16)
            response[i].arrivalTime = response[i].arrivalTime.slice(0, 10) + ' ' + response[i].arrivalTime.slice(11, 16)
            response[i].availableTickets = (response[i].availableTickets <= 0) ? 0 : response[i].availableTickets
        } 

        console.log('搜索结果:', response)
        
        // 将搜索结果传递给父组件
        emit('search-results', response)
        
        
    } catch (error) {
        console.error('搜索失败:', error)
        ElMessage.error('Search failed, please try again later.')
        
        // 搜索失败时也要通知父组件
        emit('search-results', [])
    } finally {
        searching.value = false
    }
}
</script>

<style scoped>
.card {
    width: 1080px;
    background-color: #f5f5f5; 
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); 
}
</style>