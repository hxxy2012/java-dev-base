package com.enterprisex.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 参数配置表 sys_config
 *
 * @author EnterpriseX
 */
@Data
@TableName("sys_config")
@Schema(description = "参数配置")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    @TableId(value = "config_id", type = IdType.AUTO)
    @Schema(description = "参数主键")
    private Long configId;

    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称长度不能超过100个字符")
    @Schema(description = "参数名称")
    private String configName;

    /**
     * 参数键名
     */
    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    @Schema(description = "参数键名")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    @Schema(description = "参数键值")
    private String configValue;

    /**
     * 系统内置（1是 0否）
     */
    @Schema(description = "系统内置（1是 0否）")
    private Integer configType;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
