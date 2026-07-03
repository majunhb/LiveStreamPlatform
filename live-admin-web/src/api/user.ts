import { get, post, put } from '@/utils/request'

export function getUserList(params: any) {
  return get('/admin/users', params)
}

export function getUserDetail(id: number) {
  return get(`/admin/users/${id}`)
}

export function updateUserStatus(id: number, status: number) {
  return put(`/admin/users/${id}/status`, { status })
}

export function banUser(id: number, reason: string) {
  return post(`/admin/users/${id}/ban`, { reason })
}

export function unbanUser(id: number) {
  return post(`/admin/users/${id}/unban`)
}
