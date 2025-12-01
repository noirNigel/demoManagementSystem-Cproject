<template>
  <div class="page-container">
    <el-card>
      <div class="header">
        <span class="title">员工管理</span>
        <el-button type="primary" size="small" @click="openDialog()">
          新增员工
        </el-button>
      </div>

      <el-table :data="adminList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="storeId" label="门店ID" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                @click="openDialog(scope.row)"
            >
              编辑
            </el-button>
            <el-button
                type="danger"
                size="small"
                :disabled="scope.row.status === 0"
                @click="handleDisable(scope.row)"
            >
              禁用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑/新增对话框 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
        <el-form :model="form" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="form.username" :disabled="!!form.id" />
          </el-form-item>
          <el-form-item label="密码" v-if="!form.id">
            <el-input v-model="form.password" show-password />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.role" placeholder="请选择角色">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="普通员工" value="USER" />
            </el-select>
          </el-form-item>
          <el-form-item label="门店ID">
            <el-input v-model="form.storeId" />
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
  getAdminList,
  createAdmin,
  updateAdmin,
  disableAdmin
} from '../../api/system'

const adminList = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')

const form = reactive({
  id: null,
  username: '',
  password: '',
  role: 'USER',
  storeId: null,
  status: 1
})

function fetchData() {
  getAdminList().then(res => {
    // 看你后端是否包了一层数据，如果返回的是 {code, data, msg}
    // 就改成 res.data 之类的
    adminList.value = res
  })
}

function openDialog(row) {
  if (row) {
    dialogTitle.value = '编辑员工'
    form.id = row.id
    form.username = row.username
    form.password = ''
    form.role = row.role
    form.storeId = row.storeId
    form.status = row.status
  } else {
    dialogTitle.value = '新增员工'
    form.id = null
    form.username = ''
    form.password = ''
    form.role = 'USER'
    form.storeId = null
    form.status = 1
  }
  dialogVisible.value = true
}

function handleSubmit() {
  if (!form.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!form.id && !form.password) {
    ElMessage.warning('请输入密码')
    return
  }

  const payload = {
    username: form.username,
    password: form.password,
    role: form.role,
    storeId: form.storeId,
    status: form.status
  }

  const req = form.id
      ? updateAdmin(form.id, payload)
      : createAdmin(payload)

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

function handleDisable(row) {
  ElMessageBox.confirm(
      `确定要禁用员工「${row.username}」吗？`,
      '提示',
      { type: 'warning' }
  )
      .then(() => disableAdmin(row.id))
      .then(() => {
        ElMessage.success('已禁用')
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
