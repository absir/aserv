package com.absir.servlet;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.context.lang.LangBundle;
import com.absir.server.in.InFacade;
import com.absir.server.in.InFacadeFactory;
import com.absir.server.in.Input;

/**
 * Created by absir on 16/3/16.
 */
@Base
@Bean
public class InputRequestFactory extends InFacadeFactory {

    @Override
    public boolean isSupport(Input input) {
        return input instanceof InputRequest;
    }

    @Override
    public InFacade getInputFacade(Input input) {
        return new InputRequestFacade();
    }

    public static class InputRequestFacade extends InFacade<InputRequest> {

        @Override
        public String getAddress() {
            return null;
        }

        @Override
        public Integer getLocaleCode() {
            return LangBundle.ME.getLocaleCode(input.getRequest().getLocale());
        }

        @Override
        public Object getSession(String name) {
            return input.getSession(name);
        }

        @Override
        public void setSession(String name, Object value) {
            input.setSession(name, value);
        }

        @Override
        public void removeSession(String name) {
            input.removeSession(name);
        }

        @Override
        public String getCookie(String name) {
            return input.getCookie(name);
        }

        @Override
        public void setCookie(String name, String value, String path, long remember) {
            input.setCookie(name, value, path, remember);
        }

        @Override
        public void removeCookie(String name, String path) {
            input.removeCookie(name, path);
        }

        @Override
        public String getUserAgent() {
            return input.getRequest().getHeader("user-agent");
        }
    }
}
