package com.enterprisex.common.core.exception;

/**
 * 认证异常
 *
 * @author EnterpriseX
 */
public class AuthenticationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Integer code) {
        super(message, code);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Integer code, String message, String detailMessage) {
        super(code, message, detailMessage);
    }
}
