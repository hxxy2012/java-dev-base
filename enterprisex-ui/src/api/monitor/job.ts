import request from '@/utils/request';

/**
 * 定时任务类型
 */
export interface Job {
  jobId?: number;
  jobName: string;
  jobGroup: string;
  invokeTarget: string;
  cronExpression: string;
  misfirePolicy?: string;
  concurrent?: number;
  status?: number;
  createBy?: string;
  createTime?: string;
  updateBy?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * 定时任务日志类型
 */
export interface JobLog {
  jobLogId: number;
  jobName: string;
  jobGroup: string;
  invokeTarget: string;
  jobMessage: string;
  status: number;
  exceptionInfo?: string;
  createTime: string;
}

/**
 * 查询定时任务列表
 */
export function listJob(params: any) {
  return request({
    url: '/monitor/job/list',
    method: 'get',
    params,
  });
}

/**
 * 查询定时任务详细
 */
export function getJob(jobId: number) {
  return request({
    url: `/monitor/job/${jobId}`,
    method: 'get',
  });
}

/**
 * 新增定时任务
 */
export function addJob(data: Job) {
  return request({
    url: '/monitor/job',
    method: 'post',
    data,
  });
}

/**
 * 修改定时任务
 */
export function updateJob(data: Job) {
  return request({
    url: '/monitor/job',
    method: 'put',
    data,
  });
}

/**
 * 删除定时任务
 */
export function delJob(jobId: number | number[]) {
  const ids = Array.isArray(jobId) ? jobId.join(',') : jobId;
  return request({
    url: `/monitor/job/${ids}`,
    method: 'delete',
  });
}

/**
 * 任务状态修改
 */
export function changeJobStatus(jobId: number, status: number, jobGroup: string) {
  return request({
    url: '/monitor/job/changeStatus',
    method: 'put',
    data: {
      jobId,
      status,
      jobGroup,
    },
  });
}

/**
 * 定时任务立即执行一次
 */
export function runJob(jobId: number, jobGroup: string) {
  return request({
    url: '/monitor/job/run',
    method: 'post',
    data: {
      jobId,
      jobGroup,
    },
  });
}

/**
 * 查询调度日志列表
 */
export function listJobLog(params: any) {
  return request({
    url: '/monitor/job/log/list',
    method: 'get',
    params,
  });
}

/**
 * 删除调度日志
 */
export function delJobLog(jobLogId: number | number[]) {
  const ids = Array.isArray(jobLogId) ? jobLogId.join(',') : jobLogId;
  return request({
    url: `/monitor/job/log/${ids}`,
    method: 'delete',
  });
}

/**
 * 清空调度日志
 */
export function cleanJobLog() {
  return request({
    url: '/monitor/job/log/clean',
    method: 'delete',
  });
}
