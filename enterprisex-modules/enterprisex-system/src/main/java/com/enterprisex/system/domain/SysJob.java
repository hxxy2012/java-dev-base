package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enterprisex.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务对象 sys_job
 *
 * @author EnterpriseX
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job")
@Schema(description = "定时任务对象")
public class SysJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "job_id", type = IdType.AUTO)
    @Schema(description = "任务ID")
    private Long jobId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称长度不能超过64个字符")
    @Schema(description = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @NotBlank(message = "任务组名不能为空")
    @Size(max = 64, message = "任务组名长度不能超过64个字符")
    @Schema(description = "任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @NotBlank(message = "调用目标不能为空")
    @Size(max = 500, message = "调用目标长度不能超过500个字符")
    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @Size(max = 255, message = "cron表达式长度不能超过255个字符")
    @Schema(description = "cron执行表达式")
    private String cronExpression;

    /**
     * 计划执行错误策略：1立即执行 2执行一次 3放弃执行
     */
    @Schema(description = "计划执行错误策略：1立即执行 2执行一次 3放弃执行")
    private String misfirePolicy;

    /**
     * 是否并发执行：0禁止 1允许
     */
    @Schema(description = "是否并发执行：0禁止 1允许")
    private Integer concurrent;

    /**
     * 状态：0暂停 1正常
     */
    @Schema(description = "状态：0暂停 1正常")
    private Integer status;
}
