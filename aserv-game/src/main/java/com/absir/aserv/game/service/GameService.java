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

    /**
     * 更新在线天数
     */
    protected static boolean updateGameDay() {
        long time = ContextUtils.getContextTime();
        int day = (int) ((time + timeZoneRawOffset) / UtilAbsir.DAY_TIME);
        if (day != gameDay) {
            gameDay = day;
            calendar.setTimeInMillis(time);
            JConfigureUtils.setOption(UPDATE_ONLINE_DAY, gameDay);
            return true;
        }

        return false;
    }

    /**
     * 载入服务
     */
    @Inject
    protected void init() {
        calendar = UtilContext.getCurrentCalendar();
        timeZoneRawOffset = calendar.getTimeZone().getRawOffset();
        gameDay = JConfigureUtils.getOption(UPDATE_ONLINE_DAY, int.class);
        gameDayUpdated = updateGameDay();
    }

    /**
     * 开始服务
     */
    @Started
    protected void started() {
        if (gameDayUpdated) {
            gameDayUpdated = false;
            ME.updateOnlines();
        }
    }

    /**
     * 更新玩家在线天数
     */
    @Async(notifier = true)
    @Schedule(cron = "0 0 0 * * *")
    protected void updateOnlines() {
        updateGameDay();
        updateOnlineContexts();
    }

    /**
     * 更新玩家天数数据
     */
    protected void updateOnlineContexts() {
        for (JbPlayerContext playerContext : (Collection<JbPlayerContext>) AGameComponent.ME.PLAYER_CONTEXT_MAP
                .values()) {
            try {
                playerContext.checkOnlineDay();

            } catch (Exception e) {
                LOGGER.error("updateOnlineContexts", e);
            }
        }
    }

}
