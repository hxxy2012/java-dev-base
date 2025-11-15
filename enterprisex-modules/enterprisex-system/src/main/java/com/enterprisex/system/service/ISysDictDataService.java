package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysDictData;

import java.util.List;

/**
 * 字典数据业务层
 *
 * @author EnterpriseX
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /**
     * 查询字典数据列表
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 新增字典数据
     */
    int insertDictData(SysDictData dictData);

    /**
     * 修改字典数据
     */
    int updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     */
    int deleteDictDataByIds(Long[] dictCodes);
}
