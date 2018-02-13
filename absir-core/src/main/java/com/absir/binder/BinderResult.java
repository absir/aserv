/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-7 下午4:26:34
 */
package com.absir.binder;

import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.property.PropertyErrors;

public class BinderResult extends PropertyErrors {

    private PropertyFilter propertyFilter;

    private EValidateType validateType;

    public int getGroup() {
        return getPropertyFilter().getGroup();
    }

    public void setGroup(int group) {
        getPropertyFilter().setGroup(group);
    }

    public String getPropertyPath() {
        return getPropertyFilter().getPropertyPath();
    }

    public void setPropertyPath(String propertyPath) {
        getPropertyFilter().setPropertyPath(propertyPath);
    }

    public PropertyFilter getPropertyFilter() {
        if (propertyFilter == null) {
            propertyFilter = new PropertyFilter();
        }

        return propertyFilter;
    }

    public void setPropertyFilter(PropertyFilter propertyFilter) {
        this.propertyFilter = propertyFilter;
    }

    public EValidateType getValidateType() {
        return validateType;
    }

    public void setValidateType(EValidateType validateType) {
        this.validateType = validateType;
    }

    public boolean isValidation() {
        return validateType != null;
    }

    public void setValidation(boolean validation) {
        validateType = validation ? EValidateType.BINDING : null;
    }

    public void ready() {
        if (propertyFilter == null) {
            propertyFilter = new PropertyFilter();

        } else {
            propertyFilter.setPropertyPath("");
        }
    }

    public boolean allowPropertyPath() {
        return propertyFilter.isMatch();
    }
}
