package com.absir.server.in;

import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.bean.inject.value.Orders;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/3/16.
 */
@Configure
public abstract class InFacadeFactory {

    @Orders
    @Inject(type = InjectType.Selectable)
    private static InFacadeFactory[] facadeFactories;

    private static final Map<Class<?>, InFacadeFactory> inputClassMapFacadeFactory = new HashMap<Class<?>, InFacadeFactory>();

    public static final InFacade<?> IN_FACADE = new InFacade() {
        @Override
        public String getAddress() {
            return null;
        }

        @Override
        public Integer getLocaleCode() {
            return null;
        }

        @Override
        public Object getSession(String name) {
            return null;
        }

        @Override
        public String getSessionValue(String name) {
            return null;
        }

        @Override
        public void setSession(String name, Object value) {

        }

        @Override
        public void removeSession(String name) {

        }

        @Override
        public String getCookie(String name) {
            return null;
        }

        @Override
        public void setCookie(String name, String value, String path, long remember) {

        }

        @Override
        public void removeCookie(String name, String path) {

        }

        @Override
        public String getUserAgent() {
            return null;
        }
    };

    public static final InFacadeFactory IN_FACADE_FACTORY = new InFacadeFactory() {
        @Override
        public boolean isSupport(Input input) {
            return false;
        }

        @Override
        public InFacade getInputFacade(Input input) {
            return IN_FACADE;
        }
    };

    public static InFacadeFactory forFacadeFactory(Input input) {
        Class<?> inputClass = input.getClass();
        InFacadeFactory facadeFactory = inputClassMapFacadeFactory.get(inputClass);
        if (facadeFactory == null) {
            for (InFacadeFactory factory : facadeFactories) {
                if (factory.isSupport(input)) {
                    facadeFactory = factory;
                    break;
                }
            }

            if (facadeFactory == null) {
                facadeFactory = IN_FACADE_FACTORY;
            }

            inputClassMapFacadeFactory.put(inputClass, facadeFactory);
        }

        return facadeFactory;
    }

    public static IFacade forFacade(Input input) {
        InFacade facade = forFacadeFactory(input).getInputFacade(input);
        if (facade == null) {
            return IN_FACADE;
        }

        facade.setInput(input);
        return facade;
    }

    public abstract boolean isSupport(Input input);

    public abstract InFacade getInputFacade(Input input);
}
