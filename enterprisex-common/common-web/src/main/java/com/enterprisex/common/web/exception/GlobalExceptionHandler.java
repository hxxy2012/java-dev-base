package com.enterprisex.common.web.exception;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author EnterpriseX
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<?> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 请求参数验证异常（@Validated）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数验证失败: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数验证失败: " + message);
    }

    /**
     * 请求参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("参数绑定失败: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数绑定失败: " + message);
    }

    /**
     * 约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.error("约束验证失败: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), "约束验证失败: " + message);
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                                HttpServletRequest request) {
        String message = String.format("缺少必需参数: %s (%s)", e.getParameterName(), e.getParameterType());
        log.error("缺少请求参数: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                           HttpServletRequest request) {
        String message = String.format("参数类型错误: %s，期望类型: %s",
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        log.error("参数类型不匹配: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                              HttpServletRequest request) {
        String message = String.format("不支持的请求方法: %s，支持的方法: %s",
                e.getMethod(),
                String.join(", ", e.getSupportedMethods() != null ? e.getSupportedMethods() : new String[0]));
        log.error("请求方法不支持: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), message);
    }

    /**
     * 文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        String message = "上传文件大小超过限制: " + (e.getMaxUploadSize() / 1024 / 1024) + "MB";
        log.error("文件上传大小超限: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("访问被拒绝: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN.value(), "没有访问权限");
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: {} - ", request.getRequestURI(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误，请联系管理员");
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error("非法参数: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数错误: " + e.getMessage());
    }

    /**
     * 非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.error("非法状态: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统状态异常: " + e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {} - ", request.getRequestURI(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统运行时错误，请联系管理员");
    }

    /**
     * 通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - ", request.getRequestURI(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统错误，请联系管理员");
    }
}
