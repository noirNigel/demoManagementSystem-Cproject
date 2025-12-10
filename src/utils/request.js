import axios from "axios";
import { processFormTimestamps } from '@/utils/timestamp'

const instance = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 10000
});

// 请求拦截器
instance.interceptors.request.use(config => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers["Authorization"] = `Bearer ${token}`;
    }

    // 透传用户 ID，供需要 userId 的接口使用（例如优惠券公共接口）
    const userInfoRaw = localStorage.getItem('userInfo');
    if (userInfoRaw) {
        try {
            const userInfo = JSON.parse(userInfoRaw);
            if (userInfo?.userId) {
                config.headers['X-User-Id'] = userInfo.userId;
            }
        } catch (e) {
            console.warn('解析用户信息失败，跳过 X-User-Id 注入', e);
        }
    }

    // 处理 POST、PUT 请求中的数据
    if (config.data && (config.method === 'post' || config.method === 'put')) {
        console.log('请求拦截器 - 处理前数据:', config.data)
        config.data = processFormTimestamps(config.data)
        console.log('请求拦截器 - 处理后数据:', config.data)
    }

    // 处理 GET 请求的 params
    if (config.params) {
        console.log('请求拦截器 - 处理前 params:', config.params)
        config.params = processFormTimestamps(config.params)
        console.log('请求拦截器 - 处理后 params:', config.params)
    }

    return config;
});

// 响应拦截器
instance.interceptors.response.use(
    response => response.data,
    error => {
        console.error('API 请求错误:', error)
        return Promise.reject(error);
    }
);

export default instance;