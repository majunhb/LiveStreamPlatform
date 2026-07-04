const TOKEN_KEY = 'admin_token'
const USER_KEY = 'admin_user'

export interface AdminUser {
  id: number
  username: string
  nickname: string
  role: string
  avatar?: string
  createTime?: string
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo(): AdminUser | null {
  const info = localStorage.getItem(USER_KEY)
  return info ? JSON.parse(info) as AdminUser : null
}

export function setUserInfo(user: AdminUser): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUserInfo(): void {
  localStorage.removeItem(USER_KEY)
}

export function clearAuth(): void {
  removeToken()
  removeUserInfo()
}
