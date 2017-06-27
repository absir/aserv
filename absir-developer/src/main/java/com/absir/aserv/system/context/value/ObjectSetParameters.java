package com.absir.aserv.system.context.value;

import com.absir.aserv.system.helper.HelperAccessor;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelReflect;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by absir on 7/6/17.
 */
public class ObjectSetParameters {

    private static Map<Class<?>, List<Field>> classMapFields = new HashMap<Class<?>, List<Field>>();

    protected List<Field> getFields() {
        Class<?> cls = getClass();
        List<Field> fields = classMapFields.get(cls);
        if (fields == null) {
            synchronized (cls) {
                fields = classMapFields.get(cls);
                if (fields == null) {
                    fields = HelperAccessor.getFields(cls);
                    classMapFields.put(cls, fields);
                }
            }
        }

        return fields;
    }

    public void setParameters(String[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return;
        }

        List<Field> fields = getFields();
        int size = fields.size();
        int length = parameters.length;
        if (size > length) {
            size = length;
        }

        for (int i = 0; i < size; i++) {
            Field field = fields.get(i);
            KernelReflect.set(this, field, DynaBinder.to(parameters[i], field.getType()));
        }
    }

    public Object[] getParameters() {
        List<Field> fields = getFields();
        int size = fields.size();
        Object[] parameters = new Object[size];
        for (int i = 0; i < size; i++) {
            parameters[i] = KernelReflect.get(this, fields.get(i));
        }

        return parameters;
    }

    public void setParameterCollection(Collection<Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        List<Field> fields = getFields();
        int size = fields.size();
        int i = 0;
        for (Object parameter : parameters) {
            if (i >= size) {
                break;
            }

            Field field = fields.get(i);
            KernelReflect.set(this, field, DynaBinder.to(parameter, field.getType()));
            i++;
        }
    }
}
