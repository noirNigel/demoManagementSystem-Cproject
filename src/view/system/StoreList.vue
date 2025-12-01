<template>
  <div class="page-container">
    <el-card>
      <div class="header">
        <span class="title">门店管理</span>
        <el-button type="primary" size="small" @click="openDialog()">
          新增门店
        </el-button>
      </div>

      <el-table :data="storeList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="门店名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column label="营业时间" width="180">
          <template #default="scope">
            {{ scope.row.openTime }} - {{ scope.row.closeTime }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '营业' : '停业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="openDialog(scope.row)">
              编辑
            </el-button>
            <el-button
                size="small"
                type="danger"
                @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form :model="form" label-width="90px">
          <el-form-item label="门店名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="form.address" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="营业开始时间">
            <el-input v-model="form.openTime" placeholder="09:00" />
          </el-form-item>
          <el-form-item label="营业结束时间">
            <el-input v-model="form.closeTime" placeholder="22:00" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch
                v-model="form.status"
                :active-value="1"
                :inactive-value="0"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getStoreList,
  createStore,
  updateStore,
  deleteStore
} from '../../api/system'

const storeList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增门店')

const form = reactive({
  id: null,
  name: '',
  address: '',
  phone: '',
  openTime: '',
  closeTime: '',
  status: 1
})

function fetchData() {
  getStoreList().then(res => {
    storeList.value = res
  })
}

function openDialog(row) {
  if (row) {
    dialogTitle.value = '编辑门店'
    form.id = row.id
    form.name = row.name
    form.address = row.address
    form.phone = row.phone
    form.openTime = row.openTime
    form.closeTime = row.closeTime
    form.status = row.status
  } else {
    dialogTitle.value = '新增门店'
    form.id = null
    form.name = ''
    form.address = ''
    form.phone = ''
    form.openTime = ''
    form.closeTime = ''
    form.status = 1
  }
  dialogVisible.value = true
}

function handleSubmit() {
  if (!form.name) {
    ElMessage.warning('请输入门店名称')
    return
  }
  const payload = { ...form }
  const req = form.id ? updateStore(form.id, payload) : createStore(payload)
  req
      .then(() => {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        fetchData()
      })
      .catch(() => {
        ElMessage.error('保存失败')
      })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除门店「${row.name}」吗？`, '提示', {
    type: 'warning'
  })
      .then(() => deleteStore(row.id))
      .then(() => {
        ElMessage.success('删除成功')
        fetchData()
      })
      .catch(() => {})
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
