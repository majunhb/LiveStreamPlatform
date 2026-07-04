import { get, post, put } from '@/utils/request'
import type { UserInfo, PageParams, PageResult } from '@/types'

export function getUserList(params: PageParams) {
  return get<PageResult<UserInfo>>('/admin/users', params as Record<string, unknown>)
}

export function getUserDetail(id: number) {
  return get<UserInfo>(`/admin/users/${id}`)
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
