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
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextBean;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.IBase;
import com.absir.property.value.Allow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.Session;

import javax.persistence.Embedded;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
@Inject
public abstract class JbPlayerContext<P extends JbPlayer, A extends JbPlayerA> extends ContextBean<Long> {

    // 功能组件
    public static final GameComponent COMPONENT = BeanFactoryUtils.get(GameComponent.class);

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

    // SOCKET连接
    @JaLang("连接")
    @JsonIgnore
    @JaEdit(editable = JeEditable.LOCKED)
    protected SocketChannel socketChannel;

    // 全部恢复
    @JsonSerialize(contentUsing = IBaseSerializer.class)
    @JaEdit(editable = JeEditable.DISABLE)
    protected transient List<Recovery> recoveries = new ArrayList<Recovery>();

    /**
     * 获取玩家基本信息
     *
     * @return the player
     */
    public P getPlayer() {
        return player;
    }

    /**
     * 获取玩家全部数据
     *
     * @return the playerA
     */
    public A getPlayerA() {
        return playerA;
    }

    /**
     * 获取登录时间
     *
     * @return the onlineTime
     */
    public long getOnlineTime() {
        return loginTime;
    }

    /**
     * 获取玩家当前连接
     *
     * @return the socketChannel
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * 设置当前玩家连接和在线状态
     *
     * @param socketChannel the socketChannel to set
     */
    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    protected void initialize() {
        PlayerService.ME.load(this);
        loginTime = ContextUtils.getContextTime();
        checkOnlineDay();

        // 初始化自动回复
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
                    if (recoveryTime > loginTime + recovery.getRecoveryInterval()) {
                        recoveryTime = loginTime + recovery.getRecoveryInterval();
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
    protected abstract void load();

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
     *
     * @param onlineDay
     */
    public void updatePlayerDay(int onlineDay) {
        playerA.setGameDay(GameService.getGameDay());
        playerA.setOnlineDay(onlineDay);
    }

    @Override
    public boolean stepDone(long contextTime) {
        for (Recovery recovery : recoveries) {
            recovery.step(contextTime);
        }

        return super.stepDone(contextTime);
    }

    @Override
    public void uninitialize() {
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

        long contextTime = ContextUtils.getContextTime();
        playerA.setLastOffline(contextTime);
        playerA.setOnlineTime(playerA.getOnlineTime() + contextTime - loginTime);
        loginTime = contextTime;
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

    /**
     * 写入封禁消息
     */
    public abstract void writeBanMessage();

    /**
     * 准备更改消息
     */
    public abstract void prepareModify();

    /**
     * 写入更改消息
     */
    public abstract void writeModifyMessage();

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
         *
         * @return
         */
        protected abstract int getRecoveryInterval();

        /**
         * 一个恢复间隔恢复量
         *
         * @return
         */
        protected abstract int getRecoveryValue();

        /**
         * 恢复属性当前值
         *
         * @return
         */
        protected abstract int getRecoveryNumber();

        /**
         * 恢复属性最大值
         *
         * @return
         */
        protected abstract int getMaxRecoveryNumber();

        /**
         * 设置恢复属性值
         *
         * @param value
         * @return
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
         *
         * @param contextTime
         */
        public void step(long contextTime) {
            if (recoveryTime <= 0) {
                // 启动恢复
                if (getRecoveryNumber() < getMaxRecoveryNumber()) {
                    start();
                }

                return;
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
                }
            }
        }
    }

}
