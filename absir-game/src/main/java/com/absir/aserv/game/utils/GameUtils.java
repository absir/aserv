/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-15 下午12:50:20
 */
package com.absir.aserv.game.utils;

import com.absir.aserv.game.battle.EResult;
import com.absir.aserv.game.battle.IResult;
import com.absir.aserv.game.value.*;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class GameUtils {

    public static final LevelExpCxt LEVEL_EXP_CXT = new LevelExpCxt();

    // 等级分布
    public static int[] atts(int att, int maxAtt, int maxLevel) {
        float iAtt = maxAtt / (float) (maxLevel - 1);
        int[] atts = new int[maxLevel + 1];
        for (int i = 0; i < maxLevel; i++) {
            atts[i + 1] = att + (int) (i * iAtt);
        }

        return atts;
    }

    // 属性BUFF计算
    public static final int attBuff2(int att, int[] buffAtt) {
        if (buffAtt == null) {
            return att;
        }

        return att * buffAtt[1] / 100 + buffAtt[0];
    }

    public static final int attBuff3(int att, int[] buffAtt) {
        if (buffAtt == null) {
            return att;
        }

        return (att + buffAtt[2]) * buffAtt[1] / 100 + buffAtt[0];
    }

    // 属性BUFF提升
    public void setBuffAttP(int[] buffs, int buffP) {
        buffs[1] += buffP;
    }

    // 经验升级
    protected static int doExp(int exp, ILevel iLevel, List<? extends IExp> iExps, int maxLevel) {
        return doExp(exp, iLevel, LEVEL_EXP_CXT, iExps, maxLevel);
    }

    // 经验升级
    protected static <T> int doExp(int exp, T obj, ILevelCxt<T> levelCxt, List<? extends IExp> iExps, int maxLevel) {
        int level = levelCxt.getLevel(obj);
        while (true) {
            IExp iExp = iExps.get(level);
            if (exp < iExp.getExp()) {
                break;
            }

            exp -= iExp.getExp();
            if (level < maxLevel) {
                levelCxt.levelUp(obj, ++level);
            }

            if (level >= maxLevel) {
                exp = 0;
                level = maxLevel;
            }
        }

        levelCxt.setLevel(obj, level);
        return exp;
    }

    public static void modifyExp(int exp, ILevelExp iLevelExp, List<? extends IExp> iExps, int maxLevel) {
        modifyExp(exp, iLevelExp, LEVEL_EXP_CXT, iExps, maxLevel);
    }

    // 更改经验
    public static <T> void modifyExp(int exp, T obj, ILevelExpCxt<T> levelExpCxt, List<? extends IExp> iExps, int maxLevel) {
        if (levelExpCxt.getLevel(obj) > maxLevel) {
            return;
        }

        exp += levelExpCxt.getExp(obj);
        if (exp < 0) {
            exp = 0;

        } else {
            levelExpCxt.setExp(obj, doExp(exp, obj, levelExpCxt, iExps, maxLevel));
        }
    }

    public static <T extends ILevelExp, V> void modifyExpValue(int exp, V value, T obj, List<? extends IExpValue<V>> iExpValues, int maxLevel) {
        modifyExpValue(exp, value, obj, LEVEL_EXP_CXT, iExpValues, maxLevel);
    }

    public static <T, V> void modifyExpValue(int exp, V value, T obj, ILevelExpCxt<T> levelExpCxt, List<? extends IExpValue<V>> iExpValues, int maxLevel) {
        if (levelExpCxt.getLevel(obj) > maxLevel) {
            return;
        }

        exp += levelExpCxt.getExp(obj);
        if (exp < 0) {
            exp = 0;

        } else {
            int level = levelExpCxt.getLevel(obj);
            while (true) {
                IExpValue<V> iExpValue = iExpValues.get(level);
                int levelExp = iExpValue.getExp(value);
                if (exp < levelExp) {
                    break;
                }

                exp -= levelExp;
                if (level < maxLevel) {
                    levelExpCxt.levelUp(obj, ++level);
                }

                if (level >= maxLevel) {
                    exp = 0;
                    level = maxLevel;
                }
            }

            levelExpCxt.setLevel(obj, level);
        }

        levelExpCxt.setExp(obj, exp);
    }

    public static <T extends ILevelExp> void modifyExpNumber(int number, ILevelExp iLevelExp, List<? extends IExp> iExps, int maxLevel) {
        modifyExpNumber(number, iLevelExp, LEVEL_EXP_CXT, iExps, maxLevel);
    }

    public static <T> void modifyExpNumber(int number, T obj, ILevelExpCxt<T> levelExpCxt, List<? extends IExp> iExps, int maxLevel) {
        number += levelExpCxt.getExp(obj);
        if (number < 0) {
            number = 0;

        } else {
            int level = levelExpCxt.getLevel(obj);
            while (level < maxLevel) {
                if (number < iExps.get(level).getExp()) {
                    break;
                }

                levelExpCxt.levelUp(obj, ++level);
            }

            levelExpCxt.setLevel(obj, level);
        }

        levelExpCxt.setExp(obj, number);
    }

    public static <T, V> void modifyExpNumberValue(int number, V value, T obj, ILevelExpCxt<T> levelExpCxt, List<? extends IExpValue<V>> iExpValues, int maxLevel) {
        number += levelExpCxt.getExp(obj);
        if (number < 0) {
            number = 0;

        } else {
            int level = levelExpCxt.getLevel(obj);
            while (level < maxLevel) {
                if (number < iExpValues.get(level).getExp(value)) {
                    break;
                }

                levelExpCxt.levelUp(obj, ++level);
            }

            levelExpCxt.setLevel(obj, level);
        }

        levelExpCxt.setExp(obj, number);
    }

    // 反转战斗结果
    public static void revert(IResult result) {
        if (result.getResult() == EResult.VICTORY) {
            result.setResult(EResult.LOSS);

        } else if (result.getResult() == EResult.LOSS) {
            result.setResult(EResult.VICTORY);
        }
    }

}
