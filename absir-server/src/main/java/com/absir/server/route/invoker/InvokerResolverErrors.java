package com.absir.server.route.invoker;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.binder.BinderResult;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.server.value.Errors;

import java.lang.reflect.Method;

/**
 * Created by absir on 16/7/20.
 */
@Base
@Bean
public class InvokerResolverErrors implements InvokerResolver<Boolean> {

    @Value("server.errors.view")
    protected String errorView = "errors";

    @Override
    public Boolean getInvoker(Method method) {
        return method.getAnnotation(Errors.class) == null ? null : Boolean.TRUE;
    }

    @Override
    public Boolean getInvoker(Class<?> beanClass) {
        return beanClass.getAnnotation(Errors.class) == null ? null : Boolean.TRUE;
    }

    @Override
    public void resolveBefore(Boolean invoker, OnPut onPut) throws Exception {
        Input input = onPut.getInput();
        if (input.hasBinderData()) {
            BinderResult binderResult = input.getBinderData().getBinderResult();
            if (binderResult.hashErrors()) {
                input.getModel().put("errors", binderResult.getPropertyErrors());
                onPut.setReturnedResolver(ReturnedResolverView.ME, errorView);
                throw new ServerException(ServerStatus.ON_SUCCESS);
            }
        }
    }

    @Override
    public void resolveAfter(Object returnValue, Boolean invoker, OnPut onPut) throws Exception {

    }
}
