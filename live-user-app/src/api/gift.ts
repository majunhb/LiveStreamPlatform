import { gifts, giftRecords, mockDelay } from '@/mock/data'
import type { GiftRecord } from '@/mock/data'

export async function getGiftList() {
  const result = await mockDelay(gifts)
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function sendGift(roomId: string, giftId: string, count: number) {
  const gift = gifts.find(g => g.id === giftId)
  if (!gift) {
    return {
      code: 400,
      message: '礼物不存在',
      data: null
    }
  }
  const record: GiftRecord = {
    id: String(Date.now()),
    userId: '1',
    username: '星际旅行者',
    giftId: gift.id,
    giftName: gift.name,
    giftIcon: gift.icon,
    count,
    timestamp: Date.now()
  }
  const result = await mockDelay({
    record,
    totalCoins: gift.price * count
  })
  return {
    code: 200,
    message: '赠送成功',
    data: result
  }
}

export async function getGiftRecords(roomId: string, page = 1, pageSize = 20) {
  const result = await mockDelay({
    list: giftRecords,
    total: giftRecords.length,
    page,
    pageSize
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}
