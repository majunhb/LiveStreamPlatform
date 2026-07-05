import { transactionRecords, rechargePackages, mockDelay } from '@/mock/data'

export async function getWallet() {
  const result = await mockDelay({
    coins: 1280,
    diamonds: 0,
    level: 15
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function getRechargePackages() {
  const result = await mockDelay(rechargePackages)
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function recharge(coins: number, amount: number, payMethod: string) {
  const bonus = rechargePackages.find(p => p.coins === coins)?.bonus || 0
  const totalCoins = coins + bonus
  const result = await mockDelay({
    success: true,
    coins: totalCoins,
    bonus,
    orderId: String(Date.now())
  })
  return {
    code: 200,
    message: '充值成功',
    data: result
  }
}

export async function getTransactionRecords(page = 1, pageSize = 20) {
  const result = await mockDelay({
    list: transactionRecords,
    total: transactionRecords.length,
    page,
    pageSize
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}

export async function deductCoins(amount: number) {
  const result = await mockDelay({
    success: true,
    remaining: Math.max(0, 1280 - amount)
  })
  return {
    code: 200,
    message: 'success',
    data: result
  }
}
