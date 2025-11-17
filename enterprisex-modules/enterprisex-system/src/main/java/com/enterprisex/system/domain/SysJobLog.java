package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务日志对象 sys_job_log
 *
 * @author EnterpriseX
 */
@Data
@TableName("sys_job_log")
@Schema(description = "定时任务日志对象")
public class SysJobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务日志ID
     */
    @TableId(value = "job_log_id", type = IdType.AUTO)
    @Schema(description = "任务日志ID")
    private Long jobLogId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @Schema(description = "任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    /**
     * 日志信息
     */
    @Schema(description = "日志信息")
    private String jobMessage;

    /**
     * 执行状态：0失败 1成功
     */
    @Schema(description = "执行状态：0失败 1成功")
    private Integer status;

    /**
     * 异常信息
     */
    @Schema(description = "异常信息")
    private String exceptionInfo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
