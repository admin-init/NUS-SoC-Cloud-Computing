<template>
    <el-container class="decoration">
      <el-aside width="250px"></el-aside>
      <el-main class="decoration-main">
        <el-row>
            <el-col :span="24">
                <img src="/image/dc0.png" class="decoration-img1" />
            </el-col>
        </el-row>
        <el-row :gutter="15">
            <el-col :span="12"> <img src="/image/dc1.jpg" class="decoration-img2"/> </el-col>
            <el-col :span="12" > <img src="/image/dc2.jpg" class="decoration-img2" /> </el-col>
        </el-row>
        <el-row style="height: 15px;"></el-row>
        <el-row  gutter="15">
            <el-col :span="12"> <img src="/image/dc3.jpg" class="decoration-img2" /> </el-col>
            <el-col :span="12" > <img src="/image/dc4.jpg" class="decoration-img2" /> </el-col>
        </el-row>
      </el-main>
      <el-aside width="250px"></el-aside>
    </el-container>
</template>

<script setup>

</script>

<style scoped>
.decoration {
    width: 100%;
    height: 100%;
}
.decoration-main {
    width: inherit;
    height: 100%;
}
.decoration-img1 {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
.decoration-img2 {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>