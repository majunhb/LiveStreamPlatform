import { liveRooms, users, mockDelay, categories } from '@/mock/data'
import type { LiveRoom } from '@/mock/data'

export async function getLiveList(category?: string, page = 1, pageSize = 20) {
  let rooms = [...liveRooms]
  if (category && category !== '推荐' && category !== '关注') {
    rooms = rooms.filter(r => r.category === category)
  }
  const start = (page - 1) * pageSize
  const result = rooms.slice(start, start + pageSize)
  const data = await mockDelay({
    list: result,
    total: rooms.length,
    page,
    pageSize
  })
  return {
    code: 200,
    message: 'success',
    data
  }
}

export async function getLiveRoom(roomId: string) {
  const room = liveRooms.find(r => r.id === roomId) || liveRooms[0]
  const result = await mockDelay({ ...room })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getCategories() {
  const result = await mockDelay(categories)
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getFollowedLives() {
  const result = await mockDelay(liveRooms.slice(0, 4))
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function startLive(title: string, category: string) {
  const user = users[0]
  const newRoom: LiveRoom = {
    id: String(Date.now()),
    title,
    cover: 'linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%)',
    category,
    anchor: user,
    viewers: 0,
    likes: 0,
    isLive: true,
    startedAt: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    description: title
  }
  const result = await mockDelay(newRoom)
  return {
    code: 200,
    message: '开播成功',
    data: result
  }
}

export async function stopLive(roomId: string) {
  const result = await mockDelay({ success: true })
  return {
    code: 200,
    message: '下播成功',
    data: result
  }
}

export async function searchLives(keyword: string) {
  const result = liveRooms.filter(r =>
    r.title.includes(keyword) || r.anchor.nickname.includes(keyword) || r.category.includes(keyword)
  )
  const data = await mockDelay(result)
  return {
    code: 200,
    message: 'success',
    data
  }
}
