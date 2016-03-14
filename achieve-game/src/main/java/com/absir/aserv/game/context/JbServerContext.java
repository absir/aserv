/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月13日 下午3:53:04
 */
package com.absir.aserv.game.context;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.context.core.ContextBean;
import com.absir.sockser.JiServer;

/**
 * @author absir
 *
 */
public abstract class JbServerContext<S extends JiServer> extends ContextBean<Long> {

    /**
     * server
     */
    @JaLang("服务区")
    private S server;

    /**
     * @return the server
     */
    public S getServer() {
        return server;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.core.Context#initialize()
     */
    @Override
    protected void initialize() {
        ServerService.ME.load(this);
    }

    /**
     * 载入数据
     */
    protected abstract void load();

    /*
     * (non-Javadoc)
     *
     * @see com.absir.context.core.ContextBean#uninitialize()
     */
    @Override
    public void uninitialize() {
        ServerService.ME.save(this);
    }

    /**
     * 保存数据
     */
    protected abstract void save();

}
