/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-8 下午5:42:51
 */
package com.absir.aserv.system.service.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcCondition.ConditionProperty;
import com.absir.aserv.jdbc.JdbcCondition.Conditions;
import com.absir.aserv.system.bean.base.JbRelation;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.IAssocDao;
import com.absir.aserv.system.dao.IRelateDao;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocEntity;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocField;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocFieldFactory;
import com.absir.orm.hibernate.boost.EntityAssoc.Referenced;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JiAssoc;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AssocServiceUtils {

	/**
	 * @param assocClass
	 * @param entityName
	 * @param user
	 * @param permission
	 * @param jdbcCondition
	 * @return
	 */
	public static JdbcCondition assocConditions(Class<? extends JiAssoc> assocClass, String entityName, JiUserBase user, JePermission permission, JdbcCondition jdbcCondition) {
		return assocConditions(assocClass, entityName, user, permission, null, jdbcCondition);
	}

	/**
	 * @param assocClass
	 * @param entityName
	 * @param user
	 * @param mapStrategies
	 * @param jdbcCondition
	 * @return
	 */
	public static JdbcCondition assocConditions(Class<? extends JiAssoc> assocClass, String entityName, JiUserBase user, JePermission permission, Map<String, List<Object>> mapStrategies,
			JdbcCondition jdbcCondition) {
		if (jdbcCondition == null) {
			jdbcCondition = new JdbcCondition();
		}

		Conditions conditions = new Conditions(jdbcCondition.getConditions());
		Conditions includeConditions = new Conditions(conditions);
		Conditions excludeConditions = new Conditions(conditions);
		Conditions joinConditions = new Conditions(conditions);
		assocConditions(assocClass, entityName, entityName, user, permission, mapStrategies, jdbcCondition, includeConditions, excludeConditions, joinConditions, SessionFactoryUtils.get()
				.getAssocDepth());
		return jdbcCondition;
	}

	/**
	 * 获取条件查询对象
	 * 
	 * @param assocFieldFactory
	 * @return
	 */
	private static Object assocDao(AssocFieldFactory assocFieldFactory) {
		Object assocDao = assocFieldFactory.getAssocDao();
		if (assocDao == null) {
			synchronized (assocFieldFactory) {
				assocDao = assocFieldFactory.getAssocDao();
				if (assocDao == null) {
					Class<? extends IAssocDao> assocDaoClass = IAssocDao.class.isAssignableFrom(assocFieldFactory.getReferenceEntityClass()) ? (Class<? extends IAssocDao>) assocFieldFactory
							.getReferenceEntityClass() : null;
					assocDao = BeanFactoryUtils.getRegisterBeanObject(assocFieldFactory.getReferenceEntityName(), IAssocDao.class, assocDaoClass);
					if (assocDao == null) {
						assocDao = KernelLang.NULL_OBJECT;
					}

					assocFieldFactory.setAssocDao(assocDao);
				}
			}
		}

		return assocDao;
	}

	/**
	 * 条件关联传递处理
	 * 
	 * @param assocClass
	 * @param rootEntityName
	 * @param entityName
	 * @param user
	 * @param permission
	 * @param mapStrategies
	 * @param jdbcCondition
	 * @param includeConditions
	 * @param excludeConditions
	 * @param joinConditions
	 * @param assocDepth
	 * @return
	 */
	private static String assocConditions(Class<? extends JiAssoc> assocClass, String rootEntityName, String entityName, JiUserBase user, JePermission permission,
			Map<String, List<Object>> mapStrategies, JdbcCondition jdbcCondition, Conditions includeConditions, Conditions excludeConditions, Conditions joinConditions, int assocDepth) {
		if (--assocDepth < 0) {
			return null;
		}

		String entityId = SessionFactoryUtils.getIdentifierName(entityName, null);
		if (entityId == null) {
			return null;
		}

		/**
		 * 实体关联传递
		 */
		Collection<AssocEntity> assocEntities = SessionFactoryUtils.get().getNameMapAssocEntities().get(entityName);
		if (assocEntities != null) {
			for (AssocEntity assocEntity : assocEntities) {
				if (assocClass.isAssignableFrom(assocEntity.getEntityClass())) {
					jdbcCondition.openProperty(new ConditionProperty(assocEntity.getEntityName()));
					assocConditions(assocClass, rootEntityName, assocEntity.getEntityName(), user, permission, mapStrategies, jdbcCondition, includeConditions, excludeConditions, joinConditions,
							assocDepth);
					String joinAlias = jdbcCondition.getCurrentPropertyAlias();
					jdbcCondition.closeProperty();
					if (joinAlias != null) {
						HelperCondition.leftJoin(jdbcCondition, "$" + assocEntity.getEntityName(), joinAlias);
					}

				} else if (JbRelation.class.isAssignableFrom(assocEntity.getEntityClass()) && KernelClass.isAssignableFrom(assocClass, assocEntity.getAssocClasses())) {
					Object baseDao = BeanDao.getBaseDao((SessionFactoryUtils.getEntityClass(assocEntity.getEntityName())));
					if (baseDao instanceof IRelateDao) {
						jdbcCondition.openProperty(new ConditionProperty(assocEntity.getEntityName()));
						jdbcCondition.openProperty(new ConditionProperty(assocEntity.getReferenceEntityName()));
						assocConditions(assocClass, rootEntityName, assocEntity.getReferenceEntityName(), user, permission, mapStrategies, jdbcCondition, includeConditions, excludeConditions,
								joinConditions, assocDepth);
						String joinAlias = jdbcCondition.getCurrentPropertyAlias();
						jdbcCondition.closeProperty();
						if (joinAlias != null) {
							HelperCondition.leftJoin(jdbcCondition, "$" + assocEntity.getReferenceEntityName(), joinAlias);
						}

						joinAlias = jdbcCondition.getCurrentPropertyAlias();
						jdbcCondition.closeProperty();
						if (joinAlias != null) {
							HelperCondition.leftJoin(jdbcCondition, "$" + assocEntity.getEntityName(), joinAlias);
							((IRelateDao) (baseDao)).relateConditions(rootEntityName, user, mapStrategies == null ? null : mapStrategies.get(assocEntity.getEntityName()), joinAlias, entityId,
									jdbcCondition, includeConditions, excludeConditions, joinConditions);
						}
					}
				}
			}
		}

		/**
		 * 实体属性传递
		 */
		Collection<AssocField> assocFields = SessionFactoryUtils.get().getNameMapAssocFields().get(entityName);
		if (assocFields != null) {
			for (AssocField assocField : assocFields) {
				if (KernelClass.isAssignableFrom(assocClass, assocField.getAssocClasses())) {
					// 属性条件
					if (assocField.getReferenced() == Referenced.Factory) {
						Object assocDao = assocDao((AssocFieldFactory) assocField);
						if (assocDao == KernelLang.NULL_OBJECT) {
							continue;
						}

						IAssocDao iAssocDao = (IAssocDao) assocDao;
						if (iAssocDao.supportAssocClass(assocClass, rootEntityName, user, permission)) {
							iAssocDao.assocConditions(rootEntityName, user, permission, null, jdbcCondition, includeConditions, excludeConditions);
						}
					}

					// 实体字段关联...

					// 实体传递其他关联
					else {
						jdbcCondition.openProperty(new ConditionProperty(assocField.getReferenceEntityName()));
						assocConditions(assocClass, rootEntityName, assocField.getReferenceEntityName(), user, permission, mapStrategies, jdbcCondition, includeConditions, excludeConditions,
								joinConditions, assocDepth);
						String joinAlias = jdbcCondition.getCurrentPropertyAlias();
						jdbcCondition.closeProperty();
						if (joinAlias != null) {
							HelperCondition.leftJoinFetch(jdbcCondition, assocField.getFieldName(), joinAlias);
						}
					}
				}
			}
		}

		/**
		 * 实体关联解析
		 */
		Object baseDao = BeanDao.getBaseDao((SessionFactoryUtils.getEntityClass(entityName)));
		if (baseDao instanceof IAssocDao) {
			IAssocDao assocDao = (IAssocDao) baseDao;
			if (assocDao.supportAssocClass(assocClass, rootEntityName, user, permission)) {
				assocDao.assocConditions(rootEntityName, user, permission, mapStrategies == null ? null : mapStrategies.get(entityName), jdbcCondition, includeConditions, excludeConditions);
			}
		}

		return entityId;
	}
}
