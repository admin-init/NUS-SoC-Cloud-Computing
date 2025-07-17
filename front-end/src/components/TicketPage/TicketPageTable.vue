<template>
    <el-card class="table-card">
        <template #header>
            <span>Ticket Search Results</span>
        </template>
        
        <el-table 
            :data="tickets" 
            :loading="loading"
            style="width: 100%"
            empty-text="No tickets found"
        >
            <el-table-column prop="scheduleId" label="Schedule No." width="120" />
            <el-table-column prop="startStation" label="Start Station" width="200" />
            <el-table-column prop="endStation" label="End Station" width="200" />
            <el-table-column prop="departureTime" label="Start Time" width="160" />
            <el-table-column prop="arrivalTime" label="Arrival Time" width="160" />
            <el-table-column prop="availableTickets" label="Remaining" width="100" />
            <el-table-column width="80">
                <template #default="scope">
                    <el-button 
                        type="primary" 
                        size="small"
                        @click="bookTicket(scope.row)"
                        :disabled="scope.row.remainingTickets <= 0"
                    >
                        预订
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { ticketAPI } from '@/api/ticket.js'

// 接收 props
const props = defineProps({
    tickets: {
        type: Array,
        default: () => []
    },
    loading: {
        type: Boolean,
        default: false
    }
})

// 预订车票
const bookTicket = async (ticket) => {
    if (!localStorage.getItem('user')) {
        ElMessage.warning('Please log in first.')
        return
    }
    console.log('Booking ticket:', ticket)
    try {
        const result = await ticketAPI.bookTicket({
            userId: 1,
            ticketId: ticket.ticketId
        })

        console.log('Booking result:', result)
        if (!result) {
            ElMessage.error('Booking failed, please try again later.')
            return
        }

        if (result.data != 'Order Created Successfully') {
            ElMessage.error('No available tickets left.')
            return
        }

        ElMessage.success('Book successful!')
        
        const idCard = JSON.parse(localStorage.getItem('user')).idCard
        console.log('User ID Card:', idCard)
        const order = JSON.parse(localStorage.getItem('orders')) || {}
        order[idCard] = order[idCard] || []
        order[idCard].unshift({
            ticketId: ticket.id,
            trainNumber: ticket.trainNumber,
            startStation: ticket.startStation,
            endStation: ticket.endStation,
            departureTime: ticket.departureTime,
            arrivalTime: ticket.arrivalTime
        })
        localStorage.setItem('orders', JSON.stringify(order))

        
    } catch (error) {
        console.error('Booking failed:', error)
        ElMessage.error('Booking failed, please try again later.')
    }
}
</script>

<style scoped>
.table-card {
    margin-top: 20px;
}
</style>