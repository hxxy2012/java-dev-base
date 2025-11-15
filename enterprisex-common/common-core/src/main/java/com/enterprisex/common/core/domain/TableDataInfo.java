package com.enterprisex.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应数据
 *
 * @author EnterpriseX
 */
@Data
@NoArgsConstructor
public class TableDataInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 时间戳
     */
    private Long timestamp;

    public TableDataInfo(List<T> list, long total) {
        this.code = R.SUCCESS;
        this.msg = "查询成功";
        this.rows = list;
        this.total = total;
        this.timestamp = System.currentTimeMillis();
    }

    public TableDataInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回
     */
    public static <T> TableDataInfo<T> ok(List<T> list, long total) {
        return new TableDataInfo<>(list, total);
    }

    /**
     * 失败返回
     */
    public static <T> TableDataInfo<T> fail(String msg) {
        return new TableDataInfo<>(R.FAIL, msg);
    }
}
