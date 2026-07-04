import { get, post } from '@/utils/request'
import type { LoginResult, AdminInfoResult } from '@/types'

export function login(data: { username: string; password: string }) {
  return post<LoginResult>('/admin/auth/login', data)
}

export function getAdminInfo() {
  return get<AdminInfoResult>('/admin/auth/info')
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return post('/admin/auth/update-password', data)
}
