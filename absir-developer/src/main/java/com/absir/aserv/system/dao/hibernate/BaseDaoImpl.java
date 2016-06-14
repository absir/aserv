/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-27 上午9:55:32
 */
package com.absir.aserv.system.dao.hibernate;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.dao.BaseDao;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelLang.ObjectTemplate;
import com.absir.core.kernel.KernelObject;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class BaseDaoImpl<T, ID extends Serializable> implements BaseDao<T, ID> {

    protected static final TypeVariable T_VARIABLE = BaseDaoImpl.class.getTypeParameters()[0];

    private static final Map<Class<?>, BaseDaoImpl<?, ?>> Base_Class_Map_Dao = new HashMap<Class<?>, BaseDaoImpl<?, ?>>();

    private Class<?> baseClass;

    public BaseDaoImpl() {
        if (baseClass == null) {
            baseClass = KernelClass.typeClass(getClass(), T_VARIABLE);
        }

        Base_Class_Map_Dao.put(baseClass, this);
    }

    public BaseDaoImpl(Class<?> baseClass) {
        this.baseClass = baseClass;
        Base_Class_Map_Dao.put(baseClass, this);
    }

    public static <T> BaseDaoImpl<T, ?> getBaseDaoImpl(Class<T> baseClass) {
        BaseDaoImpl<T, ?> baseDao = (BaseDaoImpl<T, ?>) Base_Class_Map_Dao.get(baseClass);
        if (baseDao == null) {
            synchronized (baseClass) {
                baseDao = (BaseDaoImpl<T, ?>) Base_Class_Map_Dao.get(baseClass);
                if (baseDao == null) {
                    final ObjectTemplate<BaseDaoImpl<?, ?>> objectBaseDao = new ObjectTemplate<BaseDaoImpl<?, ?>>();
                    KernelClass.doWithSuperClass(baseClass, new CallbackBreak<Class<?>>() {

                        @Override
                        public void doWith(Class<?> template) throws BreakException {
                            BaseDaoImpl<?, ?> baseDao = Base_Class_Map_Dao.get(template);
                            if (baseDao != null) {
                                objectBaseDao.object = baseDao;
                                throw new BreakException();
                            }
                        }
                    });

                    if (objectBaseDao.object == null) {
                        baseDao = new BaseDaoImpl<T, Serializable>(baseClass);

                    } else {
                        baseDao = (BaseDaoImpl<T, ?>) KernelObject.clone(objectBaseDao.object);
                        baseDao.baseClass = baseClass;
                    }

                    Base_Class_Map_Dao.put(baseClass, baseDao);
                }
            }
        }

        return baseDao;
    }

    public Class<?> getBaseClass() {
        return baseClass;
    }

    @Override
    public void crud(Crud crud, CrudProperty property, CrudHandler crudHandler, T entity) {
    }
}