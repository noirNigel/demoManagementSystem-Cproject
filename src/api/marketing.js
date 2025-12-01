import api from '@/utils/request'

// 优惠券相关API
export const couponApi = {
    // 获取优惠券列表
    getCoupons: (params) => api.get('/api/marketing/coupons', { params }),
    // 获取优惠券详情
    getCoupon: (id) => api.get(`/api/marketing/coupons/${id}`),
    // 创建优惠券
    createCoupon: (data) => api.post('/api/marketing/coupons', data),
    // 更新优惠券
    updateCoupon: (id, data) => api.put(`/api/marketing/coupons/${id}`, data),
    // 删除优惠券
    deleteCoupon: (id) => api.delete(`/api/marketing/coupons/${id}`),
    // 更新优惠券状态
    updateCouponStatus: (id, status) => api.put(`/api/marketing/coupons/${id}/status`, { status }),
    // 获取可领取的优惠券
    getAvailableCoupons: (userId) => api.get('/api/marketing/coupons/available', { params: { userId } }),
    // 领取优惠券
    claimCoupon: (data) => api.post('/api/marketing/coupons/claim', data),
    // 获取用户优惠券
    getUserCoupons: (userId, status) => api.get(`/api/marketing/coupons/user/${userId}`, { params: { status } }),
    // 使用优惠券
    useCoupon: (data) => api.post('/api/marketing/coupons/use', data),
    // 退回优惠券
    returnCoupon: (userCouponId) => api.post(`/api/marketing/coupons/return/${userCouponId}`)
}

// @/api/marketing.js
export const memberLevelApi = {
    // 获取会员等级列表
    getLevels: () => api.get('/api/marketing/member-levels'),

    // 获取会员等级详情
    getLevel: (id) => api.get(`/api/marketing/member-levels/${id}`),

    // 创建会员等级
    createLevel: (data) => api.post('/api/marketing/member-levels', data),

    // 更新会员等级
    updateLevel: (id, data) => api.put(`/api/marketing/member-levels/${id}`, data),

    // 删除会员等级
    deleteLevel: (id) => api.delete(`/api/marketing/member-levels/${id}`),

    // 更新会员等级状态
    updateLevelStatus: (id, status) => api.put(`/api/marketing/member-levels/${id}/status`, { status }),

    // 获取用户等级
    getUserLevel: (points) => api.get('/api/marketing/member-levels/user-level', { params: { points } }),

    // 计算会员价格
    calculateMemberPrice: (data) => api.post('/api/marketing/member-levels/calculate-price', data),

    // 计算获得积分
    calculateEarnedPoints: (data) => api.post('/api/marketing/member-levels/calculate-points', data)
}

// @/api/marketing.js
export const promotionApi = {
    // 获取促销活动列表
    getPromotions: () => api.get('/api/marketing/promotions'),

    // 获取促销活动详情
    getPromotion: (id) => api.get(`/api/marketing/promotions/${id}`),

    // 创建促销活动
    createPromotion: (data) => api.post('/api/marketing/promotions', data),

    // 更新促销活动
    updatePromotion: (id, data) => api.put(`/api/marketing/promotions/${id}`, data),

    // 删除促销活动
    deletePromotion: (id) => api.delete(`/api/marketing/promotions/${id}`),

    // 更新促销活动状态
    updatePromotionStatus: (id, status) => api.put(`/api/marketing/promotions/${id}/status`, { status }),

    // 获取进行中的促销活动
    getActivePromotions: () => api.get('/api/marketing/promotions/active'),

    // 根据类型获取促销活动
    getPromotionsByType: (type) => api.get(`/api/marketing/promotions/type/${type}`),

    // 计算促销价格
    calculatePromotionPrice: (data) => api.post('/api/marketing/promotions/calculate-price', data)
}
// 积分商城相关API
export const pointsMallApi = {
    // 获取积分商品列表
    getGoods: (params) => api.get('/api/marketing/points-mall/goods', { params }),
    // 获取积分商品详情
    getGoodsById: (id) => api.get(`/api/marketing/points-mall/goods/${id}`),
    // 创建积分商品
    createGoods: (data) => api.post('/api/marketing/points-mall/goods', data),
    // 更新积分商品
    updateGoods: (id, data) => api.put(`/api/marketing/points-mall/goods/${id}`, data),
    // 删除积分商品
    deleteGoods: (id) => api.delete(`/api/marketing/points-mall/goods/${id}`),
    // 更新积分商品状态
    updateGoodsStatus: (id, status) => api.put(`/api/marketing/points-mall/goods/${id}/status`, { status }),
    // 获取可兑换商品
    getAvailableGoods: () => api.get('/api/marketing/points-mall/goods/available'),
    // 兑换商品
    exchangeGoods: (data) => api.post('/api/marketing/points-mall/exchange', data),
    // 获取用户兑换记录
    getUserExchanges: (userId) => api.get(`/api/marketing/points-mall/exchange/user/${userId}`),
    // 更新兑换状态
    updateExchangeStatus: (exchangeId, status) => api.put(`/api/marketing/points-mall/exchange/${exchangeId}/status`, { status })
}

// 营销活动概览
export const marketingApi = {
    getOverview: () => api.get('/api/marketing/overview'),
    getDashboard: () => api.get('/api/marketing/dashboard')
}