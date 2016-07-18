/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 下午4:18:15
 */
package com.absir.property;

import java.util.ArrayList;
import java.util.List;

public class PropertyErrors {

    private List<PropertyError> propertyErrors = new ArrayList<PropertyError>();

    public List<PropertyError> getPropertyErrors() {
        return propertyErrors;
    }

    public void addPropertyError(PropertyError propertyError) {
        propertyErrors.add(propertyError);
    }

    public void addPropertyError(String propertyPath, String errorMessage, Object errorObject) {
        PropertyError propertyError = new PropertyError();
        propertyError.setPropertyPath(propertyPath);
        propertyError.setErrorMessage(errorMessage);
        propertyError.setErrorObject(errorObject);
        addPropertyError(propertyError);
    }

    public void rejectValue(String propertyPath, String errorMessage, Object errorObject) {
        int size = propertyErrors.size();
        if (size > 0) {
            if (propertyErrors.get(size - 1).getPropertyPath() == propertyPath) {
                return;
            }
        }

        PropertyError propertyError = new PropertyError();
        propertyError.setPropertyPath(propertyPath);
        propertyError.setErrorMessage(errorMessage);
        propertyError.setErrorObject(errorObject);
        propertyErrors.add(propertyError);
    }

    public boolean hashErrors() {
        return !propertyErrors.isEmpty();
    }

    public boolean contain(String propertyPath) {
        for (PropertyError propertyError : propertyErrors) {
            if (propertyError.getPropertyPath().equals(propertyPath)) {
                return true;
            }
        }

        return false;
    }
}
