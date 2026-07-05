export interface User {
  id: number
  username: string
  nickname: string
  avatar: string
  phone: string
  email: string
  gender: number
  bio: string
  status: number
  userType: number
  coinBalance: number
  lastLoginTime: string
  createTime: string
}

export interface LiveRoom {
  id: number
  userId: number
  title: string
  coverUrl: string
  description: string
  playUrl: string
  status: number
  viewerCount: number
  totalViewerCount: number
  likeCount: number
  isRecommend: number
  startTime: string
  anchorName?: string
  anchorAvatar?: string
  category?: string
}

export interface ShortVideo {
  id: string
  userId: number
  title: string
  description: string
  videoUrl: string
  coverUrl: string
  duration: number
  playCount: number
  likeCount: number
  commentCount: number
  status: number
  createTime: string
  authorName?: string
  authorAvatar?: string
}

export interface Danmaku {
  id: string
  roomId: number
  userId: number
  username: string
  content: string
  color: string
  fontSize: number
  sendTime: string
}

export interface Gift {
  id: number
  name: string
  icon: string
  price: number
  description: string
  animationType?: string
}

export interface GiftRecord {
  id: number
  roomId: number
  fromUserId: number
  toUserId: number
  giftId: number
  giftName: string
  giftIcon: string
  count: number
  totalCoin: number
  createTime: string
  fromUserName?: string
  toUserName?: string
}

export interface Wallet {
  id: number
  userId: number
  balance: number
  totalRecharge: number
  totalConsume: number
  updateTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
}
