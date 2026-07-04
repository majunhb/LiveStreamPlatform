import axios, { type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from '@/utils/auth'
import router from '@/router'

/** 通用API响应结构 */
export interface ApiResponse<T = unknown> {
  code: number
  data: T
  message: string
}

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: unknown) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      // Token 过期
      if (res.code === 401) {
        clearAuth()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res as any
  },
  (error: { response?: { status: number }; message?: string }) => {
    const status = error.response?.status
    if (status === 401) {
      clearAuth()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else if (status === 403) {
      ElMessage.error('没有权限访问')
    } else if (status === 500) {
      ElMessage.error('服务器错误')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service

// 便捷方法
export function get<T = unknown>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return service.get(url, { params }) as Promise<ApiResponse<T>>
}

export function post<T = unknown>(url: string, data?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return service.post(url, data) as Promise<ApiResponse<T>>
}

export function put<T = unknown>(url: string, data?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return service.put(url, data) as Promise<ApiResponse<T>>
}

export function del<T = unknown>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return service.delete(url, { params }) as Promise<ApiResponse<T>>
}
