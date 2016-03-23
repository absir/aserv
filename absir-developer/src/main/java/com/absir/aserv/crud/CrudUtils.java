/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-7 下午5:42:28
 */
package com.absir.aserv.crud;

import com.absir.aop.AopProxy;
import com.absir.aserv.crud.CrudHandler.CrudInvoker;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.support.Developer;
import com.absir.aserv.support.developer.DModel;
import com.absir.aserv.support.developer.IDeveloper;
import com.absir.aserv.support.developer.IModel;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.helper.HelperLang;
import com.absir.aserv.system.service.CrudService;
import com.absir.bean.basis.Configure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.core.util.UtilRuntime;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JoEntity;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
@Configure
public abstract class CrudUtils {

    private static final Map<JoEntity, CrudEntity> Jo_Entity_Map_Crud_Entity = new HashMap<JoEntity, CrudEntity>();

    private static final Map<JoEntity, DModel> Jo_Entity_Map_Crud_Model = new HashMap<JoEntity, DModel>();

    private static final Map<String, String[]> Jo_Entity_Map_Crud_Fields = new HashMap<String, String[]>();
    private static Map<Class<? extends Enum>, Map<String, String[]>> enumMapMetaMap = new HashMap<Class<? extends Enum>, Map<String, String[]>>();

    public static JoEntity newJoEntity(String entityName, Class<?> entityClass) {
        if (entityName == null) {
            while (AopProxy.class.isAssignableFrom(entityClass) || HibernateProxy.class.isAssignableFrom(entityClass)) {
                entityClass = entityClass.getSuperclass();
            }

            entityName = SessionFactoryUtils.getEntityNameNull(entityClass);
            if (entityName == null) {
                entityName = entityClass.getSimpleName();
                ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
                if (crudSupply == null || crudSupply.getEntityClass(entityName) != entityClass) {
                    entityName = null;
                }
            }

        } else if (entityClass == null) {
            ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
            if (crudSupply != null) {
                entityClass = crudSupply.getEntityClass(entityName);
            }
        }

        return new JoEntity(entityName, entityClass);
    }

    public static Map<String, Object> crudRecord(JoEntity joEntity, Object entity, PropertyFilter filter) {
        if (entity == null) {
            return null;
        }

        CrudEntity crudEntity = getCrudEntity(joEntity);
        if (crudEntity == null) {
            return null;
        }

        Map<String, Object> record = new HashMap<String, Object>();
        if (filter == null) {
            filter = new PropertyFilter();

        } else {
            filter = filter.newly();
        }

        String propertyPath = filter.getPropertyPath();
        Iterator<CrudPropertyReference> rIterator = crudEntity.getCrudPropertyReferencesIterator();
        if (rIterator != null) {
            while (rIterator.hasNext()) {
                CrudPropertyReference crudPropertyReference = rIterator.next();
                if (filter.isMatchPath(propertyPath, crudPropertyReference.getCrudProperty().getName())) {
                    record.put(filter.getPropertyPath(), crudPropertyReference.getCrudProperty().get(entity));
                }
            }
        }

        Iterator<CrudProperty> pIterator = crudEntity.getCrudPropertiesIterator();
        if (pIterator != null) {
            while (pIterator.hasNext()) {
                CrudProperty crudProperty = pIterator.next();
                if (filter.isMatchPath(propertyPath, crudProperty.getName())) {
                    record.put(filter.getPropertyPath(), crudProperty.get(entity));
                }
            }
        }

        return record;
    }

    public static void crud(JaCrud.Crud crud, Map<String, Object> crudRecord, JoEntity joEntity, Object entity,
                            PropertyFilter filter, final JiUserBase user) {
        CrudEntity crudEntity = getCrudEntity(joEntity);
        if (crudEntity == null) {
            return;
        }

        if (filter == null) {
            filter = new PropertyFilter();

        } else {
            filter = filter.newly();
        }

        CrudInvoker crudInvoker = new CrudInvoker(crud, crudRecord, filter, crudEntity, entity) {

            @Override
            public boolean isSupport(CrudProperty crudProperty) {
                return filter.allow(crudProperty.getInclude(), crudProperty.getExclude());
            }

            @Override
            public void crudInvoke(CrudProperty crudProperty, Object entity) {
                crudProperty.crudProcessor.crud(crudProperty, entity, this, user);
            }
        };

        if (entity instanceof ICrudBean) {
            ((ICrudBean) entity).processCrud(crud, crudInvoker);
        }

        crud(entity, crudEntity, crudInvoker);
    }

    protected static void crud(Object entity, CrudEntity crudEntity, CrudInvoker crudInvoker) {
        if (crudEntity != null) {
            PropertyFilter filter = crudInvoker.filter;
            String propertyPath = filter.getPropertyPath();
            Iterator<CrudPropertyReference> rIterator = crudEntity.getCrudPropertyReferencesIterator();
            List<Object[]> updateRecords = null;
            if (rIterator != null) {
                while (rIterator.hasNext()) {
                    CrudPropertyReference crudPropertyReference = rIterator.next();
                    CrudProperty crudProperty = crudPropertyReference.getCrudProperty();
                    if (filter.isMatchPath(propertyPath, crudProperty.getName())) {
                        crudPropertyReference.crud(entity, crudInvoker);
                        if (crudInvoker.crud == Crud.UPDATE && entity == crudInvoker.getRoot()
                                && crudInvoker.getCrudRecord() != null) {
                            Object record = crudProperty.get(entity);
                            if (record != null) {
                                Object property = crudProperty.get(entity);
                                if (record != property) {
                                    if (updateRecords == null) {
                                        updateRecords = new ArrayList<Object[]>();
                                    }

                                    updateRecords.add(new Object[]{filter.getPropertyPath(), crudPropertyReference, record,
                                            property});
                                }
                            }
                        }
                    }
                }
            }

            Iterator<CrudProperty> pIterator = crudEntity.getCrudPropertiesIterator();
            if (pIterator != null) {
                while (pIterator.hasNext()) {
                    CrudProperty crudProperty = pIterator.next();
                    if (crudInvoker.isSupport(crudInvoker.crud, crudProperty)
                            && filter.isMatchPath(propertyPath, crudProperty.getName())) {
                        crudInvoker.crudInvoke(crudProperty, entity);
                    }
                }
            }

            if (updateRecords != null) {
                crudInvoker.crud = Crud.DELETE;
                for (Object[] update : updateRecords) {
                    crudInvoker.filter.setPropertyPath((String) update[0]);
                    CrudPropertyReference crudPropertyReference = (CrudPropertyReference) update[1];
                    CrudProperty crudProperty = crudPropertyReference.getCrudProperty();
                    crudProperty.set(entity, update[2]);
                    crudPropertyReference.crud(entity, crudInvoker);
                    crudProperty.set(entity, update[3]);
                }

                crudInvoker.crud = Crud.UPDATE;
            }
        }
    }

    public static Boolean getCrudFilter(JoEntity joEntity) {
        return getCrudModel(joEntity).isFilter();
    }

    public static DModel getCrudModel(JoEntity joEntity) {
        DModel model = Jo_Entity_Map_Crud_Model.get(joEntity);
        if (model == null) {
            synchronized (joEntity.getEntityToken()) {
                model = Jo_Entity_Map_Crud_Model.get(joEntity);
                if (model == null) {
                    String runtimeName = UtilRuntime.getRuntimeName(CrudUtils.class, "crudFiters/" + joEntity);
                    if (IDeveloper.ME == null) {
                        model = (DModel) Developer.getRuntime(runtimeName);

                    } else {
                        IModel iModel = IDeveloper.ME.getModelEntity(joEntity);
                        model = iModel == null ? null : iModel.getModel();
                        if (model != null) {
                            Developer.setRuntime(runtimeName, model);
                        }
                    }

                    if (model == null) {
                        model = DModel.DEFAULT;
                    }

                    Jo_Entity_Map_Crud_Model.put(joEntity, model);
                }
            }
        }

        return model;
    }

    public static Map<String, String[]> getEnumMetaMap(Class<? extends Enum> enumClass) {
        Map<String, String[]> enumMap = enumMapMetaMap.get(enumClass);
        if (enumMap == null) {
            synchronized (enumClass) {
                enumMap = enumMapMetaMap.get(enumClass);
                if (enumMap == null) {
                    enumMap = new LinkedHashMap<String, String[]>();
                    Enum<?>[] enums = enumClass.getEnumConstants();
                    for (Enum<?> enumerate : enums) {
                        enumMap.put(enumerate.name(), HelperLang.getEnumNameCaptions(enumerate));
                    }

                    enumMapMetaMap.put(enumClass, enumMap);
                }
            }
        }

        return enumMap;
    }

    public static String[] getGroupFields(JoEntity joEntity, String group) {
        String crudFieldsKey = joEntity.getEntityName() + joEntity.getClass() + "/" + group;
        String[] fields = Jo_Entity_Map_Crud_Fields.get(crudFieldsKey);
        if (fields == null) {
            synchronized (joEntity.getEntityToken()) {
                fields = Jo_Entity_Map_Crud_Fields.get(crudFieldsKey);
                if (fields == null) {
                    String runtimeName = UtilRuntime.getRuntimeName(CrudUtils.class, "crudFields/" + crudFieldsKey);
                    if (IDeveloper.ME == null) {
                        fields = (String[]) Developer.getRuntime(runtimeName);

                    } else {
                        fields = IDeveloper.ME.getGroupFields(joEntity, group);
                        Developer.setRuntime(runtimeName, fields);
                    }

                    if (fields == null) {
                        fields = KernelLang.NULL_STRINGS;
                    }

                    Jo_Entity_Map_Crud_Fields.put(crudFieldsKey, fields);
                }
            }
        }

        return fields;
    }

    public static CrudEntity getCrudEntity(JoEntity joEntity) {
        CrudEntity crudEntity = Jo_Entity_Map_Crud_Entity.get(joEntity);
        if (crudEntity != null) {
            return crudEntity.isCrudEntityNone() ? null : crudEntity;
        }

        synchronized (joEntity.getEntityToken()) {
            crudEntity = generateCrudEntity(joEntity);
        }

        return crudEntity.isCrudEntityNone() ? null : crudEntity;
    }

    public static CrudProperty getCrudProperty(JoEntity joEntity, String name) {
        CrudEntity crudEntity = getCrudEntity(joEntity);
        if (crudEntity != null && crudEntity.crudProperties != null) {
            for (CrudProperty crudProperty : crudEntity.crudProperties) {
                if (crudProperty.getName().equals(name)) {
                    return crudProperty;
                }
            }
        }

        return null;
    }

    public static CrudPropertyReference getCrudPropertyReference(JoEntity joEntity, String name) {
        CrudEntity crudEntity = getCrudEntity(joEntity);
        if (crudEntity != null && crudEntity.crudPropertyReferences != null) {
            for (CrudPropertyReference crudPropertyReference : crudEntity.crudPropertyReferences) {
                if (crudPropertyReference.getCrudProperty().getName().equals(name)) {
                    return crudPropertyReference;
                }
            }
        }

        return null;
    }

    protected static CrudEntity generateCrudEntity(JoEntity joEntity, Class<?>[] fieldComponentTypes, int index) {
        if (fieldComponentTypes != null && joEntity.getEntityClass() == KernelArray.get(fieldComponentTypes, index)) {
            return generateCrudEntity(joEntity);
        }

        return null;
    }

    private static CrudEntity generateCrudEntity(JoEntity joEntity) {
        CrudEntity crudEntity = Jo_Entity_Map_Crud_Entity.get(joEntity);
        if (crudEntity != null) {
            return crudEntity;
        }

        List<JCrudField> crudFields = null;
        String runtimeName = UtilRuntime.getRuntimeName(CrudUtils.class, "Crud_Fields_" + joEntity.toString());
        if (IDeveloper.ME == null) {
            crudFields = (List<JCrudField>) UtilRuntime.getRuntime(runtimeName);

        } else {
            crudFields = IDeveloper.ME.getCrudFields(joEntity);
        }

        crudEntity = new CrudEntity();
        for (JCrudField crudField : crudFields) {
            addCrudEntityProperty(joEntity, crudEntity, crudField, joEntity.getEntityClass());
        }

        crudEntity.joEntity = joEntity;
        Jo_Entity_Map_Crud_Entity.put(joEntity, crudEntity);
        crudEntity.initCrudEntity();
        return crudEntity;
    }

    private static void addCrudEntityProperty(JoEntity joEntity, CrudEntity crudEntity, JCrudField crudField, Class<?> entityClass) {
        ICrudProcessor crudProcessor = getCrudProcessor(joEntity, crudField);
        if (crudProcessor == null && crudField.getJoEntity() == null && crudField.getKeyJoEntity() == null) {
            return;
        }

        CrudProperty crudProperty = null;
        if (crudField.getName() == null) {
            crudProperty = new CrudPropertyNone();

        } else if (Map.class.isAssignableFrom(entityClass)) {
            CrudPropertyName crudPropertyName = new CrudPropertyName();
            crudPropertyName.name = crudField.getName();
            crudProperty = crudPropertyName;

        } else {
            Accessor accessor = UtilAccessor.getAccessorProperty(entityClass, crudField.getName());
            if (accessor == null) {
                return;
            }

            CrudPropertyAccessor crudPropertyAccessor = new CrudPropertyAccessor();
            crudPropertyAccessor.name = crudField.getName();
            crudPropertyAccessor.accessor = accessor;
            crudProperty = crudPropertyAccessor;
        }

        crudProperty.crudProcessor = crudProcessor;
        crudProperty.type = crudField.getType();
        crudProperty.include = crudField.getInclude();
        crudProperty.exclude = crudField.getExclude();
        crudProperty.jCrud = crudField.getjCrud();
        crudProperty.keyEntity = crudField.getKeyJoEntity();
        crudProperty.valueEntity = crudField.getJoEntity();

        if (crudProcessor != null && crudProperty.jCrud != null) {
            crudEntity.addCrudProperty(crudProperty);
        }

        crudProperty.crudEntity = crudEntity;
        if (crudField.getCruds() != null && !(crudField.getJoEntity() == null && crudField.getKeyJoEntity() == null)) {
            CrudPropertyReference crudPropertyReference = null;
            if (Map.class.isAssignableFrom(crudProperty.type)) {
                crudPropertyReference = new CrudPropertyMap();
                Class<?>[] fieldComponentTypes = KernelClass.componentClasses(crudField.getType());
                if (crudField.getJoEntity() != null) {
                    crudPropertyReference.valueCrudEntity = generateCrudEntity(crudField.getJoEntity(), fieldComponentTypes, 1);
                }

                if (crudField.getKeyJoEntity() != null) {
                    ((CrudPropertyMap) crudPropertyReference).keyCrudEntity = generateCrudEntity(crudField.getKeyJoEntity(), fieldComponentTypes, 0);
                }

                if ((crudPropertyReference.valueCrudEntity == null || crudPropertyReference.valueCrudEntity.crudProperties == null)
                        && (((CrudPropertyMap) crudPropertyReference).keyCrudEntity == null || ((CrudPropertyMap) crudPropertyReference).keyCrudEntity.crudProperties == null)) {
                    return;
                }

            } else if (crudField.getJoEntity() != null) {
                if (Collection.class.isAssignableFrom(crudProperty.type)) {
                    crudPropertyReference = new CrudPropertyCollection();

                } else if (Collection.class.isAssignableFrom(crudProperty.type)) {
                    crudPropertyReference = new CrudPropertyArray();

                } else {
                    crudPropertyReference = new CrudPropertyEntity();
                }

                crudPropertyReference.valueCrudEntity = generateCrudEntity(crudField.getJoEntity(), KernelClass.componentClasses(crudField.getType()), 0);
                if (crudPropertyReference.valueCrudEntity == null || crudPropertyReference.valueCrudEntity.crudProperties == null) {
                    return;
                }
            }

            if (crudPropertyReference != null) {
                crudPropertyReference.crudProperty = crudProperty;
                crudPropertyReference.cruds = crudField.getCruds();
                crudEntity.addCrudPropertyReference(crudPropertyReference);
            }
        }
    }

    private static ICrudProcessor getCrudProcessor(JoEntity joEntity, JCrudField crudField) {
        if (crudField.getjCrud() == null) {
            return null;
        }

        ICrudFactory crudFactory = BeanFactoryUtils.getRegisterBeanObject(crudField.getjCrud().getValue(), ICrudFactory.class,
                (Class<? extends ICrudFactory>) crudField.getjCrud().getFactory());
        return crudFactory == null ? null : crudFactory.getProcessor(joEntity, crudField);
    }

    private static class CrudPropertyNone extends CrudProperty {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Object get(Object entity) {
            return null;
        }

        @Override
        public void set(Object entity, Object propertyValue) {
        }
    }

    private static class CrudPropertyName extends CrudProperty {

        private String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object get(Object entity) {
            return ((Map) entity).get(name);
        }

        @Override
        public void set(Object entity, Object propertyValue) {
            ((Map) entity).put(name, propertyValue);
        }
    }

    private static class CrudPropertyAccessor extends CrudProperty {

        private String name;

        private Accessor accessor;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object get(Object entity) {
            return accessor.get(entity);
        }

        @Override
        public void set(Object entity, Object propertyValue) {
            accessor.set(entity, propertyValue);
        }

        @Override
        public Accessor getAccessor() {
            return accessor;
        }
    }

    private static class CrudPropertyEntity extends CrudPropertyReference {

        @Override
        protected void crud(Object entity, CrudInvoker crudInvoker) {
            if (KernelArray.contain(cruds, crudInvoker.crud)) {
                entity = crudProperty.get(entity);
                if (entity != null) {
                    CrudUtils.crud(entity, valueCrudEntity, crudInvoker);
                }
            }
        }
    }

    private static class CrudPropertyCollection extends CrudPropertyReference {

        @Override
        protected void crud(Object entity, CrudInvoker crudInvoker) {
            if (KernelArray.contain(cruds, crudInvoker.crud)) {
                entity = crudProperty.get(entity);
                if (entity != null) {
                    String propertyPath = crudInvoker.filter.getPropertyPath();
                    int i = 0;
                    for (Object element : (Collection<Object>) entity) {
                        if (crudInvoker.filter.isMatch(String.valueOf(i++))) {
                            CrudUtils.crud(element, valueCrudEntity, crudInvoker);
                        }

                        crudInvoker.filter.setPropertyPath(propertyPath);
                    }
                }
            }
        }
    }

    private static class CrudPropertyArray extends CrudPropertyReference {

        @Override
        protected void crud(Object entity, CrudInvoker crudInvoker) {
            if (KernelArray.contain(cruds, crudInvoker.crud)) {
                entity = crudProperty.get(entity);
                if (entity != null) {
                    String propertyPath = crudInvoker.filter.getPropertyPath();
                    int i = 0;
                    for (Object element : (Object[]) entity) {
                        if (crudInvoker.filter.isMatch(String.valueOf(i++))) {
                            CrudUtils.crud(element, valueCrudEntity, crudInvoker);
                        }

                        crudInvoker.filter.setPropertyPath(propertyPath);
                    }
                }
            }
        }
    }

    protected static class CrudPropertyMap extends CrudPropertyReference {

        protected CrudEntity keyCrudEntity;

        @Override
        protected void crud(Object entity, CrudInvoker crudInvoker) {
            if (KernelArray.contain(cruds, crudInvoker.crud)) {
                entity = crudProperty.get(entity);
                if (entity != null) {
                    String propertyPath = crudInvoker.filter.getPropertyPath();
                    int i = 0;
                    for (Entry<Object, Object> entry : ((Map<Object, Object>) entity).entrySet()) {
                        if (keyCrudEntity != null && crudInvoker.filter.isMatch(String.valueOf(i))) {
                            CrudUtils.crud(entry.getKey(), keyCrudEntity, crudInvoker);
                        }

                        if (valueCrudEntity != null && crudInvoker.filter.isMatchPath(propertyPath, ":" + String.valueOf(i))) {
                            CrudUtils.crud(entry.getValue(), valueCrudEntity, crudInvoker);
                        }

                        crudInvoker.filter.setPropertyPath(propertyPath);
                        i++;
                    }
                }
            }
        }
    }
}
