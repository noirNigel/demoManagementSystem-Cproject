<template>
  <div class="product-form-page">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑商品' : '添加商品' }}</h1>
      <div class="header-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">
          保存
        </el-button>
      </div>
    </div>

    <el-card>
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="product-form"
      >
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="商品名称" prop="name">
                  <el-input v-model="formData.name" placeholder="请输入商品名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="SKU" prop="sku">
                  <el-input v-model="formData.sku" placeholder="请输入SKU编码" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="分类" prop="categoryId">
                  <el-cascader
                      v-model="formData.categoryId"
                      :options="categoryTree"
                      :props="categoryProps"
                      placeholder="请选择分类"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="formData.status">
                    <el-radio :label="1">上架</el-radio>
                    <el-radio :label="0">下架</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="商品图片">
              <el-upload
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleImageChange"
                  accept="image/*"
              >
                <el-button type="primary">上传图片</el-button>
                <template #tip>
                  <div class="el-upload__tip">支持 jpg、png 格式图片</div>
                </template>
              </el-upload>
              <div v-if="formData.image" class="image-preview">
                <el-image
                    :src="formData.image"
                    fit="cover"
                    class="preview-image"
                />
                <div class="image-actions">
                  <el-button link type="danger" @click="formData.image = ''">
                    删除
                  </el-button>
                </div>
              </div>
            </el-form-item>

            <el-form-item label="商品描述" prop="description">
              <el-input
                  v-model="formData.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入商品描述"
              />
            </el-form-item>
          </el-tab-pane>

          <!-- 价格库存 -->
          <el-tab-pane label="价格库存" name="price">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="销售价格" prop="price">
                  <el-input-number
                      v-model="formData.price"
                      :min="0"
                      :precision="2"
                      style="width: 100%"
                  >
                    <template #prefix>¥</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="成本价格" prop="cost">
                  <el-input-number
                      v-model="formData.cost"
                      :min="0"
                      :precision="2"
                      style="width: 100%"
                  >
                    <template #prefix>¥</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="库存数量" prop="stock">
                  <el-input-number
                      v-model="formData.stock"
                      :min="0"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="预警阈值" prop="warningThreshold">
                  <el-input-number
                      v-model="formData.warningThreshold"
                      :min="0"
                      style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 配方管理 -->
          <el-tab-pane label="配方管理" name="recipe">
            <el-form-item label="配方信息">
              <el-input
                  v-model="formData.recipe"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入配方信息，支持JSON格式"
              />
            </el-form-item>
            <div class="recipe-example">
              <h4>配方示例（JSON格式）:</h4>
              <pre>{{ recipeExample }}</pre>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/utils/request'

const router = useRouter()
const route = useRoute()

// 数据状态
const loading = ref(false)
const formRef = ref()
const activeTab = ref('basic')
const categoryTree = ref([])

// 表单数据
const formData = reactive({
  name: '',
  sku: '',
  categoryId: null,
  status: 1,
  price: 0,
  cost: 0,
  stock: 0,
  warningThreshold: 10,
  image: '',
  description: '',
  recipe: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  sku: [
    { required: true, message: '请输入SKU编码', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入销售价格', trigger: 'blur' }
  ]
}

// 分类选择器配置
const categoryProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  checkStrictly: true
}

// 计算属性
const isEdit = computed(() => route.params.id)
const recipeExample = computed(() => JSON.stringify({
  ingredients: [
    { name: '面粉', quantity: 500, unit: 'g' },
    { name: '糖', quantity: 100, unit: 'g' },
    { name: '鸡蛋', quantity: 2, unit: '个' }
  ],
  steps: [
    '混合所有原料',
    '搅拌均匀',
    '烘烤30分钟'
  ],
  cost: 15.5
}, null, 2))

// 加载分类树
const loadCategoryTree = async () => {
  try {
    const response = await api.get('/api/categories/tree')
    categoryTree.value = response || []
  } catch (error) {
    console.error('加载分类树失败:', error)
  }
}

// 加载商品详情
const loadProductDetail = async () => {
  try {
    const response = await api.get(`/api/products/${route.params.id}`)

    // 兼容后端返回的不同字段命名，确保图片与描述能够正常回显
    formData.name = response.name || ''
    formData.sku = response.sku || ''
    formData.categoryId = response.categoryId || null
    formData.status = response.status ?? 1
    formData.price = response.price ?? 0
    formData.cost = response.cost ?? 0
    formData.stock = response.stock ?? 0
    formData.warningThreshold = response.warningThreshold ?? 10
    formData.image = response.image || response.imageUrl || ''
    formData.description = response.description || response.desc || ''
    formData.recipe = response.recipe || ''
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  }
}

// 图片上传处理
const handleImageChange = (file) => {
  // 这里简化处理，实际项目中需要上传到服务器
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.image = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 保存商品
const handleSave = async () => {
  try {
    await formRef.value.validate()

    loading.value = true

    const submitData = { ...formData }

    // 同步可能存在的后端字段命名差异
    submitData.imageUrl = submitData.image
    submitData.desc = submitData.description
    if (Array.isArray(submitData.categoryId)) {
      submitData.categoryId = submitData.categoryId[submitData.categoryId.length - 1]
    }

    if (isEdit.value) {
      await api.put(`/api/products/${route.params.id}`, submitData)
      ElMessage.success('更新成功')
    } else {
      await api.post('/api/products', submitData)
      ElMessage.success('添加成功')
    }

    router.push('/admin/products')
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    console.error('保存失败:', error)
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    loading.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/admin/products')
}

// 生命周期
onMounted(() => {
  loadCategoryTree()
  if (isEdit.value) {
    loadProductDetail()
  }
})
</script>

<style scoped>
.product-form-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.product-form {
  max-width: 800px;
}

.image-preview {
  margin-top: 10px;
  display: flex;
  align-items: flex-start;
}

.preview-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  margin-right: 12px;
}

.recipe-example {
  margin-top: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.recipe-example pre {
  margin: 8px 0 0;
  font-size: 12px;
  color: #666;
}
</style>