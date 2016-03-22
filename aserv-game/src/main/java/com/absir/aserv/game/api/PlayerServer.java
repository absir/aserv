/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-11 上午10:56:52
 */
package com.absir.aserv.game.api;

import com.absir.aserv.game.bean.JbPlayer;
import com.absir.aserv.game.context.GameComponent;
import com.absir.aserv.game.context.JbPlayerContext;
import com.absir.aserv.game.context.PlayerService;
import com.absir.aserv.system.api.ApiServer;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.context.core.ContextUtils;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.server.socket.InputSocketImpl;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PlayerServer extends ApiServer {

    public static JbPlayerContext getPlayerContext(Input input) {
        Object playerContext = input.getAttribute("playerContext");
        return playerContext == null || !(playerContext instanceof JbPlayerContext) ? null
                : (JbPlayerContext) playerContext;
    }

    public static void setPlayerContext(Input input, JbPlayerContext playerContext) {
        input.setAttribute("playerContext", playerContext);
    }

    @Override
    protected SecurityContext onAuthentication(Input input) throws Throwable {
        SecurityContext securityContext = super.onAuthentication(input);
        if (securityContext == null) {
            if (input instanceof InputSocketImpl) {
                Long playerId = (Long) input.getId();
                JbPlayerContext playerContext = (JbPlayerContext) ContextUtils.getContext(GameComponent.ME.PLAYER_CONTEXT_CLASS,
                        playerId);
                if (!onAuthPlayerContext(playerContext)) {
                    throw new ServerException(ServerStatus.NO_LOGIN);
                }

                setPlayerContext(input, playerContext);
                return null;
            }

            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        Long playerId = PlayerService.ME.getPlayerId(null, securityContext.getUser());
        if (playerId == null) {
            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        JbPlayerContext playerContext = (JbPlayerContext) ContextUtils.getContext(GameComponent.ME.PLAYER_CONTEXT_CLASS,
                playerId);
        if (!onAuthPlayerContext(playerContext)) {
            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        setPlayerContext(input, playerContext);
        return securityContext;
    }

    protected boolean onAuthPlayerContext(JbPlayerContext playerContext) {
        if (playerContext == null) {
            return false;
        }

        JbPlayer player = playerContext.getPlayer();
        if (player == null || player.getCreateTime() == 0) {
            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        if (player.getBanTime() > ContextUtils.getContextTime()) {
            playerContext.writeBanMessage();
            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        return true;
    }

}
