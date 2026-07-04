import { get, post, put, del } from '@/utils/request'
import type { GiftInfo, WithdrawInfo, WalletRecordInfo, PageParams, PageResult } from '@/types'

// 礼物管理
export function getGiftList(params: PageParams) {
  return get<PageResult<GiftInfo>>('/admin/gifts', params as Record<string, unknown>)
}

export function createGift(data: Partial<GiftInfo>) {
  return post('/admin/gifts', data as Record<string, unknown>)
}

export function updateGift(id: number, data: Partial<GiftInfo>) {
  return put(`/admin/gifts/${id}`, data as Record<string, unknown>)
}

export function deleteGift(id: number) {
  return del(`/admin/gifts/${id}`)
}

// 提现管理
export function getWithdrawList(params: PageParams) {
  return get<PageResult<WithdrawInfo>>('/admin/withdrawals', params as Record<string, unknown>)
}

export function approveWithdraw(id: number) {
  return post(`/admin/withdrawals/${id}/approve`)
}

export function rejectWithdraw(id: number, reason: string) {
  return post(`/admin/withdrawals/${id}/reject`, { reason })
}

// 钱包
export function getWalletRecords(params: PageParams) {
  return get<PageResult<WalletRecordInfo>>('/admin/wallet/records', params as Record<string, unknown>)
}
