/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-4-3 下午5:18:30
 */
package com.absir.aserv.developer.model;

import com.absir.aserv.support.developer.IField;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.value.JeEditable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBField implements IField {

    protected JCrudField crudField;

    protected IField valueField;

    protected String entityName;

    protected String valueEntityName;

    protected String caption;

    protected String[] groups;

    protected boolean generated;

    protected boolean canOrder = true;

    protected boolean nullable = true;

    protected boolean collection;

    protected String mappedBy;

    protected List<String> types = new ArrayList<String>();

    protected JeEditable editable = JeEditable.ENABLE;

    protected Map<String, Object> metas = new HashMap<String, Object>();

    protected Object defaultEntity;

    private String nodeName;

    public DBField() {
        crudField = new JCrudField();
    }

    public DBField(DBColumn column) {
        this();
    }

    public JCrudField getCrudField() {
        return crudField;
    }

    public String getName() {
        return crudField.getName();
    }

    public String getNodeName() {
        if (nodeName == null) {
            nodeName = crudField.getName().replace('.', '-');
        }

        return nodeName;
    }

    public Class<?> getType() {
        return crudField.getType();
    }

    public IField getValueField() {
        return valueField;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getValueEntityName() {
        return valueEntityName;
    }

    public String getCaption() {
        return caption;
    }

    public String[] getGroups() {
        return groups;
    }

    public boolean isGenerated() {
        return generated;
    }

    public boolean isCanOrder() {
        return canOrder;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isCollection() {
        return collection;
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public List<String> getTypes() {
        return types;
    }

    public JeEditable getEditable() {
        return editable;
    }

    public Map<String, Object> getMetas() {
        return metas;
    }

    @Override
    public final Object getDefaultEntity() {
        if (defaultEntity == null) {
            defaultEntity = instanceDefaultEntity();
        }

        return defaultEntity;
    }

    protected Object instanceDefaultEntity() {
        return null;
    }
}
