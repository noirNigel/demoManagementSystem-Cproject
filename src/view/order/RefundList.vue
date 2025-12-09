<template>
  <div class="refund-list-page">
    <div class="page-header">
      <h1>售后处理</h1>
    </div>

    <!-- 搜索条件 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="客户姓名">
          <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="退款中" value="REFUNDING" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 退款订单表格 -->
    <el-card>
      <el-table
          v-loading="loading"
          :data="tableData"
          stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户" width="120">
          <template #default="{ row }">
            <div>
              <div>{{ row.customerName || '未知' }}</div>
              <div class="customer-phone">{{ row.customerPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="refundAmount" label="退款金额" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.refundAmount) }}</template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'REFUNDING' ? 'warning' : 'success'">
              {{ row.status === 'REFUNDING' ? '退款中' : '已退款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="success" v-if="row.status === 'REFUNDING'" @click="handleApprove(row)">通过</el-button>
            <el-button link type="danger" v-if="row.status === 'REFUNDING'" @click="handleReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/request'

const router = useRouter()

// 数据状态
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  orderNo: '',
  customerName: '',
  status: '', // 默认展示全部退款相关订单
  page: 1,
  size: 10
})

// 加载退款订单
const loadRefundOrders = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/orders', { params: queryParams })
    // 过滤出退款相关的订单，并按时间倒序
    tableData.value = (response.content || [])
        .filter(order => order.status === 'REFUNDING' || order.status === 'REFUNDED')
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
    total.value = tableData.value.length
  } catch (error) {
    console.error('加载退款订单失败:', error)
    ElMessage.error('加载退款订单失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadRefundOrders()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    orderNo: '',
    customerName: '',
    status: '',
    page: 1
  })
  loadRefundOrders()
}

// 查看订单详情
const handleView = (row) => {
  router.push(`/admin/orders/${row.id}`)
}

// 通过退款
const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要通过此退款申请吗？', '提示', {
      type: 'warning'
    })

    await api.put(`/api/orders/${row.id}/refund/approve`)
    ElMessage.success('退款已通过')
    loadRefundOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 拒绝退款
const handleReject = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝退款', {
      inputPlaceholder: '请输入拒绝原因',
      inputValidator: (value) => {
        if (!value) {
          return '拒绝原因不能为空'
        }
      }
    })

    await api.put(`/api/orders/${row.id}/refund/reject`, { reason: value })
    ElMessage.success('退款已拒绝')
    loadRefundOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 格式化函数
const formatMoney = (val) => {
  if (val === null || val === undefined) return '0.00'
  const num = Number(val)
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

// 分页
const handleSizeChange = (size) => {
  queryParams.size = size
  loadRefundOrders()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  loadRefundOrders()
}

// 生命周期
onMounted(() => {
  loadRefundOrders()
})
</script>

<style scoped>
.refund-list-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.customer-phone {
  font-size: 12px;
  color: #909399;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>