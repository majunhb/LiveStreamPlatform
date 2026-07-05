import { request } from '@/utils/request'
import { users, mockDelay } from '@/mock/data'
import type { User } from '@/mock/data'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  phone: string
}

export async function login(params: LoginParams) {
  const user = users.find(u => u.username === params.username)
  if (user && params.password === '123456') {
    const result = await mockDelay({
      token: `mock_token_${Date.now()}`,
      user: { ...user }
    })
    return {
      code: 200,
      message: '登录成功',
      data: result
    }
  }
  return {
    code: 401,
    message: '用户名或密码错误',
    data: null
  }
}

export async function register(params: RegisterParams) {
  const newUser: User = {
    id: String(users.length + 1),
    username: params.username,
    nickname: params.username,
    avatar: '',
    bio: '这个人很懒，什么都没写~',
    level: 1,
    followers: 0,
    following: 0,
    likes: 0,
    isAnchor: false,
    phone: params.phone
  }
  const result = await mockDelay({
    token: `mock_token_${Date.now()}`,
    user: newUser
  })
  return {
    code: 200,
    message: '注册成功',
    data: result
  }
}

export async function guestLogin() {
  const guestUser: User = {
    id: 'guest',
    username: 'guest',
    nickname: '游客用户',
    avatar: '',
    bio: '游客模式体验中~',
    level: 1,
    followers: 0,
    following: 0,
    likes: 0,
    isAnchor: false
  }
  const result = await mockDelay({
    token: `guest_token_${Date.now()}`,
    user: guestUser
  })
  return {
    code: 200,
    message: '游客登录成功',
    data: result
  }
}

export async function logout() {
  return {
    code: 200,
    message: '退出成功',
    data: null
  }
}

export async function getUserInfo() {
  const user = users[0]
  const result = await mockDelay({ ...user })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}
