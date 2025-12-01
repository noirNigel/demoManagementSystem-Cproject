<template>
  <div class="print-setting-page">
    <div class="page-header">
      <h1>打印设置</h1>
    </div>

    <el-row :gutter="20">
      <!-- 小票模板设置 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>小票模板设置</span>
          </template>

          <el-form :model="templateForm" label-width="100px">
            <el-form-item label="店铺名称">
              <el-input v-model="templateForm.shopName" placeholder="请输入店铺名称" />
            </el-form-item>
            <el-form-item label="店铺地址">
              <el-input v-model="templateForm.shopAddress" placeholder="请输入店铺地址" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="templateForm.shopPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="页眉内容">
              <el-input
                  v-model="templateForm.header"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入页眉内容"
              />
            </el-form-item>
            <el-form-item label="页脚内容">
              <el-input
                  v-model="templateForm.footer"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入页脚内容"
              />
            </el-form-item>
            <el-form-item label="打印份数">
              <el-input-number
                  v-model="templateForm.copies"
                  :min="1"
                  :max="5"
                  controls-position="right"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveTemplate">保存模板</el-button>
              <el-button @click="previewTemplate">预览效果</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 打印设备管理 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>打印设备管理</span>
          </template>

          <div class="printer-list">
            <div class="printer-item" v-for="printer in printers" :key="printer.id">
              <div class="printer-info">
                <div class="printer-name">{{ printer.name }}</div>
                <div class="printer-status">
                  <el-tag :type="printer.status === 'online' ? 'success' : 'danger'" size="small">
                    {{ printer.status === 'online' ? '在线' : '离线' }}
                  </el-tag>
                </div>
              </div>
              <div class="printer-actions">
                <el-button link type="primary" @click="testPrinter(printer)">测试</el-button>
                <el-button link type="danger" @click="removePrinter(printer)">删除</el-button>
              </div>
            </div>

            <div class="add-printer">
              <el-button type="dashed" style="width: 100%;" @click="showAddPrinter = true">
                <el-icon><Plus /></el-icon>
                添加打印设备
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 打印测试 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>打印测试</span>
          </template>

          <el-form label-width="80px">
            <el-form-item label="测试内容">
              <el-input
                  v-model="testContent"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入测试打印内容"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTestPrint">测试打印</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加打印设备对话框 -->
    <el-dialog
        v-model="showAddPrinter"
        title="添加打印设备"
        width="400px"
    >
      <el-form :model="newPrinter" label-width="80px">
        <el-form-item label="设备名称">
          <el-input v-model="newPrinter.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="newPrinter.type" placeholder="请选择设备类型">
            <el-option label="USB打印机" value="usb" />
            <el-option label="网络打印机" value="network" />
            <el-option label="蓝牙打印机" value="bluetooth" />
          </el-select>
        </el-form-item>
        <el-form-item label="连接地址" v-if="newPrinter.type === 'network'">
          <el-input v-model="newPrinter.address" placeholder="例如: 192.168.1.100:9100" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddPrinter = false">取消</el-button>
        <el-button type="primary" @click="addPrinter">添加</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
        v-model="showPreview"
        title="小票预览"
        width="400px"
    >
      <div class="receipt-preview">
        <div class="receipt-content">
          <div class="receipt-header">
            <h3>{{ templateForm.shopName || '店铺名称' }}</h3>
            <p>{{ templateForm.shopAddress || '店铺地址' }}</p>
            <p>电话: {{ templateForm.shopPhone || '联系电话' }}</p>
            <p>--------------------------------</p>
          </div>

          <div class="receipt-body">
            <div class="order-info">
              <p>订单号: TEST2024000001</p>
              <p>时间: {{ new Date().toLocaleString() }}</p>
              <p>--------------------------------</p>
            </div>

            <div class="order-items">
              <div class="item">
                <span>测试商品</span>
                <span>1 × ¥10.00</span>
              </div>
              <div class="item">
                <span>另一个商品</span>
                <span>2 × ¥15.00</span>
              </div>
            </div>

            <div class="order-total">
              <p>--------------------------------</p>
              <p>合计: ¥40.00</p>
            </div>
          </div>

          <div class="receipt-footer">
            <p>{{ templateForm.footer || '谢谢惠顾，欢迎再次光临！' }}</p>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showPreview = false">关闭</el-button>
        <el-button type="primary" @click="printPreview">打印预览</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const showAddPrinter = ref(false)
const showPreview = ref(false)
const testContent = ref('这是一个打印测试内容')

// 模板表单
const templateForm = reactive({
  shopName: '我的店铺',
  shopAddress: '北京市朝阳区xxx路xxx号',
  shopPhone: '400-123-4567',
  header: '欢迎光临，祝您购物愉快！',
  footer: '谢谢惠顾，欢迎再次光临！',
  copies: 1
})

// 打印设备列表
const printers = ref([
  { id: 1, name: '前台打印机', type: 'usb', status: 'online' },
  { id: 2, name: '后厨打印机', type: 'network', status: 'online' },
  { id: 3, name: '备用打印机', type: 'bluetooth', status: 'offline' }
])

// 新打印机
const newPrinter = reactive({
  name: '',
  type: 'usb',
  address: ''
})

// 保存模板
const saveTemplate = () => {
  // 在实际项目中，这里应该调用API保存设置
  localStorage.setItem('printTemplate', JSON.stringify(templateForm))
  ElMessage.success('模板设置已保存')
}

// 预览模板
const previewTemplate = () => {
  showPreview.value = true
}

// 打印预览
const printPreview = () => {
  window.print()
  ElMessage.info('请使用浏览器的打印功能打印预览')
}

// 添加打印机
const addPrinter = () => {
  if (!newPrinter.name) {
    ElMessage.error('请输入设备名称')
    return
  }

  printers.value.push({
    id: Date.now(),
    name: newPrinter.name,
    type: newPrinter.type,
    address: newPrinter.address,
    status: 'online'
  })

  showAddPrinter.value = false
  Object.assign(newPrinter, {
    name: '',
    type: 'usb',
    address: ''
  })

  ElMessage.success('打印设备添加成功')
}

// 测试打印机
const testPrinter = (printer) => {
  ElMessage.info(`正在测试打印机: ${printer.name}`)
  // 在实际项目中，这里应该调用打印测试API
}

// 删除打印机
const removePrinter = (printer) => {
  ElMessageBox.confirm(`确定要删除打印机 "${printer.name}" 吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    printers.value = printers.value.filter(p => p.id !== printer.id)
    ElMessage.success('打印机已删除')
  })
}

// 测试打印
const handleTestPrint = () => {
  if (!testContent.value) {
    ElMessage.error('请输入测试内容')
    return
  }

  ElMessage.info('正在发送打印任务...')
  // 在实际项目中，这里应该调用打印API
}

// 生命周期
onMounted(() => {
  // 加载保存的模板设置
  const savedTemplate = localStorage.getItem('printTemplate')
  if (savedTemplate) {
    Object.assign(templateForm, JSON.parse(savedTemplate))
  }
})
</script>

<style scoped>
.print-setting-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.printer-list {
  min-height: 200px;
}

.printer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  margin-bottom: 8px;
}

.printer-info {
  flex: 1;
}

.printer-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.add-printer {
  margin-top: 16px;
}

.receipt-preview {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
}

.receipt-content {
  text-align: center;
}

.receipt-header h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
}

.receipt-header p {
  margin: 4px 0;
}

.receipt-body {
  text-align: left;
  margin: 16px 0;
}

.order-items .item {
  display: flex;
  justify-content: space-between;
  margin: 4px 0;
}

.order-total {
  text-align: right;
  font-weight: bold;
}

@media print {
  .receipt-preview {
    font-size: 10px;
  }
}
</style>