package com.monolith.hik;

import com.monolith.dsxp.DsxpException;

/**
 * Date: 2025/6/30 15:12
 * Author: Levent
 * 海康异常
 */
public class HIKException extends DsxpException {
    public HIKException() {
    }

    public HIKException(String message) {
        super(message);
    }

    public HIKException(String message, Throwable cause) {
        super(message, cause);
    }

    public HIKException(Throwable cause) {
        super(cause);
    }

    public HIKException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
