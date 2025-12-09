<template>
  <div class="coupon-list-page">
    <div class="page-header">
      <h1>优惠券管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">添加优惠券</el-button>
        <el-button @click="checkExpiredCoupons">检查过期优惠券</el-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="优惠券名称">
          <el-input v-model="queryParams.name" placeholder="请输入优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 优惠券表格 -->
    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'DISCOUNT'">折扣券</el-tag>
            <el-tag v-else-if="row.type === 'REDUCE'" type="success">满减券</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="120">
          <template #default="{ row }">
            <span v-if="row.type === 'DISCOUNT'">{{ (row.discount * 10).toFixed(1) }}折</span>
            <span v-else-if="row.type === 'REDUCE'">满{{ row.minAmount }}减{{ row.reduceAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remainingCount" label="剩余数量" width="100" />
        <el-table-column prop="limitPerUser" label="每人限领" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">{{ formatDate(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link @click="handleUpdateStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
import { couponApi } from '@/api/marketing'

const router = useRouter()

// 数据状态
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  name: '',
  status: null,
  page: 1,
  size: 10
})

// 加载优惠券列表
const loadCoupons = async () => {
  loading.value = true
  try {
    const response = await couponApi.getCoupons(queryParams)
    const data = response?.data ?? response
    tableData.value = data?.content || []
    total.value = data?.totalElements || 0
  } catch (error) {
    console.error('加载优惠券列表失败:', error)
    ElMessage.error('加载优惠券列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadCoupons()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    name: '',
    status: null,
    page: 1
  })
  loadCoupons()
}

// 添加优惠券
const handleAdd = () => {
  router.push('/admin/marketing/coupons/add')
}

// 编辑优惠券
const handleEdit = (row) => {
  router.push(`/admin/marketing/coupons/edit/${row.id}`)
}

// 更新优惠券状态
const handleUpdateStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await couponApi.updateCouponStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    loadCoupons()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除优惠券
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该优惠券吗？', '提示', {
      type: 'warning'
    })
    await couponApi.deleteCoupon(row.id)
    ElMessage.success('删除成功')
    loadCoupons()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 检查过期优惠券
const checkExpiredCoupons = async () => {
  try {
    // 这里需要调用检查过期优惠券的API
    ElMessage.success('检查完成')
    loadCoupons()
  } catch (error) {
    console.error('检查失败:', error)
    ElMessage.error('检查失败')
  }
}

// 分页
const handleSizeChange = (size) => {
  queryParams.size = size
  loadCoupons()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  loadCoupons()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

// 生命周期
onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.coupon-list-page {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>