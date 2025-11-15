package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysDictData;
import com.enterprisex.system.service.ISysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 获取字典数据列表
     */
    @Operation(summary = "查询字典数据列表")
    @GetMapping("/list")
    public TableDataInfo<SysDictData> list(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Operation(summary = "根据字典类型查询字典数据")
    @GetMapping("/type/{dictType}")
    public R<List<SysDictData>> dictType(@PathVariable String dictType) {
        List<SysDictData> list = dictDataService.selectDictDataByType(dictType);
        return R.ok(list);
    }

    /**
     * 根据字典数据编码获取详细信息
     */
    @Operation(summary = "获取字典数据详情")
    @GetMapping("/{dictCode}")
    public R<SysDictData> getInfo(@PathVariable Long dictCode) {
        SysDictData dictData = dictDataService.getById(dictCode);
        if (dictData == null) {
            return R.fail("字典数据不存在");
        }
        return R.ok(dictData);
    }

    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDictData dictData) {
        return R.toAjax(dictDataService.insertDictData(dictData));
    }

    /**
     * 修改字典数据
     */
    @Operation(summary = "修改字典数据")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDictData dictData) {
        return R.toAjax(dictDataService.updateDictData(dictData));
    }

    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据")
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        return R.toAjax(dictDataService.deleteDictDataByIds(dictCodes));
    }
}
