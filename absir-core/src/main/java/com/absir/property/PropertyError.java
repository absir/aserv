/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 下午4:13:41
 */
package com.absir.property;

import com.absir.core.kernel.KernelString;

public class PropertyError {

    private String propertyPath;

    private String errorMessage;

    private Object errorObject;

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(Object errorObject) {
        this.errorObject = errorObject;
    }

    @Override
    public String toString() {
        return "Error[" + propertyPath + "]:" + errorMessage + "=" + (errorObject == null || errorObject instanceof Object[] ? errorObject : ('[' + KernelString.implode((Object[]) errorObject, ',') + ']'));
    }
}
