/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年10月16日 下午3:19:39
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.game.context.JbPlayerContext;
import com.absir.aserv.game.context.JbServerContext;

import java.util.Collection;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class GameSlaveUpgrade extends SlaveUpgradeService {

    protected abstract void kickAll();

    protected void reloadResource() {
        JbPlayerContext.COMPONENT.reloadComponent();
    }

    public void saveAll(int unit) {
        LOGGER.info("saveAll begin");
        LOGGER.info("saveAllPlayer count:" + JbPlayerContext.COMPONENT.PLAYER_CONTEXT_MAP.size());
        for (JbPlayerContext playerContext : (Collection<JbPlayerContext>) JbPlayerContext.COMPONENT.PLAYER_CONTEXT_MAP
                .values()) {
            for (int i = 0; i < unit; i++) {
                try {
                    playerContext.uninitialize();
                    break;

                } catch (Exception e) {
                    LOGGER.error("savePlayer", e);
                }
            }
        }

        LOGGER.info("saveAllServer count:" + JbPlayerContext.COMPONENT.SERVER_CONTEXT_MAP.size());
        for (JbServerContext serverContext : (Collection<JbServerContext>) JbPlayerContext.COMPONENT.SERVER_CONTEXT_MAP
                .values()) {
            for (int i = 0; i < unit; i++) {
                try {
                    serverContext.uninitialize();
                    break;

                } catch (Exception e) {
                    LOGGER.error("saveServer", e);
                }
            }
        }

        LOGGER.info("saveAll complete");
    }
}
