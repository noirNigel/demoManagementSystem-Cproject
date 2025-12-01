<template>
  <div class="category-list-page">
    <div class="page-header">
      <h1>分类管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">添加分类</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>分类树</span>
          </template>
          <div class="category-tree">
            <el-tree
                ref="treeRef"
                :data="categoryTree"
                node-key="id"
                :props="treeProps"
                :expand-on-click-node="false"
                :highlight-current="true"
                @node-click="handleNodeClick"
            >
              <template #default="{ node, data }">
                <span class="custom-tree-node">
                  <span>{{ node.label }}</span>
                  <span class="node-actions">
                    <el-button link type="primary" @click="handleAddChild(data)">
                      添加子类
                    </el-button>
                    <el-button link @click="handleEdit(data)">编辑</el-button>
                    <el-button link type="danger" @click="handleDelete(data)">
                      删除
                    </el-button>
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>
            <span>{{ currentCategory ? '编辑分类' : '添加分类' }}</span>
          </template>
          <el-form
              ref="formRef"
              :model="formData"
              :rules="rules"
              label-width="100px"
          >
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入分类名称" />
            </el-form-item>

            <el-form-item label="父级分类">
              <el-cascader
                  v-model="formData.parentId"
                  :options="categoryTree"
                  :props="cascaderProps"
                  placeholder="请选择父级分类"
                  style="width: 100%"
                  clearable
              />
            </el-form-item>

            <el-form-item label="排序" prop="sortOrder">
              <el-input-number
                  v-model="formData.sortOrder"
                  :min="0"
                  controls-position="right"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="loading">
                {{ currentCategory ? '更新' : '保存' }}
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/request'

// 数据状态
const treeRef = ref()
const formRef = ref()
const categoryTree = ref([])
const currentCategory = ref(null)
const loading = ref(false)

// 表单数据
const formData = reactive({
  name: '',
  parentId: null,
  sortOrder: 0
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 树形配置
const treeProps = {
  label: 'name',
  children: 'children'
}

// 级联选择器配置
const cascaderProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true,
  emitPath: false
}

// 加载分类树
const loadCategoryTree = async () => {
  try {
    const response = await api.get('/api/categories/tree')
    categoryTree.value = response || []
  } catch (error) {
    console.error('加载分类树失败:', error)
    ElMessage.error('加载分类树失败')
  }
}

// 节点点击
const handleNodeClick = (data) => {
  currentCategory.value = data
  Object.assign(formData, {
    name: data.name,
    parentId: data.parentId,
    sortOrder: data.sortOrder
  })
}

// 添加分类
const handleAdd = () => {
  currentCategory.value = null
  handleReset()
}

// 添加子分类
const handleAddChild = (data) => {
  currentCategory.value = null
  handleReset()
  formData.parentId = data.id
}

// 编辑分类
const handleEdit = (data) => {
  currentCategory.value = data
  Object.assign(formData, {
    name: data.name,
    parentId: data.parentId,
    sortOrder: data.sortOrder
  })
}

// 删除分类
const handleDelete = async (data) => {
  try {
    await ElMessageBox.confirm(
        `确定删除分类 "${data.name}" 吗？`,
        '提示',
        { type: 'warning' }
    )

    await api.delete(`/api/categories/${data.id}`)
    ElMessage.success('删除成功')
    loadCategoryTree()
    handleReset()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      const errorMessage = error.response?.data?.message || '删除失败'
      ElMessage.error(errorMessage)
    }
  }
}

// 保存分类
const handleSave = async () => {
  try {
    await formRef.value.validate()

    loading.value = true

    const submitData = { ...formData }

    // 如果 parentId 是数组（级联选择器的值），取最后一个
    if (Array.isArray(submitData.parentId)) {
      submitData.parentId = submitData.parentId.length > 0
          ? submitData.parentId[submitData.parentId.length - 1]
          : null
    }

    if (currentCategory.value) {
      // 更新
      await api.put(`/api/categories/${currentCategory.value.id}`, submitData)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await api.post('/api/categories', submitData)
      ElMessage.success('添加成功')
    }

    loadCategoryTree()
    handleReset()
  } catch (error) {
    if (error.errors) return

    console.error('保存失败:', error)
    const errorMessage = error.response?.data?.message || '保存失败'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  currentCategory.value = null
  Object.assign(formData, {
    name: '',
    parentId: null,
    sortOrder: 0
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 生命周期
onMounted(() => {
  loadCategoryTree()
})
</script>

<style scoped>
.category-list-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.category-tree {
  min-height: 400px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.node-actions {
  display: flex;
  gap: 4px;
}
</style>