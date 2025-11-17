import request from '@/utils/request';

/**
 * 备份信息
 */
export interface BackupInfo {
  fileName: string;
  filePath: string;
  fileSize: number;
  fileSizeStr: string;
  createTime: string;
}

/**
 * 创建数据库备份
 */
export const createBackup = () => {
  return request({
    url: '/system/backup/create',
    method: 'post',
  });
};

/**
 * 获取备份列表
 */
export const getBackupList = () => {
  return request<BackupInfo[]>({
    url: '/system/backup/list',
    method: 'get',
  });
};

/**
 * 下载备份文件
 */
export const downloadBackup = (fileName: string) => {
  return `/api/system/backup/download/${fileName}`;
};

/**
 * 恢复数据库备份
 */
export const restoreBackup = (fileName: string) => {
  return request({
    url: `/system/backup/restore/${fileName}`,
    method: 'post',
  });
};

/**
 * 删除备份文件
 */
export const deleteBackup = (fileName: string) => {
  return request({
    url: `/system/backup/${fileName}`,
    method: 'delete',
  });
};

/**
 * 上传备份文件
 */
export const uploadBackup = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/system/backup/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
