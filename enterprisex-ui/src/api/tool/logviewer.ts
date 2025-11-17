import request from '@/utils/request';

/**
 * 日志文件信息
 */
export interface LogFileInfo {
  fileName: string;
  filePath: string;
  fileSize: number;
  fileSizeStr: string;
  lastModified: string;
}

/**
 * 日志内容
 */
export interface LogContent {
  fileName: string;
  content: string;
  totalLines: number;
  fileSize: number;
  fileSizeStr: string;
  lastModified: string;
}

/**
 * 获取日志文件列表
 */
export const getLogFileList = () => {
  return request<LogFileInfo[]>({
    url: '/system/logviewer/list',
    method: 'get',
  });
};

/**
 * 获取日志文件内容
 */
export const getLogContent = (fileName: string, lines?: number) => {
  return request<LogContent>({
    url: '/system/logviewer/content',
    method: 'get',
    params: { fileName, lines },
  });
};

/**
 * 搜索日志内容
 */
export const searchLog = (fileName: string, keyword: string, maxLines?: number) => {
  return request<LogContent>({
    url: '/system/logviewer/search',
    method: 'get',
    params: { fileName, keyword, maxLines },
  });
};

/**
 * 下载日志文件
 */
export const downloadLog = (fileName: string) => {
  return `/api/system/logviewer/download/${fileName}`;
};

/**
 * 清空日志文件
 */
export const clearLog = (fileName: string) => {
  return request({
    url: `/system/logviewer/clear/${fileName}`,
    method: 'post',
  });
};

/**
 * 删除日志文件
 */
export const deleteLog = (fileName: string) => {
  return request({
    url: `/system/logviewer/${fileName}`,
    method: 'delete',
  });
};
