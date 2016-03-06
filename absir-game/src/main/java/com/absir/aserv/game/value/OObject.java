/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-4 上午9:49:16
 */
package com.absir.aserv.game.value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.absir.aserv.game.utils.GameUtils;
import com.absir.aserv.system.bean.dto.IBaseSerializer;
import com.absir.aserv.system.bean.proxy.JiBase;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class OObject<T extends OObject> implements JiBase {

	// 对象编号
	private Serializable id;

	// 冻结状态
	protected boolean frozen;

	// 无敌状态
	protected boolean invincible;

	// 目标卡牌
	@JsonSerialize(using = IBaseSerializer.class)
	protected T target;

	// 血量
	protected int hp;

	// 最大血量
	protected int maxHp;

	// 攻击力
	protected int atk;

	// BUFF状态
	protected Queue<OBuff> buffs;

	// 属性BUFF计算
	protected transient HashMap<String, float[]> attBuffs = null;

	// 治疗
	public static final String TREAT = "T";

	// 最大血量变化
	public static final String MAX_HP = "MH";

	// 死亡
	public static final String DIE = "DIE";

	// 添加BUFF
	public static final String ADD_BUFF = "AB";

	// 移除BUFF
	public static final String REMOVE_BUFF = "RB";

	/**
	 * @param id
	 */
	public OObject(Serializable id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Serializable getId() {
		return id;
	}

	/**
	 * @return the frozen
	 */
	public boolean isFrozen() {
		return frozen;
	}

	/**
	 * @param frozen
	 *            the frozen to set
	 */
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/**
	 * @return the invincible
	 */
	public boolean isInvincible() {
		return invincible;
	}

	/**
	 * @param invincible
	 *            the invincible to set
	 */
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}

	/**
	 * @return the target
	 */
	public T getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(T target) {
		this.target = target;
	}

	/**
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * @param hp
	 *            the hp to set
	 */
	protected void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * @return the maxHp
	 */
	public int getMaxHp() {
		return maxHp;
	}

	/**
	 * @param maxHp
	 *            the maxHp to set
	 */
	public void setMaxHp(int maxHp) {
		if (maxHp != this.maxHp) {
			addReportDetail(null, MAX_HP, maxHp);
			this.maxHp = maxHp;
		}
	}

	/**
	 * @return the atk
	 */
	public int getAtk() {
		return atk;
	}

	/**
	 * @param atk
	 *            the atk to set
	 */
	public void setAtk(int atk) {
		this.atk = atk;
	}

	/**
	 * @return the buffs
	 */
	public Queue<OBuff> getBuffs() {
		return buffs;
	}

	/**
	 * 对象步进
	 * 
	 * @param time
	 * @param result
	 */
	public final void step(long time, IResult result) {
		if (frozen || died()) {
			return;
		}

		// BUFF状态步进
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				oBuff.step(this, time, result);
				buffResult(iterator, oBuff, result);
			}
		}

		// 自动攻击
		if (result.getResult() == EResult.CONTINUE && getAtk() > 0 && atk()) {
			targetResult(result, true);
			if (result.getResult() == EResult.CONTINUE && !target.isInvincible()) {
				atk(target, time, result);
			}
		}
	}

	/**
	 * @param iterator
	 * @param oBuff
	 * @param result
	 */
	protected final void buffResult(Iterator<OBuff> iterator, OBuff oBuff, IResult result) {
		if (result.isDone()) {
			result.setDone(false);
			iterator.remove();
			addReportDetail(null, REMOVE_BUFF, oBuff.getId());
			if (oBuff instanceof OBuffReverse) {
				((OBuffReverse) oBuff).revert(this, result);
			}
		}
	}

	/**
	 * 是否可以攻击
	 * 
	 * @return
	 */
	public abstract boolean atk();

	/**
	 * 寻找目标
	 * 
	 * @return
	 */
	protected abstract T fetchTarget();

	/**
	 * 战斗目标结果
	 * 
	 * @param result
	 * @param invincible
	 */
	public final void targetResult(IResult result, boolean invincible) {
		if (target != null) {
			if (target.died()) {
				target = null;

			} else if (!(invincible && target.isInvincible())) {
				return;
			}
		}

		target = fetchTarget();
		if (target == null) {
			result.setResult(EResult.VICTORY);
		}
	}

	/**
	 * 自动攻击
	 * 
	 * @param target
	 * @param time
	 * @param result
	 * @return
	 */
	protected abstract void atk(T target, long time, IResult result);

	/**
	 * 攻击目标
	 * 
	 * @param target
	 * @param atk
	 * @param damageFrom
	 * @param result
	 */
	public final int atk(T target, int atk, Object damageFrom, IResult result) {
		// 攻击BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffAttack) {
					IBuffAttack buffAttack = (IBuffAttack) oBuff;
					if (buffAttack.supportsFrom(damageFrom)) {
						atk = buffAttack.attack(atk, damageFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		// 执行伤害
		int damage = target.damage(this, atk, damageFrom, result);

		// 伤害附加BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffDamage) {
					IBuffDamage buffDamage = (IBuffDamage) oBuff;
					if (buffDamage.supportsFrom(damageFrom)) {
						buffDamage.damage(this, target, damage, damageFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		// 伤害反弹BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = target.buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffReBound) {
					IBuffReBound buffReflect = (IBuffReBound) oBuff;
					if (buffReflect.supportsFrom(damageFrom)) {
						damage = buffReflect.reBound(this, target, damage, damageFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		return damage;
	}

	/**
	 * @param from
	 * @param atk
	 * @param damageFrom
	 * @param result
	 * @return
	 */
	public final int damage(T from, int atk, Object damageFrom, IResult result) {
		// 防御BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffDefence) {
					IBuffDefence buffDefence = (IBuffDefence) oBuff;
					if (buffDefence.supportsFrom(damageFrom)) {
						atk = buffDefence.defence(atk, damageFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		// 执行伤害
		if (atk < 0) {
			return 0;
		}

		int damage = hp;
		if (damage > atk) {
			setHp(damage - atk);
			damage = atk;

		} else {
			setHp(0);
		}

		// 伤害战报
		addReportDetail(null, null, hp);

		// 检测死亡
		if (died()) {
			// 死亡BUFF
			if (buffs != null) {
				Iterator<OBuff> iterator = buffs.iterator();
				while (iterator.hasNext()) {
					OBuff oBuff = iterator.next();
					if (oBuff instanceof IBuffDie) {
						IBuffDie buffDie = (IBuffDie) oBuff;
						buffDie.die(this, from, damage, from, result);
					}
				}
			}

			// 执行死亡
			addReportDetail(null, DIE, null);
			die(result);
			if (from == null) {
				if (target == null) {
					targetResult(result, false);
				}

				from = target;
			}

			// 检测战斗结果
			if (result.getResult() == EResult.CONTINUE) {
				if (from != null) {
					from.targetResult(result, false);
				}

			} else {
				GameUtils.revert(result);
			}
		}

		return damage;
	}

	/**
	 * 角色死亡
	 * 
	 * @param result
	 */
	public void die(IResult result) {
	}

	/**
	 * 角色是否死亡
	 * 
	 * @return
	 */
	public boolean died() {
		return hp <= 0;
	}

	/**
	 * 治疗目标
	 * 
	 * @param target
	 * @param hp
	 * @param hpFrom
	 * @param result
	 */
	public final int treat(T target, int hp, Object hpFrom, IResult result) {
		// 治疗BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffTreat) {
					IBuffTreat buffTreat = (IBuffTreat) oBuff;
					if (buffTreat.supportsFrom(hpFrom)) {
						hp = buffTreat.treat(hp, hpFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		// 执行治疗
		int treat = target.treat(hp, hpFrom, result);

		// 治疗附加BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffTreatDo) {
					IBuffTreatDo buffTreatDo = (IBuffTreatDo) oBuff;
					if (buffTreatDo.supportsFrom(hpFrom)) {
						buffTreatDo.treatDo(this, target, treat, hpFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		// 治疗收益附加BUFF
		if (target.buffs != null) {
			Iterator<OBuff> iterator = target.buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffTreatReflect) {
					IBuffTreatReflect buffTreatReflect = (IBuffTreatReflect) oBuff;
					if (buffTreatReflect.supportsFrom(hpFrom)) {
						buffTreatReflect.treatReflect(this, target, treat, hpFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		return treat;
	}

	/**
	 * @param hp
	 * @param hpFrom
	 * @param result
	 * @return
	 */
	public final int treat(int hp, Object hpFrom, IResult result) {
		// 治疗收益BUFF
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (oBuff instanceof IBuffTreatBoost) {
					IBuffTreatBoost buffTreatBoost = (IBuffTreatBoost) oBuff;
					if (buffTreatBoost.supportsFrom(hpFrom)) {
						hp = buffTreatBoost.boost(hp, hpFrom, result);
						buffResult(iterator, oBuff, result);
					}
				}
			}
		}

		// 执行治疗
		return treat(hp, result);
	}

	/**
	 * @param hp
	 * @param result
	 * @return
	 */
	public final int treat(int hp, IResult result) {
		if (hp < 0) {
			return 0;
		}

		int treat = hp;
		hp += this.hp;
		if (hp > maxHp) {
			treat = maxHp - this.hp;
			hp = maxHp;
		}

		// 治疗战报
		setHp(hp);
		addReportDetail(null, TREAT, hp);
		return treat;
	}

	/**
	 * 添加BUFF
	 * 
	 * @param buff
	 * @param result
	 */
	public final void addBuff(OBuff buff, IResult result) {
		int id = buffs.size();
		if (buffs == null) {
			synchronized (this) {
				if (buffs == null) {
					buffs = new ConcurrentLinkedQueue<OBuff>();
				}
			}

		} else {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				int against = oBuff.against(oBuff);
				if (against < 0) {
					return;

				} else if (against > 0) {
					result.setDone(true);
					buffResult(iterator, oBuff, result);

				} else {
					int oId = oBuff.getId();
					if (id <= oId) {
						id = oId + 1;
					}
				}
			}
		}

		buff.setId(id);
		addReportDetail(null, ADD_BUFF, buff);
		buff.effect(this, result);
		buffs.add(buff);
	}

	/**
	 * 取消BUFF
	 * 
	 * @param buffId
	 * @param result
	 */
	protected void clearBuff(long buffId, IResult result) {
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				OBuff oBuff = iterator.next();
				if (buffId == (long) oBuff.getId()) {
					result.setDone(true);
					buffResult(iterator, iterator.next(), result);
					break;
				}
			}
		}
	}

	/**
	 * 清除全部BUFFS
	 * 
	 * @param result
	 */
	public void clearAllBuffs(IResult result) {
		if (buffs != null) {
			Iterator<OBuff> iterator = buffs.iterator();
			while (iterator.hasNext()) {
				result.setDone(true);
				buffResult(iterator, iterator.next(), result);
			}
		}
	}

	/**
	 * AttBuff结构
	 * 
	 * @param name
	 * @return
	 */
	protected float[] getAttBuffs(String name) {
		if (attBuffs == null) {
			synchronized (this) {
				if (attBuffs == null) {
					attBuffs = new HashMap<String, float[]>();
				}
			}
		}

		float[] buffs = attBuffs.get(name);
		if (buffs == null) {
			synchronized (attBuffs) {
				buffs = attBuffs.get(name);
				if (buffs == null) {
					buffs = new float[2];
					buffs[0] = 0;
					buffs[1] = 1;
					attBuffs.put(name, buffs);
				}
			}
		}

		return buffs;
	}

	/**
	 * 计算AttBuff
	 * 
	 * @param base
	 * @param buffs
	 * @return
	 */
	protected float getAttBuff(float base, float[] buffs) {
		return base * buffs[1] + buffs[0];
	}

	/**
	 * 比例增减
	 * 
	 * @param name
	 * @param base
	 * @param buff
	 * @return
	 */
	public float getBuffAtt(String name, float base, float buff) {
		float[] buffs = getAttBuffs(name);
		buffs[0] += buff;
		return getAttBuff(base, buffs);
	}

	/**
	 * 比例提升
	 * 
	 * @param name
	 * @param base
	 * @param buffP
	 * @return
	 */
	public float getBuffAttP(String name, float base, float buffP) {
		float[] buffs = getAttBuffs(name);
		buffs[1] *= buffP;
		return getAttBuff(base, buffs);
	}

	/**
	 * 比例恢复
	 * 
	 * @param name
	 * @param base
	 * @param buffP
	 * @return
	 */
	public float getBuffAttPR(String name, float base, float buffP) {
		float[] buffs = getAttBuffs(name);
		buffs[1] /= buffP;
		return getAttBuff(base, buffs);
	}

	/**
	 * 添加战报
	 * 
	 * @param targets
	 * @param effect
	 * @param parameters
	 */
	public abstract void addReportDetail(T target, String effect, Object parameters);
}