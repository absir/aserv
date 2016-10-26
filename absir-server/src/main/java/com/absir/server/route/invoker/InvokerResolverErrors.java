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
    protected static String errorView = "errors";

    public static void checkError(BinderResult binderResult, OnPut onPut) {
        if (binderResult != null && !binderResult.hashErrors()) {
            return;
        }

        if (onPut == null) {
            onPut = OnPut.get();
            if (onPut == null) {
                return;
            }
        }

        Input input = onPut.getInput();
        if (binderResult == null) {
            if (!input.hasBinderData()) {
                return;
            }

            binderResult = input.getBinderData().getBinderResult();
            if (!binderResult.hashErrors()) {
                return;
            }
        }

        input.getModel().put("errors", binderResult.getPropertyErrors());
        onPut.setReturnedResolver(ReturnedResolverView.ME, errorView);
        throw new ServerException(ServerStatus.ON_SUCCESS);
    }

    public static void onError(String propertyPath, String error, String errorObject, OnPut onPut) {
        if (onPut == null) {
            onPut = OnPut.get();
            if (onPut == null) {
                return;
            }
        }

        onErrorMessage(propertyPath, onPut.getInput().getLangMessage(error), errorObject, onPut);
    }

    public static void onErrorMessage(String propertyPath, String errorMessage, String errorObject, OnPut onPut) {
        if (onPut == null) {
            onPut = OnPut.get();
            if (onPut == null) {
                return;
            }
        }

        Input input = onPut.getInput();
        BinderResult binderResult = input.getBinderData().getBinderResult();
        binderResult.addPropertyError(propertyPath, errorMessage, errorObject);
        input.getModel().put("errors", binderResult.getPropertyErrors());
        onPut.setReturnedResolver(ReturnedResolverView.ME, errorView);
        throw new ServerException(ServerStatus.ON_SUCCESS);
    }

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
        checkError(null, onPut);
    }

    @Override
    public void resolveAfter(Object returnValue, Boolean invoker, OnPut onPut) throws Exception {

    }

}
