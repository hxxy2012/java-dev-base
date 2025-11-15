package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysDept;
import com.enterprisex.system.domain.SysUser;
import com.enterprisex.system.mapper.SysDeptMapper;
import com.enterprisex.system.mapper.SysUserMapper;
import com.enterprisex.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();

        // 部门名称模糊查询
        if (StringUtils.isNotEmpty(dept.getDeptName())) {
            queryWrapper.like(SysDept::getDeptName, dept.getDeptName());
        }

        // 状态查询
        if (dept.getStatus() != null) {
            queryWrapper.eq(SysDept::getStatus, dept.getStatus());
        }

        // 父部门ID查询
        if (dept.getParentId() != null) {
            queryWrapper.eq(SysDept::getParentId, dept.getParentId());
        }

        // 过滤删除的数据
        queryWrapper.eq(SysDept::getDelFlag, 0);

        // 按照排序字段升序
        queryWrapper.orderByAsc(SysDept::getOrderNum);

        return list(queryWrapper);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());

        for (SysDept dept : depts) {
            // 如果是顶级节点（父部门不在当前列表中），直接添加到结果集
            if (dept.getParentId() == null || dept.getParentId() == 0L || !tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }

        if (returnList.isEmpty()) {
            returnList = depts;
        }

        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept dept) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, dept);
        dept.setChildren(childList);

        for (SysDept child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept dept) {
        List<SysDept> childList = new ArrayList<>();

        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(dept.getDeptId())) {
                childList.add(n);
            }
        }

        return childList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept dept) {
        return getChildList(list, dept).size() > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDept(SysDept dept) {
        // 校验部门名称唯一性
        if (!checkDeptNameUnique(dept)) {
            throw new ServiceException("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }

        // 设置祖级列表
        if (dept.getParentId() != null && dept.getParentId() != 0L) {
            SysDept parentDept = baseMapper.selectById(dept.getParentId());
            if (parentDept != null) {
                dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
            }
        } else {
            dept.setAncestors("0");
        }

        return baseMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept dept) {
        // 校验部门名称唯一性
        if (!checkDeptNameUnique(dept)) {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }

        // 不能将自己设置为父部门
        if (dept.getDeptId() != null && dept.getDeptId().equals(dept.getParentId())) {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，不能将自己设置为父部门");
        }

        // 更新祖级列表
        if (dept.getParentId() != null && dept.getParentId() != 0L) {
            SysDept parentDept = baseMapper.selectById(dept.getParentId());
            if (parentDept != null) {
                String newAncestors = parentDept.getAncestors() + "," + dept.getParentId();
                String oldAncestors = dept.getAncestors();
                dept.setAncestors(newAncestors);
                updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
            }
        } else {
            dept.setAncestors("0");
        }

        return baseMapper.updateById(dept);
    }

    /**
     * 修改子元素关系
     */
    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.apply("FIND_IN_SET({0}, ancestors)", deptId);
        List<SysDept> children = baseMapper.selectList(queryWrapper);

        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }

        if (!children.isEmpty()) {
            children.forEach(child -> baseMapper.updateById(child));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptById(Long deptId) {
        // 存在子部门，不允许删除
        if (hasChildByDeptId(deptId)) {
            throw new ServiceException("存在子部门，不允许删除");
        }

        // 部门下存在用户，不允许删除
        if (hasDeptUser(deptId)) {
            throw new ServiceException("部门下存在用户，不允许删除");
        }

        return baseMapper.deleteById(deptId);
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getDeptName, dept.getDeptName());
        queryWrapper.eq(SysDept::getParentId, dept.getParentId());
        queryWrapper.eq(SysDept::getDelFlag, 0);

        if (dept.getDeptId() != null) {
            queryWrapper.ne(SysDept::getDeptId, dept.getDeptId());
        }

        return count(queryWrapper) == 0;
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getParentId, deptId);
        queryWrapper.eq(SysDept::getDelFlag, 0);
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean hasDeptUser(Long deptId) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeptId, deptId);
        return userMapper.selectCount(queryWrapper) > 0;
    }
}
