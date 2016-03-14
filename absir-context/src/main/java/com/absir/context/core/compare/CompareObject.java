/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-22 下午2:56:18
 */
package com.absir.context.core.compare;

import com.absir.core.kernel.KernelClass;
import com.absir.property.Property;
import com.absir.property.PropertyObject;

import java.util.Collection;
import java.util.Map;

/**
 * @author absir
 *
 */
@SuppressWarnings({"rawtypes"})
public class CompareObject implements PropertyObject<CompareAbstract> {

    /**
     * compareField
     */
    private CompareField compareField = new CompareField();

    /**
     * compareArray
     */
    private CompareArray compareArray = new CompareArray();

    /**
     * compareCollection
     */
    private CompareCollection compareCollection = new CompareCollection();

    /**
     * compareBean
     */
    private CompareBean compareBean = new CompareBean();

    /**
     * compareMap
     */
    private CompareMap compareMap = new CompareMap();

    /*
     * (non-Javadoc)
     *
     * @see com.absir.property.PropertyObject#getPropertyData(java.lang.String,
     * com.absir.property.Property)
     */
    @Override
    public CompareAbstract getPropertyData(String name, Property property) {
        if (property.getAllow() < 0) {
            return null;
        }

        Class<?> type = property.getType();
        if (type == null) {
            type = KernelClass.rawClass(property.getGenericType());
        }

        if (type.isArray()) {
            return compareArray;
        }

        if (Collection.class.isAssignableFrom(type)) {
            return compareCollection;
        }

        if (Map.class.isAssignableFrom(type)) {
            return compareMap;
        }

        if (KernelClass.isBasicClass(type)) {
            return compareField;
        }

        return compareBean;
    }
}
