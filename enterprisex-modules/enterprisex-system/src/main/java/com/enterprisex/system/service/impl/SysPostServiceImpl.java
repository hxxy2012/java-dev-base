package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysPost;
import com.enterprisex.system.mapper.SysPostMapper;
import com.enterprisex.system.service.ISysPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 岗位业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements ISysPostService {

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        LambdaQueryWrapper<SysPost> queryWrapper = new LambdaQueryWrapper<>();

        // 岗位编码模糊查询
        if (StringUtils.isNotEmpty(post.getPostCode())) {
            queryWrapper.like(SysPost::getPostCode, post.getPostCode());
        }

        // 岗位名称模糊查询
        if (StringUtils.isNotEmpty(post.getPostName())) {
            queryWrapper.like(SysPost::getPostName, post.getPostName());
        }

        // 状态查询
        if (post.getStatus() != null) {
            queryWrapper.eq(SysPost::getStatus, post.getStatus());
        }

        // 按照排序字段升序
        queryWrapper.orderByAsc(SysPost::getPostSort);

        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPost(SysPost post) {
        // 校验岗位编码唯一性
        if (!checkPostCodeUnique(post)) {
            throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }

        // 校验岗位名称唯一性
        if (!checkPostNameUnique(post)) {
            throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }

        return baseMapper.insert(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePost(SysPost post) {
        // 校验岗位编码唯一性
        if (!checkPostCodeUnique(post)) {
            throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }

        // 校验岗位名称唯一性
        if (!checkPostNameUnique(post)) {
            throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }

        return baseMapper.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePostByIds(Long[] postIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(postIds));
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        LambdaQueryWrapper<SysPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPost::getPostCode, post.getPostCode());

        if (post.getPostId() != null) {
            queryWrapper.ne(SysPost::getPostId, post.getPostId());
        }

        return count(queryWrapper) == 0;
    }

    @Override
    public boolean checkPostNameUnique(SysPost post) {
        LambdaQueryWrapper<SysPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPost::getPostName, post.getPostName());

        if (post.getPostId() != null) {
            queryWrapper.ne(SysPost::getPostId, post.getPostId());
        }

        return count(queryWrapper) == 0;
    }
}
