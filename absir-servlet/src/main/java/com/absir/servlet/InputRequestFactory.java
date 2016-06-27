package com.absir.servlet;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InFacade;
import com.absir.server.in.InFacadeFactory;
import com.absir.server.in.Input;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by absir on 16/3/16.
 */
@Base
@Bean
public class InputRequestFactory extends InFacadeFactory {

    @Value("input.request.address.proxy")
    protected static boolean addressProxy;

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
            if (addressProxy) {
                HttpServletRequest request = input.getRequest();
                String address = request.getHeader("x-forwarded-for");
                if (KernelString.isEmpty(address) || "unknown".equalsIgnoreCase(address)) {
                    address = request.getHeader("Proxy-Client-IP");
                    if (KernelString.isEmpty(address) || "unknown".equalsIgnoreCase(address)) {
                        address = request.getHeader("WL-Proxy-Client-IP");
                        if (KernelString.isEmpty(address) || "unknown".equalsIgnoreCase(address)) {
                            address = request.getHeader("WL-Proxy-Client-IP");
                        }
                    }
                }

                //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
                if (address != null && address.length() > 15) { //"***.***.***.***".length() = 15
                    if (address.indexOf(",") > 0) {
                        address = address.substring(0, address.indexOf(","));
                    }
                }

                return address;
            }

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
