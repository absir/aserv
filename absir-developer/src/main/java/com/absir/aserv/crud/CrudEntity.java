/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-8 下午2:44:47
 */
package com.absir.aserv.crud;

import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.orm.value.JoEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrudEntity {

    public static final JaCrud.Crud[] ALL = new JaCrud.Crud[]{JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE, JaCrud.Crud.DELETE};

    protected JoEntity joEntity;

    protected List<CrudPropertyReference> crudPropertyReferences;

    protected List<CrudProperty> crudProperties;

    protected boolean crudEntityNone;

    protected void addCrudPropertyReference(CrudPropertyReference crudPropertyReference) {
        if (crudPropertyReferences == null) {
            crudPropertyReferences = new ArrayList<CrudPropertyReference>();
        }

        crudPropertyReferences.add(crudPropertyReference);
    }

    protected void addCrudProperty(CrudProperty crudProperty) {
        if (crudProperties == null) {
            crudProperties = new ArrayList<CrudProperty>();
        }

        crudProperties.add(crudProperty);
    }

    public JoEntity getJoEntity() {
        return joEntity;
    }

    public Iterator<CrudPropertyReference> getCrudPropertyReferencesIterator() {
        return crudPropertyReferences == null ? null : crudPropertyReferences.iterator();
    }

    public Iterator<CrudProperty> getCrudPropertiesIterator() {
        return crudProperties == null ? null : crudProperties.iterator();
    }

    protected void initCrudEntity() {
        crudEntityNone = crudProperties == null && crudPropertyReferences == null && !ICrudBean.class.isAssignableFrom(joEntity.getEntityClass());
    }

    public boolean isCrudEntityNone() {
        return crudEntityNone;
    }
}
