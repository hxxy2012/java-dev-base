package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.common.redis.service.RedisCache;
import com.enterprisex.system.domain.SysConfig;
import com.enterprisex.system.mapper.SysConfigMapper;
import com.enterprisex.system.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 参数配置 服务层实现
 *
 * @author EnterpriseX
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 系统配置缓存Key前缀
     */
    private static final String CONFIG_CACHE_KEY = "sys_config:";

    /**
     * 缓存过期时间（分钟）
     */
    private static final int CACHE_EXPIRE_MINUTES = 30;

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(config.getConfigName())) {
            queryWrapper.like(SysConfig::getConfigName, config.getConfigName());
        }
        if (StringUtils.hasText(config.getConfigKey())) {
            queryWrapper.like(SysConfig::getConfigKey, config.getConfigKey());
        }
        if (config.getConfigType() != null) {
            queryWrapper.eq(SysConfig::getConfigType, config.getConfigType());
        }

        queryWrapper.orderByAsc(SysConfig::getConfigId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据参数键名查询参数值（带缓存）
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }

        // 尝试从缓存获取
        String cacheKey = CONFIG_CACHE_KEY + configKey;
        String cachedValue = redisCache.getCacheObject(cacheKey);
        if (cachedValue != null) {
            log.debug("从缓存获取系统配置: configKey={}, value={}", configKey, cachedValue);
            return cachedValue;
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = baseMapper.selectOne(queryWrapper);

        if (config != null && StringUtils.hasText(config.getConfigValue())) {
            // 存入缓存
            redisCache.setCacheObject(cacheKey, config.getConfigValue(), CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("系统配置已缓存: configKey={}, value={}", configKey, config.getConfigValue());
            return config.getConfigValue();
        }

        return null;
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置
     * @return true 唯一 / false 不唯一
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        Long configId = config.getConfigId() == null ? -1L : config.getConfigId();
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey, config.getConfigKey());
        SysConfig info = baseMapper.selectOne(queryWrapper);
        return info == null || info.getConfigId().equals(configId);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConfig(SysConfig config) {
        if (!checkConfigKeyUnique(config)) {
            throw new ServiceException("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        int result = baseMapper.insert(config);
        if (result > 0) {
            // 清除缓存
            clearConfigCache(config.getConfigKey());
        }
        return result;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConfig(SysConfig config) {
        if (!checkConfigKeyUnique(config)) {
            throw new ServiceException("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        int result = baseMapper.updateById(config);
        if (result > 0) {
            // 清除缓存
            clearConfigCache(config.getConfigKey());
            log.info("修改系统配置并清除缓存: configKey={}", config.getConfigKey());
        }
        return result;
    }

    /**
     * 批量删除参数配置
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConfigByIds(Long[] configIds) {
        // 先查询所有配置并验证
        List<SysConfig> configsToDelete = new java.util.ArrayList<>();
        for (Long configId : configIds) {
            SysConfig config = baseMapper.selectById(configId);
            if (config != null) {
                if (config.getConfigType() == 1) {
                    throw new ServiceException("内置参数【" + config.getConfigKey() + "】不能删除");
                }
                configsToDelete.add(config);
            }
        }

        // 执行删除
        int result = baseMapper.deleteBatchIds(Arrays.asList(configIds));
        if (result > 0) {
            // 清除已删除配置的缓存
            for (SysConfig config : configsToDelete) {
                clearConfigCache(config.getConfigKey());
            }
            log.info("删除系统配置并清除缓存: count={}", result);
        }
        return result;
    }

    /**
     * 清除指定配置的缓存
     *
     * @param configKey 参数键名
     */
    private void clearConfigCache(String configKey) {
        if (StringUtils.hasText(configKey)) {
            String cacheKey = CONFIG_CACHE_KEY + configKey;
            redisCache.deleteObject(cacheKey);
            log.debug("清除系统配置缓存: configKey={}", configKey);
        }
    }

    /**
     * 清除所有配置缓存
     */
    public void clearAllConfigCache() {
        try {
            java.util.Collection<String> keys = redisCache.keys(CONFIG_CACHE_KEY + "*");
            if (keys != null && !keys.isEmpty()) {
                redisCache.deleteObject(keys);
                log.info("清除所有系统配置缓存: count={}", keys.size());
            }
        } catch (Exception e) {
            log.error("清除系统配置缓存失败", e);
        }
    }
}
