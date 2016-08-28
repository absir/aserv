/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-13 下午5:04:15
 */
package com.absir.aserv.configure;

import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelReflect;
import com.absir.orm.hibernate.SessionFactoryUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class JConfigureBase implements IBase<Serializable> {

    private transient boolean deleteClear;

    protected transient Map<Field, JConfigure> fieldMapConfigure = new HashMap<Field, JConfigure>();

    @Override
    public Serializable getId() {
        return 0;
    }

    //@JaLang("删除清理")
    //@JaEdit(editable = JeEditable.ENABLE)
    public final boolean isDeleteClear() {
        return deleteClear;
    }

    public void setDeleteClear(boolean deleteClear) {
        this.deleteClear = deleteClear;
    }

    protected String getIdentifier() {
        return getClass().getName() + "@" + getId();
    }

    protected Object set(String value, Field field) {
        Class<? extends Serializable> identifierType = SessionFactoryUtils.getIdentifierType(null, field.getType(), SessionFactoryUtils.get().getSessionFactory());
        if (identifierType == null) {
            return DynaBinderUtils.to(value, field.getGenericType());

        } else {
            return BeanService.ME.get(field.getType(), DynaBinderUtils.to(value, identifierType));
        }
    }

    protected String getFieldKey(String fieldName, Locale locale) {
        return fieldName + '@' + locale;
    }

    public final void merge() {
        for (Entry<Field, JConfigure> entry : fieldMapConfigure.entrySet()) {
            JConfigure configure = entry.getValue();
            configure.setValue(DynaBinderUtils.to(KernelReflect.get(this, entry.getKey()), String.class));
        }

        mergeConfigures(fieldMapConfigure.values());
    }

    protected void mergeConfigures(Collection<JConfigure> configures) {
        BeanService.ME.mergers(configures);
    }
}
