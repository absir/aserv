/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-14 下午12:54:06
 */
package com.absir.aserv.configure.xls;

import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public class XlsBaseTypeDescriptor extends AbstractTypeDescriptor<XlsBase> {

    protected XlsBaseTypeDescriptor(Class<? extends XlsBase> type) {
        super((Class) type);
    }

    @Override
    public String toString(XlsBase value) {
        return KernelString.valueOf(value.getId());
    }

    @Override
    public XlsBase fromString(String string) {
        return XlsUtils.findXlsBean(getJavaTypeClass(), string);
    }

    @Override
    public <X> X unwrap(XlsBase value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        return KernelDyna.to(value.getId(), type);
    }

    @Override
    public <X> XlsBase wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }

        return XlsUtils.findXlsBean(getJavaTypeClass(), value);
    }
}
