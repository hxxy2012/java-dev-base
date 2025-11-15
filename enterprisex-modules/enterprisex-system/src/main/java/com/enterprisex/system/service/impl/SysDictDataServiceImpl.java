package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.utils.StringUtils;
import com.enterprisex.system.domain.SysDictData;
import com.enterprisex.system.mapper.SysDictDataMapper;
import com.enterprisex.system.service.ISysDictDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 字典数据业务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();

        // 字典类型查询
        if (StringUtils.isNotEmpty(dictData.getDictType())) {
            queryWrapper.eq(SysDictData::getDictType, dictData.getDictType());
        }

        // 字典标签模糊查询
        if (StringUtils.isNotEmpty(dictData.getDictLabel())) {
            queryWrapper.like(SysDictData::getDictLabel, dictData.getDictLabel());
        }

        // 状态查询
        if (dictData.getStatus() != null) {
            queryWrapper.eq(SysDictData::getStatus, dictData.getStatus());
        }

        // 按照排序字段升序
        queryWrapper.orderByAsc(SysDictData::getDictSort);

        return list(queryWrapper);
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictType, dictType);
        queryWrapper.eq(SysDictData::getStatus, 1); // 只查询正常状态的数据
        queryWrapper.orderByAsc(SysDictData::getDictSort);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDictData(SysDictData dictData) {
        return baseMapper.insert(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictData(SysDictData dictData) {
        return baseMapper.updateById(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDictDataByIds(Long[] dictCodes) {
        return baseMapper.deleteBatchIds(Arrays.asList(dictCodes));
    }
}
