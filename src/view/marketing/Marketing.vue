<template>
  <div class="marketing-page">
    <el-container class="app-container">
      <el-aside width="200px" class="app-sidebar">
        <el-menu
            :default-active="$route.path"
            router
            class="sidebar-menu"
        >
          <el-menu-item index="/admin/marketing/coupons">
            <el-icon><Ticket /></el-icon>
            <span>优惠券管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/marketing/member-levels">
            <el-icon><Star /></el-icon>
            <span>会员等级</span>
          </el-menu-item>
          <el-menu-item index="/admin/marketing/banners">
            <el-icon><Picture /></el-icon>
            <span>轮播图管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/marketing/promotions">
            <el-icon><Promotion /></el-icon>
            <span>促销活动</span>
          </el-menu-item>
          <el-menu-item index="/admin/marketing/points-mall">
            <el-icon><Goods /></el-icon>
            <span>积分商城</span>
          </el-menu-item>
          <el-menu-item index="/admin/marketing/points-exchange">
            <el-icon><List /></el-icon>
            <span>积分兑换记录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { Ticket, Star, Promotion, Goods, List, Picture } from '@element-plus/icons-vue'
</script>

<style scoped>
.marketing-page {
  height: 100%;
}

.app-container {
  height: 100%;
}

.app-sidebar {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
}

.sidebar-menu {
  border: none;
  height: 100%;
}

.app-main {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>