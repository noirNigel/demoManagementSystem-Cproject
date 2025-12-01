<template>
  <div class="points-exchange">
    <el-card>
      <div class="title">积分兑换记录</div>

      <el-table :data="records" style="width: 100%; margin-top: 20px">
        <el-table-column prop="user" label="用户" />
        <el-table-column prop="item" label="兑换商品" />
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="time" label="兑换时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === '已发放' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const records = ref([
  {
    id: 1,
    user: '张三',
    item: '10元代金券',
    points: 100,
    time: '2025-01-01 12:00',
    status: '已发放'
  },
  {
    id: 2,
    user: '李四',
    item: '20元券',
    points: 200,
    time: '2025-01-02 15:20',
    status: '待发放'
  }
])
</script>

<style scoped>
.title {
  font-size: 18px;
  font-weight: bold;
}
</style>
