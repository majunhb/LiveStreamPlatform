import type {
  User,
  LiveRoom,
  ShortVideo,
  Danmaku,
  Gift,
  GiftRecord,
  Wallet,
  PageResult
} from '@/types'

// ============ Helpers ============
function delay(min = 300, max = 800): Promise<void> {
  const ms = Math.floor(Math.random() * (max - min)) + min
  return new Promise((resolve) => setTimeout(resolve, ms))
}

function nowISO(): string {
  return new Date().toISOString()
}

function rid(prefix = ''): string {
  return prefix + Math.random().toString(36).slice(2, 10) + Date.now().toString(36)
}

// Avatar gradients keyed by index
const avatarGradients = [
  'linear-gradient(135deg,#FF2D55,#AF52ED)',
  'linear-gradient(135deg,#00D4FF,#AF52ED)',
  'linear-gradient(135deg,#FFC24B,#FF2D55)',
  'linear-gradient(135deg,#AF52ED,#FF2D55)',
  'linear-gradient(135deg,#00D4FF,#FFC24B)',
  'linear-gradient(135deg,#FF6B9D,#AF52ED)',
  'linear-gradient(135deg,#5B8CFF,#00D4FF)',
  'linear-gradient(135deg,#FF2D55,#FF8A5B)'
]

export function avatarStyle(name?: string, idx = 0): string {
  return avatarGradients[idx % avatarGradients.length]
}

export function firstChar(name?: string): string {
  return (name || 'L').trim().charAt(0).toUpperCase()
}

// ============ Mock Users ============
const mockUsers: User[] = [
  {
    id: 1,
    username: 'demo',
    nickname: '星球漫游者',
    avatar: 'demo',
    phone: '138****8888',
    email: 'demo@live.cn',
    gender: 1,
    bio: '在 LIVE星球，每一秒都值得被点亮 ✨',
    status: 1,
    userType: 1,
    coinBalance: 8800,
    lastLoginTime: nowISO(),
    createTime: '2024-01-10T08:00:00.000Z'
  },
  {
    id: 101,
    username: 'anchor_game',
    nickname: '电竞小霸王',
    avatar: 'game',
    phone: '139****0001',
    email: 'game@live.cn',
    gender: 1,
    bio: '峡谷之巅，带你飞 ✈️',
    status: 1,
    userType: 2,
    coinBalance: 0,
    lastLoginTime: nowISO(),
    createTime: '2024-02-01T08:00:00.000Z'
  },
  {
    id: 102,
    username: 'anchor_music',
    nickname: '夜莺Luna',
    avatar: 'music',
    phone: '139****0002',
    email: 'luna@live.cn',
    gender: 2,
    bio: '深夜电台，治愈你的每一个失眠夜 🎶',
    status: 1,
    userType: 2,
    coinBalance: 0,
    lastLoginTime: nowISO(),
    createTime: '2024-02-05T08:00:00.000Z'
  },
  {
    id: 103,
    username: 'anchor_chat',
    nickname: '话痨阿杰',
    avatar: 'chat',
    phone: '139****0003',
    email: 'ajie@live.cn',
    gender: 1,
    bio: '陪你聊到天亮，主打一个真诚 ☕',
    status: 1,
    userType: 2,
    coinBalance: 0,
    lastLoginTime: nowISO(),
    createTime: '2024-02-10T08:00:00.000Z'
  },
  {
    id: 104,
    username: 'anchor_food',
    nickname: '深夜食堂·喵厨',
    avatar: 'food',
    phone: '139****0004',
    email: 'miao@live.cn',
    gender: 2,
    bio: '一人食也要好好吃饭 🍜',
    status: 1,
    userType: 2,
    coinBalance: 0,
    lastLoginTime: nowISO(),
    createTime: '2024-02-15T08:00:00.000Z'
  },
  {
    id: 105,
    username: 'anchor_dance',
    nickname: '舞姬·小鹿',
    avatar: 'dance',
    phone: '139****0005',
    email: 'lulu@live.cn',
    gender: 2,
    bio: '霓虹之下，每一帧都是舞台 💃',
    status: 1,
    userType: 2,
    coinBalance: 0,
    lastLoginTime: nowISO(),
    createTime: '2024-02-20T08:00:00.000Z'
  }
]

const anchors = mockUsers.filter((u) => u.userType === 2)

// ============ Mock Live Rooms ============
const coverGradients = [
  'linear-gradient(135deg,#FF2D55 0%,#AF52ED 50%,#00D4FF 100%)',
  'linear-gradient(135deg,#5B8CFF 0%,#AF52ED 100%)',
  'linear-gradient(135deg,#FFC24B 0%,#FF2D55 100%)',
  'linear-gradient(135deg,#00D4FF 0%,#5B8CFF 100%)',
  'linear-gradient(135deg,#FF6B9D 0%,#AF52ED 100%)',
  'linear-gradient(135deg,#FF8A5B 0%,#FF2D55 100%)',
  'linear-gradient(135deg,#AF52ED 0%,#FF6B9D 100%)',
  'linear-gradient(135deg,#00D4FF 0%,#FFC24B 100%)'
]

const roomSeeds: Array<{
  title: string
  desc: string
  category: string
  anchor: User
}> = [
  { title: '【王者荣耀】冲国服韩信，兄弟们冲鸭！', desc: '巅峰赛2000分实战教学，韩信打野思路全解析', category: '游戏', anchor: anchors[0] },
  { title: '深夜电台｜今夜，我们聊聊那些没说出口的话', desc: '治愈系陪伴，点歌互动，弹幕点歌第一优先', category: '音乐', anchor: anchors[1] },
  { title: '聊天房｜一个有温度的深夜树洞', desc: '工作吐槽/情感答疑/段子手在线营业', category: '聊天', anchor: anchors[2] },
  { title: '深夜食堂·一人食治愈料理', desc: '今天做一碗暖心的豚骨拉面 🍜', category: '美食', anchor: anchors[3] },
  { title: '霓虹舞房｜新舞蹈首跳，求不喷 🙈', desc: 'K-pop翻跳练习，跟着弹幕一起摇摆', category: '音乐', anchor: anchors[4] },
  { title: '原神 4.7 深渊满星实战，阵容搭配讲解', desc: '低配满星阵容演示，新手友好', category: '游戏', anchor: anchors[0] },
  { title: '钢琴弹唱｜周杰伦专场，青春的BGM', desc: '点歌台开启，弹幕点歌，今晚不睡了', category: '音乐', anchor: anchors[1] },
  { title: '情感连麦｜失恋了来找我，一杯酒的故事', desc: '匿名连麦，倾听你的故事', category: '聊天', anchor: anchors[2] },
  { title: '家常菜教学｜20分钟搞定三菜一汤', desc: '番茄炒蛋/可乐鸡翅/紫菜蛋花汤', category: '美食', anchor: anchors[3] },
  { title: '街舞Battle｜周五夜场热身，谁敢来战', desc: 'Popping/Hiphop 自由展示', category: '音乐', anchor: anchors[4] }
]

const mockRooms: LiveRoom[] = roomSeeds.map((seed, i) => ({
  id: i + 1,
  userId: seed.anchor.id,
  title: seed.title,
  coverUrl: `gradient:${i}`,
  description: seed.desc,
  playUrl: `mock://live/${i + 1}`,
  status: 1,
  viewerCount: 1200 + Math.floor(Math.random() * 80000),
  totalViewerCount: 50000 + Math.floor(Math.random() * 500000),
  likeCount: 800 + Math.floor(Math.random() * 60000),
  isRecommend: i < 4 ? 1 : 0,
  startTime: new Date(Date.now() - (1 + i) * 3600 * 1000).toISOString(),
  anchorName: seed.anchor.nickname,
  anchorAvatar: seed.anchor.avatar,
  category: seed.category
}))

// ============ Mock Short Videos ============
const videoSeeds: Array<{
  title: string
  desc: string
  author: User
  duration: number
}> = [
  { title: '这个韩信连招，我练了三天三夜', desc: '#王者荣耀 #韩信 #打野教学', author: anchors[0], duration: 32 },
  { title: '翻唱《晴天》｜窗外雨下得好大', desc: '#周杰伦 #翻唱 #治愈', author: anchors[1], duration: 58 },
  { title: '凌晨三点的城市，比你想象的温柔', desc: '#城市夜景 #航拍 #vlog', author: anchors[1], duration: 45 },
  { title: '30秒学会番茄炒蛋，新手零失败', desc: '#家常菜 #快手菜 #教程', author: anchors[3], duration: 30 },
  { title: 'K-pop翻跳｜Queencard 慢动作分解', desc: '#翻跳 #Kpop #舞蹈', author: anchors[4], duration: 41 },
  { title: '深夜树洞｜那些成年人才懂的疲惫', desc: '#情感 #深夜 #治愈', author: anchors[2], duration: 67 },
  { title: '一人食｜这碗豚骨拉面治愈了我', desc: '#美食 #拉面 #一人食', author: anchors[3], duration: 38 },
  { title: '原神深渊 12层 三星速通', desc: '#原神 #深渊 #攻略', author: anchors[0], duration: 52 },
  { title: '钢琴弹唱《稻香》｜童年回来了', desc: '#周杰伦 #钢琴 #弹唱', author: anchors[1], duration: 49 },
  { title: '街舞Freestyle｜炸场现场', desc: '#街舞 #Popping #Battle', author: anchors[4], duration: 35 },
  { title: '关于"内卷"，我想说几句真话', desc: '#职场 #思考 #脱口秀', author: anchors[2], duration: 72 },
  { title: '可乐鸡翅秘方大公开，超下饭', desc: '#家常菜 #鸡翅 #教程', author: anchors[3], duration: 28 }
]

const mockVideos: ShortVideo[] = videoSeeds.map((seed, i) => ({
  id: `v${i + 1}`,
  userId: seed.author.id,
  title: seed.title,
  description: seed.desc,
  videoUrl: `mock://video/${i + 1}`,
  coverUrl: `gradient:${i}`,
  duration: seed.duration,
  playCount: 10000 + Math.floor(Math.random() * 900000),
  likeCount: 500 + Math.floor(Math.random() * 80000),
  commentCount: 50 + Math.floor(Math.random() * 5000),
  status: 1,
  createTime: new Date(Date.now() - i * 86400 * 1000).toISOString(),
  authorName: seed.author.nickname,
  authorAvatar: seed.author.avatar
}))

// ============ Mock Gifts ============
const mockGifts: Gift[] = [
  { id: 1, name: '玫瑰', icon: '🌹', price: 1, description: '一朵小红花，送给喜欢的人', animationType: 'fade' },
  { id: 2, name: '爱心', icon: '❤️', price: 5, description: '比心比心，满满的爱', animationType: 'fade' },
  { id: 3, name: '棒棒糖', icon: '🍭', price: 10, description: '甜甜的童年味道', animationType: 'fade' },
  { id: 4, name: '蛋糕', icon: '🎂', price: 50, description: '生日快乐，许个愿吧', animationType: 'fade' },
  { id: 5, name: '跑车', icon: '🏎️', price: 100, description: '风驰电掣，排面拉满', animationType: 'fly' },
  { id: 6, name: '飞机', icon: '✈️', price: 200, description: '起飞！全屏起飞', animationType: 'fly' },
  { id: 7, name: '火箭', icon: '🚀', price: 500, description: '冲向宇宙，浩瀚无垠', animationType: 'rocket' },
  { id: 8, name: '嘉年华', icon: '🎪', price: 1000, description: '顶级盛典，全服闪耀', animationType: 'carnival' }
]

// ============ Mock Danmaku pool ============
const danmakuPool = [
  '主播好帅！', '666', '这操作绝了', '哈哈哈哈哈', '主播来首晴天呗',
  '前排打卡', '宝藏主播', '已三连', '关注了关注了', '主播声音真好听',
  '泪目了', '这波细节满分', '萌新刚进，发生甚么了', '冲冲冲', '主播下播早点休息',
  '这画质爱了', '弹幕护体', '6666666', '主播加油', '今天的妆好好看',
  '这局稳了', '哇！这操作', '我直接一个关注', '深夜档氛围感拉满', '主播什么时候下播呀'
]
const danmakuColors = ['#FFFFFF', '#FFC24B', '#00D4FF', '#FF6B9D', '#AF52ED', '#7CFFB2']
const danmakuUsers = ['夜行的猫', '糖醋排骨', '今天也要元气', '一只柴犬', 'JetBrainsBoy', '不吃香菜', '深海鱼', '阿巴阿巴', '柠檬精本精', '佛系观众']

// gift records
const mockGiftRecords: GiftRecord[] = [
  {
    id: 1, roomId: 1, fromUserId: 201, toUserId: 101, giftId: 1, giftName: '玫瑰',
    giftIcon: '🌹', count: 9, totalCoin: 9, createTime: nowISO(),
    fromUserName: '糖醋排骨', toUserName: '电竞小霸王'
  },
  {
    id: 2, roomId: 1, fromUserId: 202, toUserId: 101, giftId: 7, giftName: '火箭',
    giftIcon: '🚀', count: 1, totalCoin: 500, createTime: nowISO(),
    fromUserName: '今天也要元气', toUserName: '电竞小霸王'
  },
  {
    id: 3, roomId: 2, fromUserId: 203, toUserId: 102, giftId: 8, giftName: '嘉年华',
    giftIcon: '🎪', count: 1, totalCoin: 1000, createTime: nowISO(),
    fromUserName: '深海鱼', toUserName: '夜莺Luna'
  }
]

// wallet cache (in-memory across calls in a session)
let sessionWallet: Wallet = {
  id: 1,
  userId: 1,
  balance: 8800,
  totalRecharge: 10000,
  totalConsume: 1200,
  updateTime: nowISO()
}

// token <-> user map (simulate account registry)
const accountRegistry: Record<string, { password: string; user: User }> = {
  demo: { password: '123456', user: mockUsers[0] }
}

// ============ API ============
export const api = {
  async login(username: string, password: string): Promise<{ token: string; user: User }> {
    await delay()
    const account = accountRegistry[username.trim()]
    if (!account || account.password !== password) {
      // allow any non-empty password for demo convenience? No — stick to demo credentials.
      if (username.trim() === 'demo' && password === '123456') {
        // fallback
      } else {
        throw new Error('用户名或密码错误，可使用 demo / 123456 体验')
      }
    }
    const user = account?.user ?? mockUsers[0]
    const u: User = { ...user, lastLoginTime: nowISO() }
    sessionWallet = { ...sessionWallet, userId: u.id }
    return { token: rid('tok_'), user: u }
  },

  async register(username: string, password: string): Promise<{ token: string; user: User }> {
    await delay()
    if (!username.trim() || password.length < 6) {
      throw new Error('用户名不能为空，密码至少 6 位')
    }
    if (accountRegistry[username.trim()]) {
      throw new Error('该用户名已被注册')
    }
    const newUser: User = {
      id: Math.floor(Math.random() * 10000) + 5000,
      username: username.trim(),
      nickname: username.trim(),
      avatar: username.trim(),
      phone: '138****' + Math.floor(1000 + Math.random() * 9000),
      email: `${username.trim()}@live.cn`,
      gender: 0,
      bio: ' LIVE星球的新居民 ✨',
      status: 1,
      userType: 1,
      coinBalance: 1000,
      lastLoginTime: nowISO(),
      createTime: nowISO()
    }
    accountRegistry[username.trim()] = { password, user: newUser }
    sessionWallet = {
      id: Math.floor(Math.random() * 1000),
      userId: newUser.id,
      balance: 1000,
      totalRecharge: 1000,
      totalConsume: 0,
      updateTime: nowISO()
    }
    return { token: rid('tok_'), user: newUser }
  },

  async guestLogin(): Promise<{ token: string; user: User }> {
    await delay(200, 400)
    const account = accountRegistry['demo']
    const user: User = { ...(account?.user ?? mockUsers[0]), lastLoginTime: nowISO() }
    sessionWallet = { ...sessionWallet, userId: user.id }
    return { token: rid('tok_'), user }
  },

  async getLivingRooms(page = 1, size = 10, category?: string): Promise<PageResult<LiveRoom>> {
    await delay()
    let list = [...mockRooms]
    if (category && category !== '推荐') {
      list = list.filter((r) => r.category === category)
    }
    // sort recommend first then by viewer count
    list.sort((a, b) => b.isRecommend - a.isRecommend || b.viewerCount - a.viewerCount)
    const total = list.length
    const start = (page - 1) * size
    const records = list.slice(start, start + size).map((r) => ({
      ...r,
      viewerCount: r.viewerCount + Math.floor(Math.random() * 200)
    }))
    return { records, total }
  },

  async getRoomDetail(roomId: number): Promise<LiveRoom> {
    await delay()
    const room = mockRooms.find((r) => r.id === roomId)
    if (!room) throw new Error('直播间不存在')
    return { ...room, viewerCount: room.viewerCount + Math.floor(Math.random() * 500) }
  },

  async getRecommendFeed(page = 1, size = 8): Promise<PageResult<ShortVideo>> {
    await delay()
    const shuffled = [...mockVideos].sort(() => Math.random() - 0.5)
    const total = shuffled.length
    const start = (page - 1) * size
    const records = shuffled.slice(start, start + size)
    return { records, total }
  },

  async getHotVideos(page = 1, size = 8): Promise<PageResult<ShortVideo>> {
    await delay()
    const sorted = [...mockVideos].sort((a, b) => b.playCount - a.playCount)
    const total = sorted.length
    const start = (page - 1) * size
    const records = sorted.slice(start, start + size)
    return { records, total }
  },

  async getRoomDanmakus(roomId: number): Promise<Danmaku[]> {
    await delay(200, 500)
    const count = 16
    const list: Danmaku[] = Array.from({ length: count }, (_, i) => ({
      id: rid('dm_'),
      roomId,
      userId: 300 + i,
      username: danmakuUsers[i % danmakuUsers.length],
      content: danmakuPool[Math.floor(Math.random() * danmakuPool.length)],
      color: danmakuColors[Math.floor(Math.random() * danmakuColors.length)],
      fontSize: 14 + Math.floor(Math.random() * 3) * 2,
      sendTime: new Date(Date.now() - (count - i) * 1500).toISOString()
    }))
    return list
  },

  async sendDanmaku(roomId: number, content: string): Promise<Danmaku> {
    await delay(100, 250)
    return {
      id: rid('dm_'),
      roomId,
      userId: 1,
      username: '我',
      content,
      color: '#FFFFFF',
      fontSize: 16,
      sendTime: nowISO()
    }
  },

  async getGiftList(): Promise<Gift[]> {
    await delay(200, 400)
    return [...mockGifts]
  },

  async sendGift(
    roomId: number,
    receiverId: number,
    giftId: number,
    quantity: number
  ): Promise<GiftRecord> {
    await delay(200, 500)
    const gift = mockGifts.find((g) => g.id === giftId)
    if (!gift) throw new Error('礼物不存在')
    const receiver = mockRooms.find((r) => r.id === roomId)
    const record: GiftRecord = {
      id: Math.floor(Math.random() * 100000),
      roomId,
      fromUserId: 1,
      toUserId: receiverId,
      giftId,
      giftName: gift.name,
      giftIcon: gift.icon,
      count: quantity,
      totalCoin: gift.price * quantity,
      createTime: nowISO(),
      fromUserName: '我',
      toUserName: receiver?.anchorName || '主播'
    }
    mockGiftRecords.unshift(record)
    return record
  },

  async getRoomGiftRecords(roomId: number): Promise<GiftRecord[]> {
    await delay(200, 400)
    const roomRecords = mockGiftRecords.filter((r) => r.roomId === roomId)
    return roomRecords.length ? roomRecords : mockGiftRecords.slice(0, 3)
  },

  async getWallet(): Promise<Wallet> {
    await delay(200, 400)
    return { ...sessionWallet, updateTime: nowISO() }
  },

  async recharge(amount: number): Promise<Wallet> {
    await delay(300, 600)
    if (amount <= 0) throw new Error('充值金额需大于 0')
    sessionWallet = {
      ...sessionWallet,
      balance: sessionWallet.balance + amount,
      totalRecharge: sessionWallet.totalRecharge + amount,
      updateTime: nowISO()
    }
    return { ...sessionWallet }
  },

  async getUserInfo(): Promise<User> {
    await delay(200, 400)
    return { ...mockUsers[0] }
  },

  async getMyVideos(): Promise<ShortVideo[]> {
    await delay(300, 500)
    // simulate "my" uploads as 3 sample videos
    return mockVideos.slice(0, 3).map((v) => ({ ...v, authorName: '我', authorAvatar: 'me' }))
  }
}

// expose generators for the live room (auto danmaku)
export function makeAutoDanmaku(roomId: number): Danmaku {
  return {
    id: rid('dm_'),
    roomId,
    userId: 300 + Math.floor(Math.random() * 200),
    username: danmakuUsers[Math.floor(Math.random() * danmakuUsers.length)],
    content: danmakuPool[Math.floor(Math.random() * danmakuPool.length)],
    color: danmakuColors[Math.floor(Math.random() * danmakuColors.length)],
    fontSize: 14 + Math.floor(Math.random() * 3) * 2,
    sendTime: nowISO()
  }
}

export { coverGradients, avatarGradients }
