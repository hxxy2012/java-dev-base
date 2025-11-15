package com.enterprisex.system.controller;

import com.enterprisex.common.core.annotation.BusinessType;
import com.enterprisex.common.core.annotation.Log;
import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.domain.TableDataInfo;
import com.enterprisex.system.domain.SysPost;
import com.enterprisex.system.service.ISysPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理控制器
 *
 * @author EnterpriseX
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/system/post")
public class SysPostController {

    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @Operation(summary = "查询岗位列表")
    @GetMapping("/list")
    public TableDataInfo<SysPost> list(SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        return TableDataInfo.ok(list, list.size());
    }

    /**
     * 根据岗位ID获取详细信息
     */
    @Operation(summary = "获取岗位详情")
    @GetMapping("/{postId}")
    public R<SysPost> getInfo(@PathVariable Long postId) {
        SysPost post = postService.getById(postId);
        if (post == null) {
            return R.fail("岗位不存在");
        }
        return R.ok(post);
    }

    /**
     * 新增岗位
     */
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @Operation(summary = "新增岗位")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysPost post) {
        return R.toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改岗位")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysPost post) {
        return R.toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除岗位")
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable Long[] postIds) {
        return R.toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 状态修改
     */
    @Operation(summary = "修改岗位状态")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysPost post) {
        SysPost updatePost = new SysPost();
        updatePost.setPostId(post.getPostId());
        updatePost.setStatus(post.getStatus());
        return R.toAjax(postService.updateById(updatePost));
    }
}
