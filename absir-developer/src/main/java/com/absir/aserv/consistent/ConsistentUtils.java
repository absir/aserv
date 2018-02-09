package com.absir.aserv.consistent;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.configure.JConfigureUtils;
import com.absir.context.core.ContextUtils;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsistentUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ConsistentUtils.class);

    private static Map<String, List<IEntityMerge>> mapEntityMerges;

    private static List<String> entityKeys;

    public static List<String> getEntityKeys() {
        List<String> keys = entityKeys;
        if (keys == null) {
            synchronized (ConsistentUtils.class) {
                if (entityKeys == null) {
                    entityKeys = new ArrayList<String>(mapEntityMerges.keySet());
                }

                keys = entityKeys;
            }
        }

        return keys;
    }

    protected static synchronized void addEntityMerges(String entityKey, IEntityMerge<?> entityMerge) {
        if (mapEntityMerges == null) {
            mapEntityMerges = new HashMap<String, List<IEntityMerge>>();
        }

        List<IEntityMerge> entityMerges = mapEntityMerges.get(entityKey);
        boolean addKey = mapEntityMerges == null;
        if (ContextUtils.getStartedCount() > 0) {
            entityMerges = entityMerges == null ? new ArrayList<IEntityMerge>() : new ArrayList<IEntityMerge>(entityMerges);
            entityMerges.add(entityMerge);
            mapEntityMerges.put(entityKey, entityMerges);

        } else {
            if (entityMerges == null) {
                entityMerges = new ArrayList<IEntityMerge>();
                mapEntityMerges.put(entityKey, entityMerges);
            }

            entityMerges.add(entityMerge);
        }

        if (addKey) {
            entityKeys = null;
        }
    }

    public static void reloadAll() {
        JConfigureUtils.reloadAllConfigure();
        if (mapEntityMerges != null) {
            for (String entityKey : getEntityKeys()) {
                merge(entityKey, null, null, IEntityMerge.MergeType.RELOAD);
            }
        }
    }

    public static void pubConfigure(JConfigureBase configureBase) {
        if (IConsistent.ME != null) {
            IConsistent.ME.pubConfigure(configureBase);
        }
    }

    public static <T> void addEntityMerges(String entityName, Class<T> entityClass, final IEntityMerge<T> entityMerge) {
        addEntityMergesForce(false, entityName, entityClass, entityMerge);
    }


    public static boolean support(String entityKey) {
        return mapEntityMerges != null && mapEntityMerges.containsKey(entityKey);
    }

    public static void merge(String entityKey, String entityName, Object entity, IEntityMerge.MergeType mergeType) {
        if (mapEntityMerges != null) {
            List<IEntityMerge> entityMerges = mapEntityMerges.get(entityKey);
            if (entityMerges != null) {
                for (IEntityMerge entityMerge : entityMerges) {
                    try {
                        entityMerge.merge(entityName, entity, mergeType, null);

                    } catch (Throwable e) {
                        LOGGER.error("consistent merge error at " + entity, e);
                    }
                }
            }
        }
    }

    public static <T> void addEntityMergesForce(boolean force, String entityName, Class<T> entityClass, final IEntityMerge<T> entityMerge) {
        if (IConsistent.ME == null || !(force || IConsistent.ME.isMergeEntity(entityName, entityClass))) {
            L2EntityMergeService.ME.addEntityMerges(entityName, entityClass, entityMerge);

        } else {
            final String entityKey = entityName + '@' + entityClass;
            addEntityMerges(entityKey, entityMerge);
            L2EntityMergeService.ME.addEntityMerges(entityName, entityClass, new IEntityMerge<T>() {
                @Override
                public void merge(String entityName, T entity, MergeType mergeType, Object mergeEvent) {
                    entityMerge.merge(entityName, entity, mergeType, mergeEvent);
                    IConsistent.ME.pubMergeEntity(entityKey, entityName, entity, mergeType);
                }
            });
        }
    }

}
