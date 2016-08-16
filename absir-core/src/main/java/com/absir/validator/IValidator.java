package com.absir.validator;

import com.absir.binder.BinderResult;

/**
 * Created by absir on 16/8/16.
 */
public interface IValidator {

    public void validatorResult(String propertyPath, BinderResult result);

}
