import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

// 设置中文语言
dayjs.locale('zh-cn')

export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  return dayjs(date).format(format)
}

export const formatRelativeTime = (date) => {
  const now = dayjs()
  const target = dayjs(date)
  const diff = now.diff(target, 'minute')
  
  if (diff < 1) {
    return '刚刚'
  } else if (diff < 60) {
    return `${diff}分钟前`
  } else if (diff < 1440) {
    return `${Math.floor(diff / 60)}小时前`
  } else if (diff < 43200) {
    return `${Math.floor(diff / 1440)}天前`
  } else {
    return target.format('YYYY-MM-DD')
  }
}

export const isToday = (date) => {
  return dayjs(date).isSame(dayjs(), 'day')
}

export const isYesterday = (date) => {
  return dayjs(date).isSame(dayjs().subtract(1, 'day'), 'day')
} 