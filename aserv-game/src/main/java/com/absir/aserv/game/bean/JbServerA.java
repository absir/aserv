package com.absir.aserv.game.bean;

import com.absir.aserv.system.bean.base.JbBeanL;

import javax.persistence.MappedSuperclass;

/**
 * Created by absir on 4/9/17.
 */
@MappedSuperclass
public class JbServerA extends JbBeanL {

    private int gameDay;

    private int gameWeek;

    public int getGameDay() {
        return gameDay;
    }

    public void setGameDay(int gameDay) {
        this.gameDay = gameDay;
    }

    public int getGameWeek() {
        return gameWeek;
    }

    public void setGameWeek(int gameWeek) {
        this.gameWeek = gameWeek;
    }
}
