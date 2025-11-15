package com.enterprisex.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprisex.system.domain.SysNotice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告 数据层
 *
 * @author EnterpriseX
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {
}
