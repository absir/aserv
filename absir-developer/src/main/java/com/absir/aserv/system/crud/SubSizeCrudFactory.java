/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-16 上午11:01:29
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.ICrudFactory;
import com.absir.aserv.crud.ICrudProcessor;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaSubSize;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;

import java.lang.reflect.Field;
import java.util.Collection;

public class SubSizeCrudFactory implements ICrudFactory, ICrudProcessor {

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user) {
        Object property = crudProperty.get(entity);
        if (property == null) {
            crudProperty.set(entity, 0);
            return;
        }

        if (!(property instanceof Integer)) {
            return;
        }

        Object[] parameters = crudProperty.getjCrud().getParameters();
        if (parameters[0] == null) {
            return;
        }

        if (parameters[0] instanceof String) {
            Class<?> entityClass = crudProperty.getCrudEntity().getJoEntity().getEntityClass();
            parameters[0] = UtilAccessor.getAccessorObj(entity, (String) parameters[0], entityClass == null ? null : entityClass.getName());
        }

        try {
            Collection<?> subtable = (Collection<?>) ((Accessor) parameters[0]).get(entity);
            if ((Integer) property >= subtable.size()) {
                crudProperty.set(entity, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        if (crudField.getjCrud().getParameters().length < 0) {
            return null;
        }

        if (KernelString.isEmpty((String) crudField.getjCrud().getParameters()[0])) {
            Field field = KernelReflect.declaredField(joEntity.getEntityClass(), crudField.getName());
            if (field == null) {
                return null;
            }

            JaSubSize jaSubSize = field.getAnnotation(JaSubSize.class);
            if (jaSubSize == null) {
                return null;
            }

            crudField.getjCrud().getParameters()[0] = jaSubSize.value();
        }

        return this;
    }
}
