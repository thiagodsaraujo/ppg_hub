package com.ppghub.domain.exception;

/**
 * Exception lançada quando o limite de requisições (rate limit) é excedido.
 * Resulta em HTTP 429 Too Many Requests.
 */
public class RateLimitExceededException extends PpgHubException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message, "RATE_LIMIT_EXCEEDED");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
