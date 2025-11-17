package com.enterprisex.common.web.exception;

import com.enterprisex.common.core.domain.R;
import com.enterprisex.common.core.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.UUID;
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
     * 404异常 - 资源未找到
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("请求地址不存在: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.NOT_FOUND.value(), "请求地址不存在: " + request.getRequestURI());
    }

    /**
     * HTTP消息不可读异常（通常是JSON格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("请求体解析失败: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.BAD_REQUEST.value(), "请求数据格式错误，请检查JSON格式");
    }

    /**
     * 数据库唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public R<?> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.error("数据库唯一键冲突: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.CONFLICT.value(), "数据已存在，请勿重复操作");
    }

    /**
     * 数据完整性约束异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("数据完整性约束异常: {} - {}", request.getRequestURI(), e.getMessage());
        String message = "数据操作失败";
        if (e.getMessage() != null) {
            if (e.getMessage().contains("foreign key")) {
                message = "存在关联数据，无法删除";
            } else if (e.getMessage().contains("Duplicate entry")) {
                message = "数据已存在，请勿重复添加";
            }
        }
        return R.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("数据库访问异常 [ErrorID: {}]: {} - ", errorId, request.getRequestURI(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "数据库操作失败，请联系管理员（错误ID: " + errorId.substring(0, 8) + "）");
    }

    /**
     * SQL异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleSQLException(SQLException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("SQL执行异常 [ErrorID: {}]: {} - SQLState: {}, ErrorCode: {}",
                errorId, request.getRequestURI(), e.getSQLState(), e.getErrorCode(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "数据库查询失败，请联系管理员（错误ID: " + errorId.substring(0, 8) + "）");
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("运行时异常 [ErrorID: {}]: {} - ", errorId, request.getRequestURI(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "系统运行时错误，请联系管理员（错误ID: " + errorId.substring(0, 8) + "）");
    }

    /**
     * 通用异常（兜底异常处理）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("系统异常 [ErrorID: {}]: {} - ", errorId, request.getRequestURI(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "系统错误，请联系管理员（错误ID: " + errorId.substring(0, 8) + "）");
    }
}
