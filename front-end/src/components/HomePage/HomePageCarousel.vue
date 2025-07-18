<template>
  <div class="carousel-container">
    <img src="image/DM1.jpg" class="myimage" alt="background image">
      <HomePageForm class="main-page-form"/>
  </div>
</template>

<script setup>
import HomePageForm from './HomePageForm.vue';

</script>

<style scoped>
.carousel-container {
  width: 100%;
  margin: 0;
}

.myimage {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .carousel-container {
    width: 95%;
  }
  
  .el-carousel {
    height: 300px !important;
  }
  
  .carousel-caption h3 {
    font-size: 1.2rem;
  }
}
.main-page-form {
    position:absolute;
    top: 130px;
    left: 180px;
}
</style>