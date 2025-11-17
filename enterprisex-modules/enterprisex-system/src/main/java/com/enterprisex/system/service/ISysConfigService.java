package com.enterprisex.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enterprisex.system.domain.SysConfig;

import java.util.List;

/**
 * 参数配置 服务层
 *
 * @author EnterpriseX
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置
     * @return 参数配置集合
     */
    List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置
     * @return true 唯一 / false 不唯一
     */
    boolean checkConfigKeyUnique(SysConfig config);

    /**
     * 新增参数配置
     *
     * @param config 参数配置
     * @return 结果
     */
    int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     *
     * @param config 参数配置
     * @return 结果
     */
    int updateConfig(SysConfig config);

    /**
     * 批量删除参数配置
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    int deleteConfigByIds(Long[] configIds);

    /**
     * 清除所有配置缓存
     */
    void clearAllConfigCache();
}
