package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.system.domain.SysDept;
import com.enterprisex.system.service.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @Operation(summary = "查询部门列表")
    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {
        List<SysDept> list = deptService.selectDeptList(dept);
        return R.ok(list);
    }

    /**
     * 获取部门树结构
     */
    @Operation(summary = "查询部门树")
    @GetMapping("/tree")
    public R<List<SysDept>> tree(SysDept dept) {
        List<SysDept> list = deptService.selectDeptList(dept);
        List<SysDept> tree = deptService.buildDeptTree(list);
        return R.ok(tree);
    }

    /**
     * 根据部门ID获取详细信息
     */
    @Operation(summary = "获取部门详情")
    @GetMapping("/{deptId}")
    public R<SysDept> getInfo(@PathVariable Long deptId) {
        SysDept dept = deptService.getById(deptId);
        if (dept == null) {
            return R.fail("部门不存在");
        }
        return R.ok(dept);
    }

    /**
     * 新增部门
     */
    @Operation(summary = "新增部门")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDept dept) {
        return R.toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @Operation(summary = "修改部门")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDept dept) {
        return R.toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @Operation(summary = "删除部门")
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        return R.toAjax(deptService.deleteDeptById(deptId));
    }
}
