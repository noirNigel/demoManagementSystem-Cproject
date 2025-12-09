// src/api/system.js
// 这里假设你项目里有一个 axios 封装在 src/utils/request.js
// 如果你路径不一样（比如 utils/request/index.js），把下面这行改成自己的就行
import request from '../utils/request'

// ===== 员工管理 =====
export function getAdminList() {
    return request({
        url: '/api/system/admins',
        method: 'get'
    })
}

export function createAdmin(data) {
    return request({
        url: '/api/system/admins',
        method: 'post',
        data
    })
}

export function updateAdmin(id, data) {
    return request({
        url: `/api/system/admins/${id}`,
        method: 'put',
        data
    })
}

export function disableAdmin(id) {
    return request({
        url: `/api/system/admins/${id}`,
        method: 'delete'
    })
}

export function deleteAdmin(id) {
    return request({
        url: `/api/system/admins/${id}/remove`,
        method: 'delete'
    })
}

// ===== 门店管理 =====
export function getStoreList() {
    return request({
        url: '/api/system/stores',
        method: 'get'
    })
}

export function getStoreDetail(id) {
    return request({
        url: `/api/system/stores/${id}`,
        method: 'get'
    })
}

export function createStore(data) {
    return request({
        url: '/api/system/stores',
        method: 'post',
        data
    })
}

export function updateStore(id, data) {
    return request({
        url: `/api/system/stores/${id}`,
        method: 'put',
        data
    })
}

export function deleteStore(id) {
    return request({
        url: `/api/system/stores/${id}`,
        method: 'delete'
    })
}

// ===== 系统配置 =====
export function getConfigList() {
    return request({
        url: '/api/system/config',
        method: 'get'
    })
}

export function getConfigByKey(key) {
    return request({
        url: `/api/system/config/${key}`,
        method: 'get'
    })
}

export function saveConfig(data) {
    return request({
        url: '/api/system/config',
        method: 'post',
        data
    })
}

// ===== 操作日志 =====
export function getOperationLogList() {
    return request({
        url: '/api/system/logs',
        method: 'get'
    })
}

// ===== 备份导出（可选）=====
export function exportBackup() {
    return request({
        url: '/api/system/backup/export',
        method: 'get'
    })
}
