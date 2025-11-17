package com.enterprisex.common.excel.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel工具类 - 基于EasyExcel
 *
 * @author EnterpriseX
 */
@Slf4j
public class ExcelUtil {

    /**
     * 导出Excel到响应流
     *
     * @param response HttpServletResponse
     * @param data 数据列表
     * @param clazz 数据类型
     * @param fileName 文件名（不含扩展名）
     */
    public static <T> void exportExcel(HttpServletResponse response,
                                        List<T> data,
                                        Class<T> clazz,
                                        String fileName) {
        exportExcel(response, data, clazz, fileName, "Sheet1");
    }

    /**
     * 导出Excel到响应流
     *
     * @param response HttpServletResponse
     * @param data 数据列表
     * @param clazz 数据类型
     * @param fileName 文件名（不含扩展名）
     * @param sheetName Sheet名称
     */
    public static <T> void exportExcel(HttpServletResponse response,
                                        List<T> data,
                                        Class<T> clazz,
                                        String fileName,
                                        String sheetName) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            // URL编码文件名
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), clazz)
                    .autoCloseStream(Boolean.FALSE)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    /**
     * 导出Excel到输出流
     *
     * @param outputStream 输出流
     * @param data 数据列表
     * @param clazz 数据类型
     * @param sheetName Sheet名称
     */
    public static <T> void exportExcel(OutputStream outputStream,
                                        List<T> data,
                                        Class<T> clazz,
                                        String sheetName) {
        try {
            EasyExcel.write(outputStream, clazz)
                    .autoCloseStream(Boolean.FALSE)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    /**
     * 导入Excel
     *
     * @param inputStream 输入流
     * @param clazz 数据类型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) {
        try {
            return EasyExcel.read(inputStream)
                    .head(clazz)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            log.error("导入Excel失败", e);
            throw new RuntimeException("导入Excel失败: " + e.getMessage());
        }
    }

    /**
     * 导入Excel（带自定义监听器）
     *
     * @param inputStream 输入流
     * @param clazz 数据类型
     * @param readListener 读取监听器
     */
    public static <T> void importExcel(InputStream inputStream,
                                        Class<T> clazz,
                                        com.alibaba.excel.read.listener.ReadListener<T> readListener) {
        try {
            EasyExcel.read(inputStream, clazz, readListener)
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            log.error("导入Excel失败", e);
            throw new RuntimeException("导入Excel失败: " + e.getMessage());
        }
    }

    /**
     * 导出多Sheet Excel
     *
     * @param response HttpServletResponse
     * @param fileName 文件名（不含扩展名）
     * @param sheets Sheet数据列表
     */
    public static void exportMultiSheetExcel(HttpServletResponse response,
                                              String fileName,
                                              List<SheetData<?>> sheets) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 创建ExcelWriter
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream())
                    .autoCloseStream(Boolean.FALSE)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();

            // 写入多个Sheet
            for (int i = 0; i < sheets.size(); i++) {
                SheetData<?> sheetData = sheets.get(i);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetData.getSheetName())
                        .head(sheetData.getClazz())
                        .build();
                excelWriter.write(sheetData.getData(), writeSheet);
            }

            // 完成写入
            excelWriter.finish();

        } catch (IOException e) {
            log.error("导出多Sheet Excel失败", e);
            throw new RuntimeException("导出多Sheet Excel失败: " + e.getMessage());
        }
    }

    /**
     * Sheet数据封装类
     */
    public static class SheetData<T> {
        private String sheetName;
        private Class<T> clazz;
        private List<T> data;

        public SheetData(String sheetName, Class<T> clazz, List<T> data) {
            this.sheetName = sheetName;
            this.clazz = clazz;
            this.data = data;
        }

        public String getSheetName() {
            return sheetName;
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public List<T> getData() {
            return data;
        }
    }
}
