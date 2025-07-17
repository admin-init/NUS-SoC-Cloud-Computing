<template>
    <el-container class="main-page-form">
        <el-header class="main-page-form-header">
            <el-row>
                <el-col :span="3" :offset="5"><el-icon class="ticket-icon"><Tickets /></el-icon></el-col>
                <el-col :span="14" class="ticket-title">Ticket Inquiry</el-col>
            </el-row>
        </el-header>
        <el-main>
            <el-form>
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
                <el-form-item label="Departure Date:">
                    <el-date-picker
                        v-model="form.date"
                        type="date"
                        placeholder="Select Departure Date"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item>
                    <el-col :span="12" :offset="10" style="height: 30px;">
                        <el-button 
                            type="primary" 
                            @click="onSubmit" 
                            :loading="searching"
                        >
                            {{ searching ? 'Searching...' : 'Inquiry' }}
                        </el-button>
                    </el-col>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Tickets } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { ticketAPI } from '@/api/ticket.js'

const router = useRouter()
const searching = ref(false)

const form = reactive({
    startPos: '',
    stopPos: '',
    date: ''
})

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
        // 调用API进行车票查询
        let response = await ticketAPI.searchTickets({
            startPos: form.startPos,
            stopPos: form.stopPos,
            date: form.date.getFullYear() + '-' + ((form.date.getMonth() + 1)<10 ? '0' : '')
                     +  (form.date.getMonth() + 1) + '-' + (form.date.getDate()<10 ? '0' : '') + form.date.getDate()
        })

        /**/
        for (let i = 0; i < response.length; i++) {
                
            response[i].departureTime = response[i].departureTime.slice(0, 10) + ' ' + response[i].departureTime.slice(11, 16)
            response[i].arrivalTime = response[i].arrivalTime.slice(0, 10) + ' ' + response[i].arrivalTime.slice(11, 16)
            response[i].availableTickets = (response[i].availableTickets <= 0) ? 0 : response[i].availableTickets
            
        } 
        
        console.log('搜索到的车票:', response.data)
        
        // 将搜索结果存储到sessionStorage
        sessionStorage.setItem('searchResults', JSON.stringify(response.data || response))
        
        // 跳转到车票页面，携带搜索参数
        router.push({ 
            name: 'ticket',
            query: {
                startPos: form.startPos,
                stopPos: form.stopPos,
                date: form.date.getFullYear() + '-' + ((form.date.getMonth() + 1)<10 ? '0' : '')
                     +  (form.date.getMonth() + 1) + '-' + (form.date.getDate()<10 ? '0' : '') + form.date.getDate()
            }
        })
        
    } catch (error) {
        console.error('搜索车票失败:', error.response || error.message)
        ElMessage.error('Search failed, please try again later.')
    } finally {
        searching.value = false
    }
}
</script>

<style scoped>
.main-page-form {
    width: 400px;
    height: 300px;
    background-color: #f5f5f5; 
    flex-direction: column;
    display: flex;
}
.main-page-form-header {
    width: 400px;
    height: 60px;
    background-color: rgb(155, 203, 255);
    flex-direction: column;
    display: flex;
}
.main-page-form-header .ticket-icon {
    font-size: 2rem;
    margin-top: 13px;
    color: #000000;
}
.main-page-form-header .ticket-title {
    font-size: 1.5rem;
    margin-top: 11px;
    color: #000000;
}
</style>