<template>
  <div class="order-detail-page" v-loading="loading">
    <div class="page-header">
      <h1>订单详情</h1>
      <div class="header-actions">
        <el-button @click="$router.back()">返回</el-button>
        <el-button type="primary" @click="handlePrint">打印订单</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 订单基本信息 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>订单信息</span>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(order.status)">
                {{ getStatusText(order.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="支付状态">
              <el-tag :type="getPayStatusType(order.payStatus)">
                {{ getPayStatusText(order.payStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">¥{{ formatMoney(order.totalAmount) }}</el-descriptions-item>
            <el-descriptions-item label="客户姓名">{{ order.customerName || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ order.customerPhone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ order.customerAddress }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ order.remark || '无' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(order.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDate(order.updatedAt) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 订单商品 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>商品清单</span>
          </template>

          <el-table :data="order.items" stripe>
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="price" label="单价" width="120">
              <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="subtotal" label="小计" width="120">
              <template #default="{ row }">¥{{ formatMoney(row.subtotal) }}</template>
            </el-table-column>
          </el-table>

          <div class="order-total">
            合计: ¥{{ formatMoney(order.totalAmount) }}
          </div>
        </el-card>
      </el-col>

      <!-- 订单操作 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>订单操作</span>
          </template>

          <div class="action-buttons">
            <el-button
                v-if="order.status === 'NEW'"
                type="primary"
                @click="handleConfirm"
                style="width: 100%; margin-bottom: 10px;"
            >
              确认接单
            </el-button>

            <el-button
                v-if="order.status === 'CONFIRMED'"
                type="success"
                @click="handleComplete"
                style="width: 100%; margin-bottom: 10px;"
            >
              完成订单
            </el-button>

            <el-button
                v-if="!['COMPLETED', 'CANCELLED'].includes(order.status)"
                type="danger"
                @click="handleCancel"
                style="width: 100%; margin-bottom: 10px;"
            >
              取消订单
            </el-button>

            <el-button
                v-if="order.status === 'COMPLETED'"
                type="warning"
                @click="showRefundDialog = true"
                style="width: 100%; margin-bottom: 10px;"
            >
              处理退款
            </el-button>
          </div>
        </el-card>

        <!-- 时间线 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>订单时间线</span>
          </template>

          <el-timeline>
            <el-timeline-item
                v-for="event in timelineEvents"
                :key="event.time"
                :timestamp="formatDate(event.time)"
                :type="event.type"
            >
              {{ event.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 退款对话框 -->
    <el-dialog
        v-model="showRefundDialog"
        title="处理退款"
        width="400px"
    >
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="退款原因">
          <el-input
              v-model="refundForm.refundReason"
              type="textarea"
              :rows="3"
              placeholder="请输入退款原因"
          />
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input-number
              v-model="refundForm.refundAmount"
              :min="0"
              :max="order.totalAmount"
              :precision="2"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRefund">提交退款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref({})
const showRefundDialog = ref(false)

const refundForm = reactive({
  refundReason: '',
  refundAmount: 0
})

// 时间线事件
const timelineEvents = computed(() => {
  const events = []

  if (order.value.createdAt) {
    events.push({
      time: order.value.createdAt,
      content: '订单创建',
      type: 'primary'
    })
  }

  if (order.value.paymentTime) {
    events.push({
      time: order.value.paymentTime,
      content: '支付成功',
      type: 'success'
    })
  }

  if (order.value.confirmedTime) {
    events.push({
      time: order.value.confirmedTime,
      content: '订单确认',
      type: 'success'
    })
  }

  if (order.value.completedTime) {
    events.push({
      time: order.value.completedTime,
      content: '订单完成',
      type: 'success'
    })
  }

  if (order.value.cancelledTime) {
    events.push({
      time: order.value.cancelledTime,
      content: '订单取消',
      type: 'danger'
    })
  }

  if (order.value.refundTime) {
    events.push({
      time: order.value.refundTime,
      content: '退款处理',
      type: 'warning'
    })
  }

  return events.sort((a, b) => new Date(a.time) - new Date(b.time))
})

// 加载订单详情
const loadOrderDetail = async () => {
  loading.value = true
  try {
    const response = await api.get(`/api/orders/${route.params.id}`)
    order.value = response
    refundForm.refundAmount = order.value.totalAmount || 0
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 确认订单
const handleConfirm = async () => {
  try {
    await api.put(`/api/orders/${order.value.id}/confirm`)
    ElMessage.success('接单成功')
    loadOrderDetail()
  } catch (error) {
    console.error('接单失败:', error)
    ElMessage.error('接单失败')
  }
}

// 完成订单
const handleComplete = async () => {
  try {
    await api.put(`/api/orders/${order.value.id}/complete`)
    ElMessage.success('订单已完成')
    loadOrderDetail()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    await ElMessageBox.prompt('请输入取消原因', '取消订单', {
      inputPlaceholder: '请输入取消原因',
      inputValidator: (value) => {
        if (!value) {
          return '取消原因不能为空'
        }
      }
    })

    await api.put(`/api/orders/${order.value.id}/cancel`, { reason: '用户取消' })
    ElMessage.success('订单已取消')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 处理退款
const handleRefund = async () => {
  try {
    if (!refundForm.refundReason) {
      ElMessage.error('请输入退款原因')
      return
    }

    await api.post('/api/orders/refund', {
      orderId: order.value.id,
      refundReason: refundForm.refundReason,
      refundAmount: refundForm.refundAmount
    })

    ElMessage.success('退款申请已提交')
    showRefundDialog.value = false
    loadOrderDetail()
  } catch (error) {
    console.error('退款申请失败:', error)
    ElMessage.error('退款申请失败')
  }
}

// 打印订单
const handlePrint = () => {
  window.open(`/print/order/${order.value.id}`, '_blank')
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

// 生命周期
onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
}

.order-total {
  margin-top: 16px;
  text-align: right;
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}
</style>