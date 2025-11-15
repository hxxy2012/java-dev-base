package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysDictData;
import com.enterprisex.system.domain.SysDictType;
import com.enterprisex.system.mapper.SysDictDataMapper;
import com.enterprisex.system.mapper.SysDictTypeMapper;
import com.enterprisex.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 字典类型业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();

        // 字典名称模糊查询
        if (StringUtils.isNotEmpty(dictType.getDictName())) {
            queryWrapper.like(SysDictType::getDictName, dictType.getDictName());
        }

        // 字典类型模糊查询
        if (StringUtils.isNotEmpty(dictType.getDictType())) {
            queryWrapper.like(SysDictType::getDictType, dictType.getDictType());
        }

        // 状态查询
        if (dictType.getStatus() != null) {
            queryWrapper.eq(SysDictType::getStatus, dictType.getStatus());
        }

        // 按照字典ID升序
        queryWrapper.orderByAsc(SysDictType::getDictId);

        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDictType(SysDictType dictType) {
        // 校验字典类型唯一性
        if (!checkDictTypeUnique(dictType)) {
            throw new ServiceException("新增字典类型'" + dictType.getDictName() + "'失败，字典类型已存在");
        }

        return baseMapper.insert(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dictType) {
        // 校验字典类型唯一性
        if (!checkDictTypeUnique(dictType)) {
            throw new ServiceException("修改字典类型'" + dictType.getDictName() + "'失败，字典类型已存在");
        }

        return baseMapper.updateById(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = baseMapper.selectById(dictId);
            if (dictType != null) {
                // 检查是否有字典数据关联
                LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysDictData::getDictType, dictType.getDictType());
                long count = dictDataMapper.selectCount(queryWrapper);
                if (count > 0) {
                    throw new ServiceException("字典类型'" + dictType.getDictName() + "'已分配字典数据，不能删除");
                }
            }
        }

        return baseMapper.deleteBatchIds(Arrays.asList(dictIds));
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictType::getDictType, dictType.getDictType());

        if (dictType.getDictId() != null) {
            queryWrapper.ne(SysDictType::getDictId, dictType.getDictId());
        }

        return count(queryWrapper) == 0;
    }
}
