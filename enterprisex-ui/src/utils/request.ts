import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { message } from 'antd';

// 错误消息映射
const ERROR_MESSAGES: Record<number, string> = {
  400: '请求参数错误',
  401: '未授权，请重新登录',
  403: '拒绝访问',
  404: '请求的资源不存在',
  405: '请求方法不允许',
  408: '请求超时',
  500: '服务器内部错误',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时',
};

// 不显示错误提示的接口白名单
const NO_ERROR_MESSAGE_URLS: string[] = [
  // 可以在这里添加不需要显示错误提示的接口
];

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;

    // 如果返回的状态码不是200，说明有错误
    if (res.code !== 200) {
      message.error(res.msg || '请求失败');

      // 401: 未登录或token过期
      if (res.code === 401) {
        message.warning('登录已过期，请重新登录');
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        window.location.href = '/login';
      }

      return Promise.reject(new Error(res.msg || '请求失败'));
    }

    return res;
  },
  (error: AxiosError) => {
    console.error('响应错误：', error);

    // 检查是否在白名单中
    const url = error.config?.url || '';
    const shouldShowError = !NO_ERROR_MESSAGE_URLS.some(pattern => url.includes(pattern));

    if (!shouldShowError) {
      return Promise.reject(error);
    }

    if (error.response) {
      const { status, data } = error.response;
      const errorData = data as any;

      // 401: 未登录或token过期
      if (status === 401) {
        message.error('登录已过期，请重新登录');
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        // 延迟跳转，确保消息显示
        setTimeout(() => {
          window.location.href = '/login';
        }, 1000);
        return Promise.reject(error);
      }

      // 403: 无权限
      if (status === 403) {
        message.error('您没有权限执行此操作');
        return Promise.reject(error);
      }

      // 显示错误消息
      const errorMessage = errorData?.msg || ERROR_MESSAGES[status] || `请求失败 (${status})`;
      message.error(errorMessage);
    } else if (error.request) {
      // 请求已发送但没有收到响应
      if (error.code === 'ECONNABORTED') {
        message.error('请求超时，请稍后重试');
      } else if (!navigator.onLine) {
        message.error('网络连接已断开，请检查网络');
      } else {
        message.error('网络错误，请检查网络连接');
      }
    } else {
      // 请求配置错误
      message.error(error.message || '请求配置错误');
    }

    return Promise.reject(error);
  }
);

export default service;

// 导出请求方法
export const get = <T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> => {
  return service.get(url, { params, ...config });
};

export const post = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return service.post(url, data, config);
};

export const put = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return service.put(url, data, config);
};

export const del = <T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> => {
  return service.delete(url, { params, ...config });
};
