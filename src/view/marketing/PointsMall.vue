<template>
  <div class="points-mall-page">
    <div class="page-header">
      <h1>积分商城</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">添加积分商品</el-button>
        <el-button @click="router.push('/admin/marketing/points-exchange')">
          兑换记录
        </el-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 积分商品表格 -->
    <el-card>
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
                v-if="row.image"
                :src="row.image"
                fit="cover"
                class="goods-image"
            />
            <div v-else class="no-image">无图片</div>
          </template>
        </el-table-column>
        <el-table-column prop="pointsRequired" label="所需积分" width="100" />
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            <span v-if="row.originalPrice">¥{{ row.originalPrice }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link @click="handleUpdateStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
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
import { pointsMallApi } from '@/api/marketing'

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

// 加载积分商品列表
const loadGoods = async () => {
  loading.value = true
  try {
    const response = await pointsMallApi.getGoods(queryParams)
    tableData.value = response.content || []
    total.value = response.totalElements || 0
  } catch (error) {
    console.error('加载积分商品列表失败:', error)
    ElMessage.error('加载积分商品列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadGoods()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    name: '',
    status: null,
    page: 1
  })
  loadGoods()
}

// 添加积分商品
const handleAdd = () => {
  // 这里可以跳转到添加页面或打开对话框
  ElMessage.info('添加功能开发中')
}

// 编辑积分商品
const handleEdit = (row) => {
  // 这里可以跳转到编辑页面或打开对话框
  ElMessage.info('编辑功能开发中')
}

// 更新积分商品状态
const handleUpdateStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await pointsMallApi.updateGoodsStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    loadGoods()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除积分商品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该积分商品吗？', '提示', {
      type: 'warning'
    })
    await pointsMallApi.deleteGoods(row.id)
    ElMessage.success('删除成功')
    loadGoods()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 分页
const handleSizeChange = (size) => {
  queryParams.size = size
  loadGoods()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  loadGoods()
}

// 生命周期
onMounted(() => {
  loadGoods()
})
</script>

<style scoped>
.points-mall-page {
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

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.no-image {
  width: 60px;
  height: 60px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>