import request from '@/utils/request';

/**
 * 文件信息类型
 */
export interface SysFile {
  fileId?: number;
  fileName: string;
  originalName: string;
  filePath: string;
  fileUrl?: string;
  fileSize: number;
  fileType: string;
  fileExt?: string;
  storageLocation?: string;
  uploadBy?: string;
  uploadIp?: string;
  createBy?: string;
  createTime?: string;
  updateBy?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 查询文件列表
 */
export function listFile(params: any) {
  return request({
    url: '/system/file/list',
    method: 'get',
    params,
  });
}

/**
 * 查询文件详细
 */
export function getFile(fileId: number) {
  return request({
    url: `/system/file/${fileId}`,
    method: 'get',
  });
}

/**
 * 上传文件
 */
export function uploadFile(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/system/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

/**
 * 下载文件
 */
export function downloadFile(fileId: number) {
  return request({
    url: `/system/file/download/${fileId}`,
    method: 'get',
    responseType: 'blob',
  });
}

/**
 * 获取文件URL
 */
export function getFileUrl(fileId: number) {
  return request({
    url: `/system/file/url/${fileId}`,
    method: 'get',
  });
}

/**
 * 删除文件
 */
export function delFile(fileId: number | number[]) {
  const ids = Array.isArray(fileId) ? fileId.join(',') : fileId;
  return request({
    url: `/system/file/${ids}`,
    method: 'delete',
  });
}
