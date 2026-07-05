import { users, messages, shortVideos, mockDelay } from '@/mock/data'

export async function getUserProfile(userId: string) {
  const user = users.find(u => u.id === userId) || users[0]
  const result = await mockDelay({ ...user })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function updateProfile(data: { nickname?: string; bio?: string; avatar?: string }) {
  const user = { ...users[0], ...data }
  const result = await mockDelay(user)
  return {
    code: 200,
    message: '更新成功',
    data: result
  }
}

export async function followUser(userId: string) {
  const result = await mockDelay({ success: true, following: true })
  return {
    code: 200,
    message: '关注成功',
    data: result
  }
}

export async function unfollowUser(userId: string) {
  const result = await mockDelay({ success: true, following: false })
  return {
    code: 200,
    message: '取消关注',
    data: result
  }
}

export async function getFollowList(userId: string, page = 1) {
  const result = await mockDelay({
    list: users.slice(0, 5),
    total: users.length,
    page
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getFansList(userId: string, page = 1) {
  const result = await mockDelay({
    list: users.slice(2, 7),
    total: 100,
    page
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getMessages(type: 'private' | 'system' | 'gift' = 'private') {
  const filtered = messages.filter(m => m.type === type)
  const result = await mockDelay(filtered)
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getMyVideos(page = 1) {
  const result = await mockDelay({
    list: shortVideos.slice(0, 4),
    total: 4,
    page
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getLiveHistory(page = 1) {
  const history = [
    { id: '1', title: '深夜聊天', duration: '2小时30分', viewers: 6780, date: '2024-01-15', gifts: 128 },
    { id: '2', title: '唱歌直播', duration: '1小时45分', viewers: 12500, date: '2024-01-14', gifts: 356 },
    { id: '3', title: '游戏互动', duration: '3小时10分', viewers: 8900, date: '2024-01-13', gifts: 245 }
  ]
  const result = await mockDelay({
    list: history,
    total: history.length,
    page
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getAnchorStats() {
  const result = await mockDelay({
    totalViewers: 56780,
    totalDuration: 128,
    totalGifts: 2580,
    totalLikes: 98560
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}
