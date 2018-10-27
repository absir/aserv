/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer.model;

import com.absir.aserv.crud.CrudEntity;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.crud.ICrudSubmit;
import com.absir.aserv.support.developer.DModel;
import com.absir.aserv.support.developer.IField;
import com.absir.aserv.support.developer.IModel;
import com.absir.aserv.support.developer.JCrud;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.crud.BeanCrudFactory;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAnnotation;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JoEntity;

import java.lang.reflect.TypeVariable;
import java.util.*;

public class EntityModel implements IModel {

    protected static final TypeVariable<?> TYPE_VARIABLE = ICrudSubmit.class.getTypeParameters()[0];

    private static final Map<String, Set<String>> REFERENCED_MAP = new HashMap<String, Set<String>>();

    static {
        addReferencedMap(JaEdit.GROUP_SUGGEST, JaEdit.GROUP_LIST);
        addReferencedMap(JaEdit.GROUP_SUGGEST, JaEdit.GROUP_SEARCH);
        addReferencedMap(JaEdit.GROUP_LIST, JaEdit.GROUP_SEARCH);
        addReferencedMap(JaEdit.GROUP_SUGGEST, JaEdit.GROUP_SUG);
    }

    // private boolean filter;

    private JoEntity joEntity;

    private String caption;

    private Long update;

    private DModel model;

    private IField primary;

    private List<IField> primaries = new ArrayList<IField>();

    private List<IField> fields = new ArrayList<IField>();

    private List<JCrud> jCruds;

    private Map<String, IField> fieldMap = new HashMap<String, IField>();

    private Map<String, List<IField>> groups = new HashMap<String, List<IField>>();

    private List<IField> crudFields = new ArrayList<IField>();

    private boolean beanJaCruded;

    private IField crudField;

    private Map<String, CrudUtils.CaptionCondition> submitOptionMap;

    private static void addReferencedMap(String from, String... tos) {
        Set<String> set = REFERENCED_MAP.get(from);
        if (set == null) {
            set = new HashSet<String>();
            REFERENCED_MAP.put(from, set);
        }

        for (String to : tos) {
            set.add(to);
        }
    }

    public JoEntity getJoEntity() {
        return joEntity;
    }

    public void setJoEntity(JoEntity joEntity) {
        this.joEntity = joEntity;
        Class<?> entityClass = joEntity.getEntityClass();
        if (entityClass != null) {
            if (ICrudSubmit.class.isAssignableFrom(entityClass)) {
                Class<? extends Enum> enumClass = KernelClass.typeClass(entityClass, TYPE_VARIABLE);
                if (enumClass != null) {
                    submitOptionMap = CrudUtils.getEnumCaptionCondition(enumClass);
                }
            }

            JaModel jaModel = KernelClass.fetchAnnotation(entityClass, JaModel.class);
            if (jaModel != null) {
                model = new DModel();
                UtilAnnotation.copy(jaModel, model);
            }
        }
    }

    public Map<String, CrudUtils.CaptionCondition> getSubmitOptionMap() {
        return submitOptionMap;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        if (!KernelString.isEmpty(caption) && caption.endsWith("$")) {
            caption = caption.substring(0, caption.length() - 1);
        }

        this.caption = caption;
    }

    public Long lastModified() {
        return update;
    }

    protected void setUpdate(Long update) {
        this.update = update;
    }

    public boolean isFilter() {
        return model == null ? false : model.isFilter();
    }

    protected void setFilter(boolean filter) {
        if (model == null) {
            if (filter) {
                model = new DModel();

            } else {
                return;
            }
        }

        model.setFilter(filter);
    }

    public DModel getModel() {
        return model;
    }

    public void setModel(DModel model) {
        this.model = model;
    }

    public List<JCrud> getjCruds() {
        return jCruds;
    }

    public void addJaCrud(JaCrud jaCrud) {
        if (jCruds == null) {
            jCruds = new ArrayList<JCrud>();
        }

        for (JCrud jCrud : jCruds) {
            if (jaCrud.factory() == jCrud.getFactory() && jaCrud.value().equals(jCrud.getValue())) {
                return;
            }
        }

        jCruds.add(new JCrud(jaCrud));
    }

    public void addBeanJaCrud() {
        if (!beanJaCruded) {
            beanJaCruded = true;
            JCrud beanCrud = new JCrud();
            beanCrud.setJaCrud(null, BeanCrudFactory.class, KernelLang.NULL_OBJECTS, CrudEntity.ALL);
        }
    }

    public IField getPrimary() {
        return primary;
    }

    protected void setPrimary(DBField primary) {
        this.primary = primary;
    }

    public List<IField> getPrimaries() {
        return primaries;
    }

    protected void setPrimaries(List<IField> primaries) {
        this.primaries = primaries;
    }

    public List<IField> getFields() {
        return fields;
    }

    protected void setFields(List<IField> fields) {
        this.fields = fields;
    }

    protected void addField(IField field) {
        fields.add(field);
    }

    public IField getField(String name) {
        return fieldMap.get(name);
    }

    protected void setGroups(Map<String, List<IField>> groups) {
        this.groups = groups;
    }

    public List<IField> getGroupFields(String group) {
        return groups.get(group);
    }

    protected void setGroupFields(String group, List<IField> fields) {
        groups.put(group, fields);
    }

    protected void addGroupField(String group, IField field) {
        addGroupField(group, field, true);
    }

    protected boolean isCloudSearchField(IField field) {
        Map<String, Object[]> fieldMetas = SessionFactoryUtils.getEntityFieldMetas(joEntity.getEntityName(), joEntity.getEntityClass());
        if (fieldMetas == null) {
            return false;
        }

        String[] propertyNames = field.getName().split("\\.");
        int last = propertyNames.length - 1;
        int i = 0;
        while (true) {
            // just realize aop locale there
            String propertyName = propertyNames[i];
            Object[] metas = fieldMetas.get(propertyName);
            if (metas == null) {
                return false;
            }

            if (i++ >= last) {
                return true;
            }

            if (metas.length == 2) {
                fieldMetas = SessionFactoryUtils.getEntityFieldMetas((String) metas[1], (Class<?>) metas[0]);
                if (fieldMetas == null) {
                    return false;
                }

                continue;
            }

            return false;
        }
    }

    protected void addGroupField(String group, IField field, boolean reference) {
        if (field != null) {
            if (field.getListColType() == 0 && group.equals(JaEdit.GROUP_SUGGEST)) {
                field.setListColType(1);
            }
        }

        List<IField> fields = groups.get(group);
        if (fields == null) {
            fields = new ArrayList<IField>();
            groups.put(group, fields);
        }

        if (field != null) {
            KernelList.addOnly(fields, field);
        }

        if (reference) {
            Set<String> set = REFERENCED_MAP.get(group);
            if (set != null) {
                for (String r : set) {
                    if (field != null) {
                        if (r.equals(JaEdit.GROUP_SEARCH) && !isCloudSearchField(field)) {
                            continue;
                        }
                    }

                    addGroupField(r, field, false);
                }
            }
        }
    }

    public List<IField> getCrudFields() {
        return crudFields;
    }

    public void addCrudField(IField field) {
        crudField = field;
        crudFields.add(field);
    }

    public IField getCrudField() {
        return crudField;
    }

    protected void addComplete() {
        addGroupField(JaEdit.GROUP_LIST, primary);
        for (IField parmary : primaries) {
            fieldMap.put(parmary.getName(), parmary);
            addGroupField(JaEdit.GROUP_SUGGEST, parmary);
        }

        for (IField field : fields) {
            fieldMap.put(field.getName(), field);
            if (field.getGroups() == null) {
                if ("name".equals(field.getName())) {
                    addGroupField(JaEdit.GROUP_SUGGEST, field);
                }

            } else {
                for (String group : field.getGroups()) {
                    addGroupField(group, field);
                }
            }
        }
    }
}
