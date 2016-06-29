/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer.model;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.support.Developer;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.helper.HelperLang;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.orm.value.JoEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFactory {

    private static final Map<JoEntity, EntityModel> Jo_Entity_Map_Entity_Model = new HashMap<JoEntity, EntityModel>();

    public static EntityModel getModelEntity(String entityName) {
        return getModelEntity(CrudUtils.newJoEntity(entityName, null));
    }

    public static EntityModel getModelEntity(Class<?> entityClass) {
        return getModelEntity(CrudUtils.newJoEntity(null, entityClass));
    }

    public static EntityModel getModelEntity(JoEntity joEntity) {
        if (joEntity.getEntityClass() == null) {
            return null;
        }

        EntityModel entityModel = Jo_Entity_Map_Entity_Model.get(joEntity);
        if (entityModel == null) {
            synchronized (joEntity.getEntityToken()) {
                entityModel = Jo_Entity_Map_Entity_Model.get(joEntity);
                if (entityModel == null) {
                    entityModel = generateModelEntity(joEntity);

                    if (entityModel != null) {
                        Jo_Entity_Map_Entity_Model.put(joEntity, entityModel);
                    }
                }
            }
        }

        return entityModel;
    }

    protected static EntityModel generateModelEntity(JoEntity joEntity) {
        if (joEntity.getClass() == null) {
            return null;

        } else {
            return getModelEntityClass(joEntity, new EntityModel());
        }
    }

    private static EntityModel getModelEntityClass(final JoEntity joEntity, final EntityModel entityModel) {
        if (BeanFactoryUtils.getEnvironment() != Environment.DEVELOP) {
            entityModel.setUpdate(Developer.lastModified(joEntity));
        }

        entityModel.setJoEntity(joEntity);
        String caption = HelperLang.getTypeCaption(joEntity.getEntityClass(), joEntity.getEntityName());
        Class<?> entityClass = joEntity.getEntityClass();
        if (entityClass != null) {
            if (KernelString.isEmpty(caption) || caption.equals(entityClass.getSimpleName())) {
                if (entityClass != null) {
                    MaEntity maEntity = KernelClass.fetchAnnotation(entityClass, MaEntity.class);
                    if (maEntity != null) {
                        caption = maEntity.name();
                    }
                }
            }
        }

        entityModel.setCaption(caption);
        final List<JaCrud> jaCrudList = new ArrayList<JaCrud>();
        final List<JaCrud> jaCrudScope = new ArrayList<JaCrud>();
        KernelReflect.doWithClasses(joEntity.getEntityClass(), new CallbackBreak<Class<?>>() {

            @Override
            public void doWith(Class<?> template) throws BreakException {
                for (Class<?> cls : template.getInterfaces()) {
                    JaCrud jaCrud = cls.getAnnotation(JaCrud.class);
                    if (jaCrud != null) {
                        jaCrudScope.add(jaCrud);
                    }
                }

                JaCrud jaCrud = template.getAnnotation(JaCrud.class);
                if (jaCrud != null) {
                    jaCrudScope.add(jaCrud);
                }

                jaCrudList.addAll(0, jaCrudScope);
                jaCrudScope.clear();
            }
        });

        for (JaCrud jaCrud : jaCrudList) {
            entityModel.addJaCrud(jaCrud);
        }

        EntityField.addEntityFieldScope(null, joEntity, entityModel.getFields(), entityModel);
        entityModel.addComplete();
        return entityModel;
    }
}
