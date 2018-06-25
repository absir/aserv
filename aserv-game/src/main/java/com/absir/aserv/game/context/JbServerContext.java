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
import com.absir.context.core.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class JbServerContext<SA extends JbServerA> extends ContextBean<Long> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JbServerContext.class);

    @JaLang("服务区")
    protected SA serverA;

    protected boolean checkOnlineDayed;

    private boolean asyncRunning;

    private List<Runnable> asyncRunnables;

    public SA getServerA() {
        return serverA;
    }

    private Object asyncRunningLock = new Object();

    protected void asyncRun(final Runnable runnable) {
        synchronized (asyncRunningLock) {
            if (asyncRunning) {
                if (runnable != null) {
                    if (asyncRunnables == null) {
                        asyncRunnables = new ArrayList<Runnable>();
                    }

                    asyncRunnables.add(runnable);
                }

                return;
            }

            try {
                asyncRunning = true;
                ContextUtils.getThreadPoolExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (runnable != null) {
                                try {
                                    runnable.run();

                                } catch (Throwable e) {
                                    LOGGER.error("asyncRun error", e);
                                }
                            }

                            while (true) {
                                List<Runnable> runnables = null;
                                synchronized (asyncRunningLock) {
                                    runnables = asyncRunnables;
                                    asyncRunnables = null;
                                }

                                if (runnables == null) {
                                    synchronized (asyncRunningLock) {
                                        asyncRunning = false;
                                    }

                                    break;
                                }

                                for (Runnable runnable : runnables) {
                                    try {
                                        runnable.run();

                                    } catch (Throwable e) {
                                        LOGGER.error("asyncRun error", e);
                                    }
                                }
                            }

                        } finally {
                            synchronized (asyncRunningLock) {
                                if (asyncRunning) {
                                    asyncRunning = false;
                                    if (asyncRunnables != null) {
                                        asyncRun(null);
                                    }
                                }
                            }
                        }
                    }
                });

            } catch (Throwable e) {
                asyncRunnablesClear();
                asyncRunning = false;
            }
        }
    }

    protected final void asyncRunnablesClear() {
        synchronized (asyncRunningLock) {
            asyncRunnables = null;
        }
    }

    @Override
    protected void initialize() {
        ServerService.ME.load(this);
        if (AGameComponent.ME.SERVER_CONTEXT_INIT_CALLS.hasCalls()) {
            AGameComponent.ME.SERVER_CONTEXT_INIT_CALLS.doCalls(this);
        }

        loadDone();
        checkOnlineDay();
        checkOnlineDayed = true;
    }

    /**
     * 检测游戏天数
     */
    public synchronized void checkOnlineDay() {
        if (serverA.getGameDay() != GameService.getGameDay()) {
            updateGameDay(GameService.getGameDay());
        }
    }

    protected boolean updateWeek;

    /**
     * 更新游戏天数
     */
    public void updateGameDay(int gameDay) {
        serverA.setGameDay(gameDay);
        updateWeek = serverA.getGameWeek() != GameService.getGameWeekUp();
        if (updateWeek) {
            serverA.setGameWeek(GameService.getGameWeekUp());
        }

        if (AGameComponent.ME.SERVER_CONTEXT_UPDATE_DAY_CALLS.hasCalls()) {
            AGameComponent.ME.SERVER_CONTEXT_UPDATE_DAY_CALLS.doCalls(this);
        }
    }

    /**
     * 载入数据
     */
    protected abstract void load();

    // 载入数据完成
    protected abstract void loadDone();

    @Override
    public void unInitialize() {
        if (AGameComponent.ME.SERVER_CONTEXT_UNINIT_CALLS.hasCalls()) {
            AGameComponent.ME.SERVER_CONTEXT_UNINIT_CALLS.doCalls(this);
        }

        ServerService.ME.save(this);
    }

    /**
     * 保存数据
     */
    protected abstract void save();

    private int[] callSteps = AGameComponent.ME.SERVER_CONTEXT_STEP_CALLS.createCallSteps();

    @Override
    public boolean stepDone(long contextTime) {
        if (retainAt >= 0 && super.stepDone(contextTime)) {
            return true;
        }

        if (AGameComponent.ME.SERVER_CONTEXT_STEP_CALLS.hasCalls()) {
            AGameComponent.ME.SERVER_CONTEXT_STEP_CALLS.doCallStep(this, callSteps);
        }

        return false;
    }

}
