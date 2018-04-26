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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
@Inject
public abstract class JbPlayerContext<P extends JbPlayer, A extends JbPlayerA, R> extends ContextBean<Long> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(JbPlayerContext.class);

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
    private R receiver;

    // 全部恢复
    @JsonSerialize(contentUsing = IBaseSerializer.class)
    @JaEdit(editable = JeEditable.DISABLE)
    protected transient List<Recovery> recoveries = new ArrayList<Recovery>();

    protected boolean modifyDirty;

    protected long writeMailTime;

    private boolean asyncRunning;

    private List<ARunnable<R>> asyncRunnables;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean validateSessionId(long serverId, String sessionId) {
        return player.getServerId() == serverId && KernelObject.equals(this.sessionId, sessionId);
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

    // 是否未创建角色
    public boolean isNoCharacter() {
        return player.getCreateTime() == 0;
    }

    // 账号是否被封
    public boolean isBanning() {
        return player.getBanTime() > ContextUtils.getContextTime();
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
        if (receiver != r) {
            try {
                asyncRunnablesClear();
                if (receiver != null && r != null) {
                    writeKickMessage(receiver);
                }

            } finally {
                receiver = r;
            }
        }
    }

    public static abstract class ARunnable<R> {

        protected abstract void run() throws Throwable;

    }

    public static abstract class ReceiverRunnable<R> extends ARunnable<R> {

        protected final void run() {
            throw new RuntimeException("ReceiverRunnable not support run method");
        }

        protected abstract void write(R receiver) throws Throwable;

    }

    protected void doAsync(ARunnable<R> runnable, R receiver) throws Throwable {
        if (runnable instanceof ReceiverRunnable) {
            if (receiver != null) {
                try {
                    ((ReceiverRunnable) runnable).write(receiver);

                } catch (Throwable e) {
                    writeThrow(receiver, e);
                }
            }

        } else {
            runnable.run();
        }
    }

    private Object asyncRunningLock = new Object();

    protected void asyncRun(final ARunnable<R> runnable) {
        synchronized (asyncRunningLock) {
            if (asyncRunning) {
                if (runnable != null) {
                    if (asyncRunnables == null) {
                        asyncRunnables = new ArrayList<ARunnable<R>>();
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
                            R _receiver = receiver;
                            if (runnable != null) {
                                try {
                                    doAsync(runnable, _receiver);

                                } catch (Throwable e) {
                                    LOGGER.error("asyncRun error", e);
                                }
                            }

                            while (true) {
                                List<ARunnable<R>> runnables = null;
                                synchronized (asyncRunningLock) {
                                    _receiver = receiver;
                                    runnables = asyncRunnables;
                                    asyncRunnables = null;
                                }

                                if (runnables == null) {
                                    synchronized (asyncRunningLock) {
                                        asyncRunning = false;
                                    }

                                    break;
                                }

                                for (ARunnable runnable : runnables) {
                                    if (_receiver != null && _receiver != receiver) {
                                        _receiver = null;
                                    }

                                    try {
                                        doAsync(runnable, _receiver);

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
                                        asyncWrite(null);
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

    protected synchronized void asyncRunnablesClear() {
        asyncRunnables = null;
    }

    /**
     * 异步写入
     */
    public void asyncWrite(final ReceiverRunnable<R> receiverRunnable) {
        asyncRun(receiverRunnable);
    }

    @Override
    protected void initialize() {
        PlayerService.ME.load(this);
        if (AGameComponent.ME.PLAYER_CONTEXT_INIT_CALLS.hasCalls()) {
            AGameComponent.ME.PLAYER_CONTEXT_INIT_CALLS.doCalls(this);
        }

        loadDone();
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

    boolean newPlayerA;

    /**
     * 载入数据
     */
    protected void load() {
        Session session = BeanDao.getSession();
        long id = getId();
        player = (P) session.get(AGameComponent.ME.PLAYER_CLASS, getId());
        if (player == null) {
            throw new ServerException(ServerStatus.NO_LOGIN);
        }

        newPlayerA = false;
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

    // 载入数据完成
    protected abstract void loadDone();


    /**
     * 检测在线天数
     */
    public synchronized void checkOnlineDay() {
        if (playerA.getGameDay() != GameService.getGameDay()) {
            updatePlayerDay(playerA.getOnlineDay() + 1);
        }
    }

    protected int passDay;

    /**
     * 更新在线天数
     */
    public void updatePlayerDay(int onlineDay) {
        int passDay = GameService.getGameDay() - playerA.getGameDay();
        if (passDay < 1) {
            passDay = 1;
        }

        playerA.setGameDay(GameService.getGameDay());
        playerA.setOnlineDay(onlineDay);
        if (AGameComponent.ME.PLAYER_CONTEXT_UPDATE_DAY_CALLS.hasCalls()) {
            AGameComponent.ME.PLAYER_CONTEXT_UPDATE_DAY_CALLS.doCalls(this);
        }
    }

    protected void mailDirtyAt() {
        writeMailTime = ContextUtils.getContextTime() + 3000;
    }

    private int[] callSteps = AGameComponent.ME.PLAYER_CONTEXT_STEP_CALLS.createCallSteps();

    @Override
    public boolean stepDone(long contextTime) {
        if (super.stepDone(contextTime)) {
            return true;
        }

        if (AGameComponent.ME.PLAYER_CONTEXT_STEP_CALLS.hasCalls()) {
            AGameComponent.ME.PLAYER_CONTEXT_STEP_CALLS.doCallStep(this, callSteps);
        }

        for (Recovery recovery : recoveries) {
            if (recovery.step(contextTime)) {
                modifyDirty = true;
            }
        }

        if (modifyDirty) {
            modifyDirty = false;
            writeModifyMessage();
        }

        if (writeMailTime != 0 && writeMailTime < contextTime) {
            writeMailTime = 0;
            writeMailMessage();
        }

        return false;
    }

    @Override
    public boolean unInitializeDone() {
        if (AGameComponent.ME.PLAYER_CONTEXT_UNINIT_UNDONE_CALLS.hasCalls()) {
            return !AGameComponent.ME.PLAYER_CONTEXT_UNINIT_UNDONE_CALLS.doCalls(this);
        }

        return false;
    }

    @Override
    public void unInitialize() {
        if (AGameComponent.ME.PLAYER_CONTEXT_UNINIT_CALLS.hasCalls()) {
            AGameComponent.ME.PLAYER_CONTEXT_UNINIT_CALLS.doCalls(this);
        }

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
        session.update(player);
        if (newPlayerA) {
            session.merge(playerA);
            newPlayerA = false;

        } else {
            session.update(playerA);
        }
    }

    /*
     * 写入出错
     */
    public abstract void writeThrow(R receiver, Throwable e);

    /**
     * 写入踢出消息
     */
    public abstract void writeKickMessage(R receiver);

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
        protected abstract boolean setRecoveryNumber(int number);

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
                    int newRecoveryNumber = recoveryNumber + step * getRecoveryValue();
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
                                if (setRecoveryNumber(newRecoveryNumber)) {
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
