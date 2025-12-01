<template>
  <div class="promotion-form">
    <el-card>
      <div class="title">{{ isEdit ? '编辑促销活动' : '新增促销活动' }}</div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入活动名称"></el-input>
        </el-form-item>

        <el-form-item label="活动时间" prop="time">
          <el-date-picker
              v-model="form.time"
              type="datetimerange"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="折扣力度(%)" prop="discount">
          <el-input-number v-model="form.discount" :min="1" :max="100" />
        </el-form-item>

        <el-form-item label="活动描述">
          <el-input
              v-model="form.description"
              type="textarea"
              rows="4"
              placeholder="请输入活动描述"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button @click="onSubmit" type="primary">提交</el-button>
          <el-button @click="onCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()

// 是否为编辑模式
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  time: [],
  discount: 10,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  time: [{ required: true, message: '请选择时间范围', trigger: 'change' }],
  discount: [{ required: true, message: '请输入折扣力度', trigger: 'change' }]
}

// 模拟加载数据（你可改成 API 请求）
onMounted(() => {
  if (isEdit.value) {
    form.name = '双11大促'
    form.time = ['2025-01-01 00:00:00', '2025-01-05 23:59:59']
    form.discount = 30
    form.description = '超级优惠活动'
  }
})

const onSubmit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return

    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    router.back()
  })
}

const onCancel = () => router.back()
</script>

<style scoped>
.title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}
</style>
