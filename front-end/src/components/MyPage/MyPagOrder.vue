<template>
    <div class="container">
        <div class="order-header">
            <el-row>
                <el-col :span="5" class="order-title">
                    <div>Start Station</div>
                </el-col>
                <el-col :span="5" class="order-title">
                    <div>End Station</div>
                </el-col>
                <el-col :span="5" class="order-title">
                    <div>Start Time</div>
                </el-col>
                <el-col :span="5" class="order-title">
                    <div>Arrival Time</div>
                </el-col>
                <el-col :span="4" class="order-title">
                    <div>Order State</div>
                </el-col>
            </el-row>
        </div>
        <!-- <div class="order-main" v-infinite-scroll="load"> -->
        <div class="order-main" >
            <el-card v-for="(item, i) in orderList.slice(0, ((count<20)?count:20))" :key="i" class="order-card">
                <el-row>
                    <el-col :span="5" class="order-center">{{ item.startStation }}</el-col>
                    <el-col :span="5" class="order-center">{{ item.endStation }}</el-col>
                    <el-col :span="5" class="order-center">{{ item.departureTime }}</el-col>
                    <el-col :span="5" class="order-center">{{ item.arrivalTime }}</el-col>
                    <el-col :span="4" class="order-center" style="color: red;">finish</el-col>
                </el-row>
            </el-card>
        </div>
    </div>
</template>

<script setup>

import { ref } from 'vue';

const idCard = JSON.parse(localStorage.getItem('user')).idCard;

const orderList = JSON.parse(localStorage.getItem('orders'))[idCard];

const count = ref( orderList.length )

console.log('idCard: ',idCard );
console.log('Order List:', orderList);

// const count = ref(8 <= orderList.value.length ? 8 : orderList.value.length)
// const load = () => {
//     count.value += 2;
//     count.value = count.value > orderList.value.length ? orderList.value.length : count.value;
//     console.log(count.value);
// }

</script>

<style scoped>
.container {
    width: 860px;
    height: 700px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: #f5f5f5;
}

.order-header {
    width: 800px;
    height: 80px;
    margin: 0 16px;
    padding-left: 12px;
    padding-right: 12px;
    background-color: rgb(189, 229, 254);
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.order-main {
    width: 800px;
    height: 580px;
    margin: 0;
    overflow: auto;
    background-color: #f5f5f5;
}

.order-card {
    margin-top: 10px;
}

.order-title {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    padding-top: 26px;
    text-align: center;
}

.order-center {
    text-align: center;
}

</style>
