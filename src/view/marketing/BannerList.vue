<template>
  <div class="banner-page">
    <div class="page-header">
      <h1>轮播图管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
        <el-button @click="fetchData">刷新</el-button>
      </div>
    </div>

    <el-card>
      <el-table
          v-loading="loading"
          :data="tableData"
          stripe
          empty-text="暂无轮播图"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column label="图片" width="160">
          <template #default="{ row }">
            <el-image
                v-if="row.imageUrl"
                :src="row.imageUrl"
                :preview-src-list="[row.imageUrl]"
                fit="cover"
                style="width: 120px; height: 70px; border-radius: 6px;"
            />
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column prop="linkUrl" label="跳转链接" min-width="180" show-overflow-tooltip />
        <el-table-column label="展示时间" width="240">
          <template #default="{ row }">
            <div>{{ formatDate(row.startTime) }} ~</div>
            <div>{{ formatDate(row.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">{{ formatDate(row.updatedAt) }}</template>
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
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="dialogMode === 'create' ? '新增轮播图' : '编辑轮播图'"
        width="640px"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片地址" prop="imageUrl">
          <el-input v-model="formData.imageUrl" placeholder="请输入图片 URL" />
          <div class="form-tip">需提供可访问的图片地址</div>
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="formData.linkUrl" placeholder="点击后跳转的页面地址，可选" />
        </el-form-item>
        <el-form-item label="展示时间">
          <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
          <div class="form-tip">数值越大排序越靠前</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              placeholder="请输入描述信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bannerApi } from '@/api/marketing'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref()
const dateRange = ref([])

const formData = reactive({
  id: null,
  title: '',
  imageUrl: '',
  linkUrl: '',
  startTime: '',
  endTime: '',
  status: 1,
  sortOrder: 0,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片地址', trigger: 'blur' }]
}

const resetForm = () => {
  formData.id = null
  formData.title = ''
  formData.imageUrl = ''
  formData.linkUrl = ''
  formData.startTime = ''
  formData.endTime = ''
  formData.status = 1
  formData.sortOrder = 0
  formData.description = ''
  dateRange.value = []
  formRef.value?.clearValidate()
}

const fetchData = async () => {
  loading.value = true
  try {
    const { data } = await bannerApi.getBanners()
    tableData.value = data || []
  } catch (error) {
    console.error('获取轮播图失败', error)
    ElMessage.error('获取轮播图失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  resetForm()
  dialogMode.value = 'create'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  dialogMode.value = 'edit'
  Object.assign(formData, row)
  if (row.startTime || row.endTime) {
    dateRange.value = [row.startTime, row.endTime].filter(Boolean)
  }
  dialogVisible.value = true
}

watch(dateRange, (val) => {
  formData.startTime = val?.[0] || ''
  formData.endTime = val?.[1] || ''
})

const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return

    const payload = { ...formData }
    try {
      if (dialogMode.value === 'create') {
        await bannerApi.createBanner(payload)
        ElMessage.success('创建成功')
      } else {
        await bannerApi.updateBanner(formData.id, payload)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      console.error('保存轮播图失败', error)
      ElMessage.error('保存失败，请稍后重试')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除轮播图 “${row.title}” 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await bannerApi.deleteBanner(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除轮播图失败', error)
      ElMessage.error('删除失败，请稍后再试')
    }
  }).catch(() => {})
}

const handleUpdateStatus = async (row) => {
  const nextStatus = row.status === 1 ? 0 : 1
  try {
    await bannerApi.updateBannerStatus(row.id, nextStatus)
    ElMessage.success(nextStatus === 1 ? '已启用' : '已禁用')
    fetchData()
  } catch (error) {
    console.error('更新状态失败', error)
    ElMessage.error('更新状态失败，请稍后再试')
  }
}

const formatDate = (value) => {
  if (!value) return '--'
  return dayjs(value).format('YYYY-MM-DD HH:mm')
}

onMounted(fetchData)
</script>

<style scoped>
.banner-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.form-tip {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}
</style>
