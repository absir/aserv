/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-8-8 下午5:17:28
 */
package com.absir.aserv.support.developer;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.helper.HelperString;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;

import java.io.Serializable;

@SuppressWarnings("serial")
public class JCrud implements Serializable {

    protected String value;

    protected Class<?> factory;

    protected Object[] parameters;

    protected JaCrud.Crud[] cruds;

    public JCrud() {
    }

    public JCrud(JaCrud crud) {
        setJaCrud(crud);
    }

    public void setJaCrud(JaCrud crud) {
        if (crud != null) {
            setJaCrud(crud.value(), crud.factory(), KernelArray.toArray(crud.parameters()), crud.cruds());
        }
    }

    public void setJaCrud(String value, Class<?> factory, Object[] parameters, JaCrud.Crud[] cruds) {
        this.value = value;
        this.factory = factory;
        this.parameters = parameters;
        this.cruds = cruds;
    }

    public void setJaCrudValue(String crudValue) {
        if (!KernelString.isEmpty(crudValue)) {
            String[] params = HelperString.split(crudValue, ',');
            int length = params.length;
            if (length == 1) {
                value = params[0];

            } else if (length == 2) {
                value = params[0];
                factory = KernelClass.forName(params[1]);

            } else if (length > 2) {
                value = params[0];
                factory = KernelClass.forName(params[1]);
                length -= 2;
                parameters = new Object[length];
                for (int i = 0; i < length; i++) {
                    parameters[i] = params[i + 2];
                }
            }
        }
    }

    public String getValue() {
        return value;
    }

    public Class<?> getFactory() {
        return factory;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public JaCrud.Crud[] getCruds() {
        return cruds;
    }
}
