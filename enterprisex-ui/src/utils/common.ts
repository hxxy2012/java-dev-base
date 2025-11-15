/**
 * 通用工具函数
 */

/**
 * 格式化文件大小
 * @param bytes 字节数
 * @returns 格式化后的字符串
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
};

/**
 * 下载文件
 * @param url 文件URL
 * @param filename 文件名
 */
export const downloadFile = (url: string, filename: string): void => {
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

/**
 * 下载Blob文件
 * @param blob Blob对象
 * @param filename 文件名
 */
export const downloadBlob = (blob: Blob, filename: string): void => {
  const url = window.URL.createObjectURL(blob);
  downloadFile(url, filename);
  window.URL.revokeObjectURL(url);
};

/**
 * 防抖函数
 * @param fn 函数
 * @param delay 延迟时间(ms)
 * @returns 防抖后的函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let timer: ReturnType<typeof setTimeout> | null = null;
  return (...args: Parameters<T>) => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn(...args);
    }, delay);
  };
};

/**
 * 节流函数
 * @param fn 函数
 * @param delay 延迟时间(ms)
 * @returns 节流后的函数
 */
export const throttle = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let lastTime = 0;
  return (...args: Parameters<T>) => {
    const now = Date.now();
    if (now - lastTime >= delay) {
      fn(...args);
      lastTime = now;
    }
  };
};

/**
 * 深拷贝
 * @param obj 对象
 * @returns 拷贝后的对象
 */
export const deepClone = <T>(obj: T): T => {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj.getTime()) as any;
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as any;
  if (obj instanceof Object) {
    const clonedObj = {} as T;
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key]);
      }
    }
    return clonedObj;
  }
  return obj;
};

/**
 * 生成随机字符串
 * @param length 长度
 * @returns 随机字符串
 */
export const randomString = (length: number): string => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

/**
 * 判断是否为空
 * @param value 值
 * @returns 是否为空
 */
export const isEmpty = (value: any): boolean => {
  if (value === null || value === undefined) return true;
  if (typeof value === 'string') return value.trim().length === 0;
  if (Array.isArray(value)) return value.length === 0;
  if (typeof value === 'object') return Object.keys(value).length === 0;
  return false;
};

/**
 * 获取URL参数
 * @param name 参数名
 * @returns 参数值
 */
export const getUrlParam = (name: string): string | null => {
  const url = new URL(window.location.href);
  return url.searchParams.get(name);
};

/**
 * 复制到剪贴板
 * @param text 文本
 * @returns Promise
 */
export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch (error) {
    // 降级方案
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    const success = document.execCommand('copy');
    document.body.removeChild(textarea);
    return success;
  }
};

/**
 * 格式化数字（千分位）
 * @param num 数字
 * @returns 格式化后的字符串
 */
export const formatNumber = (num: number): string => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

/**
 * 树形数据转平铺数组
 * @param tree 树形数据
 * @param childrenKey 子节点key
 * @returns 平铺数组
 */
export const treeToList = <T extends Record<string, any>>(
  tree: T[],
  childrenKey: string = 'children'
): T[] => {
  const result: T[] = [];
  const traverse = (nodes: T[]) => {
    nodes.forEach(node => {
      result.push(node);
      if (node[childrenKey] && Array.isArray(node[childrenKey])) {
        traverse(node[childrenKey]);
      }
    });
  };
  traverse(tree);
  return result;
};

/**
 * 平铺数组转树形数据
 * @param list 平铺数组
 * @param idKey ID字段名
 * @param parentIdKey 父ID字段名
 * @param childrenKey 子节点字段名
 * @returns 树形数据
 */
export const listToTree = <T extends Record<string, any>>(
  list: T[],
  idKey: string = 'id',
  parentIdKey: string = 'parentId',
  childrenKey: string = 'children'
): T[] => {
  const map: Record<string, T> = {};
  const result: T[] = [];

  // 构建映射
  list.forEach(item => {
    map[item[idKey]] = { ...item, [childrenKey]: [] };
  });

  // 构建树形结构
  list.forEach(item => {
    const node = map[item[idKey]];
    const parentId = item[parentIdKey];

    if (parentId && map[parentId]) {
      map[parentId][childrenKey].push(node);
    } else {
      result.push(node);
    }
  });

  return result;
};
