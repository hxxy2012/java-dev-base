package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysNotice;

import java.util.List;

/**
 * 通知公告 服务层
 *
 * @author EnterpriseX
 */
public interface ISysNoticeService extends IService<SysNotice> {

    /**
     * 查询通知公告列表
     *
     * @param notice 通知公告
     * @return 通知公告集合
     */
    List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增通知公告
     *
     * @param notice 通知公告
     * @return 结果
     */
    int insertNotice(SysNotice notice);

    /**
     * 修改通知公告
     *
     * @param notice 通知公告
     * @return 结果
     */
    int updateNotice(SysNotice notice);

    /**
     * 批量删除通知公告
     *
     * @param noticeIds 需要删除的通知公告ID
     * @return 结果
     */
    int deleteNoticeByIds(Long[] noticeIds);
}
