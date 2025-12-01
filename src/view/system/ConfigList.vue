<template>
  <div class="page-container">
    <el-card>
      <div class="header">
        <span class="title">系统设置</span>
        <el-button type="primary" size="small" @click="openDialog()">
          新增配置
        </el-button>
      </div>

      <el-table :data="configList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configKey" label="配置键" width="220" />
        <el-table-column prop="configValue" label="配置值" />
        <el-table-column prop="description" label="说明" width="220" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="openDialog(scope.row)">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form :model="form" label-width="100px">
          <el-form-item label="配置键">
            <el-input
                v-model="form.configKey"
                :disabled="!!form.id"
                placeholder="例如：site_name"
            />
          </el-form-item>
          <el-form-item label="配置值">
            <el-input
                v-model="form.configValue"
                type="textarea"
                :rows="4"
                placeholder="可以是字符串或 JSON"
            />
          </el-form-item>
          <el-form-item label="说明">
            <el-input v-model="form.description" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigList, saveConfig } from '../../api/system'

const configList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增配置')

const form = reactive({
  id: null,
  configKey: '',
  configValue: '',
  description: ''
})

function fetchData() {
  getConfigList().then(res => {
    configList.value = res
  })
}

function openDialog(row) {
  if (row) {
    dialogTitle.value = '编辑配置'
    form.id = row.id
    form.configKey = row.configKey
    form.configValue = row.configValue
    form.description = row.description
  } else {
    dialogTitle.value = '新增配置'
    form.id = null
    form.configKey = ''
    form.configValue = ''
    form.description = ''
  }
  dialogVisible.value = true
}

function handleSubmit() {
  if (!form.configKey) {
    ElMessage.warning('请填写配置键')
    return
  }
  const payload = {
    id: form.id,
    configKey: form.configKey,
    configValue: form.configValue,
    description: form.description
  }
  saveConfig(payload)
      .then(() => {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        fetchData()
      })
      .catch(() => {
        ElMessage.error('保存失败')
      })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
</style>
