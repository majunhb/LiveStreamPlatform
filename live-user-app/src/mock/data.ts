export interface User {
  id: string
  username: string
  nickname: string
  avatar: string
  bio: string
  level: number
  followers: number
  following: number
  likes: number
  isAnchor: boolean
  phone?: string
}

export interface LiveRoom {
  id: string
  title: string
  cover: string
  category: string
  anchor: User
  viewers: number
  likes: number
  isLive: boolean
  startedAt: string
  description: string
}

export interface ShortVideo {
  id: string
  title: string
  cover: string
  author: User
  likes: number
  comments: number
  shares: number
  music: string
  duration: number
  isLiked: boolean
}

export interface Gift {
  id: string
  name: string
  icon: string
  price: number
  animation?: string
}

export interface DanmakuMessage {
  id: string
  userId: string
  username: string
  content: string
  color?: string
}

export interface GiftRecord {
  id: string
  userId: string
  username: string
  giftId: string
  giftName: string
  giftIcon: string
  count: number
  timestamp: number
}

export interface MessageItem {
  id: string
  type: 'private' | 'system' | 'gift'
  from: User
  content: string
  time: string
  unread: number
}

export interface VideoComment {
  id: string
  user: User
  content: string
  likes: number
  isLiked: boolean
  time: string
  replies?: VideoComment[]
  replyCount: number
}

export interface TransactionRecord {
  id: string
  type: 'recharge' | 'spend'
  amount: number
  coins: number
  description: string
  time: string
}

export const categories = ['关注', '推荐', '游戏', '音乐', '聊天', '美食', '户外', '颜值']

const gradients = [
  'linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%)',
  'linear-gradient(135deg, #00D4FF 0%, #AF52ED 100%)',
  'linear-gradient(135deg, #FF6B6B 0%, #FFE66D 100%)',
  'linear-gradient(135deg, #4ECDC4 0%, #44A08D 100%)',
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
  'linear-gradient(135deg, #5ee7df 0%, #b490ca 100%)',
  'linear-gradient(135deg, #d299c2 0%, #fef9d7 100%)',
  'linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%)'
]

export const users: User[] = [
  { id: '1', username: 'demo', nickname: '星际旅行者', avatar: '', bio: '热爱生活，热爱直播 🌟', level: 15, followers: 12580, following: 328, likes: 98765, isAnchor: true, phone: '138****8888' },
  { id: '2', username: 'anchor1', nickname: '小悦儿🎵', avatar: '', bio: '每晚8点唱歌给你听 🎤', level: 42, followers: 568900, following: 125, likes: 3200000, isAnchor: true },
  { id: '3', username: 'anchor2', nickname: '游戏达人阿峰', avatar: '', bio: '王者荣耀国服选手 🏆', level: 38, followers: 425600, following: 289, likes: 1850000, isAnchor: true },
  { id: '4', username: 'anchor3', nickname: '吃货小美🍜', avatar: '', bio: '吃遍天下美食 🍕', level: 29, followers: 189000, following: 567, likes: 956000, isAnchor: true },
  { id: '5', username: 'anchor4', nickname: '户外探险家', avatar: '', bio: '记录生活，分享美好 🏔️', level: 33, followers: 312000, following: 198, likes: 1450000, isAnchor: true },
  { id: '6', username: 'anchor5', nickname: '颜值担当Luna', avatar: '', bio: '新人主播，请多关照 💖', level: 21, followers: 89500, following: 423, likes: 456000, isAnchor: true },
  { id: '7', username: 'user1', nickname: '快乐小鱼', avatar: '', bio: '开心每一天 😊', level: 8, followers: 125, following: 156, likes: 3420, isAnchor: false },
  { id: '8', username: 'user2', nickname: '夜空繁星', avatar: '', bio: '直播间常驻观众 👀', level: 12, followers: 568, following: 89, likes: 12800, isAnchor: false }
]

export const liveRooms: LiveRoom[] = [
  { id: '1', title: '深夜情歌电台 | 陪你入眠', cover: gradients[0], category: '音乐', anchor: users[1], viewers: 15680, likes: 89560, isLive: true, startedAt: '20:00', description: '用音乐温暖每一个夜晚' },
  { id: '2', title: '王者荣耀百星排位冲分', cover: gradients[1], category: '游戏', anchor: users[2], viewers: 42350, likes: 156000, isLive: true, startedAt: '19:30', description: '国服打野教学，关注上车' },
  { id: '3', title: '今天吃什么？成都火锅！', cover: gradients[2], category: '美食', anchor: users[3], viewers: 8950, likes: 34500, isLive: true, startedAt: '18:00', description: '带你吃遍成都美食' },
  { id: '4', title: '徒步登顶看日出 🌅', cover: gradients[3], category: '户外', anchor: users[4], viewers: 25600, likes: 78900, isLive: true, startedAt: '05:00', description: '黄山日出全程直播' },
  { id: '5', title: '新人第一天开播 求关注', cover: gradients[4], category: '颜值', anchor: users[5], viewers: 3250, likes: 12500, isLive: true, startedAt: '21:00', description: '新人主播，多多支持' },
  { id: '6', title: '聊天唠嗑 | 分享生活趣事', cover: gradients[5], category: '聊天', anchor: users[0], viewers: 6780, likes: 23400, isLive: true, startedAt: '20:30', description: '来和我聊聊天吧' },
  { id: '7', title: '钢琴演奏 | 经典名曲', cover: gradients[6], category: '音乐', anchor: users[1], viewers: 12300, likes: 67800, isLive: true, startedAt: '19:00', description: '钢琴小姐姐在线演奏' },
  { id: '8', title: 'LOL钻石局教学', cover: gradients[7], category: '游戏', anchor: users[2], viewers: 35600, likes: 125000, isLive: true, startedAt: '20:00', description: '英雄联盟高分段直播' },
  { id: '9', title: '深夜食堂 | 自制小龙虾', cover: gradients[8], category: '美食', anchor: users[3], viewers: 15800, likes: 56700, isLive: true, startedAt: '22:00', description: '麻辣小龙虾制作教程' },
  { id: '10', title: '海边日落直播', cover: gradients[9], category: '户外', anchor: users[4], viewers: 28900, likes: 98700, isLive: true, startedAt: '18:30', description: '三亚海边绝美日落' },
  { id: '11', title: '舞蹈练习室 | 韩舞翻跳', cover: gradients[10], category: '颜值', anchor: users[5], viewers: 18500, likes: 72300, isLive: true, startedAt: '21:30', description: '每天跳舞两小时' },
  { id: '12', title: '情感电台 | 说出你的故事', cover: gradients[11], category: '聊天', anchor: users[0], viewers: 9850, likes: 45600, isLive: true, startedAt: '23:00', description: '深夜情感树洞' }
]

export const shortVideos: ShortVideo[] = [
  { id: '1', title: '今日份的翻唱，希望大家喜欢 🎵', cover: gradients[0], author: users[1], likes: 56800, comments: 2340, shares: 890, music: '原声 - 小悦儿🎵', duration: 45, isLiked: false },
  { id: '2', title: '五杀时刻！这波操作帅炸了 🔥', cover: gradients[1], author: users[2], likes: 125000, comments: 5680, shares: 3200, music: '热门BGM', duration: 30, isLiked: true },
  { id: '3', title: '100元吃遍夜市一条街 🍢', cover: gradients[2], author: users[3], likes: 89000, comments: 3450, shares: 1560, music: '美食博主BGM', duration: 60, isLiked: false },
  { id: '4', title: '山顶的风景真的太美了 🏔️', cover: gradients[3], author: users[4], likes: 234000, comments: 8900, shares: 6700, music: '治愈系纯音乐', duration: 50, isLiked: true },
  { id: '5', title: '今天的妆容分享 💄', cover: gradients[4], author: users[5], likes: 67000, comments: 2100, shares: 980, music: '美妆视频BGM', duration: 55, isLiked: false },
  { id: '6', title: '猫咪的日常，太可爱了吧 🐱', cover: gradients[5], author: users[6], likes: 345000, comments: 12000, shares: 8900, music: '可爱萌宠BGM', duration: 20, isLiked: true },
  { id: '7', title: '一首老歌唱出多少人的回忆', cover: gradients[6], author: users[1], likes: 198000, comments: 7800, shares: 4500, music: '经典老歌', duration: 40, isLiked: false },
  { id: '8', title: '全网最详细的游戏攻略 📖', cover: gradients[7], author: users[2], likes: 78000, comments: 3200, shares: 2100, music: '游戏解说BGM', duration: 120, isLiked: false },
  { id: '9', title: '自制网红甜品，颜值超高 🍰', cover: gradients[8], author: users[3], likes: 156000, comments: 5400, shares: 3400, music: '甜品制作BGM', duration: 65, isLiked: true },
  { id: '10', title: '第一次跳伞，太刺激了！🪂', cover: gradients[9], author: users[4], likes: 456000, comments: 15000, shares: 12000, music: '极限运动BGM', duration: 35, isLiked: false },
  { id: '11', title: '跳舞的小姐姐也太好看了吧 💃', cover: gradients[10], author: users[5], likes: 289000, comments: 9800, shares: 6500, music: '热门舞蹈BGM', duration: 28, isLiked: true },
  { id: '12', title: '深夜放毒，看饿了别怪我 🍜', cover: gradients[11], author: users[3], likes: 112000, comments: 4500, shares: 2800, music: '美食ASMR', duration: 42, isLiked: false },
  { id: '13', title: '这波操作我也就看了十遍', cover: gradients[0], author: users[2], likes: 167000, comments: 6700, shares: 4200, music: '游戏精彩集锦BGM', duration: 25, isLiked: true },
  { id: '14', title: '海边的日落真的绝美 🌅', cover: gradients[1], author: users[4], likes: 523000, comments: 18000, shares: 15000, music: '海边浪漫BGM', duration: 55, isLiked: false },
  { id: '15', title: '新人报道，大家好呀 👋', cover: gradients[2], author: users[7], likes: 3400, comments: 560, shares: 120, music: '新人报道BGM', duration: 15, isLiked: false }
]

export const gifts: Gift[] = [
  { id: '1', name: '玫瑰', icon: '🌹', price: 1 },
  { id: '2', name: '爱心', icon: '❤️', price: 5 },
  { id: '3', name: '棒棒糖', icon: '🍭', price: 10 },
  { id: '4', name: '跑车', icon: '🏎️', price: 100 },
  { id: '5', name: '火箭', icon: '🚀', price: 500 },
  { id: '6', name: '飞机', icon: '✈️', price: 1000 },
  { id: '7', name: '城堡', icon: '🏰', price: 2000 },
  { id: '8', name: '嘉年华', icon: '🎪', price: 3000 },
  { id: '9', name: '皇冠', icon: '👑', price: 4000 },
  { id: '10', name: '钻石', icon: '💎', price: 5000 }
]

export const danmakuPool: string[] = [
  '主播好漂亮！',
  '666666',
  '来了来了',
  '主播唱得真好听',
  '这操作太秀了',
  '哈哈哈笑死我了',
  '关注了关注了',
  '主播加油！',
  '晚上好呀',
  '第一次来',
  '老粉报到',
  '主播今天状态真好',
  '太好看了吧',
  '爱了爱了',
  '主播哪里人呀',
  '能点首歌吗',
  '这也太强了',
  '学到了学到了',
  '主播好可爱',
  '支持主播',
  '冲冲冲！',
  '太搞笑了',
  '主播几点下播',
  '明天还来',
  '主播辛苦了',
  '送个小心心',
  '主播好温柔',
  '哈哈哈绝了',
  '前排围观',
  '打卡打卡',
  '主播声音真好听',
  '这波稳了',
  '主播真厉害',
  '我来晚了',
  '人美歌甜'
]

export const danmakuColors = [
  '#FFFFFF',
  '#FF2D55',
  '#AF52ED',
  '#00D4FF',
  '#FFD700',
  '#4ECDC4',
  '#FF6B6B',
  '#A8EDEA'
]

export const giftRecords: GiftRecord[] = [
  { id: '1', userId: '7', username: '快乐小鱼', giftId: '2', giftName: '爱心', giftIcon: '❤️', count: 1, timestamp: Date.now() - 5000 },
  { id: '2', userId: '8', username: '夜空繁星', giftId: '3', giftName: '棒棒糖', giftIcon: '🍭', count: 10, timestamp: Date.now() - 15000 },
  { id: '3', userId: '2', username: '小悦儿🎵', giftId: '5', giftName: '火箭', giftIcon: '🚀', count: 1, timestamp: Date.now() - 30000 }
]

export const messages: MessageItem[] = [
  { id: '1', type: 'private', from: users[1], content: '谢谢你的支持呀~', time: '刚刚', unread: 2 },
  { id: '2', type: 'private', from: users[2], content: '今晚有时间一起开黑吗', time: '10分钟前', unread: 0 },
  { id: '3', type: 'private', from: users[3], content: '下期想吃什么推荐一下', time: '1小时前', unread: 0 },
  { id: '4', type: 'system', from: { id: 'system', username: '系统通知', nickname: '系统通知', avatar: '', bio: '', level: 0, followers: 0, following: 0, likes: 0, isAnchor: false }, content: '您关注的主播已开播', time: '2小时前', unread: 1 },
  { id: '5', type: 'system', from: { id: 'system', username: '系统通知', nickname: '系统通知', avatar: '', bio: '', level: 0, followers: 0, following: 0, likes: 0, isAnchor: false }, content: '恭喜您获得新人礼包', time: '昨天', unread: 0 },
  { id: '6', type: 'gift', from: users[6], content: '送给主播 火箭x1', time: '3小时前', unread: 3 },
  { id: '7', type: 'gift', from: users[7], content: '送给主播 嘉年华x1', time: '昨天', unread: 0 },
  { id: '8', type: 'private', from: users[4], content: '下次一起去户外呀', time: '2天前', unread: 0 }
]

export const transactionRecords: TransactionRecord[] = [
  { id: '1', type: 'recharge', amount: 50, coins: 500, description: '充值50元套餐', time: '2024-01-15 14:30' },
  { id: '2', type: 'spend', amount: 0, coins: 50, description: '赠送礼物 - 火箭x1', time: '2024-01-15 20:15' },
  { id: '3', type: 'recharge', amount: 100, coins: 1000, description: '充值100元套餐', time: '2024-01-14 10:00' },
  { id: '4', type: 'spend', amount: 0, coins: 100, description: '赠送礼物 - 跑车x1', time: '2024-01-14 21:30' },
  { id: '5', type: 'spend', amount: 0, coins: 10, description: '赠送礼物 - 棒棒糖x1', time: '2024-01-13 19:45' },
  { id: '6', type: 'recharge', amount: 10, coins: 100, description: '充值10元套餐', time: '2024-01-12 16:20' }
]

export const rechargePackages = [
  { coins: 10, price: 1, bonus: 0 },
  { coins: 50, price: 5, bonus: 5 },
  { coins: 100, price: 10, bonus: 15 },
  { coins: 500, price: 50, bonus: 80 },
  { coins: 1000, price: 100, bonus: 200 },
  { coins: 5000, price: 500, bonus: 1500 }
]

export const hotSearches = [
  '王者荣耀',
  '热门歌曲',
  '美食探店',
  '户外探险',
  '颜值主播',
  '情感电台',
  '游戏直播',
  '跳舞视频'
]

export function mockDelay<T>(data: T, min = 300, max = 800): Promise<T> {
  const delay = Math.floor(Math.random() * (max - min + 1)) + min
  return new Promise((resolve) => {
    setTimeout(() => resolve(data), delay)
  })
}

export function getRandomDanmaku(): string {
  return danmakuPool[Math.floor(Math.random() * danmakuPool.length)]
}

export function getRandomColor(): string {
  return danmakuColors[Math.floor(Math.random() * danmakuColors.length)]
}

export function getRandomUser(): User {
  return users[Math.floor(Math.random() * users.length)]
}

export const hotLives: LiveRoom[] = [
  { ...liveRooms[1], viewers: 52360, title: '🔥 王者荣耀百星排位冲分 - 今晚冲击国服' },
  { ...liveRooms[0], viewers: 38900, title: '🎵 深夜情歌电台 | 用一首歌的时间想你' },
  { ...liveRooms[3], viewers: 29800, title: '🏔️ 徒步登顶看日出 - 黄山绝美风景' }
]

export const entryUserPool: string[] = [
  '星辰大海',
  '追光少年',
  '温柔晚风',
  '梦想家',
  '小确幸',
  '流浪诗人',
  '阳光灿烂',
  '快乐星球',
  '银河漫游',
  '甜蜜糖果',
  '风中追风',
  '月下独酌',
  '云端漫步',
  '时光旅人',
  '彩虹糖',
  '柠檬不萌',
  '小熊软糖',
  '夏日限定',
  '冬日暖阳',
  '春风十里'
]

export const videoComments: VideoComment[] = [
  { id: '1', user: users[1], content: '太好听了！单曲循环中 🎵', likes: 2356, isLiked: false, time: '2小时前', replyCount: 12 },
  { id: '2', user: users[2], content: '这操作绝了，学废了学废了', likes: 1890, isLiked: true, time: '3小时前', replyCount: 8 },
  { id: '3', user: users[3], content: '看饿了，夜宵安排！🍜', likes: 980, isLiked: false, time: '4小时前', replyCount: 5 },
  { id: '4', user: users[4], content: '风景太美了，下次一起去呀', likes: 1560, isLiked: false, time: '5小时前', replyCount: 15 },
  { id: '5', user: users[5], content: '姐姐也太好看了吧！💖', likes: 3200, isLiked: true, time: '6小时前', replyCount: 25 },
  { id: '6', user: users[6], content: '猫咪也太可爱了，想rua', likes: 5680, isLiked: false, time: '8小时前', replyCount: 32 },
  { id: '7', user: users[7], content: '老粉报到！支持主播', likes: 450, isLiked: false, time: '10小时前', replyCount: 3 },
  { id: '8', user: users[0], content: '唱得真好听，再来一首！', likes: 2100, isLiked: true, time: '12小时前', replyCount: 10 },
  { id: '9', user: users[1], content: '这个攻略太详细了，收藏！', likes: 890, isLiked: false, time: '1天前', replyCount: 6 },
  { id: '10', user: users[2], content: '看了十遍还是觉得帅 🔥', likes: 1250, isLiked: false, time: '1天前', replyCount: 9 },
  { id: '11', user: users[3], content: '求教程！看起来好好吃', likes: 760, isLiked: true, time: '1天前', replyCount: 4 },
  { id: '12', user: users[4], content: '这也太刺激了吧，不敢跳', likes: 2800, isLiked: false, time: '1天前', replyCount: 18 },
  { id: '13', user: users[5], content: '跳舞也太好看了，爱了爱了', likes: 4500, isLiked: true, time: '2天前', replyCount: 28 },
  { id: '14', user: users[6], content: '哈哈哈哈笑死我了', likes: 680, isLiked: false, time: '2天前', replyCount: 2 },
  { id: '15', user: users[7], content: '第一次来，关注了关注了', likes: 320, isLiked: false, time: '2天前', replyCount: 1 },
  { id: '16', user: users[0], content: 'bgm是什么呀？求分享', likes: 560, isLiked: false, time: '2天前', replyCount: 7 },
  { id: '17', user: users[1], content: '每天必看，已经成习惯了', likes: 1800, isLiked: true, time: '3天前', replyCount: 11 },
  { id: '18', user: users[2], content: '这波操作我给满分 💯', likes: 2200, isLiked: false, time: '3天前', replyCount: 14 },
  { id: '19', user: users[3], content: '馋了馋了，口水都流下来了', likes: 950, isLiked: false, time: '3天前', replyCount: 6 },
  { id: '20', user: users[4], content: '这才是生活该有的样子 🌅', likes: 3500, isLiked: true, time: '3天前', replyCount: 22 }
]

export function getRandomEntryUser(): string {
  return entryUserPool[Math.floor(Math.random() * entryUserPool.length)]
}
