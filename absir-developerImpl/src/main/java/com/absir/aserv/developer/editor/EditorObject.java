/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-30 下午4:48:08
 */
package com.absir.aserv.developer.editor;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.core.kernel.KernelString;
import com.absir.property.Property;
import com.absir.property.PropertyObject;

import java.util.HashMap;
import java.util.Map;

public class EditorObject implements PropertyObject<EditorObject> {

    private boolean generated;

    private boolean embedd;

    private String lang;

    private String tag;

    private Map<Object, Object> metas;

    private JaEdit edit;

    private JaCrud crud;

    private String crudValue;

    private Class<?> keyClass;

    private Class<?> valueClass;

    private String keyName;

    private String valueName;

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public boolean isEmbedd() {
        return embedd;
    }

    public void setEmbedd(boolean embedd) {
        this.embedd = embedd;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setMeta(String name, String value) {
        if (value == null) {
            if (metas != null) {
                metas.remove(name);
            }

        } else {
            if (metas == null) {
                metas = new HashMap<Object, Object>();
            }

            metas.put(name, value);
        }
    }

    public JaEdit getEdit() {
        return edit;
    }

    public void setEdit(JaEdit edit) {
        this.edit = edit;
    }

    public JaCrud getCrud() {
        return crud;
    }

    public void setCrud(JaCrud crud) {
        this.crud = crud;
    }

    public String getCrudValue() {
        return crudValue;
    }

    public void setCrudValue(String crudValue) {
        this.crudValue = crudValue;
    }

    public Map<Object, Object> getMetas() {
        return metas;
    }

    public void setMetas(Map<?, ?> map) {
        if (map == null) {
            return;
        }

        if (metas == null) {
            metas = new HashMap<Object, Object>();
        }

        metas.putAll(map);
    }

    public Class<?> getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(Class<?> keyClass) {
        this.keyClass = keyClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class<?> valueClass) {
        this.valueClass = valueClass;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public EditorObject getPropertyData(String name, Property property) {
        if (property.getAllow() < 0) {
            return null;
        }

        if (KernelString.isEmpty(lang)) {
            lang = name;
        }

        return this;
    }
}
