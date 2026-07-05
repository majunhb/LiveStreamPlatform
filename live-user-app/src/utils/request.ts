import { mockDelay } from '@/mock/data'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
  mock?: boolean
  mockData?: any
}

interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export async function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  const { url, method = 'GET', data, header = {}, mock = true, mockData } = options

  if (mock && mockData !== undefined) {
    const result = await mockDelay(mockData)
    return {
      code: 200,
      message: 'success',
      data: result
    }
  }

  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    uni.request({
      url,
      method,
      data,
      header,
      success: (res: any) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(res.data)
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

export default request
