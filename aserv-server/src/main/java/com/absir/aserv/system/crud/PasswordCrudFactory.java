/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-12-6 下午5:37:12
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.*;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperEncrypt;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyErrors;
import com.absir.server.in.Input;
import com.absir.servlet.InputRequest;

@Configure
public class PasswordCrudFactory implements ICrudFactory {

    @Value("password.salt.count")
    private static int saltCountDefault = 8;

    private final ICrudProcessor PASSWORD_PROCESSOR = new ICrudProcessorInput<String>() {

        @Override
        public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user, Input input) {
            if (input == null) {
                Accessor accessor = UtilAccessor.getAccessorProperty(entity.getClass(), crudProperty.getName() + "Base");
                if (accessor != null) {
                    String requestBody = (String) accessor.get(entity);
                    if (!KernelString.isEmpty(requestBody)) {
                        accessor.set(entity, null);
                        crud(crudProperty, entity, crudHandler, user, requestBody);
                    }
                }
            }
        }

        @Override
        public boolean isMultipart() {
            return false;
        }

        @Override
        public String crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input) {
            if (input instanceof InputRequest) {
                return ((InputRequest) input).getRequest().getParameter(handler.getFilter().getPropertyPath() + "Base");
            }

            return null;
        }

        @Override
        public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, String requestBody) {
            if (!KernelString.isEmpty(requestBody)) {
                Accessor accessor = UtilAccessor.getAccessorProperty(entity.getClass(), "salt");
                String salt = null;
                if (accessor != null) {
                    salt = Integer.toHexString(requestBody.hashCode());
                    if (!accessor.set(entity, salt)) {
                        salt = null;
                    }
                }

                Accessor countAccessor = UtilAccessor.getAccessorProperty(entity.getClass(), "saltCount");
                boolean setCount = countAccessor == null ? false : countAccessor.set(entity, saltCountDefault);
                requestBody = getPasswordEncrypt(requestBody, salt, setCount ? saltCountDefault : 0);
                crudProperty.set(entity, requestBody);
            }
        }
    };

    public static int getSaltCountDefault() {
        return saltCountDefault;
    }

    public static String getPasswordEncrypt(String password, String salt) {
        return HelperEncrypt.encryptionMD5(password, salt == null ? null : salt.getBytes());
    }

    public static String getPasswordEncrypt(String password, String salt, int saltCount) {
        return HelperEncrypt.encryptionMD5(password, salt == null ? null : salt.getBytes(), saltCount);
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        return PASSWORD_PROCESSOR;
    }

}
