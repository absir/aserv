package com.absir.aserv.system.api;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Environment;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.value.Body;
import com.absir.server.value.OnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by absir on 2016/12/1.
 */
public abstract class ApiBase {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ApiBase.class);

    /**
     * 统一异常返回
     */
    @Body
    @OnException(Throwable.class)
    protected Object onException(Throwable e, OnPut onPut) {
        Input input = onPut.getInput();
        input.setStatus(ServerStatus.ON_ERROR.getCode());
        if (BeanFactoryUtils.getEnvironment() == Environment.DEVELOP) {
            e.printStackTrace();
        }

        if (BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0 || input.isDebug()
                || !(e instanceof ServerException)) {
            LOGGER.debug("on server " + input.getUri(), e);
        }

        onPut.setReturnedFixed(false);
        if (e instanceof ServerException) {
            ServerException exception = (ServerException) e;
            Object data = exception.getExceptionData();
            if (exception.getServerStatus() == ServerStatus.ON_CODE) {
                return data == null ? "fail" : data;
            }

            if (data != null && data instanceof MessageCode) {
                return data;
            }

            MessageCode messageCode = new MessageCode();
            messageCode.setServerException(exception);
            return messageCode;
        }
        
        return new MessageCode(e);
    }

    /**
     * 消息对象
     */
    public static class MessageCode {

        public String message;

        public int code;

        public Object data;

        public MessageCode() {
        }

        public MessageCode(Throwable e) {
            if (e instanceof ServerException) {
                setServerException((ServerException) e);

            } else {
                setThrowable(e);
            }
        }

        public void setThrowable(Throwable e) {
            message = e.toString();
            code = ServerStatus.ON_ERROR.getCode();
        }

        public void setServerException(ServerException e) {
            message = e.toString();
            code = e.getServerStatus().getCode();
        }
    }
}
