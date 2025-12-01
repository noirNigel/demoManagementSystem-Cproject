<template>
  <div class="product-list-page">
    <div class="page-header">
      <h1>商品管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">添加商品</el-button>
        <el-button @click="handleExport">导出商品</el-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <el-card class="search-card">
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="SKU">
          <el-input v-model="queryParams.sku" placeholder="请输入SKU" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-cascader
              v-model="queryParams.categoryId"
              :options="categoryTree"
              :props="categoryProps"
              placeholder="请选择分类"
              clearable
          />
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

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedRows.length > 0">
      <el-space>
        <span>已选择 {{ selectedRows.length }} 项</span>
        <el-button @click="handleBatchStatus(1)">批量上架</el-button>
        <el-button @click="handleBatchStatus(0)">批量下架</el-button>
        <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
      </el-space>
    </div>

    <!-- 商品表格 -->
    <el-card>
      <el-table
          v-loading="loading"
          :data="tableData"
          @selection-change="handleSelectionChange"
          stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="200">
          <template #default="{ row }">
            <div class="product-info">
              <el-image
                  v-if="row.image"
                  :src="row.image"
                  :preview-src-list="[row.image]"
                  fit="cover"
                  class="product-image"
              />
              <div class="product-detail">
                <div class="product-name">{{ row.name }}</div>
                <div class="product-sku">SKU: {{ row.sku }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="cost" label="成本" width="100">
          <template #default="{ row }">¥{{ formatMoney(row.cost) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stock <= row.warningThreshold }">
              {{ row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link @click="handleStatus(row)">
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
import api from '@/utils/request'

const router = useRouter()

// 数据状态
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const categoryTree = ref([])

// 查询参数
const queryParams = reactive({
  name: '',
  sku: '',
  categoryId: null,
  status: null,
  page: 1,
  size: 10
})

// 分类选择器配置
const categoryProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (Array.isArray(params.categoryId)) {
      params.categoryId = params.categoryId[params.categoryId.length - 1]
    }

    const response = await api.get('/api/products', { params })
    tableData.value = response.content || []
    total.value = response.totalElements || 0
  } catch (error) {
    console.error('加载商品列表失败:', error)
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类树
const loadCategoryTree = async () => {
  try {
    const response = await api.get('/api/categories/tree')
    categoryTree.value = response || []
  } catch (error) {
    console.error('加载分类树失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadProducts()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    name: '',
    sku: '',
    categoryId: null,
    status: null,
    page: 1
  })
  loadProducts()
}

// 添加商品
const handleAdd = () => {
  router.push('/admin/products/add')
}

// 编辑商品
const handleEdit = (row) => {
  router.push(`/admin/products/edit/${row.id}`)
}

// 上下架商品
const handleStatus = async (row) => {
  try {
    await api.put(`/api/products/${row.id}/status`, {
      status: row.status === 1 ? 0 : 1
    })
    ElMessage.success('操作成功')
    loadProducts()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除商品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/api/products/${row.id}`)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量选择
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 批量上下架
const handleBatchStatus = async (status) => {
  try {
    await ElMessageBox.confirm(
        `确定${status === 1 ? '上架' : '下架'}选中的 ${selectedRows.value.length} 个商品吗？`,
        '提示',
        { type: 'warning' }
    )

    const ids = selectedRows.value.map(item => item.id)
    await api.post('/api/products/batch-status', {
      ids,
      status
    })

    ElMessage.success('操作成功')
    selectedRows.value = []
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
        `确定删除选中的 ${selectedRows.value.length} 个商品吗？`,
        '提示',
        { type: 'warning' }
    )

    const ids = selectedRows.value.map(item => item.id)
    await api.post('/api/products/batch-delete', ids)

    ElMessage.success('删除成功')
    selectedRows.value = []
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 导出商品
const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

// 分页
const handleSizeChange = (size) => {
  queryParams.size = size
  loadProducts()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  loadProducts()
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
  loadProducts()
  loadCategoryTree()
})
</script>

<style scoped>
.product-list-page {
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

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 12px;
}

.product-detail {
  flex: 1;
}

.product-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.product-sku {
  font-size: 12px;
  color: #909399;
}

.low-stock {
  color: #f56c6c;
  font-weight: 500;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>