package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysDictType;
import com.enterprisex.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     */
    @Operation(summary = "查询字典类型列表")
    @GetMapping("/list")
    public TableDataInfo<SysDictType> list(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据字典类型ID获取详细信息
     */
    @Operation(summary = "获取字典类型详情")
    @GetMapping("/{dictId}")
    public R<SysDictType> getInfo(@PathVariable Long dictId) {
        SysDictType dictType = dictTypeService.getById(dictId);
        if (dictType == null) {
            return R.fail("字典类型不存在");
        }
        return R.ok(dictType);
    }

    /**
     * 新增字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @Operation(summary = "新增字典类型")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDictType dictType) {
        return R.toAjax(dictTypeService.insertDictType(dictType));
    }

    /**
     * 修改字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改字典类型")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDictType dictType) {
        return R.toAjax(dictTypeService.updateDictType(dictType));
    }

    /**
     * 删除字典类型
     */
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{dictIds}")
    public R<Void> remove(@PathVariable Long[] dictIds) {
        return R.toAjax(dictTypeService.deleteDictTypeByIds(dictIds));
    }
}
