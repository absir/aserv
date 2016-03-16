/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午7:27:41
 */
package com.absir.server.exception;

public enum ServerStatus {

    IN_FAILED(0),

    ON_SUCCESS(200),

    ON_CODE(201),

    ON_DELETED(204),

    ON_FAIL(205),

    NO_USER(220),

    NO_VERIFY(221),

    ON_TIMEOUT(222),

    ON_DENIED(304),

    ON_ERROR(400),

    NO_LOGIN(402),

    ON_FORBIDDEN(403),

    IN_404(404),

    IN_405(405),

    NO_PARAM(437),;

    private int code;

    ServerStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
