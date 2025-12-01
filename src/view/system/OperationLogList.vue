<template>
  <div class="page-container">
    <el-card>
      <div class="header">
        <span class="title">操作日志</span>
        <el-button size="small" @click="fetchData">刷新</el-button>
      </div>

      <el-table
          :data="logList"
          border
          style="width: 100%"
          height="550px"
          size="small"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="createdAt" label="时间" width="170" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="140" />
        <el-table-column prop="action" label="操作" width="160" />
        <el-table-column prop="requestMethod" label="方法" width="90" />
        <el-table-column prop="requestUri" label="URI" width="200" />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="costTime" label="耗时(ms)" width="100" />
        <el-table-column prop="requestParams" label="参数">
          <template #default="scope">
            <el-popover
                placement="left"
                :width="400"
                trigger="click"
                v-if="scope.row.requestParams"
            >
              <p style="white-space: pre-wrap">
                {{ scope.row.requestParams }}
              </p>
              <template #reference>
                <el-button type="primary" link>查看</el-button>
              </template>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOperationLogList } from '../../api/system'

const logList = ref([])

function fetchData() {
  getOperationLogList().then(res => {
    logList.value = res
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
</style>
