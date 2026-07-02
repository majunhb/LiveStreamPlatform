import { get, post, put } from '@/utils/request'

export function getLiveRoomList(params: any) {
  return get('/admin/live-rooms', params)
}

export function getLiveRoomDetail(id: number) {
  return get(`/admin/live-rooms/${id}`)
}

export function forceStopLive(id: number, reason: string) {
  return post(`/admin/live-rooms/${id}/force-stop`, { reason })
}

export function updateLiveRoomStatus(id: number, status: number) {
  return put(`/admin/live-rooms/${id}/status`, { status })
}

export function getLiveStatistics(params: any) {
  return get('/admin/live-rooms/statistics', params)
}
