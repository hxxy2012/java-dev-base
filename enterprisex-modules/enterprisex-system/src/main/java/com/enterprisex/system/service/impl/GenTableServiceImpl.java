package com.enterprisex.system.service.impl;

import com.enterprisex.system.domain.GenTable;
import com.enterprisex.system.domain.GenTableColumn;
import com.enterprisex.system.service.GenTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务实现
 *
 * @author EnterpriseX
 */
@Slf4j
@Service
public class GenTableServiceImpl implements GenTableService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<GenTable> selectDbTableList(String tableName, String tableComment) {
        String sql = "SELECT table_name, table_comment, create_time, update_time " +
                "FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) " +
                "AND table_name NOT LIKE 'qrtz_%' " +
                "AND table_name NOT LIKE 'gen_%'";

        List<Object> params = new ArrayList<>();

        if (tableName != null && !tableName.isEmpty()) {
            sql += " AND table_name LIKE ?";
            params.add("%" + tableName + "%");
        }

        if (tableComment != null && !tableComment.isEmpty()) {
            sql += " AND table_comment LIKE ?";
            params.add("%" + tableComment + "%");
        }

        sql += " ORDER BY create_time DESC";

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            GenTable table = new GenTable();
            table.setTableName(rs.getString("table_name"));
            table.setTableComment(rs.getString("table_comment"));
            table.setCreateTime(rs.getTimestamp("create_time") != null ?
                    rs.getTimestamp("create_time").toLocalDateTime() : null);
            // 设置类名
            table.setClassName(convertToCamelCase(rs.getString("table_name")));
            // 设置包名
            table.setPackageName("com.enterprisex.system");
            // 设置模块名
            table.setModuleName("system");
            // 设置业务名
            table.setBusinessName(rs.getString("table_name").replace("sys_", "").replace("_", ""));
            // 设置功能名
            table.setFunctionName(rs.getString("table_comment"));
            // 设置作者
            table.setFunctionAuthor("EnterpriseX");
            return table;
        });
    }

    @Override
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName) {
        String sql = "SELECT column_name, column_comment, column_type, column_key, extra, " +
                "is_nullable, data_type " +
                "FROM information_schema.columns " +
                "WHERE table_schema = (SELECT database()) " +
                "AND table_name = ? " +
                "ORDER BY ordinal_position";

        return jdbcTemplate.query(sql, new Object[]{tableName}, (rs, rowNum) -> {
            GenTableColumn column = new GenTableColumn();
            column.setColumnName(rs.getString("column_name"));
            column.setColumnComment(rs.getString("column_comment"));
            column.setColumnType(rs.getString("column_type"));
            column.setJavaField(convertToCamelCaseField(rs.getString("column_name")));
            column.setJavaType(convertToJavaType(rs.getString("data_type")));
            column.setIsPk("PRI".equals(rs.getString("column_key")) ? "1" : "0");
            column.setIsIncrement(rs.getString("extra").contains("auto_increment") ? "1" : "0");
            column.setIsRequired("NO".equals(rs.getString("is_nullable")) ? "1" : "0");
            column.setIsInsert("1");
            column.setIsEdit("1");
            column.setIsList("1");
            column.setIsQuery("1");
            column.setQueryType("EQ");
            column.setHtmlType("input");
            return column;
        });
    }

    @Override
    public byte[] downloadCode(String tableName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        try {
            Map<String, String> codeMap = previewCode(tableName);
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue().getBytes());
                zip.closeEntry();
            }
            zip.close();
        } catch (IOException e) {
            log.error("生成代码失败", e);
        }

        return outputStream.toByteArray();
    }

    @Override
    public Map<String, String> previewCode(String tableName) {
        Map<String, String> codeMap = new LinkedHashMap<>();

        List<GenTable> tables = selectDbTableList(tableName, null);
        if (tables.isEmpty()) {
            return codeMap;
        }

        GenTable table = tables.get(0);
        List<GenTableColumn> columns = selectDbTableColumnsByName(tableName);

        // 生成Entity
        codeMap.put(table.getClassName() + ".java", generateEntity(table, columns));

        // 生成Mapper
        codeMap.put(table.getClassName() + "Mapper.java", generateMapper(table));

        // 生成Service
        codeMap.put(table.getClassName() + "Service.java", generateService(table));

        // 生成ServiceImpl
        codeMap.put(table.getClassName() + "ServiceImpl.java", generateServiceImpl(table));

        // 生成Controller
        codeMap.put(table.getClassName() + "Controller.java", generateController(table));

        return codeMap;
    }

    /**
     * 生成Entity代码
     */
    private String generateEntity(GenTable table, List<GenTableColumn> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(table.getPackageName()).append(".domain;\n\n");
        sb.append("import com.baomidou.mybatisplus.annotation.*;\n");
        sb.append("import com.enterprisex.common.core.domain.BaseEntity;\n");
        sb.append("import lombok.Data;\n");
        sb.append("import lombok.EqualsAndHashCode;\n\n");
        sb.append("/**\n");
        sb.append(" * ").append(table.getFunctionName()).append("对象 ").append(table.getTableName()).append("\n");
        sb.append(" *\n");
        sb.append(" * @author ").append(table.getFunctionAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("@Data\n");
        sb.append("@EqualsAndHashCode(callSuper = true)\n");
        sb.append("@TableName(\"").append(table.getTableName()).append("\")\n");
        sb.append("public class ").append(table.getClassName()).append(" extends BaseEntity {\n\n");
        sb.append("    private static final long serialVersionUID = 1L;\n\n");

        for (GenTableColumn column : columns) {
            sb.append("    /**\n");
            sb.append("     * ").append(column.getColumnComment()).append("\n");
            sb.append("     */\n");
            if ("1".equals(column.getIsPk())) {
                sb.append("    @TableId(value = \"").append(column.getColumnName()).append("\", type = IdType.AUTO)\n");
            }
            sb.append("    private ").append(column.getJavaType()).append(" ").append(column.getJavaField()).append(";\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 生成Mapper代码
     */
    private String generateMapper(GenTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(table.getPackageName()).append(".mapper;\n\n");
        sb.append("import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n");
        sb.append("import ").append(table.getPackageName()).append(".domain.").append(table.getClassName()).append(";\n");
        sb.append("import org.apache.ibatis.annotations.Mapper;\n\n");
        sb.append("/**\n");
        sb.append(" * ").append(table.getFunctionName()).append("Mapper接口\n");
        sb.append(" *\n");
        sb.append(" * @author ").append(table.getFunctionAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("@Mapper\n");
        sb.append("public interface ").append(table.getClassName()).append("Mapper extends BaseMapper<").append(table.getClassName()).append("> {\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 生成Service代码
     */
    private String generateService(GenTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(table.getPackageName()).append(".service;\n\n");
        sb.append("import com.baomidou.mybatisplus.extension.service.IService;\n");
        sb.append("import ").append(table.getPackageName()).append(".domain.").append(table.getClassName()).append(";\n\n");
        sb.append("/**\n");
        sb.append(" * ").append(table.getFunctionName()).append("服务接口\n");
        sb.append(" *\n");
        sb.append(" * @author ").append(table.getFunctionAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("public interface ").append(table.getClassName()).append("Service extends IService<").append(table.getClassName()).append("> {\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 生成ServiceImpl代码
     */
    private String generateServiceImpl(GenTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(table.getPackageName()).append(".service.impl;\n\n");
        sb.append("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n");
        sb.append("import ").append(table.getPackageName()).append(".domain.").append(table.getClassName()).append(";\n");
        sb.append("import ").append(table.getPackageName()).append(".mapper.").append(table.getClassName()).append("Mapper;\n");
        sb.append("import ").append(table.getPackageName()).append(".service.").append(table.getClassName()).append("Service;\n");
        sb.append("import org.springframework.stereotype.Service;\n\n");
        sb.append("/**\n");
        sb.append(" * ").append(table.getFunctionName()).append("服务实现\n");
        sb.append(" *\n");
        sb.append(" * @author ").append(table.getFunctionAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("@Service\n");
        sb.append("public class ").append(table.getClassName()).append("ServiceImpl extends ServiceImpl<")
                .append(table.getClassName()).append("Mapper, ").append(table.getClassName())
                .append("> implements ").append(table.getClassName()).append("Service {\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 生成Controller代码
     */
    private String generateController(GenTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(table.getPackageName()).append(".controller;\n\n");
        sb.append("import com.enterprisex.common.core.domain.R;\n");
        sb.append("import com.enterprisex.common.core.page.TableDataInfo;\n");
        sb.append("import ").append(table.getPackageName()).append(".domain.").append(table.getClassName()).append(";\n");
        sb.append("import ").append(table.getPackageName()).append(".service.").append(table.getClassName()).append("Service;\n");
        sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
        sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n\n");
        sb.append("/**\n");
        sb.append(" * ").append(table.getFunctionName()).append("控制器\n");
        sb.append(" *\n");
        sb.append(" * @author ").append(table.getFunctionAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("@Tag(name = \"").append(table.getFunctionName()).append("\")\n");
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/").append(table.getModuleName()).append("/").append(table.getBusinessName()).append("\")\n");
        sb.append("public class ").append(table.getClassName()).append("Controller {\n\n");
        sb.append("    @Autowired\n");
        sb.append("    private ").append(table.getClassName()).append("Service ").append(toLowerCaseFirstChar(table.getClassName())).append("Service;\n\n");
        sb.append("    @Operation(summary = \"查询").append(table.getFunctionName()).append("列表\")\n");
        sb.append("    @GetMapping(\"/list\")\n");
        sb.append("    public TableDataInfo<").append(table.getClassName()).append("> list() {\n");
        sb.append("        return TableDataInfo.build(").append(toLowerCaseFirstChar(table.getClassName())).append("Service.list());\n");
        sb.append("    }\n\n");
        sb.append("    @Operation(summary = \"新增").append(table.getFunctionName()).append("\")\n");
        sb.append("    @PostMapping\n");
        sb.append("    public R<Void> add(@RequestBody ").append(table.getClassName()).append(" entity) {\n");
        sb.append("        ").append(toLowerCaseFirstChar(table.getClassName())).append("Service.save(entity);\n");
        sb.append("        return R.ok();\n");
        sb.append("    }\n\n");
        sb.append("    @Operation(summary = \"修改").append(table.getFunctionName()).append("\")\n");
        sb.append("    @PutMapping\n");
        sb.append("    public R<Void> edit(@RequestBody ").append(table.getClassName()).append(" entity) {\n");
        sb.append("        ").append(toLowerCaseFirstChar(table.getClassName())).append("Service.updateById(entity);\n");
        sb.append("        return R.ok();\n");
        sb.append("    }\n\n");
        sb.append("    @Operation(summary = \"删除").append(table.getFunctionName()).append("\")\n");
        sb.append("    @DeleteMapping(\"/{ids}\")\n");
        sb.append("    public R<Void> remove(@PathVariable Long[] ids) {\n");
        sb.append("        ").append(toLowerCaseFirstChar(table.getClassName())).append("Service.removeByIds(java.util.Arrays.asList(ids));\n");
        sb.append("        return R.ok();\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 表名转换成Java类名
     */
    private String convertToCamelCase(String tableName) {
        String[] parts = tableName.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }

    /**
     * 列名转换成Java属性名
     */
    private String convertToCamelCaseField(String columnName) {
        String[] parts = columnName.split("_");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].length() > 0) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }

    /**
     * 数据库类型转换成Java类型
     */
    private String convertToJavaType(String dbType) {
        if (dbType.contains("int")) {
            if (dbType.contains("tinyint")) {
                return "Integer";
            } else if (dbType.contains("bigint")) {
                return "Long";
            } else {
                return "Integer";
            }
        } else if (dbType.contains("varchar") || dbType.contains("char") || dbType.contains("text")) {
            return "String";
        } else if (dbType.contains("decimal") || dbType.contains("numeric")) {
            return "java.math.BigDecimal";
        } else if (dbType.contains("datetime") || dbType.contains("timestamp")) {
            return "java.time.LocalDateTime";
        } else if (dbType.contains("date")) {
            return "java.time.LocalDate";
        } else if (dbType.contains("time")) {
            return "java.time.LocalTime";
        } else {
            return "String";
        }
    }

    /**
     * 首字母小写
     */
    private String toLowerCaseFirstChar(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
