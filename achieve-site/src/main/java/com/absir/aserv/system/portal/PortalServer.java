package com.absir.aserv.system.portal;

import com.absir.aserv.system.helper.HelperInput;
import com.absir.server.exception.ServerException;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.server.value.OnException;
import org.hibernate.exception.ConstraintViolationException;

/**
 * Created by absir on 16/7/21.
 */
public class PortalServer {

    @OnException(Exception.class)
    protected void onException(Exception e, OnPut onPut) throws Exception {
        Input input = onPut.getInput();
        InModel model = input.getModel();
        model.put("e", e);
        if (e instanceof ConstraintViolationException) {
            model.put("message", e.getCause().getMessage());

        } else {
            if (e.getClass() == ServerException.class) {
                model.put("message", ((ServerException) e).getServerStatus());

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
