package com.enterprisex.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enterprisex.common.core.exception.ServiceException;
import com.enterprisex.system.domain.SysConfig;
import com.enterprisex.system.mapper.SysConfigMapper;
import com.enterprisex.system.service.ISysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author EnterpriseX
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

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
     * 根据参数键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = baseMapper.selectOne(queryWrapper);
        return config != null ? config.getConfigValue() : null;
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
        return baseMapper.insert(config);
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
        return baseMapper.updateById(config);
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
        for (Long configId : configIds) {
            SysConfig config = baseMapper.selectById(configId);
            if (config != null && config.getConfigType() == 1) {
                throw new ServiceException("内置参数【" + config.getConfigKey() + "】不能删除");
            }
        }
        return baseMapper.deleteBatchIds(Arrays.asList(configIds));
    }
}
