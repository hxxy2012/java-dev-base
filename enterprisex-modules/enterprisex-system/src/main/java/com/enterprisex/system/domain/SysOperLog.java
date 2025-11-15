package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志对象 sys_oper_log
 *
 * @author EnterpriseX
 */
@Data
@TableName("sys_oper_log")
@Schema(description = "操作日志对象")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "oper_id", type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long operId;

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "业务类型：0其它1新增2修改3删除4授权5导出6导入7强退8清空")
    private Integer businessType;

    @Schema(description = "方法名称")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "操作类别：0其它1后台用户2手机端用户")
    private Integer operatorType;

    @Schema(description = "操作人员")
    private String operName;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "请求URL")
    private String operUrl;

    @Schema(description = "主机地址")
    private String operIp;

    @Schema(description = "操作地点")
    private String operLocation;

    @Schema(description = "请求参数")
    private String operParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态：0失败1成功")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间")
    private Date operTime;

    @Schema(description = "消耗时间（毫秒）")
    private Long costTime;
}
