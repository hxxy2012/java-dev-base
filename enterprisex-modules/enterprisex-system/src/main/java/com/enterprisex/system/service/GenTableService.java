package com.enterprisex.system.service;

import com.enterprisex.system.domain.GenTable;
import com.enterprisex.system.domain.GenTableColumn;

import java.util.List;
import java.util.Map;

/**
 * 代码生成服务接口
 *
 * @author EnterpriseX
 */
public interface GenTableService {

    /**
     * 查询数据库表列表
     *
     * @param tableName  表名称
     * @param tableComment 表描述
     * @return 数据库表集合
     */
    List<GenTable> selectDbTableList(String tableName, String tableComment);

    /**
     * 查询据库列表
     *
     * @param tableName 表名称
     * @return 数据库表列信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName);

    /**
     * 生成代码（下载方式）
     *
     * @param tableName 表名称
     * @return 数据
     */
    byte[] downloadCode(String tableName);

    /**
     * 预览代码
     *
     * @param tableName 表名称
     * @return 预览数据列表
     */
    Map<String, String> previewCode(String tableName);
}
