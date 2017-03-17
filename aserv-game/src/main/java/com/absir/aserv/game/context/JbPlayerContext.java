/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月20日 下午1:06:29
 */
package com.absir.aserv.game.context;

import com.absir.aserv.game.bean.JbPlayer;
import com.absir.aserv.game.bean.JbPlayerA;
import com.absir.aserv.game.service.GameService;
import com.absir.aserv.system.bean.dto.IBaseSerializer;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextBean;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelObject;
import com.absir.property.value.Allow;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.Session;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
@Inject
public abstract class JbPlayerContext<P extends JbPlayer, A extends JbPlayerA, R> extends ContextBean<Long> {

    // 验证登录(加快断线重连验证)
    protected String sessionId;

    // 玩家基本数据
    @Embedded
    @Allow
    protected P player;

    // 玩家更多数据
    @Embedded
    @Allow
    protected A playerA;

    // 登录时间
    @JaEdit(editable = JeEditable.LOCKED, types = "dateTime")
    @Allow
    protected long loginTime;

    // 连接接收
    @JaLang(value = "连接", tag = "connect")
    @JsonIgnore
    @JaEdit(editable = JeEditable.LOCKED)
    protected R receiver;

    // 全部恢复
    @JsonSerialize(contentUsing = IBaseSerializer.class)
    @JaEdit(editable = JeEditable.DISABLE)
    protected transient List<Recovery> recoveries = new ArrayList<Recovery>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean validateSessionId(String sessionId) {
        return KernelObject.equals(this.sessionId, sessionId);
    }

    /**
     * 获取玩家基本信息
     */
    public P getPlayer() {
        return player;
    }

    public A getPlayerA() {
        return playerA;
    }

    /**
     * 获取登录时间
     */
    public long getOnlineTime() {
        return loginTime;
    }

    /**
     * 获取玩家当前连接
     */
    public R getReceiver() {
        return receiver;
    }

    /**
     * 设置当前玩家连接和在线状态
     */
    public synchronized void setReceiver(R r) {
        if (r != receiver) {
            if (r != null && receiver != null) {
                writeKickMessage();
            }

            receiver = r;
        }
    }

    @Override
    protected void initialize() {
        PlayerService.ME.load(this);
        loginTime = ContextUtils.getContextTime();
        checkOnlineDay();

        // 初始化最后离线时间
        long lastOffline = playerA.getLastOffline();
        if (lastOffline > loginTime) {
            lastOffline = loginTime;
        }

        // 初始化自动回复
        long[] recoveryTimes = playerA.getRecoveryTimes();
        int i = 0;
        for (Recovery recovery : recoveries) {
            if (recovery.getRecoveryInterval() > 0) {
                if (recoveryTimes == null || recoveryTimes.length <= i) {
                    recovery.recoveryTime = lastOffline;

                } else {
                    long recoveryTime = recoveryTimes[i];
                    long maxRecoveryTime = loginTime + recovery.getRecoveryInterval();
                    if (recoveryTime > maxRecoveryTime) {
                        recoveryTime = maxRecoveryTime;
                    }

                    recovery.recoveryTime = recoveryTime;
                }
            }

            i++;
        }

        // 自动步进一次
        stepDone(ContextUtils.getContextTime());
    }

    /**
     * 载入数据
     */
    protected void load() {
        Session session = BeanDao.getSession();
        long id = getId();
        player = (P) session.get(AGameComponent.ME.PLAYERA_CLASS, getId());
        if (player == null) {
            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        boolean newPlayerA = false;
        playerA = (A) BeanDao.get(session, AGameComponent.ME.PLAYERA_CLASS, getId());
        if (playerA == null) {
            newPlayerA = true;
            playerA = (A) AGameComponent.ME.createPlayerA();
            playerA.setId(id);
            playerA.setLastOffline(ContextUtils.getContextTime());
        }

        load(session, newPlayerA);
    }

    /**
     * 载入更多数据
     */
    protected abstract void load(Session session, boolean newPlayerA);

    /**
     * 检测在线天数
     */
    public synchronized void checkOnlineDay() {
        if (playerA.getGameDay() != GameService.getGameDay()) {
            updatePlayerDay(playerA.getOnlineDay() + 1);
        }
    }

    /**
     * 更新在线天数
     */
    public void updatePlayerDay(int onlineDay) {
        playerA.setGameDay(GameService.getGameDay());
        playerA.setOnlineDay(onlineDay);
    }

    private boolean asyncRunning;

    private List<Runnable> asyncRunnables;

    /**
     * 异步执行
     */
    public synchronized void async(final Runnable asyncRunnable) {
        if (asyncRunning) {
            if (asyncRunnables == null) {
                asyncRunnables = new ArrayList<Runnable>();
            }

            asyncRunnables.add(asyncRunnable);
            return;
        }

        asyncRunning = true;
        try {
            ContextUtils.getThreadPoolExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (asyncRunnable != null) {
                            asyncRunnable.run();
                        }

                        R _receiver = null;
                        while (true) {
                            List<Runnable> runnables = null;
                            synchronized (JbPlayerContext.this) {
                                runnables = asyncRunnables;
                                asyncRunnables = null;
                                _receiver = receiver;
                            }

                            if (runnables == null) {
                                synchronized (this) {
                                    asyncRunning = false;
                                }

                                break;
                            }

                            for (Runnable runnable : runnables) {
                                if (_receiver != receiver) {
                                    break;
                                }

                                runnable.run();
                            }
                        }

                    } finally {
                        synchronized (JbPlayerContext.this) {
                            if (asyncRunning) {
                                asyncRunning = false;
                                if (asyncRunnables != null) {
                                    async(null);
                                }
                            }
                        }
                    }
                }
            });

        } catch (Throwable e) {
            asyncClear();
            asyncRunning = false;
        }
    }

    protected synchronized void asyncClear() {
        asyncRunnables = null;
    }

    protected boolean modifyDirty;

    protected static final long DIRTY_TIME = 1000;

    protected long writeMailTime;

    protected void mailDirtyAt() {
        writeMailTime = ContextUtils.getContextTime() + DIRTY_TIME;
    }

    @Override
    public boolean stepDone(long contextTime) {
        if (super.stepDone(contextTime)) {
            return true;
        }

        for (Recovery recovery : recoveries) {
            if (recovery.step(contextTime)) {
                modifyDirty = true;
            }
        }

        if (modifyDirty) {
            modifyDirty = false;
            async(new Runnable() {
                @Override
                public void run() {
                    writeModifyMessage();
                }
            });
        }

        if (writeMailTime != 0 && writeMailTime < contextTime) {
            writeMailTime = 0;
            async(new Runnable() {
                @Override
                public void run() {
                    writeMailMessage();
                }
            });
        }

        return false;
    }

    @Override
    public void unInitialize() {
        long[] recoveryTimes = playerA.getRecoveryTimes();
        if (recoveryTimes == null || recoveryTimes.length != recoveries.size()) {
            recoveryTimes = new long[recoveries.size()];
            playerA.setRecoveryTimes(recoveryTimes);
        }

        int i = 0;
        for (Recovery recovery : recoveries) {
            recoveryTimes[i] = recovery.recoveryTime;
            i++;
        }

        long lastOffline = ContextUtils.getContextTime();
        playerA.setLastOffline(lastOffline);
        playerA.setOnlineTime(playerA.getOnlineTime() + lastOffline - loginTime);
        loginTime = lastOffline;
        PlayerService.ME.save(this);
    }

    /**
     * 保存数据
     */
    protected void save() {
        Session session = BeanDao.getSession();
        session.merge(player);
        session.merge(playerA);
    }

    /*
     * 关闭连接
     */
    public abstract void writeThrow(Throwable e);

    /**
     * 写入登录消息
     */
    public abstract void writeKickMessage();

    /**
     * 写入封禁消息
     */
    public abstract void writeBanMessage();

    /**
     * 写入更改消息
     */
    public abstract void writeModifyMessage();

    /**
     * 写入邮件消息
     */
    protected abstract void writeMailMessage();

    /**
     * 恢复
     *
     * @author absir
     */
    public abstract class Recovery implements IBase<Long> {

        // 恢复时间
        protected long recoveryTime;

        /**
         * 初始化
         */
        public Recovery() {
            recoveries.add(this);
        }

        @Override
        public Long getId() {
            return recoveryTime;
        }

        /**
         * 恢复间隔
         */
        protected abstract int getRecoveryInterval();

        /**
         * 一个恢复间隔恢复量
         */
        protected abstract int getRecoveryValue();

        /**
         * 恢复属性当前值
         */
        protected abstract int getRecoveryNumber();

        /**
         * 恢复属性最大值
         */
        protected abstract int getMaxRecoveryNumber();

        /**
         * 设置恢复属性值
         */
        protected abstract boolean setRecoveryValue(int value);

        /**
         * 开启恢复
         */
        public void start() {
            if (recoveryTime <= 0) {
                recoveryTime = ContextUtils.getContextTime() + getRecoveryInterval();
            }
        }

        /**
         * 恢复步进
         */
        public boolean step(long contextTime) {
            if (recoveryTime <= 0) {
                // 启动恢复
                if (getRecoveryNumber() < getMaxRecoveryNumber()) {
                    start();
                }

                return false;
            }

            if (recoveryTime <= contextTime) {
                int recoveryInterval = getRecoveryInterval();
                if (recoveryInterval > 0) {
                    // 最小1个恢复间隔
                    int step = (int) ((contextTime - recoveryTime) / recoveryInterval) + 1;
                    // 下次恢复时间
                    recoveryTime += step * recoveryInterval;
                    // 恢复间隔计算恢复数值
                    int recoveryNumber = getRecoveryNumber();
                    int newRecoveryNumber = recoveryNumber + step;
                    // 比较最大恢复数值
                    int maxRecoveryNumber = getMaxRecoveryNumber();
                    if (newRecoveryNumber > maxRecoveryNumber) {
                        newRecoveryNumber = maxRecoveryNumber;
                        recoveryTime = 0;
                    }

                    if (newRecoveryNumber > recoveryNumber) {
                        synchronized (JbPlayerContext.this) {
                            // 计算实际恢复
                            if (newRecoveryNumber > getRecoveryNumber()) {
                                if (setRecoveryValue(newRecoveryNumber)) {
                                    recoveryTime = 0;
                                }

                            } else {
                                recoveryTime = 0;
                            }
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

}
