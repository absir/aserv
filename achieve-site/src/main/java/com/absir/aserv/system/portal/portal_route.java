package com.absir.aserv.system.portal;

import com.absir.aserv.system.server.ServerDiyView;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.value.Body;
import com.absir.server.value.Server;
import com.absir.servlet.InputRequest;

/**
 * Created by absir on 16/8/2.
 */
@Server
public class portal_route extends PortalServer {

    @Body
    public void portal(String view, InputRequest inputRequest) throws Exception {
        if (view.indexOf("..") >= 0) {
            return;
        }

        try {
            ServerDiyView.ME.render("/WEB-INF/portal/" + view + ServerDiyView.ME.getSuffix(), inputRequest);

        } catch (Exception e) {
            if (ServerDiyView.ME.isDeveloperNotExist(e, inputRequest)) {
                throw new ServerException(ServerStatus.IN_404);
            }

            throw e;
        }
    }

}
