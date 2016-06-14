/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-5-4 上午10:20:38
 */
package com.absir.aserv.developer.model;

import com.absir.aserv.crud.CrudEntity;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.developer.DeveloperService;
import com.absir.aserv.developer.editor.EditorObject;
import com.absir.aserv.developer.editor.EditorSupply;
import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.support.developer.IField;
import com.absir.aserv.support.developer.JCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaEmbedd;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.crud.BeanCrudFactory;
import com.absir.aserv.system.helper.HelperLang;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.CrudService;
import com.absir.binder.BinderUtils;
import com.absir.client.helper.HelperJson;
import com.absir.context.lang.LangBundle;
import com.absir.core.base.IBase;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.*;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.core.util.UtilAnnotation;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.EntityAssoc.EntityAssocEntity;
import com.absir.orm.value.*;
import com.absir.property.Property;
import com.absir.property.PropertyData;
import com.absir.validator.Validator;
import com.absir.validator.ValidatorSupply;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityField extends DBField {

    protected boolean embedd;

    public EntityField(String name, Property property, EditorObject editorObject, JoEntity joEntity) {
        // set properties
        crudField.setName(name);
        if (property == null) {
            return;
        }

        // setPropertyField
        crudField.setType(property.getType());
        crudField.setInclude(property.getInclude());
        crudField.setExclude(property.getExclude());

        // is valueField
        if (editorObject == null || joEntity == null) {
            return;
        }

        generated = editorObject.isGenerated();
        embedd = editorObject.isEmbedd();
        String field = HelperString.substringAfter(name, ".");
        caption = HelperLang.getFieldCaption(editorObject.getLang(), editorObject.getTag(), KernelString.isEmpty(field) ? name : field, joEntity.getEntityClass());

        // set joEntity
        Accessor accessor = null;
        Class<?> targetEntity = null;
        while (accessor == null) {
            accessor = property.getAccessor();
            ElementCollection elementCollection = accessor.getAnnotation(ElementCollection.class, true);
            if (elementCollection != null) {
                crudField.setCruds(CrudEntity.ALL);
                break;
            }

            ManyToMany manyToMany = accessor.getAnnotation(ManyToMany.class, true);
            if (manyToMany != null) {
                mappedBy = manyToMany.mappedBy();
                targetEntity = manyToMany.targetEntity();
                setCrudCascadeTypes(manyToMany.cascade());
                break;
            }

            OneToMany oneToMany = accessor.getAnnotation(OneToMany.class, true);
            if (oneToMany != null) {
                mappedBy = oneToMany.mappedBy();
                targetEntity = oneToMany.targetEntity();
                setCrudCascadeTypes(oneToMany.cascade());
                break;
            }

            ManyToOne manyToOne = accessor.getAnnotation(ManyToOne.class, true);
            if (manyToOne != null) {
                targetEntity = manyToOne.targetEntity();
                setCrudCascadeTypes(manyToOne.cascade());
                break;
            }

            OneToOne oneToOne = accessor.getAnnotation(OneToOne.class, true);
            if (oneToOne != null) {
                targetEntity = oneToOne.targetEntity();
                setCrudCascadeTypes(oneToOne.cascade());
                break;
            }

            setCrudCascadeTypes(null);
        }

        boolean referenceCrudKey = false;
        boolean referenceCrud = false;
        // set fieldType
        Class<?> fieldType = property.getType();
        Class[] componentClasses = property.getGenericType() == null ? KernelClass.componentClasses(fieldType) : KernelClass.componentClasses(property.getGenericType());
        entityName = SessionFactoryUtils.getEntityNameNull(componentClasses[0]);
        boolean mapped = false;
        if (!typeFieldType(fieldType)) {
            if (Map.class.isAssignableFrom(fieldType)) {
                mapped = true;
                canOrder = false;
                if (targetEntity != null && targetEntity != void.class) {
                    componentClasses[1] = targetEntity;
                }

                // 自动关联实体
                valueEntityName = SessionFactoryUtils.getEntityNameNull(componentClasses[1]);
                if (valueEntityName != null) {
                    crudField.setJoEntity(CrudUtils.newJoEntity(valueEntityName, null));

                } else if (componentClasses[1] != null && KernelClass.isCustomClass(componentClasses[1])) {
                    crudField.setJoEntity(CrudUtils.newJoEntity(null, componentClasses[1]));
                }

                if (entityName != null) {
                    crudField.setKeyJoEntity(CrudUtils.newJoEntity(entityName, null));

                } else if (componentClasses[0] != null && KernelClass.isCustomClass(componentClasses[0])) {
                    crudField.setKeyJoEntity(CrudUtils.newJoEntity(null, componentClasses[0]));
                }

                if (!typeFieldType(componentClasses[0]) && KernelClass.isCustomClass(componentClasses[0])) {
                    // crudField.setCruds(JaCrud.ALL);
                    referenceCrudKey = true;
                }

                EntityField valueField = new EntityField(crudField.getName(), property, null, null);
                valueField.crudField.setType(componentClasses[1]);
                this.valueField = valueField;
                if (!valueField.typeFieldType(componentClasses[1]) && KernelClass.isCustomClass(componentClasses[1])) {
                    referenceCrud = true;
                }

                types.add(0, JaEdit.EDIT_SUBTABLE);

            } else {
                // 自动关联实体
                if (entityName != null) {
                    crudField.setJoEntity(CrudUtils.newJoEntity(entityName, null));

                } else if (componentClasses[0] != null && KernelClass.isCustomClass(componentClasses[0])) {
                    crudField.setJoEntity(CrudUtils.newJoEntity(null, componentClasses[0]));
                }

                if (entityName == null) {
                    if (joEntity.getEntityName() != null) {
                        if (JiAssoc.class.isAssignableFrom(joEntity.getEntityClass()) && "assocId".equals(crudField.getName())) {
                            EntityAssocEntity assocEntity = SessionFactoryUtils.getEntityAssocEntity(joEntity.getEntityName());
                            if (assocEntity != null) {
                                entityName = assocEntity.getAssocName();
                            }

                        } else if (JiRelation.class.isAssignableFrom(JiRelation.class) && "relateId".endsWith(crudField.getName())) {
                            EntityAssocEntity assocEntity = SessionFactoryUtils.getEntityAssocEntity(joEntity.getEntityName());
                            if (assocEntity != null) {
                                entityName = assocEntity.getAssocEntity().getReferenceEntityName();
                            }
                        }
                    }
                }

                if (entityName == null) {
                    Class<?> componentClass = componentClasses[0];
                    if (fieldType.isArray()) {
                        if (KernelClass.isBasicClass(fieldType.getComponentType())) {
                            // types.add("params");

                        } else {
                            canOrder = false;
                        }

                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        canOrder = false;
                        collection = true;
                        if (!typeFieldType(componentClass) && KernelClass.isCustomClass(componentClass)) {
                            // crudField.setCruds(JaCrud.ALL);
                            referenceCrud = true;
                        }

                        types.add(0, JaEdit.EDIT_SUBTABLE);
                    }

                } else {
                    // referenceCrud = true;
                    referenceCrud = true;
                    if (Collection.class.isAssignableFrom(fieldType)) {
                        canOrder = false;
                    }
                }

                if (mappedBy == null && (crudField.getCruds() == null || !(KernelArray.contain(crudField.getCruds(), Crud.CREATE) && KernelArray.contain(crudField.getCruds(), Crud.DELETE)))) {
                    if (types.size() > 0 && JaEdit.EDIT_SUBTABLE.equals(types.get(0))) {
                        types.remove(0);
                    }
                }
            }
        }

        // set JaName JaNames JaClasses
        JaName jaName = property.getAccessor().getAnnotation(JaName.class, true);
        if (jaName != null) {
            if (!KernelString.isEmpty(jaName.value())) {
                entityName = jaName.value();
            }

        } else {
            JaNames jaNames = property.getAccessor().getAnnotation(JaNames.class, true);
            if (jaNames != null) {
                if (!KernelString.isEmpty(jaNames.key())) {
                    entityName = jaNames.key();
                }

                if (!KernelString.isEmpty(jaNames.value())) {
                    valueEntityName = jaNames.value();
                }
            }
        }

        Class<?> entityClass = null;
        Class<?> valueEntityClass = null;
        JaClasses jaClasses = property.getAccessor().getAnnotation(JaClasses.class, true);
        if (jaClasses != null) {
            if (jaClasses.key() != void.class) {
                entityClass = jaClasses.key();
                if (entityName == null) {
                    entityName = SessionFactoryUtils.getEntityNameNull(entityClass);
                }
            }

            if (jaClasses.value() != void.class) {
                valueEntityClass = jaClasses.value();
                if (valueEntityName == null) {
                    valueEntityName = SessionFactoryUtils.getEntityNameNull(valueEntityClass);
                }
            }
        }

        if (mapped) {
            if (crudField.getKeyJoEntity() == null && !KernelString.isEmpty(entityName)) {
                crudField.setKeyJoEntity(new JoEntity(entityName, null));
            }

            if (crudField.getJoEntity() == null && !KernelString.isEmpty(valueEntityName)) {
                crudField.setJoEntity(new JoEntity(valueEntityName, null));
            }
        }

        // set editorName
        String editorName = editorObject.getValueName();
        if (KernelString.isEmpty(editorName)) {
            editorName = editorObject.getValueClass() == null ? null : SessionFactoryUtils.getEntityNameNull(editorObject.getValueClass());
        }

        valueEntityName = editorName == null ? entityName : editorName;
        if (Map.class.isAssignableFrom(property.getType())) {
            editorName = editorObject.getKeyName();
            if (KernelString.isEmpty(editorName)) {
                editorName = editorObject.getKeyClass() == null ? null : SessionFactoryUtils.getEntityNameNull(editorObject.getKeyClass());
            }

            if (editorName != null) {
                entityName = editorName;
            }

        } else {
            entityName = valueEntityName;
        }

        if (crudField.getJoEntity() == null || fieldType != componentClasses[0]) {
            embedd = false;

        } else if (!embedd) {
            embedd = fieldType.getAnnotation(JaEmbedd.class) != null || fieldType.getAnnotation(Embeddable.class) != null;
        }

        // embedd referenceCrudKey
        if (componentClasses.length == 2) {
            if (entityName != null) {
                if (componentClasses[0] == SessionFactoryUtils.getIdentifierType(entityName, null)) {
                    embedd = false;
                    referenceCrudKey = false;
                }

                if (crudField.getKeyJoEntity() == null) {
                    crudField.setKeyJoEntity(new JoEntity(entityName, entityClass));
                }
            }

            if (valueEntityName != null) {
                if (componentClasses[1] == SessionFactoryUtils.getIdentifierType(valueEntityName, null)) {
                    embedd = false;
                    referenceCrud = false;
                }

                if (crudField.getJoEntity() == null) {
                    crudField.setJoEntity(new JoEntity(valueEntityName, valueEntityClass));
                }
            }

        } else {
            if (entityName != null) {
                if (componentClasses[0] == SessionFactoryUtils.getIdentifierType(entityName, null)) {
                    embedd = false;
                    referenceCrud = false;
                }

                if (crudField.getJoEntity() == null) {
                    crudField.setJoEntity(new JoEntity(entityName, entityClass));
                }
            }
        }

        // embedd curd select selects
        if (embedd) {
            crudField.setCruds(CrudEntity.ALL);

        } else {
            if (mapped) {
                if (entityName != null) {
                    types.add(referenceCrudKey ? "select" : "iselect");

                } else if (referenceCrudKey) {
                    crudField.setCruds(CrudEntity.ALL);
                }

                if (valueEntityName != null) {
                    valueField.getTypes().add(referenceCrud ? "select" : "iselect");

                } else if (referenceCrud) {
                    crudField.setCruds(CrudEntity.ALL);
                }

            } else if (Collection.class.isAssignableFrom(fieldType)) {
                if (entityName != null) {
                    types.add(referenceCrud ? "selects" : "iselects");

                } else if (referenceCrud) {
                    crudField.setCruds(CrudEntity.ALL);
                }

            } else {
                if (entityName != null) {
                    types.add(referenceCrud ? "select" : fieldType.isArray() ? "iselects" : "iselect");

                } else {
                    if (fieldType.isArray() && KernelClass.isBasicClass(fieldType.getComponentType())) {
                        types.add("params");
                    }
                }
            }
        }

        // set crud
        if (editorObject.getCrud() != null || editorObject.getCrudValue() != null) {
            JCrud jCrud = crudField.getjCrud();
            if (jCrud == null) {
                jCrud = new JCrud();
                crudField.setjCrud(jCrud);
            }

            jCrud.setJaCrud(editorObject.getCrud());
            jCrud.setJaCrudValue(editorObject.getCrudValue());
            if ((KernelString.isEmpty(jCrud.getValue()) && (jCrud.getFactory() == null || jCrud.getFactory() == void.class)) || (jCrud.getCruds() == null || jCrud.getCruds().length == 0)) {
                crudField.setjCrud(null);
            }
        }

        // set edit
        if (editorObject.getEdit() != null) {
            UtilAnnotation.copy(editorObject.getEdit(), this);
            if (editorObject.getEdit().types().length > 0) {
                types.addAll(0, KernelArray.toList(editorObject.getEdit().types()));
            }

            Object metasObject = null;
            if (!KernelString.isEmpty(editorObject.getEdit().metas())) {
                try {
                    metasObject = HelperJson.decode(editorObject.getEdit().metas(), Map.class);

                } catch (Exception e) {
                    DeveloperService.LOGGER.error("could not load metas " + field, e);
                }
            }

            if (metasObject != null) {
                KernelObject.copy(metasObject, metas);
            }
        }

        // set metas
        if (editorObject.getMetas() != null) {
            for (Entry<Object, Object> entry : editorObject.getMetas().entrySet()) {
                metas.put(entry.getKey().toString(), entry.getValue());
            }
        }

        // set metas valueField
        if (valueField != null) {
            Object valueFieldMap = metas.get("valueField");
            if (valueFieldMap != null && valueFieldMap instanceof Map) {
                DynaBinder.mapTo((Map) valueFieldMap, valueField);
            }
        }

        // property allow
        if (property.getAllow() < 0) {
            types.add("null");

        } else if (property.getAllow() > 0) {
            types.add("none");
        }
    }

    public static void addEntityFieldScope(String name, JoEntity joEntity, Collection<IField> fieldScope, EntityModel entityModel) {
        String identifierName = null;
        String entityName = joEntity.getEntityName();
        if (entityName == null) {
            entityName = joEntity.getEntityClass().getSimpleName();
        }

        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
        if (crudSupply != null) {
            identifierName = crudSupply.getIdentifierName(entityName);
        }

        ValidatorSupply validatorSupply = BinderUtils.getValidatorSupply();
        validatorSupply.getPropertyMap(joEntity.getEntityClass());
        Map<String, PropertyData> propertyMap = EditorSupply.ME.getPropertyMap(joEntity.getEntityClass());
        for (Entry<String, PropertyData> entry : propertyMap.entrySet()) {
            PropertyData propertyData = entry.getValue();
            Property property = propertyData.getProperty();
            EditorObject editorObject = EditorSupply.ME.getPropertyObject(entry.getValue());
            if (editorObject == null || property.getAllow() < 0 || isTransientField(property, editorObject)) {
                continue;
            }

            String fieldName = entry.getKey();
            if (!KernelString.isEmpty(name)) {
                fieldName = name + '.' + fieldName;
            }

            EntityField entityField = null;
            boolean primary = false;
            if (entityModel != null && (property.getAccessor().getAnnotation(Id.class, true) != null || (identifierName != null && identifierName.equals(fieldName)))) {
                entityField = new EntityField(fieldName, property, editorObject, joEntity);
                if ((entityField.getType() == Object.class || entityField.getType() == Serializable.class) && IBase.class.isAssignableFrom(joEntity.getEntityClass()) && fieldName.equals("id")) {
                    entityField.getCrudField().setType(KernelClass.typeClass(joEntity.getEntityClass(), IBase.ID_VARIABLE));
                }

                primary = true;
                entityModel.setPrimary(entityField);

            } else if (!isMappedByField(property, editorObject)) {
                entityField = new EntityField(fieldName, property, editorObject, joEntity);
            }

            if (entityModel != null) {
                if (entityField == null || entityField.getEditable() != JeEditable.ENABLE) {
                    entityModel.setFilter(true);
                }

                if (entityField != null) {
                    if (name == null) {
                        entityModel.addCrudField(entityField);

                    } else {
                        if (name.indexOf('.') < 0) {
                            IField curdField = entityModel.getCrudField();
                            if (curdField.getCrudField().getjCrud() == null) {
                                if (LangBundle.isI18n() && property.getAccessor().getAnnotation(Langs.class, true) != null) {
                                    JCrud jCrud = new JCrud();
                                    jCrud.setJaCrud(null, BeanCrudFactory.class, KernelLang.NULL_OBJECTS, CrudEntity.ALL);
                                    curdField.getCrudField().setjCrud(jCrud);
                                }
                            }
                        }
                    }

                    if (entityField.getEditable() == JeEditable.ENABLE) {
                        entityModel.addGroupField("editable", entityField);

                    } else if (entityField.getEditable() == JeEditable.OPTIONAL) {
                        entityModel.addGroupField("optional", entityField);

                    } else if (entityField.getEditable() == JeEditable.LOCKED) {
                        entityModel.addGroupField("locked", entityField);

                    } else if (entityField.getEditable() == JeEditable.LOCKABLE) {
                        entityField.editable = JeEditable.LOCKED;

                    } else if (entityField.getEditable() == JeEditable.LOCKNONE) {
                        entityField.editable = JeEditable.LOCKED;
                        entityModel.addGroupField("none", entityField);
                    }
                }
            }

            if (entityField != null) {
                entityField.addEntityFieldScope(validatorSupply.getPropertyObject(propertyData), primary ? entityModel.getPrimaries() : fieldScope, entityModel);
            }
        }
    }

    private static boolean isTransientField(Property property, EditorObject editorObject) {
        if (editorObject != null) {
            if (editorObject.getEdit() != null) {
                return editorObject.getEdit().editable() == JeEditable.DISABLE;
            }

            Field field = property.getField();
            if (field == null) {
                Method method = property.getAccessor().getGetter();
                if (method != null) {
                    return Modifier.isTransient(method.getModifiers()) || method.getAnnotation(Transient.class) != null;
                }

            } else {
                return Modifier.isTransient(field.getModifiers()) || field.getAnnotation(Transient.class) != null;
            }
        }

        return true;
    }

    private static boolean isMappedByField(Property property, EditorObject editorObject) {
        OneToMany oneToMany = property.getAccessor().getAnnotation(OneToMany.class, true);
        ManyToMany manyToMany = property.getAccessor().getAnnotation(ManyToMany.class, true);
        if ((oneToMany == null || oneToMany.mappedBy().isEmpty()) && (manyToMany == null || manyToMany.mappedBy().isEmpty())) {
            return false;
        }

        if (editorObject.getEdit() != null && editorObject.getEdit().types().length > 0) {
            return false;
        }

        return true;
    }

    protected void setCrudCascadeTypes(CascadeType[] cascadeTypes) {
        if (cascadeTypes == null) {
            crudField.setCruds(CrudEntity.ALL);
            return;
        }

        Set<Crud> cruds = new HashSet<Crud>();
        for (CascadeType cascadeType : cascadeTypes) {
            if (cascadeType == CascadeType.ALL) {
                crudField.setCruds(CrudEntity.ALL);
                return;

            } else if (cascadeType == CascadeType.PERSIST) {
                cruds.add(Crud.CREATE);

            } else if (cascadeType == CascadeType.MERGE) {
                cruds.add(Crud.UPDATE);

            } else if (cascadeType == CascadeType.REMOVE) {
                cruds.add(Crud.DELETE);
            }
        }

        if (cruds.size() > 0) {
            crudField.setCruds(KernelCollection.toArray(cruds, Crud.class));
        }
    }

    protected boolean typeFieldType(Class<?> fieldType) {
        if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
            types.add("option");

        } else if (Enum.class.isAssignableFrom(fieldType)) {
            types.add("enum");
            metas.put("values", CrudUtils.getEnumMetaMap((Class<? extends Enum>) fieldType));

        } else if (Date.class.isAssignableFrom(fieldType)) {
            types.add("date");

        } else {
            return KernelClass.isBasicClass(fieldType);
        }

        return true;
    }

    public void addEntityFieldScope(List<Validator> validators, Collection<IField> fieldScope, EntityModel entityModel) {
        // set validators
        if (validators != null) {
            Object validatorObject = metas.get("validators");
            Map<String, Object> validatorMap = validatorObject == null || !(validatorObject instanceof Map) ? null : (Map<String, Object>) validatorObject;
            if (validatorMap == null) {
                validatorMap = new HashMap<String, Object>();
                metas.put("validators", validatorMap);
            }

            List<String> validatorClasses = new ArrayList<String>();
            for (Validator validator : validators) {
                String validatorClass = validator.getValidateClass(validatorMap);
                if (!KernelString.isEmpty(validatorClass)) {
                    if ("required".equals(validatorClass)) {
                        nullable = false;

                    } else {
                        validatorClasses.add(validatorClass);
                    }
                }
            }

            if (validatorClasses.size() > 0) {
                metas.put("validatorClass", KernelString.implode(validatorClasses, ' '));
            }
        }

        if (embedd && editable != JeEditable.LOCKED) {
            addEntityFieldScope(crudField.getName(), crudField.getJoEntity(), fieldScope, entityModel);

        } else {
            fieldScope.add(this);
        }
    }
}
