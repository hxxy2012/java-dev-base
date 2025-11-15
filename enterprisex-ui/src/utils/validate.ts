/**
 * 表单验证工具函数
 */

/**
 * 验证手机号
 * @param phone 手机号
 * @returns 是否有效
 */
export const validatePhone = (phone: string): boolean => {
  return /^1[3-9]\d{9}$/.test(phone);
};

/**
 * 验证邮箱
 * @param email 邮箱
 * @returns 是否有效
 */
export const validateEmail = (email: string): boolean => {
  return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email);
};

/**
 * 验证身份证号
 * @param idCard 身份证号
 * @returns 是否有效
 */
export const validateIdCard = (idCard: string): boolean => {
  return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard);
};

/**
 * 验证URL
 * @param url URL
 * @returns 是否有效
 */
export const validateUrl = (url: string): boolean => {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

/**
 * 验证IP地址
 * @param ip IP地址
 * @returns 是否有效
 */
export const validateIP = (ip: string): boolean => {
  return /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/.test(ip);
};

/**
 * 验证端口号
 * @param port 端口号
 * @returns 是否有效
 */
export const validatePort = (port: number | string): boolean => {
  const portNum = typeof port === 'string' ? parseInt(port, 10) : port;
  return portNum >= 1 && portNum <= 65535;
};

/**
 * 验证密码强度
 * @param password 密码
 * @returns 强度等级 0-4
 */
export const validatePasswordStrength = (password: string): number => {
  if (!password) return 0;

  let strength = 0;

  // 长度
  if (password.length >= 8) strength++;
  if (password.length >= 12) strength++;

  // 包含小写字母
  if (/[a-z]/.test(password)) strength++;

  // 包含大写字母
  if (/[A-Z]/.test(password)) strength++;

  // 包含数字
  if (/\d/.test(password)) strength++;

  // 包含特殊字符
  if (/[^a-zA-Z\d]/.test(password)) strength++;

  // 归一化到0-4
  return Math.min(Math.floor(strength / 2), 4);
};

/**
 * 验证用户名
 * @param username 用户名
 * @returns 是否有效
 */
export const validateUsername = (username: string): boolean => {
  // 4-20位，字母、数字、下划线
  return /^[a-zA-Z0-9_]{4,20}$/.test(username);
};

/**
 * 验证中文
 * @param text 文本
 * @returns 是否全为中文
 */
export const validateChinese = (text: string): boolean => {
  return /^[\u4e00-\u9fa5]+$/.test(text);
};

/**
 * 验证数字
 * @param num 数字
 * @param options 选项
 * @returns 是否有效
 */
export const validateNumber = (
  num: number | string,
  options?: {
    min?: number;
    max?: number;
    integer?: boolean;
  }
): boolean => {
  const numValue = typeof num === 'string' ? parseFloat(num) : num;

  if (isNaN(numValue)) return false;

  if (options?.integer && !Number.isInteger(numValue)) return false;

  if (options?.min !== undefined && numValue < options.min) return false;

  if (options?.max !== undefined && numValue > options.max) return false;

  return true;
};

/**
 * Ant Design表单验证规则 - 手机号
 */
export const phoneRule = {
  pattern: /^1[3-9]\d{9}$/,
  message: '请输入有效的手机号',
};

/**
 * Ant Design表单验证规则 - 邮箱
 */
export const emailRule = {
  pattern: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  message: '请输入有效的邮箱地址',
};

/**
 * Ant Design表单验证规则 - 用户名
 */
export const usernameRule = {
  pattern: /^[a-zA-Z0-9_]{4,20}$/,
  message: '用户名为4-20位字母、数字或下划线',
};

/**
 * Ant Design表单验证规则 - 密码
 */
export const passwordRule = {
  min: 6,
  message: '密码长度不能少于6位',
};
