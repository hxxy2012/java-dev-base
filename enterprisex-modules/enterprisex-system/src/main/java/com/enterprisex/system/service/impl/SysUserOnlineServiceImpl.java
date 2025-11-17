package com.enterprisex.system.service.impl;

import com.enterprisex.common.core.constant.CacheConstants;
import com.enterprisex.common.redis.service.RedisCache;
import com.enterprisex.system.domain.SysUserOnline;
import com.enterprisex.system.service.ISysUserOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线用户服务实现
 *
 * @author EnterpriseX
 */
@Slf4j
@Service
public class SysUserOnlineServiceImpl implements ISysUserOnlineService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 在线用户缓存Key前缀
     */
    private static final String ONLINE_USER_KEY = CacheConstants.ONLINE_TOKEN_KEY;

    @Override
    public List<SysUserOnline> selectOnlineList() {
        try {
            Collection<String> keys = redisCache.keys(ONLINE_USER_KEY + "*");
            List<SysUserOnline> userOnlineList = new ArrayList<>();

            for (String key : keys) {
                SysUserOnline userOnline = redisCache.getCacheObject(key);
                if (userOnline != null) {
                    userOnlineList.add(userOnline);
                }
            }

            // 按登录时间倒序排序
            return userOnlineList.stream()
                    .sorted((o1, o2) -> Long.compare(o2.getLoginTime(), o1.getLoginTime()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询在线用户失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public SysUserOnline selectOnlineByToken(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return null;
        }
        return redisCache.getCacheObject(ONLINE_USER_KEY + tokenId);
    }

    @Override
    public List<SysUserOnline> selectOnlineByUserName(String username) {
        if (username == null || username.isEmpty()) {
            return new ArrayList<>();
        }

        return selectOnlineList().stream()
                .filter(user -> username.equals(user.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean forceLogout(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        try {
            redisCache.deleteObject(ONLINE_USER_KEY + tokenId);
            log.info("强制登出用户: tokenId={}", tokenId);
            return true;
        } catch (Exception e) {
            log.error("强制登出用户失败: tokenId={}", tokenId, e);
            return false;
        }
    }

    @Override
    public int batchForceLogout(String[] tokenIds) {
        if (tokenIds == null || tokenIds.length == 0) {
            return 0;
        }

        int count = 0;
        for (String tokenId : tokenIds) {
            if (forceLogout(tokenId)) {
                count++;
            }
        }

        return count;
    }
}
