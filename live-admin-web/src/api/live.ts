import { get, post, put } from '@/utils/request'
import type { LiveRoomInfo, LiveStatistics, PageParams, PageResult } from '@/types'

export function getLiveRoomList(params: PageParams) {
  return get<PageResult<LiveRoomInfo>>('/admin/live-rooms', params as Record<string, unknown>)
}

export function getLiveRoomDetail(id: number) {
  return get<LiveRoomInfo>(`/admin/live-rooms/${id}`)
}

export function forceStopLive(id: number, reason: string) {
  return post(`/admin/live-rooms/${id}/force-stop`, { reason })
}

export function updateLiveRoomStatus(id: number, status: number) {
  return put(`/admin/live-rooms/${id}/status`, { status })
}

export function getLiveStatistics(params: PageParams) {
  return get<LiveStatistics>('/admin/live-rooms/statistics', params as Record<string, unknown>)
}
