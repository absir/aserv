/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-7 下午5:42:28
 */
package com.absir.aserv.crud;

import com.absir.aserv.crud.CrudHandler.CrudInvoker;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CrudContextUtils extends CrudUtils {

    private final static Map<JoEntity, Boolean> Jo_Entity_Map_Mutilpart = new HashMap<JoEntity, Boolean>();

    public static boolean isMultipart(JoEntity joEntity) {
        Boolean mutilpart = Jo_Entity_Map_Mutilpart.get(joEntity);
        if (mutilpart == null) {
            synchronized (joEntity.getEntityToken()) {
                mutilpart = Jo_Entity_Map_Mutilpart.get(joEntity);
                if (mutilpart == null) {
                    mutilpart = isMultipart(getCrudEntity(joEntity));
                    Jo_Entity_Map_Mutilpart.put(joEntity, mutilpart);
                }
            }
        }

        return mutilpart;
    }

    private static boolean isMultipart(CrudEntity crudEntity) {
        if (crudEntity == null) {
            return false;
        }

        if (crudEntity.crudProperties != null) {
            for (CrudProperty crudProperty : crudEntity.crudProperties) {
                if (crudProperty.crudProcessor instanceof ICrudProcessorInput && ((ICrudProcessorInput) crudProperty.crudProcessor).isMultipart()) {
                    return true;
                }
            }
        }

        if (crudEntity.crudPropertyReferences != null) {
            for (CrudPropertyReference crudPropertyReference : crudEntity.crudPropertyReferences) {
                if (isMultipart(crudPropertyReference.valueCrudEntity)) {
                    return true;
                }

                if (crudPropertyReference instanceof CrudPropertyMap && isMultipart(((CrudPropertyMap) crudPropertyReference).keyCrudEntity)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void crud(JaCrud.Crud crud, boolean persist, Map<String, Object> crudRecord, JoEntity joEntity, Object entity, final JiUserBase user, PropertyFilter filter, final PropertyErrors errors,
                            final Input input) {
        CrudEntity crudEntity = getCrudEntity(joEntity);
        if (crudEntity == null) {
            return;
        }

        PropertyFilter crudFilter = new PropertyFilter();
        if (filter == null) {
            filter = crudFilter;

        } else {
            filter = filter.newly();
        }

        if (errors != null && input != null) {
            final List<Object> entities = new ArrayList<Object>();
            final List<CrudProperty> crudProperties = new ArrayList<CrudProperty>();
            final List requestBodies = new ArrayList<Object>();
            final CrudInvoker crudInvoker = new CrudInvoker(crud, crudRecord, filter, crudEntity, entity) {

                @Override
                public boolean isSupport(CrudProperty crudProperty) {
                    return crudProperty.crudProcessor instanceof ICrudProcessorInput;
                }

                @Override
                public void crudInvoke(CrudProperty crudProperty, Object entity) {
                    entities.add(entity);
                    crudProperties.add(crudProperty);
                    requestBodies.add(((ICrudProcessorInput) crudProperty.crudProcessor).crud(crudProperty, errors, this, user, input));
                }
            };

            crudInvoker.persist = persist;
            crud(entity, crudEntity, crudInvoker);
            if (errors.hashErrors()) {
                return;
            }

            int size = entities.size();
            for (int i = 0; i < size; i++) {
                CrudProperty crudProperty = crudProperties.get(i);
                ((ICrudProcessorInput) crudProperty.crudProcessor).crud(crudProperty, entities.get(i), crudInvoker, user, requestBodies.get(i));
            }
        }

        crudFilter.setPropertyPath("");
        CrudInvoker crudInvoker = new CrudInvoker(crud, crudRecord, crudFilter, crudEntity, entity) {

            @Override
            public boolean isSupport(CrudProperty crudProperty) {
                return true;
            }

            @Override
            public void crudInvoke(CrudProperty crudProperty, Object entity) {
                crudProperty.crudProcessor.crud(crudProperty, entity, this, user, input);
            }
        };

        crudInvoker.persist = persist;
        crud(entity, crudEntity, crudInvoker);

        if (entity instanceof ICrudBean) {
            ((ICrudBean) entity).processCrud(crud, crudInvoker, input);
        }
    }

    public static void crud(JaCrud.Crud crud, boolean persist, Map<String, Object> crudRecord, JoEntity joEntity, Object entity, JiUserBase user, PropertyFilter filter) {
        crud(crud, persist, crudRecord, joEntity, entity, user, filter, null, null);
    }
}
