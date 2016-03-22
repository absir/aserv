/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年11月13日 下午4:08:45
 */
package com.absir.aserv.game.context;

import com.absir.aserv.game.bean.JbPlayer;
import com.absir.aserv.game.bean.JbPlayerA;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class GameComponent<P extends JbPlayerContext, S extends JbServerContext> {

    // 功能组件
    public static final GameComponent ME = BeanFactoryUtils.get(GameComponent.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(GameComponent.class);

    public final Class<P> PLAYER_CONTEXT_CLASS;

    public final Class<S> SERVER_CONTEXT_CLASS;

    public final Class<? extends JbPlayer> PLAYER_CLASS;

    public final Class<P> PLAYERA_CLASS;

    // 全部在线角色
    public final Map<Long, P> PLAYER_CONTEXT_MAP;

    // 全部在线服务
    public final Map<Long, S> SERVER_CONTEXT_MAP;

    /**
     * 初始化
     */
    public GameComponent() {
        Class[] args = KernelClass.argumentClasses(getClass());
        PLAYER_CONTEXT_CLASS = args[0];
        SERVER_CONTEXT_CLASS = args[1];
        args = KernelClass.argumentClasses(PLAYER_CONTEXT_CLASS);
        PLAYER_CLASS = args[0];
        PLAYERA_CLASS = args[1];
        PLAYER_CONTEXT_MAP = (Map<Long, P>) (Object) ContextUtils.getContextFactory()
                .getContextMap(PLAYER_CONTEXT_CLASS);
        SERVER_CONTEXT_MAP = (Map<Long, S>) (Object) ContextUtils.getContextFactory()
                .getContextMap(SERVER_CONTEXT_CLASS);
    }

    /**
     * 载入配置
     */
    @Inject
    public abstract void reloadComponent() throws IOException;

    public P findPlayerContext(long playerId) {
        return PLAYER_CONTEXT_MAP.get(playerId);
    }

    public S findServerContext(long serverId) {
        return SERVER_CONTEXT_MAP.get(serverId);
    }

    public abstract JbPlayer createPlayer();

    public abstract JbPlayerA createPlayerA();

}
