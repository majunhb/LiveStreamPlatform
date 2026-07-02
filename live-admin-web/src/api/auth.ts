import { get, post } from '@/utils/request'

export function login(data: { username: string; password: string }) {
  return post('/admin/auth/login', data)
}

export function getAdminInfo() {
  return get('/admin/auth/info')
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return post('/admin/auth/update-password', data)
}
