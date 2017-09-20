/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月13日 下午3:53:04
 */
package com.absir.aserv.game.context;

import com.absir.aserv.game.bean.JbServerA;
import com.absir.aserv.game.service.GameService;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.context.core.ContextBean;

public abstract class JbServerContext<SA extends JbServerA> extends ContextBean<Long> {

    @JaLang("服务区")
    protected SA serverA;

    public SA getServerA() {
        return serverA;
    }

    @Override
    protected void initialize() {
        ServerService.ME.load(this);
        loadDone();
        checkOnlineDay();
    }

    /**
     * 检测游戏天数
     */
    public synchronized void checkOnlineDay() {
        if (serverA.getGameDay() != GameService.getGameDay()) {
            updateGameDay(GameService.getGameDay());
        }
    }

    /**
     * 更新游戏天数
     */
    public final void updateGameDay(int gameDay) {
        serverA.setGameDay(gameDay);
        boolean updateWeek = serverA.getGameWeek() != GameService.getGameWeek();
        if (updateWeek) {
            serverA.setGameWeek(GameService.getGameWeek());
        }

        updateGameDayWeek(updateWeek);
    }

    public void updateGameDayWeek(boolean updateWeek) {
    }

    /**
     * 载入数据
     */
    protected abstract void load();

    // 载入数据完成
    protected abstract void loadDone();

    @Override
    public void unInitialize() {
        ServerService.ME.save(this);
    }

    /**
     * 保存数据
     */
    protected abstract void save();

    @Override
    public boolean stepDone(long contextTime) {
        return retainAt >= 0 && super.stepDone(contextTime);
    }

}
