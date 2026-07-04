import { get, post, del } from '@/utils/request'
import type { VideoInfo, PageParams, PageResult } from '@/types'

export function getVideoList(params: PageParams) {
  return get<PageResult<VideoInfo>>('/admin/videos', params as Record<string, unknown>)
}

export function getVideoDetail(id: number) {
  return get<VideoInfo>(`/admin/videos/${id}`)
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
