package com.ppghub.domain.exception;

import lombok.Getter;

/**
 * Exceção base para todas as exceções de negócio do PPG Hub.
 */
@Getter
public class PpgHubException extends RuntimeException {

    private final String errorCode;

    public PpgHubException(String message) {
        super(message);
        this.errorCode = "PPG_HUB_ERROR";
    }

    public PpgHubException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PpgHubException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PPG_HUB_ERROR";
    }

    public PpgHubException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
