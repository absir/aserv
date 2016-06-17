/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-25 下午4:01:35
 */
package com.absir.aserv.crud;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelObject;

import java.util.HashMap;
import java.util.Map;

public abstract class CrudHandler {

    protected JaCrud.Crud crud;

    protected boolean persist;

    protected Map<String, Object> crudRecord;

    protected PropertyFilter filter;

    protected CrudEntity crudEntity;

    protected Object root;

    protected Object rootEntity;

    protected Map<String, Object> entityMap;

    protected String propertyPath;

    protected Object entity;

    protected boolean created;

    public CrudHandler(JaCrud.Crud crud, Map<String, Object> crudRecord, PropertyFilter filter, CrudEntity crudEntity, Object root) {
        this.crud = crud;
        this.filter = filter;
        this.crudEntity = crudEntity;
        this.root = root;
    }

    public JaCrud.Crud getCrud() {
        return crud;
    }

    public Map<String, Object> getCrudRecord() {
        return crudRecord;
    }

    public boolean isPersist() {
        return persist;
    }

    protected void setPersist(boolean persist) {
        this.persist = persist;
    }

    public PropertyFilter getFilter() {
        return filter;
    }

    public CrudEntity getCrudEntity() {
        return crudEntity;
    }

    public Object getRoot() {
        return root;
    }

    public Object getRootEntity() {
        if (rootEntity == null) {
            if (crudEntity.getJoEntity().getEntityName() == null) {
                if (crudEntity.getJoEntity().getEntityClass() == null) {
                    return (rootEntity = root);
                }
            }

            rootEntity = BeanDao.getLoadedEntity(null, crudEntity.getJoEntity().getEntityName(), root);
            if (rootEntity == null) {
                rootEntity = root;
            }
        }

        return rootEntity;
    }

    public Object getEntity() {
        if (propertyPath == filter.getPropertyPath()) {
            if (entity != null) {
                return entity;
            }

        } else {
            propertyPath = filter.getPropertyPath();
        }

        if (entityMap == null) {
            entityMap = new HashMap<String, Object>();

        } else {
            entity = entityMap.get(propertyPath);
            if (entity != null) {
                return entity;
            }
        }

        entity = KernelObject.expressGetter(getRootEntity(), propertyPath);
        entityMap.put(propertyPath, entity);
        return entity;
    }

    public boolean doCreate() {
        if (created) {
            return false;
        }

        created = true;
        return crud == Crud.CREATE;
    }

    protected static abstract class CrudInvoker extends CrudHandler {

        public CrudInvoker(Crud crud, Map<String, Object> crudRecord, PropertyFilter filter, CrudEntity crudEntity, Object root) {
            super(crud, crudRecord, filter, crudEntity, root);
        }

        public boolean isSupport(Crud crud, CrudProperty crudProperty) {
            if (filter.allow(crudProperty.getInclude(), crudProperty.getExclude()) && isSupport(crudProperty)) {
                Crud[] cruds = crudProperty.getjCrud().getCruds();
                if (cruds == CrudEntity.ALL || KernelArray.contain(cruds, crud)) {
                    return true;
                }
            }

            return false;
        }

        protected abstract boolean isSupport(CrudProperty crudProperty);

        public abstract void crudInvoke(CrudProperty crudProperty, Object entity);
    }
}
