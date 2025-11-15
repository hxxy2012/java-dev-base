import dayjs from 'dayjs';

/**
 * 日期工具函数
 */

/**
 * 格式化日期时间
 * @param date 日期
 * @param format 格式
 * @returns 格式化后的字符串
 */
export const formatDateTime = (
  date: string | Date | number,
  format: string = 'YYYY-MM-DD HH:mm:ss'
): string => {
  if (!date) return '';
  return dayjs(date).format(format);
};

/**
 * 格式化日期
 * @param date 日期
 * @returns 格式化后的字符串
 */
export const formatDate = (date: string | Date | number): string => {
  return formatDateTime(date, 'YYYY-MM-DD');
};

/**
 * 格式化时间
 * @param date 日期
 * @returns 格式化后的字符串
 */
export const formatTime = (date: string | Date | number): string => {
  return formatDateTime(date, 'HH:mm:ss');
};

/**
 * 获取相对时间
 * @param date 日期
 * @returns 相对时间字符串
 */
export const getRelativeTime = (date: string | Date | number): string => {
  const now = dayjs();
  const target = dayjs(date);
  const diff = now.diff(target, 'second');

  if (diff < 60) {
    return '刚刚';
  } else if (diff < 3600) {
    return `${Math.floor(diff / 60)}分钟前`;
  } else if (diff < 86400) {
    return `${Math.floor(diff / 3600)}小时前`;
  } else if (diff < 604800) {
    return `${Math.floor(diff / 86400)}天前`;
  } else {
    return formatDate(date);
  }
};

/**
 * 判断是否为今天
 * @param date 日期
 * @returns 是否为今天
 */
export const isToday = (date: string | Date | number): boolean => {
  return dayjs(date).isSame(dayjs(), 'day');
};

/**
 * 判断是否为本周
 * @param date 日期
 * @returns 是否为本周
 */
export const isThisWeek = (date: string | Date | number): boolean => {
  return dayjs(date).isSame(dayjs(), 'week');
};

/**
 * 判断是否为本月
 * @param date 日期
 * @returns 是否为本月
 */
export const isThisMonth = (date: string | Date | number): boolean => {
  return dayjs(date).isSame(dayjs(), 'month');
};

/**
 * 获取日期范围
 * @param type 类型
 * @returns [开始日期, 结束日期]
 */
export const getDateRange = (
  type: 'today' | 'yesterday' | 'week' | 'month' | 'year'
): [string, string] => {
  const now = dayjs();
  let start: dayjs.Dayjs;
  let end: dayjs.Dayjs;

  switch (type) {
    case 'today':
      start = now.startOf('day');
      end = now.endOf('day');
      break;
    case 'yesterday':
      start = now.subtract(1, 'day').startOf('day');
      end = now.subtract(1, 'day').endOf('day');
      break;
    case 'week':
      start = now.startOf('week');
      end = now.endOf('week');
      break;
    case 'month':
      start = now.startOf('month');
      end = now.endOf('month');
      break;
    case 'year':
      start = now.startOf('year');
      end = now.endOf('year');
      break;
    default:
      start = now.startOf('day');
      end = now.endOf('day');
  }

  return [formatDateTime(start.toDate()), formatDateTime(end.toDate())];
};

/**
 * 获取日期差值
 * @param date1 日期1
 * @param date2 日期2
 * @param unit 单位
 * @returns 差值
 */
export const getDateDiff = (
  date1: string | Date | number,
  date2: string | Date | number,
  unit: 'day' | 'hour' | 'minute' | 'second' = 'day'
): number => {
  return dayjs(date1).diff(dayjs(date2), unit);
};
