package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 数据库管理Controller
 */
@Slf4j
@Tag(name = "数据库管理")
@RestController
@RequestMapping("/system/database")
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有数据库表
     */
    @Operation(summary = "获取所有数据库表")
    @GetMapping("/tables")
    public R<List<Map<String, Object>>> getTables() {
        String sql = "SELECT " +
                "TABLE_NAME as tableName, " +
                "TABLE_COMMENT as tableComment, " +
                "TABLE_ROWS as tableRows, " +
                "DATA_LENGTH as dataLength, " +
                "INDEX_LENGTH as indexLength, " +
                "CREATE_TIME as createTime, " +
                "UPDATE_TIME as updateTime " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "ORDER BY CREATE_TIME DESC";

        List<Map<String, Object>> tables = jdbcTemplate.queryForList(sql);
        return R.ok(tables);
    }

    /**
     * 获取表结构
     */
    @Operation(summary = "获取表结构")
    @GetMapping("/table/{tableName}")
    public R<Map<String, Object>> getTableInfo(@PathVariable String tableName) {
        Map<String, Object> result = new HashMap<>();

        // 获取表信息
        String tableSql = "SELECT " +
                "TABLE_NAME as tableName, " +
                "TABLE_COMMENT as tableComment, " +
                "TABLE_ROWS as tableRows, " +
                "DATA_LENGTH as dataLength, " +
                "INDEX_LENGTH as indexLength, " +
                "CREATE_TIME as createTime, " +
                "UPDATE_TIME as updateTime, " +
                "ENGINE as engine, " +
                "TABLE_COLLATION as collation " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";

        List<Map<String, Object>> tableInfo = jdbcTemplate.queryForList(tableSql, tableName);
        if (!tableInfo.isEmpty()) {
            result.put("tableInfo", tableInfo.get(0));
        }

        // 获取列信息
        String columnSql = "SELECT " +
                "COLUMN_NAME as columnName, " +
                "COLUMN_TYPE as columnType, " +
                "IS_NULLABLE as isNullable, " +
                "COLUMN_KEY as columnKey, " +
                "COLUMN_DEFAULT as columnDefault, " +
                "EXTRA as extra, " +
                "COLUMN_COMMENT as columnComment " +
                "FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                "ORDER BY ORDINAL_POSITION";

        List<Map<String, Object>> columns = jdbcTemplate.queryForList(columnSql, tableName);
        result.put("columns", columns);

        // 获取索引信息
        String indexSql = "SELECT " +
                "INDEX_NAME as indexName, " +
                "COLUMN_NAME as columnName, " +
                "NON_UNIQUE as nonUnique, " +
                "INDEX_TYPE as indexType " +
                "FROM information_schema.STATISTICS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                "ORDER BY INDEX_NAME, SEQ_IN_INDEX";

        List<Map<String, Object>> indexes = jdbcTemplate.queryForList(indexSql, tableName);
        result.put("indexes", indexes);

        return R.ok(result);
    }

    /**
     * 执行SQL查询（只读）
     */
    @Operation(summary = "执行SQL查询")
    @PostMapping("/query")
    public R<Map<String, Object>> executeQuery(@RequestBody Map<String, String> params) {
        String sql = params.get("sql");

        if (sql == null || sql.trim().isEmpty()) {
            return R.fail("SQL语句不能为空");
        }

        // 安全检查：只允许SELECT语句
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("SELECT")) {
            return R.fail("只允许执行SELECT查询语句");
        }

        // 限制返回行数
        if (!upperSql.contains("LIMIT")) {
            sql = sql + " LIMIT 1000";
        }

        try {
            long startTime = System.currentTimeMillis();
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            long executionTime = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            response.put("rowCount", result.size());
            response.put("executionTime", executionTime + "ms");

            return R.ok(response);
        } catch (Exception e) {
            log.error("SQL execution failed", e);
            return R.fail("SQL执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据库统计信息
     */
    @Operation(summary = "获取数据库统计信息")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 数据库大小
        String sizeSql = "SELECT " +
                "SUM(DATA_LENGTH + INDEX_LENGTH) as totalSize, " +
                "SUM(DATA_LENGTH) as dataSize, " +
                "SUM(INDEX_LENGTH) as indexSize " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE()";

        List<Map<String, Object>> sizeInfo = jdbcTemplate.queryForList(sizeSql);
        if (!sizeInfo.isEmpty()) {
            stats.put("size", sizeInfo.get(0));
        }

        // 表统计
        String tableCountSql = "SELECT COUNT(*) as tableCount " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE()";

        Integer tableCount = jdbcTemplate.queryForObject(tableCountSql, Integer.class);
        stats.put("tableCount", tableCount);

        // 总行数
        String rowCountSql = "SELECT SUM(TABLE_ROWS) as totalRows " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE()";

        Long totalRows = jdbcTemplate.queryForObject(rowCountSql, Long.class);
        stats.put("totalRows", totalRows);

        // 数据库版本
        String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        stats.put("version", version);

        // 当前数据库
        String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        stats.put("database", dbName);

        // 字符集
        String charset = jdbcTemplate.queryForObject(
                "SELECT DEFAULT_CHARACTER_SET_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = DATABASE()",
                String.class);
        stats.put("charset", charset);

        return R.ok(stats);
    }

    /**
     * 获取慢查询日志
     */
    @Operation(summary = "获取慢查询统计")
    @GetMapping("/slow-queries")
    public R<Map<String, Object>> getSlowQueries() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查慢查询日志是否开启
            String slowQueryLog = jdbcTemplate.queryForObject(
                    "SELECT @@slow_query_log", String.class);
            result.put("slowQueryLogEnabled", "ON".equals(slowQueryLog));

            // 获取慢查询时间阈值
            String longQueryTime = jdbcTemplate.queryForObject(
                    "SELECT @@long_query_time", String.class);
            result.put("longQueryTime", longQueryTime + "s");

        } catch (Exception e) {
            log.warn("Failed to get slow query info", e);
            result.put("slowQueryLogEnabled", false);
            result.put("message", "慢查询日志功能未启用");
        }

        return R.ok(result);
    }

    /**
     * 优化表
     */
    @Operation(summary = "优化表")
    @PostMapping("/optimize/{tableName}")
    public R<String> optimizeTable(@PathVariable String tableName) {
        try {
            jdbcTemplate.execute("OPTIMIZE TABLE " + tableName);
            return R.ok("表优化成功");
        } catch (Exception e) {
            log.error("Failed to optimize table: " + tableName, e);
            return R.fail("表优化失败: " + e.getMessage());
        }
    }

    /**
     * 分析表
     */
    @Operation(summary = "分析表")
    @PostMapping("/analyze/{tableName}")
    public R<String> analyzeTable(@PathVariable String tableName) {
        try {
            jdbcTemplate.execute("ANALYZE TABLE " + tableName);
            return R.ok("表分析成功");
        } catch (Exception e) {
            log.error("Failed to analyze table: " + tableName, e);
            return R.fail("表分析失败: " + e.getMessage());
        }
    }
}
