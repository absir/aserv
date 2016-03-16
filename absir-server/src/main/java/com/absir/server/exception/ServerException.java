/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-30 下午12:25:03
 */
package com.absir.server.exception;

@SuppressWarnings("serial")
public class ServerException extends RuntimeException {

    private ServerStatus serverStatus;

    private Object exceptionData;

    public ServerException(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public ServerException(ServerStatus serverStatus, Object exceptionData) {
        this(serverStatus);
        this.exceptionData = exceptionData;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public Object getExceptionData() {
        return exceptionData;
    }

    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return getClass().getName() + (message == null ? ": " : (": " + message + ": ")) + serverStatus + (exceptionData == null ? ": " : (": " + exceptionData + ": "));
    }
}
