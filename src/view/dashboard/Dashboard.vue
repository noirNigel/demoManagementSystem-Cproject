<template>
  <div class="dashboard-page">
    <!-- 顶部概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :xs="24" :sm="8" v-for="card in overviewCards" :key="card.key">
        <el-card shadow="hover" class="overview-card">
          <div class="card-header">
            <div class="card-title">{{ card.title }}</div>
            <div class="card-sub">{{ card.sub }}</div>
          </div>
          <div class="card-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主体： 左图表 右预警 -->
    <el-row :gutter="20" class="main-row" style="margin-top:16px;">
      <el-col :xs="24" :md="16">
        <el-card>
          <div class="card-toolbar">
            <div>
              <el-radio-group v-model="trendDays" size="small" @change="loadTrend">
                <el-radio-button :label="7">7天</el-radio-button>
                <el-radio-button :label="30">30天</el-radio-button>
                <el-radio-button :label="90">90天</el-radio-button>
              </el-radio-group>
            </div>
            <div>
              <el-button type="primary" size="small" @click="reloadAll">刷新</el-button>
            </div>
          </div>

          <div ref="trendChartRef" class="trend-chart"></div>

          <div style="margin-top:16px;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <div style="font-weight:600;">商品销售排行（过去 {{ topDays }} 天）</div>
              <div>
                <el-input-number v-model="topLimit" :min="1" :max="100" size="small" style="margin-right:8px" />
                <el-button size="small" @click="loadTopProducts">刷新</el-button>
                <el-button size="small" @click="exportTopProductsCsv">导出 CSV</el-button>
              </div>
            </div>

            <el-table :data="topProducts" stripe style="width:100%; margin-top:8px;">
              <el-table-column prop="productName" label="商品" />
              <el-table-column prop="soldQuantity" label="销量" width="120" />
              <el-table-column prop="revenue" label="销售额" width="140">
                <template #default="{ row }">¥ {{ formatMoney(row.revenue) }}</template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card>
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div style="font-weight:600;">库存预警</div>
            <el-button size="small" @click="loadWarnings">刷新</el-button>
          </div>

          <el-table :data="warnings" stripe style="width:100%; margin-top:8px;">
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="quantity" label="库存" width="100" />
            <el-table-column prop="threshold" label="阈值" width="100" />
          </el-table>

          <div v-if="warnings.length === 0" class="no-warning">当前无库存预警</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import axios from 'axios';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';

/**
 * axios 实例：自动把 localStorage.token 放到 Authorization（如果存在）
 * 若你的 token 存储键不同，请修改 getToken() 实现。
 */
function getToken() {
  try {
    return localStorage.getItem('token') || null;
  } catch (e) {
    return null;
  }
}

const api = axios.create({
  baseURL: '/api', // 开发环境建议通过 dev proxy 转发到后端 http://localhost:8080
  timeout: 10000
});

api.interceptors.request.use(config => {
  const t = getToken();
  if (t) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${t}`;
  }
  return config;
}, err => Promise.reject(err));

/* ---------- 数据状态 ---------- */
const summary = ref({
  ordersToday: 0,
  salesToday: 0,
  avgOrderValue: 0
});

const overviewCards = ref([
  { key: 'orders', title: '今日订单', sub: '', value: '0' },
  { key: 'sales', title: '今日销售额', sub: '', value: '¥0.00' },
  { key: 'avg', title: '客单价', sub: '', value: '¥0.00' }
]);

const trendDays = ref(7);
const topDays = ref(30);
const topLimit = ref(10);

const trendChartRef = ref(null);
let trendChartInstance = null;

const topProducts = ref([]);
const warnings = ref([]);

/* ---------- 格式化 ---------- */
function formatMoney(val) {
  if (val === null || val === undefined) return '0.00';
  const num = Number(val);
  if (isNaN(num)) return '0.00';
  return num.toFixed(2);
}

/* ---------- 加载函数 ---------- */
async function loadSummary() {
  try {
    const res = await api.get('/dashboard/summary');
    const d = res.data;
    summary.value = d || summary.value;
    overviewCards.value = [
      { key: 'orders', title: '今日订单', sub: '', value: summary.value.ordersToday },
      { key: 'sales', title: '今日销售额', sub: '', value: `¥ ${formatMoney(summary.value.salesToday)}` },
      { key: 'avg', title: '客单价', sub: '', value: `¥ ${formatMoney(summary.value.avgOrderValue)}` }
    ];
  } catch (e) {
    console.error(e);
    ElMessage.error('加载摘要失败');
  }
}

async function loadTrend() {
  try {
    const days = trendDays.value;
    const res = await api.get(`/dashboard/trend?days=${days}`);
    const data = res.data || [];
    // x: labels, series: orders & sales
    const labels = data.map(it => it.day);
    const orders = data.map(it => it.orderCount);
    const sales = data.map(it => Number(it.sales || 0));
    renderTrendChart(labels, orders, sales);
  } catch (e) {
    console.error(e);
    ElMessage.error('加载趋势失败');
  }
}

function renderTrendChart(labels, orders, sales) {
  nextTick(() => {
    if (!trendChartRef.value) return;
    if (!trendChartInstance) {
      trendChartInstance = echarts.init(trendChartRef.value);
    }
    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['订单数', '销售额'] },
      grid: { left: '6%', right: '6%', top: '10%', bottom: '10%' },
      xAxis: { type: 'category', data: labels, boundaryGap: true },
      yAxis: [
        { type: 'value', name: '订单数' },
        { type: 'value', name: '销售额', position: 'right', axisLabel: { formatter: v => '¥' + v } }
      ],
      series: [
        { name: '订单数', type: 'bar', data: orders, yAxisIndex: 0 },
        { name: '销售额', type: 'line', data: sales, yAxisIndex: 1, smooth: true }
      ]
    };
    trendChartInstance.setOption(option);
  });
}

async function loadTopProducts() {
  try {
    const res = await api.get(`/dashboard/top-products?days=${topDays.value}&limit=${topLimit.value}`);
    topProducts.value = res.data || [];
  } catch (e) {
    console.error(e);
    ElMessage.error('加载商品排行失败');
  }
}

async function loadWarnings() {
  try {
    const res = await api.get('/dashboard/warnings/inventory?threshold=10');
    warnings.value = res.data || [];
  } catch (e) {
    console.error(e);
    ElMessage.error('加载库存预警失败');
  }
}

/* ---------- 导出 CSV（商品排行） ---------- */
function exportTopProductsCsv() {
  if (!topProducts.value || topProducts.value.length === 0) {
    ElMessage.info('没有数据可导出');
    return;
  }
  const header = ['商品ID', '商品名称', '销量', '销售额'];
  const rows = topProducts.value.map(r => [
    r.productId ?? '',
    r.productName ?? '',
    r.soldQuantity ?? 0,
    formatMoney(r.revenue ?? 0)
  ]);
  const csvContent = [header, ...rows].map(e => e.map(cell => `"${String(cell).replace(/"/g, '""')}"`).join(',')).join('\n');
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `top_products_${new Date().toISOString().slice(0,10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
  ElMessage.success('已生成 CSV 文件');
}

/* ---------- 操作：全部刷新 ---------- */
async function reloadAll() {
  await Promise.all([loadSummary(), loadTrend(), loadTopProducts(), loadWarnings()]);
}

/* ---------- 生命周期 ---------- */
onMounted(async () => {
  await reloadAll();
  window.addEventListener('resize', () => { if (trendChartInstance) trendChartInstance.resize(); });
});

</script>

<style scoped>
.dashboard-page {
  padding: 16px;
}
.overview-row { margin-bottom: 8px; }
.overview-card {
  min-height: 110px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.card-header {
  display:flex;
  justify-content:space-between;
  align-items:center;
}
.card-title { font-size:14px; color:#666; }
.card-sub { font-size:12px; color:#999; }
.card-value { font-size:22px; font-weight:700; margin-top:8px; }
.trend-chart { height: 340px; width: 100%; margin-top: 12px; }
.card-toolbar { display:flex; justify-content:space-between; align-items:center; margin-bottom:8px; }
.no-warning { margin-top:12px; color:#999; text-align:center; }
</style>
