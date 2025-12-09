<template>
  <div class="promotion-list-page">
    <div class="page-header">
      <h1>促销活动管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">添加促销活动</el-button>
        <el-button @click="debugData">调试数据</el-button>
      </div>
    </div>

    <el-card>
      <!-- 调试信息显示 -->
      <div style="margin-bottom: 16px; padding: 8px; background: #f5f5f5; border-radius: 4px;">
        <div>数据统计: 共 {{ tableData.length }} 条记录</div>
        <div v-if="tableData.length > 0">
          第一条数据: ID={{ tableData[0].id }}, 名称={{ tableData[0].name }}, 状态={{ tableData[0].status }}
        </div>
      </div>

      <el-table
          v-loading="loading"
          :data="tableData"
          stripe
          empty-text="暂无数据"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="活动名称" min-width="150" />
        <el-table-column prop="type" label="活动类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动内容" width="180">
          <template #default="{ row }">
            <span v-if="row.type === 'FULL_REDUCE'">
              满{{ row.conditionAmount }}减{{ row.reduceAmount }}
            </span>
            <span v-else-if="row.type === 'DISCOUNT'">
              {{ (row.discountRate * 10).toFixed(1) }}折
            </span>
            <span v-else-if="row.type === 'SECOND_HALF'">
              第二件半价
            </span>
            <span v-else>
              {{ row.type }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">{{ formatDate(row.endTime) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否进行中" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '进行中' : '未开始/已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link @click="handleUpdateStatus(row)">
              {{ (row.status === 1) ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'create' ? '添加促销活动' : '编辑促销活动'"
        width="700px"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
      >
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option label="满减活动" value="FULL_REDUCE" />
            <el-option label="折扣活动" value="DISCOUNT" />
            <el-option label="第二件半价" value="SECOND_HALF" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="formData.type === 'FULL_REDUCE'" label="满减条件" required>
          <el-input-number
              v-model="formData.conditionAmount"
              :min="0"
              :precision="2"
              placeholder="满多少元"
              style="width: 48%; margin-right: 4%"
          />
          <el-input-number
              v-model="formData.reduceAmount"
              :min="0"
              :precision="2"
              placeholder="减多少元"
              style="width: 48%"
          />
        </el-form-item>

        <el-form-item v-if="formData.type === 'DISCOUNT'" label="折扣率" prop="discountRate">
          <el-input-number
              v-model="formData.discountRate"
              :min="0.01"
              :max="1"
              :step="0.01"
              :precision="2"
              placeholder="0.9表示9折"
          />
          <span class="form-tip">例如：0.9 表示 9 折</span>
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
              v-model="formData.startTime"
              type="datetime"
              placeholder="选择开始时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
              v-model="formData.endTime"
              type="datetime"
              placeholder="选择结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="活动描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入活动描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { promotionApi } from '@/api/marketing'

// 数据状态
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogType = ref('create')
const formRef = ref()

// 表单数据
const formData = reactive({
  name: '',
  type: 'FULL_REDUCE',
  conditionAmount: null,
  reduceAmount: null,
  discountRate: 0.9,
  startTime: null,
  endTime: null,
  status: 1,
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入活动名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

// 类型显示方法
const getTypeTag = (type) => {
  const typeMap = {
    'FULL_REDUCE': 'success',
    'DISCOUNT': 'warning',
    'SECOND_HALF': 'info'
  }
  return typeMap[type] || 'info'
}

const getTypeText = (type) => {
  const typeMap = {
    'FULL_REDUCE': '满减',
    'DISCOUNT': '折扣',
    'SECOND_HALF': '第二件半价'
  }
  return typeMap[type] || type
}

// 状态显示方法
const getStatusType = (status) => {
  return status === 1 ? 'success' : 'info'
}

const getStatusText = (status) => {
  if (status === 1) return '启用'
  if (status === 0) return '禁用'
  return '未知' // 处理 null 或其他值
}

// 调试方法
const debugData = () => {
  console.log('=== 当前表格数据调试 ===')
  console.log('tableData:', tableData.value)
  console.log('tableData 长度:', tableData.value.length)

  if (tableData.value && tableData.value.length > 0) {
    tableData.value.forEach((item, index) => {
      console.log(`数据[${index}]:`, JSON.stringify(item))
    })
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

// 加载促销活动列表
const loadPromotions = async () => {
  loading.value = true
  try {
    console.log('开始加载促销活动...')
    const response = await promotionApi.getPromotions()
    console.log('API响应:', response)

    // 确保是数组
    if (Array.isArray(response)) {
      tableData.value = response
      console.log('数据加载成功，数量:', response.length)
    } else {
      console.warn('响应不是数组:', response)
      tableData.value = []
    }
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载促销活动列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 添加促销活动
const handleAdd = () => {
  dialogType.value = 'create'
  dialogVisible.value = true
  resetForm()
}

// 编辑促销活动
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(formData, {
    ...row,
    startTime: normalizeDisplayTime(row.startTime),
    endTime: normalizeDisplayTime(row.endTime)
  })
}

// 更新促销活动状态
const handleUpdateStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await promotionApi.updatePromotionStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    loadPromotions()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除促销活动
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该促销活动吗？', '提示', {
      type: 'warning'
    })
    await promotionApi.deletePromotion(row.id)
    ElMessage.success('删除成功')
    loadPromotions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    const payload = buildSubmitPayload()

    if (dialogType.value === 'create') {
      await promotionApi.createPromotion(payload)
      ElMessage.success('添加成功')
    } else {
      await promotionApi.updatePromotion(formData.id, payload)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadPromotions()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    name: '',
    type: 'FULL_REDUCE',
    conditionAmount: null,
    reduceAmount: null,
    discountRate: 0.9,
    startTime: null,
    endTime: null,
    status: 1,
    description: ''
  })
}

// 构建提交载荷，确保时间格式符合后端 LocalDateTime 自动解析
const buildSubmitPayload = () => {
  const formatToIso = (value) => {
    if (!value) return null
    // value 已是 'YYYY-MM-DD HH:mm:ss' 字符串，转为标准 ISO 字符串
    const normalized = value.replace(' ', 'T')
    return normalized
  }

  return {
    ...formData,
    startTime: formatToIso(formData.startTime),
    endTime: formatToIso(formData.endTime)
  }
}

const normalizeDisplayTime = (value) => {
  if (!value) return null
  // 兼容 ISO 时间和已有格式
  return value.replace('T', ' ').replace(/\.\d+Z?$/, '')
}

// 生命周期
onMounted(() => {
  loadPromotions()
})
</script>

<style scoped>
.promotion-list-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>