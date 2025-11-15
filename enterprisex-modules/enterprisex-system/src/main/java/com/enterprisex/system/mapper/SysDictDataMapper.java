package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysDictData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典数据表 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {
}
