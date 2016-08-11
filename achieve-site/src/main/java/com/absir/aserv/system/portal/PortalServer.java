package com.absir.aserv.system.portal;

import com.absir.aserv.system.helper.HelperInput;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.service.SecurityService;
import com.absir.core.util.UtilAbsir;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.server.value.Before;
import com.absir.server.value.OnException;
import org.hibernate.exception.ConstraintViolationException;

/**
 * Created by absir on 16/7/21.
 */
public class PortalServer {

    public static final String SECURITY_NAME = "api";

    @Before
    protected SecurityContext autoLogin(Input input) {
        if (SecurityService.ME != null) {
            return SecurityService.ME.autoLogin(SECURITY_NAME, true, -1, input);
        }

        return null;
    }

    @OnException(Exception.class)
    protected void onException(Exception e, OnPut onPut) throws Exception {
        Input input = onPut.getInput();
        InModel model = input.getModel();
        Throwable throwable = UtilAbsir.forCauseThrowable(e);
        model.put("e", throwable);
        if (throwable instanceof ConstraintViolationException) {
            if (!model.containsKey("message")) {
                model.put("message", throwable.getCause().getMessage());
            }

        } else {
            if (throwable.getClass() == ServerException.class) {
                ServerStatus serverStatus = ((ServerException) throwable).getServerStatus();
                if (serverStatus == ServerStatus.IN_404) {
                    throw e;
                }

                if (!model.containsKey("message")) {
                    model.put("message", serverStatus);
                }

            } else {
                throw e;
            }
        }

        if (HelperInput.isAjax(input)) {
            onPut.setReturnedResolver(ReturnedResolverView.ME, "exception.ajax");

        } else {
            onPut.setReturnedResolver(ReturnedResolverView.ME, "exception");
        }
    }
}
