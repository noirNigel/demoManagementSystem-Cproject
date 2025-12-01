<template>
  <div class="inventory-list-page">
    <div class="page-header">
      <h1>库存管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleBatchUpdate">批量更新</el-button>
        <el-button @click="handleExport">导出库存</el-button>
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
        <el-form-item label="库存状态">
          <el-select v-model="queryParams.lowStock" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="低库存" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 库存表格 -->
    <el-card>
      <el-table
          v-loading="loading"
          :data="tableData"
          stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" min-width="200">
          <template #default="{ row }">
            <div class="product-info">
              <el-image
                  v-if="row.image"
                  :src="row.image"
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
        <el-table-column prop="stock" label="当前库存" width="120">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stock <= row.warningThreshold }">
              {{ row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="warningThreshold" label="预警阈值" width="120">
          <template #default="{ row }">
            <el-input-number
                v-model="row.warningThreshold"
                :min="0"
                size="small"
                @change="handleThresholdChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="库存状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.stock <= row.warningThreshold ? 'danger' : 'success'">
              {{ row.stock <= row.warningThreshold ? '低库存' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="库存操作" width="200">
          <template #default="{ row }">
            <el-input-number
                v-model="row.newStock"
                :min="0"
                size="small"
                placeholder="调整数量"
                style="width: 120px; margin-right: 8px"
            />
            <el-button
                size="small"
                type="primary"
                :disabled="!row.newStock"
                @click="handleStockUpdate(row)"
            >
              更新
            </el-button>
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

    <!-- 批量更新对话框 -->
    <el-dialog
        v-model="batchDialogVisible"
        title="批量更新库存"
        width="500px"
    >
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="调整类型">
          <el-radio-group v-model="batchForm.type">
            <el-radio label="set">设置为</el-radio>
            <el-radio label="increase">增加</el-radio>
            <el-radio label="decrease">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number
              v-model="batchForm.quantity"
              :min="0"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/request'

// 数据状态
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const batchDialogVisible = ref(false)

// 查询参数
const queryParams = reactive({
  name: '',
  sku: '',
  lowStock: null,
  page: 1,
  size: 10
})

// 批量更新表单
const batchForm = reactive({
  type: 'set',
  quantity: 0
})

// 加载库存列表
const loadInventory = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/products', { params: queryParams })
    const products = response.content || []

    // 为每个商品添加 newStock 字段用于临时编辑
    tableData.value = products.map(product => ({
      ...product,
      newStock: null
    }))
    total.value = response.totalElements || 0
  } catch (error) {
    console.error('加载库存列表失败:', error)
    ElMessage.error('加载库存列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1
  loadInventory()
}

// 重置搜索
const handleReset = () => {
  Object.assign(queryParams, {
    name: '',
    sku: '',
    lowStock: null,
    page: 1
  })
  loadInventory()
}

// 更新预警阈值
const handleThresholdChange = async (row) => {
  try {
    await api.put(`/api/products/${row.id}/warning-threshold`, {
      threshold: row.warningThreshold
    })
    ElMessage.success('预警阈值更新成功')
  } catch (error) {
    console.error('更新预警阈值失败:', error)
    ElMessage.error('更新预警阈值失败')
    // 恢复原值
    loadInventory()
  }
}

// 更新单个商品库存
const handleStockUpdate = async (row) => {
  try {
    await api.put(`/api/products/${row.id}/stock`, {
      stock: row.newStock
    })
    ElMessage.success('库存更新成功')
    row.newStock = null
    loadInventory()
  } catch (error) {
    console.error('更新库存失败:', error)
    ElMessage.error('更新库存失败')
  }
}

// 批量更新
const handleBatchUpdate = () => {
  batchDialogVisible.value = true
  Object.assign(batchForm, {
    type: 'set',
    quantity: 0
  })
}

// 确认批量更新
const handleBatchConfirm = async () => {
  try {
    await ElMessageBox.confirm('确定要批量更新库存吗？', '提示', {
      type: 'warning'
    })

    // 这里简化处理，实际应该选择要更新的商品
    ElMessage.info('批量更新功能开发中')
    batchDialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量更新失败:', error)
      ElMessage.error('批量更新失败')
    }
  }
}

// 导出库存
const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

// 分页
const handleSizeChange = (size) => {
  queryParams.size = size
  loadInventory()
}

const handleCurrentChange = (page) => {
  queryParams.page = page
  loadInventory()
}

// 生命周期
onMounted(() => {
  loadInventory()
})
</script>

<style scoped>
.inventory-list-page {
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