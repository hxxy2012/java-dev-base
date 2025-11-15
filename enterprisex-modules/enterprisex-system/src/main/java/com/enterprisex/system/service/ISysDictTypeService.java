package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysDictType;

import java.util.List;

/**
 * 字典类型业务层
 *
 * @author EnterpriseX
 */
public interface ISysDictTypeService extends IService<SysDictType> {

    /**
     * 查询字典类型列表
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 新增字典类型
     */
    int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型
     */
    int updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 校验字典类型是否唯一
     */
    boolean checkDictTypeUnique(SysDictType dictType);
}
