/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 下午1:56:37
 */
package com.absir.aserv.game.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.game.context.AGameComponent;
import com.absir.aserv.game.context.JbPlayerContext;
import com.absir.aserv.game.context.JbServerContext;
import com.absir.aserv.game.context.PlayerService;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Started;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Collection;

@SuppressWarnings({"unchecked", "rawtypes"})
@Base
@Bean
public class GameService {

    public static final String UPDATE_ONLINE_DAY = GameService.class + "@UPDATE_ONLINE_DAY";

    public static final GameService ME = BeanFactoryUtils.get(GameService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    protected static Calendar calendar;

    protected static int timeZoneRawOffset;

    private static int gameDay;

    private static int gameWeek;

    private boolean gameDayUpdated;

    public static Calendar getCalendar() {
        return calendar;
    }

    public static int getTimeZoneRawOffset() {
        return timeZoneRawOffset;
    }

    public static int getGameDay() {
        return gameDay;
    }

    public static int getGameWeek() {
        return gameWeek;
    }

    // 是否是周末
    public static boolean isWeekDay() {
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return day == 0 || day >= 6;
    }

    /**
     * 更新在线天数
     */
    protected final boolean updateGameDay() {
        long time = ContextUtils.getContextTime();
        int day = (int) ((time + timeZoneRawOffset) / UtilAbsir.DAY_TIME);
        if (day != gameDay) {
            gameDay = day;
            calendar.setTimeInMillis(time);
            JConfigureUtils.setOption(UPDATE_ONLINE_DAY, gameDay);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            boolean updateWeek = week != gameWeek;
            if (updateWeek) {
                gameWeek = week;
            }

            updateGameDayWeek(false, true, updateWeek);
            return true;
        }

        return false;
    }

    protected void updateGameDayWeek(boolean init, boolean updateDay, boolean updateWeek) {
    }

    /**
     * 载入服务
     */
    @Inject
    protected void init() {
        calendar = UtilContext.getCurrentCalendar();
        timeZoneRawOffset = calendar.getTimeZone().getRawOffset();
        gameDay = JConfigureUtils.getOption(UPDATE_ONLINE_DAY, int.class);
        gameWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        gameDayUpdated = updateGameDay();
        if (!gameDayUpdated) {
            updateGameDayWeek(true, false, false);
        }
    }

    /**
     * 开始服务
     */
    @Started
    protected void started() {
        if (gameDayUpdated) {
            gameDayUpdated = false;
            ME.updateServerGameDay();
        }
    }

    /**
     * 更新服务器在线天数
     */
    @Async(notifier = true)
    @Schedule(cron = "0 0 0 * * *")
    protected void updateServerGameDay() {
        try {
            mergeAllPlayer();

        } finally {
            try {
                updateOnlineServerContext();

            } finally {
                try {
                    updateGameDay();

                } finally {
                    checkOnlinePlayerContexts();
                }
            }
        }
    }

    public void mergeAllPlayer() {
        if (AGameComponent.ME.PLAYER_CONTEXT_MAP == null) {
            return;
        }

        for (JbPlayerContext playerContext : (Collection<JbPlayerContext>) AGameComponent.ME.PLAYER_CONTEXT_MAP
                .values()) {
            try {
                if (!playerContext.unInitializeDone()) {
                    PlayerService.ME.mergePlayer(playerContext);
                }

            } catch (Exception e) {
                LOGGER.error("mergeAllPlayer", e);
            }
        }
    }

    public void saveAllPlayerAndServer() {
        if (AGameComponent.ME.PLAYER_CONTEXT_MAP != null) {
            for (JbPlayerContext playerContext : (Collection<JbPlayerContext>) AGameComponent.ME.PLAYER_CONTEXT_MAP
                    .values()) {
                try {
                    if (!playerContext.unInitializeDone()) {
                        playerContext.unInitialize();
                    }

                } catch (Exception e) {
                    LOGGER.error("saveAllPlayer", e);
                }
            }
        }

        if (AGameComponent.ME.SERVER_CONTEXT_MAP != null) {
            for (JbServerContext serverContext : (Collection<JbServerContext>) AGameComponent.ME.SERVER_CONTEXT_MAP
                    .values()) {
                try {
                    if (!serverContext.unInitializeDone()) {
                        serverContext.unInitialize();
                    }

                } catch (Exception e) {
                    LOGGER.error("saveAllServer", e);
                }
            }
        }
    }

    /**
     * 更新服务器天数数据
     */
    protected void updateOnlineServerContext() {
        if (AGameComponent.ME.SERVER_CONTEXT_MAP == null) {
            return;
        }

        int gameDay = getGameDay() + 1;
        for (JbServerContext serverContext : (Collection<JbServerContext>) AGameComponent.ME.SERVER_CONTEXT_MAP
                .values()) {
            try {
                serverContext.updateGameDay(gameDay);

            } catch (Exception e) {
                LOGGER.error("updateOnlineServerContext", e);
            }
        }
    }

    /**
     * 检测更新玩家天数数据
     */
    protected void checkOnlinePlayerContexts() {
        if (AGameComponent.ME.PLAYER_CONTEXT_MAP == null) {
            return;
        }

        for (JbPlayerContext playerContext : (Collection<JbPlayerContext>) AGameComponent.ME.PLAYER_CONTEXT_MAP
                .values()) {
            try {
                playerContext.checkOnlineDay();

            } catch (Exception e) {
                LOGGER.error("checkOnlinePlayerContexts", e);
            }
        }
    }

}
