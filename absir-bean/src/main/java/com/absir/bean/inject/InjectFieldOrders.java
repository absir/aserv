/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-19 下午2:43:31
 */
package com.absir.bean.inject;

import com.absir.bean.basis.BeanFactory;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelList.Orderable;

import java.lang.reflect.Field;
import java.util.List;

public class InjectFieldOrders extends InjectField {

    Class<?> beanType;

    private InjectFieldOrders(Field field, String injectName, InjectType injectType, Class<?> beanType) {
        super(field, injectName, injectType);
        this.beanType = beanType;
    }

    public static InjectField getInjectField(Field field, String injectName, InjectType injectType, Orders orders) {
        Class<?> beanType = null;
        if (orders != null) {
            if (field.getType().isArray() || List.class.isAssignableFrom(field.getType())) {
                beanType = KernelClass.componentClass(field.getGenericType());
                if (beanType != null && Orderable.class.isAssignableFrom(beanType)) {
                    beanType = null;
                }
            }
        }

        return beanType == null ? new InjectField(field, injectName, injectType) : new InjectFieldOrders(field, injectName, injectType, beanType);
    }

    @Override
    protected Object parameter(BeanFactory beanFactory) {
        List<?> beanObjects = beanFactory.getBeanObjects(beanType);
        if (beanObjects == null || beanObjects.isEmpty()) {
            if (injectType == InjectType.Required) {
                throw new RuntimeException("BeanName = " + value + " is " + beanObjects + " not match " + beanType);
            }

            return null;
        }

        KernelList.sortCommonObjects(beanObjects);
        return DynaBinder.INSTANCE.bind(beanObjects, null, field.getGenericType());
    }
}
