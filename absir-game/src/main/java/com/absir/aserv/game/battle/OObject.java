/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-4 上午9:49:16
 */
package com.absir.aserv.game.battle;

import com.absir.aserv.system.bean.dto.IBaseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class OObject<O extends OObject, R extends IResult> {

    // 冻结状态
    protected boolean frozen;

    // 无敌状态
    protected boolean invincible;

    // 血量
    protected int hp;

    // 最大血量
    protected int maxHp;

    // 目标卡牌
    @JsonSerialize(using = IBaseSerializer.class)
    protected O target;

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public int getHp() {
        return hp;
    }

    protected void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    protected void setMaxHp(int mHp) {
        if (maxHp != mHp) {
            // 最大血量战报
            addReportMaxHp(mHp);
            maxHp = mHp;
        }
    }

    public O getTarget() {
        return target;
    }

    public void setTarget(O target) {
        this.target = target;
    }

    // 角色是否死亡
    public boolean died() {
        return hp <= 0;
    }

    // 对象步进
    public final void step(long time, R result) {
        if (frozen || died()) {
            return;
        }

        // BUFF状态步进
        stepBuff();

        // 自动攻击
        if (result.getResult() == EResult.CONTINUE) {
            forTargetResult(result, false);
            if (result.getResult() == EResult.CONTINUE) {
                if (!target.isInvincible() && atk()) {
                    atk(target, time, result);
                }
            }
        }
    }

    protected void stepBuff() {
    }

    // 战斗目标结果
    protected final void forTargetResult(R result, boolean invincible) {
        if (target != null) {
            if (target.died()) {
                target = null;

            } else if (target.isInvincible()) {
                if (invincible) {
                    return;
                }

            } else {
                return;
            }
        }

        target = findTarget(false);
        if (target == null) {
            target = findTarget(true);
        }

        if (target == null) {
            result.setResult(isAttacker() ? EResult.VICTORY : EResult.LOSS);
        }
    }

    // 攻击目标
    public final int atk(O target, int atk, Object atkFrom, R result) {
        // 攻击BUFF
        atk = atkBuff(target, atk, atkFrom, result);
        // 执行伤害
        int damage = target.damage(this, atk, atkFrom, result);
        // 伤害附加BUFF
        atkDamageBuff(target, atk, damage, atkFrom, result);
        return damage;
    }

    protected int atkBuff(O target, int atk, Object atkFrom, R result) {
        return atk;
    }

    protected void atkDamageBuff(O target, int atk, int damage, Object atkFrom, R result) {
    }

    public final int damage(O from, int atk, Object damageFrom, R result) {
        // 防御BUFF
        atk = damageDefenseBuff(from, atk, damageFrom, result);
        // 执行伤害
        if (atk < 0) {
            return 0;
        }

        // 伤害战报
        int damage = atk;
        if (hp > damage) {
            hp -= damage;

        } else {
            damage = hp;
            hp = 0;
        }

        // 伤害战报
        addReportDamage(atk, damage);
        // 伤害反弹BUFF
        damageReBoundBuff(from, atk, damage, damageFrom, result);

        // 检测死亡
        if (died()) {
            // 死亡战报执行
            addReportDie(result);
            if (result.getResult() == EResult.CONTINUE) {
                // 从对面阵营 寻找目标；判断胜利
                forTargetResult(result, true);
                if (result.getResult() == EResult.CONTINUE) {
                    if (target != null) {
                        // 对面阵营判断胜利
                        target.forTargetResult(result, true);
                    }
                }
            }

            if (result.getResult() == EResult.CONTINUE) {
                // 死亡BUFF
                damageDieBuff(from, atk, damage, damageFrom, result);
            }
        }

        return damage;
    }

    protected int damageDefenseBuff(O from, int atk, Object damageFrom, R result) {
        return atk;
    }

    protected int damageReBoundBuff(O from, int atk, int damage, Object damageFrom, R result) {
        return atk;
    }

    protected void damageDieBuff(O from, int atk, int damage, Object damageFrom, R result) {
    }

    // 治疗目标
    public final int treatTarget(O target, int treat, Object treatFrom, R result) {
        // 治疗BUFF
        treat = treatBuff(target, treat, treatFrom, result);
        // 执行治疗
        int tHp = target.treatFrom(this, treat, treatFrom, result);
        // 治疗附加BUFF
        treatTHpBuff(target, treat, tHp, treatFrom, result);
        return hp;
    }

    protected int treatBuff(O target, int treat, Object treatFrom, R result) {
        return treat;
    }

    protected void treatTHpBuff(O target, int treat, int tHp, Object treatFrom, R result) {
    }

    public final int treatFrom(O from, int treat, Object treatFrom, R result) {
        boolean dead = died();
        // 治疗收益BUFF
        treat = treatFromBuff(from, dead, treat, treatFrom, result);
        // 执行治疗
        if (treat < 0) {
            return 0;
        }

        int tHp = hp + treat;
        if (tHp > maxHp) {
            tHp = maxHp - hp;
            hp = maxHp;

        } else {
            hp = tHp;
            tHp = treat;
        }

        // 治疗战报
        addReportTreat(dead, treat, tHp);
        // 治疗反弹
        treatReBoundBuff(from, dead, treat, tHp, treatFrom, result);
        return tHp;
    }

    protected int treatFromBuff(O from, boolean dead, int treat, Object treatFrom, R result) {
        return treat;
    }

    protected void treatReBoundBuff(O from, boolean dead, int treat, int tHp, Object treatFrom, R result) {
    }

    protected abstract boolean isAttacker();

    // 寻找目标
    protected abstract O findTarget(boolean invincible);

    // 是否可以攻击
    public abstract boolean atk();

    // 自动攻击
    protected abstract void atk(O target, long time, R result);

    protected abstract void addReportMaxHp(int mHp);

    protected abstract void addReportDamage(int atk, int damage);

    protected abstract void addReportDie(R result);

    protected abstract void addReportTreat(boolean dead, int treat, int tHp);
}