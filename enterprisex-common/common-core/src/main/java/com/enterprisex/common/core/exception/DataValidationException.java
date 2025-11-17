package com.enterprisex.common.core.exception;

import com.enterprisex.common.core.constant.ErrorCode;

/**
 * 数据验证异常
 *
 * @author EnterpriseX
 */
public class DataValidationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public DataValidationException(String message) {
        super(message, ErrorCode.PARAM_ERROR);
    }

    public DataValidationException(String field, String errorMessage) {
        super(String.format("字段验证失败 [%s]: %s", field, errorMessage), ErrorCode.PARAM_ERROR);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
        setCode(ErrorCode.PARAM_ERROR);
    }
}
