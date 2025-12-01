<template>
  <div class="order-list-page">
    <div class="page-header">
      <h1>订单管理</h1>
      <div class="header-actions">
        <el-button @click="handleExport">导出订单</el-button>
        <el-button type="primary" @click="checkNewOrders">检查新订单</el-button>
      </div>
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
        <el-form-item label="手机号">
          <el-input v-model="queryParams.customerPhone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="新订单" value="NEW" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="退款中" value="REFUNDING" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="queryParams.payStatus" placeholder="请选择支付状态" clearable>
            <el-option label="未支付" value="UNPAID" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已退款" value="REFUNDED" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedRows.length > 0">
      <el-space>
        <span>已选择 {{ selectedRows.length }} 项</span>
        <el-button @click="handleBatchConfirm">批量接单</el-button>
        <el-button @click="handleBatchPrint">批量打印</el-button>
      </el-space>
    </div>

    <!-- 订单表格 -->
    <el-card>
      <el-table
          v-loading="loading"
          :data="tableData"
          @selection-change="handleSelectionChange"
          stripe
      >
        <el-table-column type="selection" width="55" />
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
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPayStatusType(row.payStatus)">
              {{ getPayStatusText(row.payStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link v-if="row.status === 'NEW'" @click="handleConfirm(row)">接单</el-button>
            <el-button link v-if="row.status === 'CONFIRMED'" @click="handleComplete(row)">完成</el-button>
            <el-button link type="danger" v-if="!['COMPLETED', 'CANCELLED'].includes(row.status)" @click="handleCancel(row)">取消</el-button>
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

    <!-- 新订单提醒 -->
    <el-dialog
        v-model="alertDialogVisible"
        title="新订单提醒"
        width="500px"
    >
      <div v-if="newOrders.length > 0">
        <p>有 {{ newOrders.length }} 个新订单：</p>
        <el-table :data="newOrders" size="small">
          <el-table-column prop="orderNo" label="订单号" />
          <el-table-column prop="customerName" label="客户" />
          <el-table-column prop="totalAmount" label="金额">
            <template #default="{ row }">¥{{ formatMoney(row.totalAmount) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else>
        <p>暂无新订单</p>
      </div>
      <template #footer>
        <el-button @click="alertDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="playAlertSound">播放提醒音</el-button>
      </template>
    </el-dialog>
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
const selectedRows = ref([])
const alertDialogVisible = ref(false)
const newOrders = ref([])
const dateRange = ref([])

// 查询参数
const queryParams = reactive({
  orderNo: '',
  customerName: '',
  customerPhone: '',
  status: '',
  payStatus: '',
  startDate: null,
  endDate: null,
  page: 1,
  size: 10
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/orders', { params: queryParams })
    tableData.value = response.content || []
    total.value = response.totalElements || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadOrders()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    orderNo: '',
    customerName: '',
    customerPhone: '',
    status: '',
    payStatus: '',
    startDate: null,
    endDate: null,
    page: 1
  })
  dateRange.value = []
  loadOrders()
}

// 日期范围变化
const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryParams.startDate = dates[0] + ' 00:00:00'
    queryParams.endDate = dates[1] + ' 23:59:59'
  } else {
    queryParams.startDate = null
    queryParams.endDate = null
  }
}

// 查看订单详情
const handleView = (row) => {
  router.push(`/admin/orders/${row.id}`)
}

// 确认订单
const handleConfirm = async (row) => {
  try {
    await api.put(`/api/orders/${row.id}/confirm`)
    ElMessage.success('接单成功')
    loadOrders()
  } catch (error) {
    console.error('接单失败:', error)
    ElMessage.error('接单失败')
  }
}

// 完成订单
const handleComplete = async (row) => {
  try {
    await api.put(`/api/orders/${row.id}/complete`)
    ElMessage.success('订单已完成')
    loadOrders()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 取消订单
const handleCancel = async (row) => {
  try {
    await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      inputPlaceholder: '请输入取消原因',
      inputValidator: (value) => {
        if (!value) {
          return '取消原因不能为空'
        }
      }
    })

    await api.put(`/api/orders/${row.id}/cancel`, { reason: '用户取消' })
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 批量选择
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量接单
const handleBatchConfirm = async () => {
  try {
    const ids = selectedRows.value
        .filter(row => row.status === 'NEW')
        .map(row => row.id)

    if (ids.length === 0) {
      ElMessage.warning('请选择新订单进行接单')
      return
    }

    await api.post('/api/orders/batch-confirm', ids)
    ElMessage.success('批量接单成功')
    selectedRows.value = []
    loadOrders()
  } catch (error) {
    console.error('批量接单失败:', error)
    ElMessage.error('批量接单失败')
  }
}

// 批量打印
const handleBatchPrint = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要打印的订单')
    return
  }

  // 在实际项目中，这里应该调用打印API
  ElMessage.info(`准备打印 ${selectedRows.value.length} 个订单`)
  selectedRows.value.forEach(order => {
    window.open(`/print/order/${order.id}`, '_blank')
  })
}

// 导出订单
const handleExport = async () => {
  try {
    const response = await api.get('/api/orders/export', {
      params: queryParams,
      responseType: 'blob'
    })

    const blob = new Blob([response], { type: 'text/csv' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `orders_${new Date().toISOString().slice(0,10)}.csv`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 检查新订单
const checkNewOrders = async () => {
  try {
    const response = await api.get('/api/orders/alerts/new')
    newOrders.value = response || []
    alertDialogVisible.value = true

    if (newOrders.value.length > 0) {
      playAlertSound()
    }
  } catch (error) {
    console.error('检查新订单失败:', error)
    ElMessage.error('检查新订单失败')
  }
}

// 播放提醒音
const playAlertSound = () => {
  // 简单的音频提醒
  const audio = new Audio('data:audio/wav;base64,UklGRigAAABXQVZFZm10IBAAAAABAAEARKwAAIhYAQACABAAZGF0YQQAAAAAAA==')
  audio.play().catch(e => console.log('音频播放失败:', e))
}

// 状态格式化
const getStatusText = (status) => {
  const statusMap = {
    'NEW': '新订单',
    'CONFIRMED': '已确认',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDING': '退款中',
    'REFUNDED': '已退款'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'NEW': 'warning',
    'CONFIRMED': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'info',
    'REFUNDING': 'danger',
    'REFUNDED': 'info'
  }
  return typeMap[status] || 'info'
}

const getPayStatusText = (payStatus) => {
  const statusMap = {
    'UNPAID': '未支付',
    'PAID': '已支付',
    'REFUNDED': '已退款'
  }
  return statusMap[payStatus] || payStatus
}

const getPayStatusType = (payStatus) => {
  const typeMap = {
    'UNPAID': 'danger',
    'PAID': 'success',
    'REFUNDED': 'info'
  }
  return typeMap[payStatus] || 'info'
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
  loadOrders()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  loadOrders()
}

// 生命周期
onMounted(() => {
  loadOrders()

  // 每30秒检查一次新订单
  setInterval(checkNewOrders, 30000)
})
</script>

<style scoped>
.order-list-page {
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

.batch-actions {
  margin-bottom: 16px;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
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