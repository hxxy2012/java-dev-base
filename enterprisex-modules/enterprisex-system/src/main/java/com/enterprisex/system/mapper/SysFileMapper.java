package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件信息Mapper接口
 */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {
}
