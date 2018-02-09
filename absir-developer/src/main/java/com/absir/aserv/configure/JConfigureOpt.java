package com.absir.aserv.configure;

import com.absir.aserv.consistent.ConsistentUtils;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.helper.HelperAccessor;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JConfigureOpt extends JConfigureBase {

    @JsonIgnore
    protected transient Map<Field, JConfigure> fieldMapConfigure;

    protected String get(Field field) {
        return DynaBinderUtils.to(KernelReflect.get(this, field), String.class);
    }

    protected Object set(String value, Field field) {
        Class<? extends Serializable> identifierType = SessionFactoryUtils.getIdentifierType(null, field.getType(), SessionFactoryUtils.get().getSessionFactory());
        if (identifierType == null) {
            return DynaBinderUtils.to(value, field.getGenericType());

        } else {
            return BeanService.ME.get(field.getType(), DynaBinderUtils.to(value, identifierType));
        }
    }

    @Override
    protected void loadInit() {
        String identifier = getIdentifier();
        Map<String, JConfigure> configureMap = new HashMap<String, JConfigure>();
        for (JConfigure configure : (List<JConfigure>) BeanService.ME.list("JConfigure", null, 0, 0, "o.id.eid", identifier)) {
            configureMap.put(configure.getId().getMid(), configure);
        }

        if (fieldMapConfigure == null) {
            fieldMapConfigure = new HashMap<Field, JConfigure>();
            for (Field field : HelperAccessor.getFields(getClass())) {
                JConfigure configure = configureMap.get(field.getName());
                if (configure == null) {
                    configure = new JConfigure();
                    configure.setId(new JEmbedSS(identifier, field.getName()));
                    configure.setValue(get(field));

                } else {
                    KernelObject.declaredSetter(this, field, set(configure.getValue(), field));
                }

                fieldMapConfigure.put(field, configure);
            }

        } else {
            for (Field field : fieldMapConfigure.keySet()) {
                JConfigure configure = configureMap.get(field.getName());
                if (configure != null) {
                    KernelObject.declaredSetter(this, field, set(configure.getValue(), field));
                }
            }
        }
    }

    @Override
    protected void copyFrom(JConfigureBase from) {
        JConfigureOpt fromOpt = (JConfigureOpt) from;
        if (fieldMapConfigure == null) {
            fieldMapConfigure = fromOpt.fieldMapConfigure;
        }

        for (Field field : fieldMapConfigure.keySet()) {
            KernelReflect.set(this, field, KernelReflect.get(from, field));
        }
    }

    @Override
    public void merge() {
        for (Map.Entry<Field, JConfigure> entry : fieldMapConfigure.entrySet()) {
            JConfigure jConfigure = entry.getValue();
            jConfigure.setValue(get(entry.getKey()));
        }

        BeanService.ME.mergers(fieldMapConfigure.values());
        ConsistentUtils.pubConfigure(this);
    }

    @Override
    protected void delete() {
        for (JConfigure configure : fieldMapConfigure.values()) {
            BeanService.ME.delete(configure);
        }

        copyFrom(KernelClass.newInstance(getClass()));
        ConsistentUtils.pubConfigure(this);
    }

}
