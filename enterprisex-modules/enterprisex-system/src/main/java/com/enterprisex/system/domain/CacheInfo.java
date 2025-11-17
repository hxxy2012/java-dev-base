package com.enterprisex.system.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 缓存信息
 *
 * @author EnterpriseX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 缓存键名
     */
    private String cacheKey;

    /**
     * 缓存值
     */
    private String cacheValue;

    /**
     * 过期时间（秒）
     */
    private Long expireTime;

    /**
     * 备注
     */
    private String remark;
}
