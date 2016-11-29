/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-11 下午3:38:20
 */
package com.absir.aserv.system.service.statics;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.support.Developer;
import com.absir.aserv.system.service.CrudService;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelCharset;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilRuntime;
import com.absir.server.in.IAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EntityStatics {

    public static String getPrimary(Object entity, String primary) {
        return DynaBinderUtils.getParamFromValue(UtilAccessor.get(entity, primary));
    }

    public static String[] getPrimarys(Collection<?> entities, String primary) {
        if (entities == null || entities.size() == 0) {
            return null;
        }

        int size = entities.size();
        String[] primaries = new String[size];
        int i = 0;
        for (Object entity : entities) {
            primaries[i++] = getPrimary(entity, primary);
        }

        return primaries;
    }

    public static String urlPrimary(String entityName, Object entity, String primary) {
        primary = getPrimary(entity, primary);
        try {
            return primary == null ? null : URLEncoder.encode(primary, KernelCharset.getDefault().displayName());

        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static Object paramId(Object id) {
        if (id == null) {
            return null;
        }

        if (id instanceof Object[]) {
            Object[] ids = (Object[]) id;
            if (ids.length == 0) {
                return null;
            }

            id = ids[0];

        } else if (id instanceof Collection) {
            Collection<Object> ids = (Collection<Object>) id;
            if (ids.size() <= 0) {
                return null;
            }
        }

        return id;
    }

    public static Object find(String entityName, Object id) {
        return find(entityName, id, Pag.getInput());
    }

    public static Object find(String entityName, Object id, IAttributes input) {
        if (id == null || (id = paramId(id)) == null) {
            return null;
        }

        String entityId = EntityStatics.class.getName() + "@" + entityName + "@" + id;
        Object entity = input.getAttribute(entityId);
        if (entity == null) {
            entity = CrudServiceUtils.find(entityName, id, null);
            input.setAttribute(entityId, entity);
        }

        return entity;
    }

    public static List<Object> list(String entityName, Object ids) {
        return list(entityName, ids, Pag.getInput());
    }

    public static List<Object> list(String entityName, Object ids, IAttributes input) {
        if (ids == null) {
            return null;
        }

        List<Object> entityList = new ArrayList<Object>();
        List<Object> list = new ArrayList<Object>();
        for (Object id : DynaBinder.to(ids, Object[].class)) {
            String entityId = EntityStatics.class.getName() + "@" + entityName + "@" + id;
            Object entity = input.getAttribute(entityId);
            if (entity == null) {
                list.add(id);

            } else {
                entityList.add(entity);
            }
        }

        if (!list.isEmpty()) {
            List<Object> entities = CrudServiceUtils.list(entityName, list.toArray(), null);
            if (entities.isEmpty()) {
                ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
                for (Object entity : entities) {
                    String entityId = EntityStatics.class.getName() + "@" + entityName + "@" + crudSupply.getIdentifier(entityName, entity);
                    input.setAttribute(entityId, entity);
                }
            }

            entityList.addAll(entities);
        }

        return entityList;
    }

    public static List list(String entityName, IAttributes input) {
        String entitiesKey = EntityStatics.class.getName() + "-" + entityName + "@LIST";
        List entities = (List) input.getAttribute(entitiesKey);
        if (entities == null) {
            entities = CrudServiceUtils.list(entityName, null, null, 0, 0);
            input.setAttribute(entitiesKey, entities);
        }

        return entities;
    }

    public static void searchConditionMap(IAttributes input) {
        Object searchConditionMap = input.getAttribute("searchConditionMap");
        if (searchConditionMap == null) {
            Object searchConditionList = input.getAttribute("searchConditionList");
            if (searchConditionList != null && searchConditionList instanceof Collection) {
                searchConditionMap = KernelCollection.toMap((Collection) searchConditionList);
                input.setAttribute("searchConditionMap", searchConditionMap);
            }
        }
    }

    public static String suggest(String entityName) {
        return "SUGGEST@" + entityName;
    }

    public static List suggest(String entityName, IAttributes input) {
        String entitiesKey = EntityStatics.class.getName() + "-" + entityName + "@SUGGEST";
        List entities = (List) input.getAttribute(entitiesKey);
        if (entities == null) {
            entities = CrudServiceUtils.list(entityName, AccessServiceUtils.suggestCondition(entityName, null), null, 0, 0);
            input.setAttribute(entitiesKey, entities);
        }

        return entities;
    }

    public static List suggest(String entityName, JdbcCondition condition, IAttributes input) {
        if (condition == null || condition.getConditions().isEmpty()) {
            return suggest(entityName, input);
        }

        String entitiesKey = EntityStatics.class.getName() + "-" + entityName + "@SUGGEST";
        List entities = (List) input.getAttribute(entitiesKey);
        if (entities == null) {
            entities = CrudServiceUtils.list(entityName, AccessServiceUtils.suggestCondition(entityName, condition), null, 0, 0);
            input.setAttribute(entitiesKey, entities);
        }

        return entities;
    }

    public static String getSharedRuntimeName(String entityName, String fieldName) {
        return UtilRuntime.getRuntimeName(EntityStatics.class, entityName + "-" + fieldName + "@SHARED");
    }

    public static Object getSharedObject(String entityName, String fieldName, IAttributes inpute) {
        return getSharedObject(getSharedRuntimeName(entityName, fieldName), inpute);
    }

    public static Object getSharedObject(String runtimeName, IAttributes attributes) {
        Object shared = attributes.getAttribute(runtimeName);
        if (shared == null) {
            shared = Developer.getRuntime(runtimeName);
            attributes.setAttribute(runtimeName, shared);
        }

        return shared;
    }

    public static void setSharedObject(String runtimeName, Object value, IAttributes attributes) {
        attributes.setAttribute(runtimeName, value);
        Developer.setRuntime(runtimeName, value);
    }

}
