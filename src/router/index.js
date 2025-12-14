import { createRouter, createWebHistory } from 'vue-router';

// Dashboard
const Dashboard = () => import('@/view/dashboard/Dashboard.vue');

// 商品模块
const ProductList = () => import('@/view/product/ProductList.vue');
const ProductForm = () => import('@/view/product/ProductForm.vue');
const InventoryList = () => import('@/view/product/InventoryList.vue');
const CategoryList = () => import('@/view/product/CategoryList.vue');

// 订单模块
const OrderList = () => import('@/view/order/OrderList.vue');
const OrderDetail = () => import('@/view/order/OrderDetail.vue');
const RefundList = () => import('@/view/order/RefundList.vue');
const PrintSetting = () => import('@/view/order/PrintSetting.vue');

// 营销模块（你已有的）
const Marketing = () => import('@/view/marketing/Marketing.vue');
const CouponList = () => import('@/view/marketing/CouponList.vue');
const CouponForm = () => import('@/view/marketing/CouponForm.vue');
const MemberLevelList = () => import('@/view/marketing/MemberLevelList.vue');
const PromotionList = () => import('@/view/marketing/PromotionList.vue');
const PromotionForm = () => import('@/view/marketing/PromotionForm.vue');
const PointsMall = () => import('@/view/marketing/PointsMall.vue');
const PointsExchange = () => import('@/view/marketing/PointsExchange.vue');
const BannerList = () => import('@/view/marketing/BannerList.vue');

// 登录页（视项目实现而定，若路径不同请改）
const Login = () => import('@/view/Login.vue');


// ★ 新增引入
import AdminList from '../view/system/AdminList.vue'
import StoreList from '../view/system/StoreList.vue'
import ConfigList from '../view/system/ConfigList.vue'
import OperationLogList from '../view/system/OperationLogList.vue'



const routes = [
    { path: '/login', name: 'Login', component: Login },

    // 仪表盘（直接作为 /admin/dashboard）
    { path: '/admin/dashboard', name: 'Dashboard', component: Dashboard, meta: { requiresAuth: true, title: '仪表盘' } },

    // 商品管理
    { path: '/admin/products', name: 'ProductList', component: ProductList, meta: { requiresAuth: true, title: '商品列表' } },
    { path: '/admin/products/add', name: 'ProductAdd', component: ProductForm, meta: { requiresAuth: true, title: '添加商品' } },
    { path: '/admin/products/edit/:id', name: 'ProductEdit', component: ProductForm, meta: { requiresAuth: true, title: '编辑商品' } },
    { path: '/admin/inventory', name: 'InventoryList', component: InventoryList, meta: { requiresAuth: true, title: '库存管理' } },
    { path: '/admin/categories', name: 'CategoryList', component: CategoryList, meta: { requiresAuth: true, title: '分类管理' } },

    // 订单管理
    { path: '/admin/orders', name: 'OrderList', component: OrderList, meta: { requiresAuth: true, title: '订单列表' } },
    { path: '/admin/orders/:id', name: 'OrderDetail', component: OrderDetail, meta: { requiresAuth: true, title: '订单详情' } },
    { path: '/admin/refunds', name: 'RefundList', component: RefundList, meta: { requiresAuth: true, title: '售后处理' } },
    { path: '/admin/print-settings', name: 'PrintSetting', component: PrintSetting, meta: { requiresAuth: true, title: '打印设置' } },

    // 营销：作为 /admin/marketing 的子路由（Marketing.vue 内部须包含 <router-view />）
    {
        path: '/admin/marketing',
        name: 'Marketing',
        component: Marketing,
        redirect: '/admin/marketing/coupons',
        meta: { requiresAuth: true, title: '营销活动' },
        children: [
            { path: 'coupons', name: 'CouponList', component: CouponList, meta: { requiresAuth: true, title: '优惠券管理' } },
            { path: 'coupons/add', name: 'CouponAdd', component: CouponForm, meta: { requiresAuth: true, title: '添加优惠券' } },
            { path: 'coupons/edit/:id', name: 'CouponEdit', component: CouponForm, meta: { requiresAuth: true, title: '编辑优惠券' } },

            { path: 'member-levels', name: 'MemberLevelList', component: MemberLevelList, meta: { requiresAuth: true, title: '会员等级' } },

            { path: 'banners', name: 'BannerList', component: BannerList, meta: { requiresAuth: true, title: '轮播图管理' } },

            { path: 'promotions', name: 'PromotionList', component: PromotionList, meta: { requiresAuth: true, title: '促销活动' } },
            { path: 'promotions/add', name: 'PromotionAdd', component: PromotionForm, meta: { requiresAuth: true, title: '添加促销活动' } },
            { path: 'promotions/edit/:id', name: 'PromotionEdit', component: PromotionForm, meta: { requiresAuth: true, title: '编辑促销活动' } },

            { path: 'points-mall', name: 'PointsMall', component: PointsMall, meta: { requiresAuth: true, title: '积分商城' } },
            { path: 'points-exchange', name: 'PointsExchange', component: PointsExchange, meta: { requiresAuth: true, title: '积分兑换记录' } }
        ]
    },

    // 兜底：未匹配路由跳转到仪表盘（可改为 /login）
    { path: '/', redirect: '/admin/dashboard' },
    { path: '/:pathMatch(.*)*', redirect: '/admin/dashboard' },

    {
        path: '/system/admins',
        component: AdminList,
        meta: { title: '员工管理' }
    },
    {
        path: '/system/stores',
        component: StoreList,
        meta: { title: '门店管理' }
    },
    {
        path: '/system/config',
        component: ConfigList,
        meta: { title: '系统设置' }
    },
    {
        path: '/system/logs',
        component: OperationLogList,
        meta: { title: '操作日志' }
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
