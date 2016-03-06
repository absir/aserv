/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-11 下午1:03:49
 */
package com.absir.aserv.system.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcEntities;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.proxy.JiUpdate;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.domain.DCondition;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.aserv.system.server.value.Bodys;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.EntityService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.utils.AuthServiceUtils;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.aserv.system.service.utils.EntityServiceUtils;
import com.absir.aserv.system.service.utils.SearchServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperJson;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JePermission;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.Input;
import com.absir.server.value.Interceptors;
import com.absir.server.value.Mapping;
import com.absir.server.value.Nullable;
import com.absir.server.value.Param;
import com.absir.server.value.Server;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Interceptors(ApiServer.TransactionRoute.class)
@Base
@Server
public class Api_entity extends ApiServer {

	@Inject
	protected Version version;

	/**
	 * @author absir
	 *
	 */
	@Bean
	protected static class Version {

		@Value("api.version.min")
		public String min = "0.0.0";

		@Value("api.version.version")
		public String max = "1.0.0";

		/** timeZone */
		public int timeZone = Calendar.getInstance().getTimeZone().getRawOffset();
	}

	/**
	 * 返回版本信息
	 * 
	 * @return
	 */
	public Object version() {
		return version;
	}

	/**
	 * 获取单个数据
	 * 
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public Object route(String entityName, String id, Input input) {
		return get(entityName, id, input);
	}

	/**
	 * 获取单个数据
	 * 
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public Object get(String entityName, String id, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) || AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		return EntityServiceUtils.find(entityName, user, id, input);
	}

	/**
	 * 获取多个数据
	 * 
	 * @param entityName
	 * @param ids
	 * @param input
	 * @return
	 */
	public List<Object> gets(String entityName, @Bodys Object[] ids, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) || AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		return EntityServiceUtils.list(entityName, user, null, ids, input);
	}

	/**
	 * 
	 * @param entityName
	 * @param input
	 * @return
	 */
	public List<Object> list(String entityName, Input input) {
		return list(entityName, 1, input);
	}

	/**
	 * 获取数据列表
	 * 
	 * @param entityName
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public List<Object> list(String entityName, Integer pageIndex, Input input) {
		return list(entityName, JdbcPage.PAGE_SIZE, pageIndex, input);
	}

	/**
	 * 获取数据列表
	 * 
	 * @param entityName
	 * @param pageSize
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public List<Object> list(String entityName, Integer pageSize, Integer pageIndex, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) || AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		if (pageIndex < 0) {
			pageIndex = 0;
		}

		if (pageSize < JdbcPage.MIN_PAGE_SIZE) {
			pageIndex = JdbcPage.MIN_PAGE_SIZE;
		}

		return EntityServiceUtils.list(entityName, user, null, null, pageIndex * pageSize, pageSize, input);
	}

	/**
	 * 获取数据列表(分页)
	 * 
	 * @param entityName
	 * @param jdbcPage
	 * @param input
	 * @return
	 */
	@Mapping(method = InMethod.POST)
	public JdbcEntities list(String entityName, @Bodys JdbcPage jdbcPage, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) || AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		return new JdbcEntities(EntityServiceUtils.list(entityName, user, null, null, jdbcPage, input), jdbcPage);
	}

	/**
	 * 高级搜索列表
	 * 
	 * @param entityName
	 * @param condition
	 * @param queue
	 * @param pageSize
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public List<Object> search(String entityName, @Param @Nullable String condition, @Param @Nullable String queue, Integer pageSize, Integer pageIndex, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT)) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		PropertyFilter propertyFilter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
		try {
			JdbcCondition jdbcCondition = condition == null ? null : SearchServiceUtils.getSearchCondition(entityName, propertyFilter, HelperJson.decodeList(condition), null);
			TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
			return EntityService.ME.list(entityName, crudSupply, user, null, jdbcCondition, HelperCondition.orderQueue(queue), pageIndex, pageSize);

		} catch (Exception e) {
			throw new ServerException(ServerStatus.NO_PARAM);
		}
	}

	/**
	 * 高级搜索列表(分页)
	 * 
	 * @param entityName
	 * @param condition
	 * @param queue
	 * @param jdbcPage
	 * @param input
	 * @return
	 */
	@Mapping(method = InMethod.POST)
	public JdbcEntities search(String entityName, @Param @Nullable String condition, @Param @Nullable String queue, @Bodys JdbcPage jdbcPage, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) && AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		PropertyFilter propertyFilter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
		try {
			JdbcCondition jdbcCondition = condition == null ? null : SearchServiceUtils.getSearchCondition(entityName, propertyFilter, HelperJson.decodeList(condition), null);
			TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
			return new JdbcEntities(EntityService.ME.list(entityName, crudSupply, user, null, jdbcCondition, HelperCondition.orderQueue(queue), jdbcPage), jdbcPage);

		} catch (Exception e) {
			throw new ServerException(ServerStatus.NO_PARAM);
		}
	}

	/**
	 * @param entityName
	 * @param input
	 * @return
	 */
	public Map<String, Object> changed(String entityName, Input input) {
		return changed(entityName, 0L, input);
	}

	/**
	 * @param entityName
	 * @param updateTime
	 * @param input
	 * @return
	 */
	public Map<String, Object> changed(String entityName, long updateTime, Input input) {
		DCondition condition = new DCondition();
		condition.setUpdateTime(updateTime);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changed(entityName, SecurityService.ME.getUserBase(input), condition, modelMap, input);
		modelMap.put("entities", modelMap.remove(entityName));
		return modelMap;
	}

	/**
	 * @param entityName
	 * @param updateTime
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public Map<String, Object> changed(String entityName, long updateTime, int pageIndex, Input input) {
		return changed(entityName, updateTime, JdbcPage.PAGE_SIZE, pageIndex, input);
	}

	/**
	 * 分段增量更新
	 * 
	 * @param entityName
	 * @param updateTime
	 * @param pageSize
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public Map<String, Object> changed(String entityName, long updateTime, int pageSize, int pageIndex, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) || AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("updateTime", System.currentTimeMillis());
		JdbcPage jdbcPage = new JdbcPage();
		jdbcPage.setPageIndex(pageIndex);
		jdbcPage.setPageSize(pageSize);

		Class<?> entityClass = CrudServiceUtils.getEntityClass(entityName);
		if (entityClass != null && !(JiUpdate.class.isAssignableFrom(entityClass))) {
			entityClass = null;
		}

		DCondition condition = new DCondition();
		condition.setUpdateTime(updateTime);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		modelMap.put("entities", EntityService.ME.list(entityName, crudSupply, user, condition, null, entityClass == null ? null : "ORDER BY o.updateTime ASC", jdbcPage));
		modelMap.put("pageCount", jdbcPage.getPageCount());
		return modelMap;
	}

	/**
	 * @param entityName
	 * @param updateTime
	 * @param input
	 * @return
	 */
	public Map<String, Object> changedId(String entityName, long updateTime, Input input) {
		Map<String, Object> modelMap = changed(entityName, updateTime, input);
		List<IBase> entities = (List<IBase>) modelMap.remove(entityName);
		int size = entities.size();
		if (size > 0 && entities.get(0) instanceof IBase) {
			List<Object> changeIds = new ArrayList<Object>(size);
			for (IBase base : entities) {
				changeIds.add(base.getId());
			}

			modelMap.put("entities", changeIds);
		}

		return modelMap;
	}

	/**
	 * @param entityName
	 * @param updateTime
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public Map<String, Object> changedId(String entityName, long updateTime, int pageIndex, Input input) {
		return changedId(entityName, updateTime, JdbcPage.PAGE_SIZE, pageIndex, input);
	}

	/**
	 * 分段增量更新(ID)
	 * 
	 * @param entityName
	 * @param updateTime
	 * @param pageSize
	 * @param pageIndex
	 * @param input
	 * @return
	 */
	public Map<String, Object> changedId(String entityName, long updateTime, int pageSize, int pageIndex, Input input) {
		Map<String, Object> modelMap = changed(entityName, updateTime, pageSize, pageIndex, input);
		List<IBase> entities = (List<IBase>) modelMap.get("entities");
		int size = entities.size();
		if (size > 0 && entities.get(0) instanceof IBase) {
			List<Object> changeIds = new ArrayList<Object>(size);
			for (IBase base : entities) {
				changeIds.add(base.getId());
			}

			modelMap.put("entities", changeIds);
		}

		return modelMap;
	}

	/**
	 * @param entityNames
	 * @param input
	 * @return
	 */
	public Map<String, Object> changed(@Bodys EntityNames entityNames, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (entityNames.names != null) {
			for (String entityName : entityNames.names) {
				changed(entityName, user, entityNames.condition, modelMap, input);
			}
		}

		return modelMap;
	}

	/**
	 * 获取增量数据
	 * 
	 * @param entityName
	 * @param user
	 * @param condition
	 * @param modelMap
	 * @param input
	 */
	private void changed(String entityName, JiUserBase user, DCondition condition, Map<String, Object> modelMap, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.SELECT) || AuthServiceUtils.selectPermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		EntityService.ME.changed(entityName, crudSupply, user, condition, modelMap);
	}

	/**
	 * 插入单条纪录
	 * 
	 * @param entityName
	 * @param entityObject
	 * @param input
	 * @return
	 */
	public Object insert(String entityName, @Bodys Object entityObject, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.INSERT))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);
		return EntityService.ME.persist(entityName, entityObject, crudSupply, user, filter);
	}

	/**
	 * 插入多条纪录
	 * 
	 * @param entityName
	 * @param entityList
	 * @param input
	 * @return
	 */
	public Object inserts(String entityName, @Bodys List<?> entityList, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!SessionFactoryUtils.entityPermission(entityName, JePermission.INSERT)) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);
		return EntityService.ME.insert(entityName, crudSupply, user, entityList, filter);
	}

	/**
	 * 更新单条纪录
	 * 
	 * @param entityName
	 * @param entityObject
	 * @param input
	 * @return
	 */
	public Object update(String entityName, @Bodys Object entityObject, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!SessionFactoryUtils.entityPermission(entityName, JePermission.UPDATE)) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user);
		return EntityService.ME.update(entityName, crudSupply, user, entityObject, filter);
	}

	/**
	 * 更新多条纪录
	 * 
	 * @param entityName
	 * @param entityList
	 * @param input
	 * @return
	 */
	public List<Object> updates(String entityName, @Bodys List<?> entityList, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!SessionFactoryUtils.entityPermission(entityName, JePermission.UPDATE)) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user);
		return EntityService.ME.update(entityName, crudSupply, user, entityList, filter);
	}

	/**
	 * 删除单条纪录
	 * 
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public Object delete(String entityName, Object id, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.DELETE) && AuthServiceUtils.deletePermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		return EntityService.ME.delete(entityName, crudSupply, user, id);
	}

	/**
	 * 删除多条纪录
	 * 
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public List<Object> deletes(String entityName, @Bodys Object[] ids, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (!(SessionFactoryUtils.entityPermission(entityName, JePermission.DELETE) && AuthServiceUtils.deletePermission(entityName, user))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		return EntityService.ME.delete(entityName, crudSupply, user, ids);
	}

	/**
	 * @param entityName
	 * @param entityMap
	 * @param input
	 * @return
	 */
	public Map<String, Object> sync(String entityName, @Bodys Map<?, ?> entityMap, Input input) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sync(entityName, SecurityService.ME.getUserBase(input), entityMap, modelMap, input);
		return modelMap;
	}

	/**
	 * @param entityMaps
	 * @param input
	 * @return
	 */
	public Map<String, Object> syncs(@Bodys Map<String, Map<?, ?>> entityMaps, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		for (Entry<String, Map<?, ?>> entry : entityMaps.entrySet()) {
			sync(entry.getKey(), user, entry.getValue(), modelMap, input);
		}

		return modelMap;
	}

	/**
	 * 同步数据
	 * 
	 * @param entityName
	 * @param user
	 * @param entityMap
	 * @param modelMap
	 * @param input
	 */
	private void sync(String entityName, JiUserBase user, Map<?, ?> entityMap, Map<String, Object> modelMap, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!(SessionFactoryUtils.entityPermissions(entityName, JePermission.INSERT, JePermission.UPDATE, JePermission.DELETE) && AuthServiceUtils.permissions(entityName, user, JePermission.SELECT,
				JePermission.INSERT, JePermission.UPDATE, JePermission.DELETE))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		PropertyFilter filter = AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_WRITE);
		EntityService.ME.sync(entityName, crudSupply, user, null, modelMap, entityMap, filter);
	}

	/**
	 * @param entityName
	 * @param entityList
	 * @param input
	 * @return
	 */
	public Map<String, Object> mirror(String entityName, @Bodys List<?> entityList, Input input) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mirror(entityName, SecurityService.ME.getUserBase(input), entityList, modelMap, input);
		return modelMap;
	}

	/**
	 * @param entityMaps
	 * @param input
	 * @return
	 */
	public Map<String, Object> mirrors(@Bodys Map<String, List<?>> entityMaps, Input input) {
		JiUserBase user = SecurityService.ME.getUserBase(input);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		for (Entry<String, List<?>> entry : entityMaps.entrySet()) {
			mirror(entry.getKey(), user, entry.getValue(), modelMap, input);
		}

		return modelMap;
	}

	/**
	 * 镜像数据
	 * 
	 * @param entityName
	 * @param user
	 * @param entityList
	 * @param modelMap
	 * @param input
	 */
	private void mirror(String entityName, JiUserBase user, List<?> entityList, Map<String, Object> modelMap, Input input) {
		ICrudSupply crudSupply = EntityService.ME.getCrudSupply(entityName);
		if (!(SessionFactoryUtils.entityPermissions(entityName, JePermission.INSERT, JePermission.DELETE) && AuthServiceUtils.permissions(entityName, user, JePermission.INSERT, JePermission.DELETE))) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}

		PropertyFilter filter = AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_WRITE);
		EntityService.ME.mirror(entityName, crudSupply, user, modelMap, entityList, filter);
	}

	/**
	 * 并行处理数据
	 * 
	 * @param mutiple
	 * @param input
	 * @return
	 */
	public Map<String, Object> multiple(@Bodys EntityMutiple mutiple, Input input) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JiUserBase user = SecurityService.ME.getUserBase(input);
		if (mutiple.mirrors != null) {
			for (Entry<String, List<?>> entry : mutiple.mirrors.entrySet()) {
				mirror(entry.getKey(), user, entry.getValue(), modelMap, input);
			}
		}

		if (mutiple.syncs != null) {
			for (Entry<String, Map<?, ?>> entry : mutiple.syncs.entrySet()) {
				sync(entry.getKey(), user, entry.getValue(), modelMap, input);
			}
		}

		if (mutiple.names != null) {
			for (String entityName : mutiple.names) {
				changed(entityName, user, mutiple.condition, modelMap, input);
			}
		}

		return modelMap;
	}

	/**
	 * @author absir
	 * 
	 */
	public static class Condition {

		/** condition */
		public DCondition condition;
	}

	/**
	 * @author absir
	 * 
	 */
	public static class EntityNames extends Condition {

		/** names */
		public List<String> names;
	}

	/**
	 * @author absir
	 * 
	 */
	public static class EntityMutiple extends EntityNames {

		/** syncs */
		public Map<String, Map<?, ?>> syncs;

		/** mirrors */
		public Map<String, List<?>> mirrors;
	}
}
