import { mockDelay, getRandomDanmaku, getRandomColor, getRandomUser } from '@/mock/data'
import type { DanmakuMessage } from '@/mock/data'

export async function getDanmakuList(roomId: string) {
  const list: DanmakuMessage[] = []
  for (let i = 0; i < 20; i++) {
    const user = getRandomUser()
    list.push({
      id: String(Date.now() + i),
      userId: user.id,
      username: user.nickname,
      content: getRandomDanmaku(),
      color: getRandomColor()
    })
  }
  const result = await mockDelay(list)
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function sendDanmaku(roomId: string, content: string, color?: string) {
  const user = getRandomUser()
  const result = await mockDelay({
    id: String(Date.now()),
    userId: user.id,
    username: user.nickname,
    content,
    color: color || '#FFFFFF'
  })
  return {
    code: 200,
    message: '发送成功',
    data: result
  }
}
