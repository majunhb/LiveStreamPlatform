import { shortVideos, mockDelay } from '@/mock/data'

export async function getVideoList(page = 1, pageSize = 15) {
  const start = (page - 1) * pageSize
  const result = shortVideos.slice(start, start + pageSize)
  const data = await mockDelay({
    list: result,
    total: shortVideos.length,
    page,
    pageSize
  })
  return {
    code: 200,
    message: 'success',
    data
  }
}

export async function getVideoDetail(videoId: string) {
  const video = shortVideos.find(v => v.id === videoId) || shortVideos[0]
  const result = await mockDelay({ ...video })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function likeVideo(videoId: string) {
  const result = await mockDelay({ success: true, likes: 1 })
  return {
    code: 200,
    message: '点赞成功',
    data: result
  }
}

export async function unlikeVideo(videoId: string) {
  const result = await mockDelay({ success: true, likes: -1 })
  return {
    code: 200,
    message: '取消点赞',
    data: result
  }
}

export async function shareVideo(videoId: string) {
  const result = await mockDelay({ success: true })
  return {
    code: 200,
    message: '分享成功',
    data: result
  }
}

export async function searchVideos(keyword: string) {
  const result = shortVideos.filter(v =>
    v.title.includes(keyword) || v.author.nickname.includes(keyword)
  )
  const data = await mockDelay(result)
  return {
    code: 200,
    message: 'success',
    data
  }
}
