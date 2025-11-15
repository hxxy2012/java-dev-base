package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysPost;

import java.util.List;

/**
 * 岗位业务层
 *
 * @author EnterpriseX
 */
public interface ISysPostService extends IService<SysPost> {

    /**
     * 查询岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 新增岗位
     */
    int insertPost(SysPost post);

    /**
     * 修改岗位
     */
    int updatePost(SysPost post);

    /**
     * 删除岗位
     */
    int deletePostByIds(Long[] postIds);

    /**
     * 校验岗位编码是否唯一
     */
    boolean checkPostCodeUnique(SysPost post);

    /**
     * 校验岗位名称是否唯一
     */
    boolean checkPostNameUnique(SysPost post);
}
