package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.system.domain.SysNotice;
import com.enterprisex.system.mapper.SysNoticeMapper;
import com.enterprisex.system.service.ISysNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 通知公告 服务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {

    /**
     * 查询通知公告列表
     *
     * @param notice 通知公告
     * @return 通知公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(notice.getNoticeTitle())) {
            queryWrapper.like(SysNotice::getNoticeTitle, notice.getNoticeTitle());
        }
        if (notice.getNoticeType() != null) {
            queryWrapper.eq(SysNotice::getNoticeType, notice.getNoticeType());
        }
        if (notice.getStatus() != null) {
            queryWrapper.eq(SysNotice::getStatus, notice.getStatus());
        }
        if (StringUtils.hasText(notice.getCreateBy())) {
            queryWrapper.like(SysNotice::getCreateBy, notice.getCreateBy());
        }

        queryWrapper.orderByDesc(SysNotice::getCreateTime);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 新增通知公告
     *
     * @param notice 通知公告
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNotice(SysNotice notice) {
        return baseMapper.insert(notice);
    }

    /**
     * 修改通知公告
     *
     * @param notice 通知公告
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateNotice(SysNotice notice) {
        return baseMapper.updateById(notice);
    }

    /**
     * 批量删除通知公告
     *
     * @param noticeIds 需要删除的通知公告ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeByIds(Long[] noticeIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(noticeIds));
    }
}
