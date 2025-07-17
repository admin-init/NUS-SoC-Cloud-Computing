<template>
    <el-card class="card"><el-form>
        <el-form-item label="乘车地点站台：">
            <el-checkbox-group v-model="form.type1">
                <el-checkbox value="Online activities" name="type1">
                    Online activities
                </el-checkbox>
                <el-checkbox value="Promotion activities" name="type1">
                    Promotion activities
                </el-checkbox>
                <el-checkbox value="Offline activities" name="type1">
                    Offline activities
                </el-checkbox>
                <el-checkbox value="Simple exposure" name="type1">
                    Simple exposure
                </el-checkbox>
            </el-checkbox-group>
        </el-form-item>
        <el-form-item label="下车地点站台：">
            <el-checkbox-group v-model="form.type2">
                <el-checkbox value="1" name="type2">
                    1
                </el-checkbox>
                <el-checkbox value="2" name="type2">
                    2
                </el-checkbox>
                <el-checkbox value="3" name="type2">
                    3
                </el-checkbox>
                <el-checkbox value="4" name="type2">
                    4
                </el-checkbox>
            </el-checkbox-group>
        </el-form-item>
        <el-button type="primary" @click="onSubmit">确认筛选</el-button>
    </el-form></el-card>
</template>

<script setup>
import { reactive } from 'vue';

let form = reactive({
    type1: [],
    type2: []
});
</script>

<style scoped>
.card {
    width: 97%;
    margin: 20px 0;
    min-width: 700px;
    background-color: #f5f5f5; /* 浅灰色背景 */
    border-radius: 10px; /* 圆角 */
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>