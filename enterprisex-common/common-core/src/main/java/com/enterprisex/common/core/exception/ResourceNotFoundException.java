package com.enterprisex.common.core.exception;

import com.enterprisex.common.core.constant.ErrorCode;

/**
 * 资源未找到异常
 *
 * @author EnterpriseX
 */
public class ResourceNotFoundException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s不存在: %s", resourceName, resourceId), ErrorCode.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        setCode(ErrorCode.NOT_FOUND);
    }
}
