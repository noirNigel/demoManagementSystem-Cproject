<template>
  <div class="member-level-list-page">
    <div class="page-header">
      <h1>会员等级管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">添加会员等级</el-button>
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
        <el-table-column prop="name" label="等级名称" width="120" />
        <el-table-column prop="minPoints" label="最低积分" width="100" />
        <el-table-column prop="discountRate" label="折扣率" width="100">
          <template #default="{ row }">
            {{ row.discountRate ? (row.discountRate * 10).toFixed(1) + '折' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="pointsMultiplier" label="积分倍数" width="100">
          <template #default="{ row }">
            {{ row.pointsMultiplier || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="birthdayBenefit" label="生日福利" min-width="150">
          <template #default="{ row }">
            {{ row.birthdayBenefit || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
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
        :title="dialogType === 'create' ? '添加会员等级' : '编辑会员等级'"
        width="600px"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
      >
        <el-form-item label="等级名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item label="最低积分" prop="minPoints">
          <el-input-number
              v-model="formData.minPoints"
              :min="0"
              placeholder="请输入最低积分要求"
          />
        </el-form-item>
        <el-form-item label="折扣率" prop="discountRate">
          <el-input-number
              v-model="formData.discountRate"
              :min="0.01"
              :max="1"
              :step="0.01"
              :precision="2"
          />
          <span class="form-tip">例如：0.9 表示 9 折</span>
        </el-form-item>
        <el-form-item label="积分倍数" prop="pointsMultiplier">
          <el-input-number
              v-model="formData.pointsMultiplier"
              :min="1"
              :step="0.1"
              :precision="1"
          />
        </el-form-item>
        <el-form-item label="生日福利" prop="birthdayBenefit">
          <el-input
              v-model="formData.birthdayBenefit"
              type="textarea"
              :rows="3"
              placeholder="请输入生日福利描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { memberLevelApi } from '@/api/marketing'

// 数据状态
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogType = ref('create')
const formRef = ref()

// 表单数据
const formData = reactive({
  name: '',
  minPoints: 0,
  discountRate: 1.0,
  pointsMultiplier: 1.0,
  birthdayBenefit: '',
  status: 1
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入等级名称', trigger: 'blur' }
  ],
  minPoints: [
    { required: true, message: '请输入最低积分', trigger: 'blur' }
  ],
  discountRate: [
    { required: true, message: '请输入折扣率', trigger: 'blur' }
  ],
  pointsMultiplier: [
    { required: true, message: '请输入积分倍数', trigger: 'blur' }
  ]
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

// 加载会员等级列表
const loadLevels = async () => {
  loading.value = true
  try {
    console.log('开始加载会员等级...')
    const response = await memberLevelApi.getLevels()
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
    ElMessage.error('加载会员等级列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 添加会员等级
const handleAdd = () => {
  dialogType.value = 'create'
  dialogVisible.value = true
  resetForm()
}

// 编辑会员等级
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(formData, row)
}

// 更新会员等级状态
const handleUpdateStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await memberLevelApi.updateLevelStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    loadLevels()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除会员等级
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该会员等级吗？', '提示', {
      type: 'warning'
    })
    await memberLevelApi.deleteLevel(row.id)
    ElMessage.success('删除成功')
    loadLevels()
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

    if (dialogType.value === 'create') {
      await memberLevelApi.createLevel(formData)
      ElMessage.success('添加成功')
    } else {
      await memberLevelApi.updateLevel(formData.id, formData)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadLevels()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    name: '',
    minPoints: 0,
    discountRate: 1.0,
    pointsMultiplier: 1.0,
    birthdayBenefit: '',
    status: 1
  })
}

// 生命周期
onMounted(() => {
  loadLevels()
})
</script>

<style scoped>
.member-level-list-page {
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