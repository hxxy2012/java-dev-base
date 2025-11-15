package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysConfig;
import com.enterprisex.system.service.ISysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "参数配置管理")
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @Operation(summary = "查询参数配置列表")
    @GetMapping("/list")
    public TableDataInfo<SysConfig> list(SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据参数编号获取详细信息
     */
    @Operation(summary = "获取参数配置详情")
    @GetMapping("/{configId}")
    public R<SysConfig> getInfo(@PathVariable Long configId) {
        SysConfig config = configService.getById(configId);
        if (config == null) {
            return R.fail("参数配置不存在");
        }
        return R.ok(config);
    }

    /**
     * 根据参数键名查询参数值
     */
    @Operation(summary = "根据参数键名查询参数值")
    @GetMapping("/configKey/{configKey}")
    public R<String> getConfigKey(@PathVariable String configKey) {
        String configValue = configService.selectConfigByKey(configKey);
        return R.ok(configValue);
    }

    /**
     * 新增参数配置
     */
    @Operation(summary = "新增参数配置")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysConfig config) {
        return R.toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @Operation(summary = "修改参数配置")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysConfig config) {
        return R.toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @Operation(summary = "删除参数配置")
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@PathVariable Long[] configIds) {
        return R.toAjax(configService.deleteConfigByIds(configIds));
    }
}
