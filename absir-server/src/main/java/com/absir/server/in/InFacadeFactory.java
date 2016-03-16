package com.absir.server.in;

import com.absir.bean.basis.Configure;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Orders;

/**
 * Created by absir on 16/3/16.
 */
@Configure
public abstract class InFacadeFactory {

    @Orders
    @Inject
    private static InFacadeFactory[] facadeFactories;

    public static final IFacade I_FACADE = new IFacade() {
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
    };

    public static IFacade forFacade(Input input) {
        InFacade facade;
        for (InFacadeFactory factory : facadeFactories) {
            facade = factory.getInputFacade(input);
            if (facade != null) {
                facade.setInput(input);
                return facade;
            }
        }

        return I_FACADE;
    }

    public abstract InFacade getInputFacade(Input input);
}
