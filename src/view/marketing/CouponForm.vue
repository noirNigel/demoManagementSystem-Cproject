<template>
  <div class="coupon-form-page">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑优惠券' : '添加优惠券' }}</h1>
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
          class="coupon-form"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="优惠券名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入优惠券名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠券类型" prop="type">
              <el-radio-group v-model="formData.type">
                <el-radio label="DISCOUNT">折扣券</el-radio>
                <el-radio label="REDUCE">满减券</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24" v-if="formData.type === 'DISCOUNT'">
          <el-col :span="12">
            <el-form-item label="折扣比例" prop="discount">
              <el-input-number
                  v-model="formData.discount"
                  :min="0.01"
                  :max="1"
                  :step="0.01"
                  :precision="2"
              />
              <span class="form-tip">例如：0.9 表示 9 折</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24" v-if="formData.type === 'REDUCE'">
          <el-col :span="12">
            <el-form-item label="满减条件" prop="minAmount">
              <el-input-number
                  v-model="formData.minAmount"
                  :min="0"
                  :precision="2"
                  placeholder="满多少元"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="减免金额" prop="reduceAmount">
              <el-input-number
                  v-model="formData.reduceAmount"
                  :min="0"
                  :precision="2"
                  placeholder="减多少元"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="发行数量" prop="totalCount">
              <el-input-number
                  v-model="formData.totalCount"
                  :min="0"
                  placeholder="0表示不限制"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每人限领" prop="limitPerUser">
              <el-input-number
                  v-model="formData.limitPerUser"
                  :min="1"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
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
          </el-col>
          <el-col :span="12">
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
          </el-col>
        </el-row>

        <el-form-item label="描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入优惠券描述"
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { couponApi } from '@/api/marketing'

const router = useRouter()
const route = useRoute()

// 数据状态
const loading = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive({
  name: '',
  type: 'DISCOUNT',
  discount: 0.9,
  minAmount: null,
  reduceAmount: null,
  totalCount: 100,
  limitPerUser: 1,
  startTime: null,
  endTime: null,
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入优惠券名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择优惠券类型', trigger: 'change' }
  ],
  discount: [
    { required: true, message: '请输入折扣比例', trigger: 'blur' }
  ],
  totalCount: [
    { required: true, message: '请输入发行数量', trigger: 'blur' }
  ],
  limitPerUser: [
    { required: true, message: '请输入每人限领数量', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

// 计算属性
const isEdit = computed(() => route.params.id)

// 加载优惠券详情
const loadCouponDetail = async () => {
  try {
    const response = await couponApi.getCoupon(route.params.id)
    Object.assign(formData, response)
  } catch (error) {
    console.error('加载优惠券详情失败:', error)
    ElMessage.error('加载优惠券详情失败')
  }
}

// 保存优惠券
const handleSave = async () => {
  try {
    await formRef.value.validate()

    loading.value = true

    if (isEdit.value) {
      await couponApi.updateCoupon(route.params.id, formData)
      ElMessage.success('更新成功')
    } else {
      await couponApi.createCoupon(formData)
      ElMessage.success('添加成功')
    }

    router.push('/admin/marketing/coupons')
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
  router.push('/admin/marketing/coupons')
}

// 生命周期
onMounted(() => {
  if (isEdit.value) {
    loadCouponDetail()
  }
})
</script>

<style scoped>
.coupon-form-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.coupon-form {
  max-width: 800px;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>