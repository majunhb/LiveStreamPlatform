import { get, post, put, del } from '@/utils/request'

// 礼物管理
export function getGiftList(params: any) {
  return get('/admin/gifts', params)
}

export function createGift(data: any) {
  return post('/admin/gifts', data)
}

export function updateGift(id: number, data: any) {
  return put(`/admin/gifts/${id}`, data)
}

export function deleteGift(id: number) {
  return del(`/admin/gifts/${id}`)
}

// 提现管理
export function getWithdrawList(params: any) {
  return get('/admin/withdrawals', params)
}

export function approveWithdraw(id: number) {
  return post(`/admin/withdrawals/${id}/approve`)
}

export function rejectWithdraw(id: number, reason: string) {
  return post(`/admin/withdrawals/${id}/reject`, { reason })
}

// 钱包
export function getWalletRecords(params: any) {
  return get('/admin/wallet/records', params)
}
