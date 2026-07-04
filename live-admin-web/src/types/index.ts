/** 通用API响应结构 */
export interface ApiResponse<T = unknown> {
  code: number
  data: T
  message: string
}

/** 分页请求参数 */
export interface PageParams {
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | undefined
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

/** 管理员信息 */
export interface AdminInfo {
  id: number
  username: string
  nickname: string
  role: string
  avatar?: string
  createTime?: string
}

/** 管理员登录响应 */
export interface LoginResult {
  token: string
  admin: AdminInfo
  roles: string[]
}

/** 管理员信息响应 */
export interface AdminInfoResult {
  admin: AdminInfo
  roles: string[]
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  phone?: string
  avatar?: string
  status: number
  userType: number
  role: string
  coinBalance: number
  lastLoginTime?: string
  createTime?: string
}

/** 礼物信息 */
export interface GiftInfo {
  id: number
  name: string
  icon?: string
  image?: string
  price: number
  sort: number
  status: number
  description?: string
  createTime?: string
}

/** 直播间信息 */
export interface LiveRoomInfo {
  id: number
  userId: number
  title: string
  cover?: string
  status: number
  onlineCount: number
  startTime?: string
  endTime?: string
  createTime?: string
}

/** 视频信息 */
export interface VideoInfo {
  id: number
  userId: number
  title: string
  cover?: string
  videoUrl?: string
  duration?: number
  status: number
  viewCount: number
  likeCount: number
  commentCount: number
  createTime?: string
}

/** 提现记录 */
export interface WithdrawInfo {
  id: number
  userId: number
  amount: number
  status: number
  reason?: string
  createTime?: string
}

/** 钱包记录 */
export interface WalletRecordInfo {
  id: number
  userId: number
  type: number
  amount: number
  balance: number
  description?: string
  createTime?: string
}

/** 直播统计 */
export interface LiveStatistics {
  totalLiveRooms: number
  onlineLiveRooms: number
  totalViewers: number
  peakViewers: number
}
