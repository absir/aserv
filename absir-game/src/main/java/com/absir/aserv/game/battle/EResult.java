/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-17 下午7:14:39
 */
package com.absir.aserv.game.battle;

import com.absir.aserv.system.bean.value.JaLang;

public enum EResult {

    @JaLang("继续战斗")
    CONTINUE,

    @JaLang("动作完成")
    DONE,

    @JaLang("战斗胜利")
    VICTORY,

    @JaLang("战斗失败")
    LOSS,;

    public static boolean isDone(EResult result) {
        return result == EResult.DONE;
    }

    public static EResult reverse(EResult result) {
        if (result == VICTORY) {
            result = LOSS;

        } else if (result == LOSS) {
            result = VICTORY;
        }

        return result;
    }
}
