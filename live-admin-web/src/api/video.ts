import { get, post, put, del } from '@/utils/request'

export function getVideoList(params: any) {
  return get('/admin/videos', params)
}

export function getVideoDetail(id: number) {
  return get(`/admin/videos/${id}`)
}

export function reviewVideo(id: number, status: number, reason?: string) {
  return post(`/admin/videos/${id}/review`, { status, reason })
}

export function deleteVideo(id: number) {
  return del(`/admin/videos/${id}`)
}

export function recommendVideo(id: number) {
  return post(`/admin/videos/${id}/recommend`)
}
