package com.enterprisex.system.controller;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.page.TableDataInfo;
import com.enterprisex.system.domain.GenTable;
import com.enterprisex.system.domain.GenTableColumn;
import com.enterprisex.system.service.GenTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author EnterpriseX
 */
@Slf4j
@Tag(name = "代码生成")
@RestController
@RequestMapping("/tool/gen")
public class GenController {

    @Autowired
    private GenTableService genTableService;

    /**
     * 查询数据库表列表
     */
    @Operation(summary = "查询数据库表列表")
    @GetMapping("/db/list")
    public TableDataInfo<GenTable> dataList(
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) String tableComment) {
        List<GenTable> list = genTableService.selectDbTableList(tableName, tableComment);
        return TableDataInfo.build(list);
    }

    /**
     * 查询数据库表列信息
     */
    @Operation(summary = "查询数据库表列信息")
    @GetMapping("/column/{tableName}")
    public R<List<GenTableColumn>> columnList(@PathVariable String tableName) {
        List<GenTableColumn> list = genTableService.selectDbTableColumnsByName(tableName);
        return R.ok(list);
    }

    /**
     * 预览代码
     */
    @Operation(summary = "预览代码")
    @GetMapping("/preview/{tableName}")
    public R<Map<String, String>> preview(@PathVariable String tableName) {
        Map<String, String> dataMap = genTableService.previewCode(tableName);
        return R.ok(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @Operation(summary = "生成代码（下载方式）")
    @GetMapping("/download/{tableName}")
    public void download(@PathVariable String tableName, HttpServletResponse response) {
        try {
            byte[] data = genTableService.downloadCode(tableName);
            response.reset();
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + tableName + ".zip\"");
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("下载代码失败", e);
        }
    }
}
