package com.enterprisex.common.core.exception;

import com.enterprisex.common.core.constant.ErrorCode;

/**
 * 文件操作异常
 *
 * @author EnterpriseX
 */
public class FileOperationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public FileOperationException(String message) {
        super(message, ErrorCode.FILE_UPLOAD_FAILED);
    }

    public FileOperationException(String message, Integer code) {
        super(message, code);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
        setCode(ErrorCode.FILE_UPLOAD_FAILED);
    }

    /**
     * 文件上传失败异常
     */
    public static FileOperationException uploadFailed(String message) {
        return new FileOperationException(message, ErrorCode.FILE_UPLOAD_FAILED);
    }

    /**
     * 文件下载失败异常
     */
    public static FileOperationException downloadFailed(String message) {
        return new FileOperationException(message, ErrorCode.FILE_DOWNLOAD_FAILED);
    }

    /**
     * 文件删除失败异常
     */
    public static FileOperationException deleteFailed(String message) {
        return new FileOperationException(message, ErrorCode.FILE_DELETE_FAILED);
    }

    /**
     * 文件类型不支持异常
     */
    public static FileOperationException unsupportedFileType(String fileType) {
        return new FileOperationException(
                String.format("不支持的文件类型: %s", fileType),
                ErrorCode.FILE_TYPE_NOT_SUPPORTED
        );
    }

    /**
     * 文件大小超限异常
     */
    public static FileOperationException fileSizeExceeded(long maxSize) {
        return new FileOperationException(
                String.format("文件大小超过限制: %dMB", maxSize / 1024 / 1024),
                ErrorCode.FILE_SIZE_EXCEEDED
        );
    }
}
