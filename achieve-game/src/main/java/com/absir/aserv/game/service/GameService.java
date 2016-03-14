/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月16日 下午1:56:37
 */
package com.absir.aserv.game.service;

import com.absir.aserv.configure.JConfigureUtils;
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

/**
 * @author absir
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Base
@Bean
public class GameService {

    /**
     * UPDATE_ONLINE_DAY
     */
    public static final String UPDATE_ONLINE_DAY = GameService.class + "@UPDATE_ONLINE_DAY";

    /**
     * ME
     */
    public static final GameService ME = BeanFactoryUtils.get(GameService.class);

    /**
     * LOGGER
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    /**
     * calendar
     */
    protected static Calendar calendar;

    /** gmtZoneTime */
    protected static int timeZoneRawOffset;

    /**
     * gameDay
     */
    private static int gameDay;

    /**
     * gameDayUpdated
     */
    private boolean gameDayUpdated;

    /**
     * @return the calendar
     */
    public static Calendar getCalendar() {
        return calendar;
    }

    /**
     * @return the timeZoneRawOffset
     */
    public static int getTimeZoneRawOffset() {
        return timeZoneRawOffset;
    }

    /**
     * @return the gameDay
     */
    public static int getGameDay() {
        return gameDay;
    }

    /**
     * 更新在线天数
     *
     * @return
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
        for (JbPlayerContext playerContext : (Collection<JbPlayerContext>) JbPlayerContext.COMPONENT.PLAYER_CONTEXT_MAP
                .values()) {
            try {
                playerContext.checkOnlineDay();

            } catch (Exception e) {
                LOGGER.error("updateOnlineContexts", e);
            }
        }
    }

}
