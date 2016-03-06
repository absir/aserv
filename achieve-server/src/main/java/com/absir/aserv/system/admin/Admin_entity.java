/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-8 下午12:43:09
 */
package com.absir.aserv.system.admin;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.absir.aserv.configure.xls.XlsUtils;
import com.absir.aserv.crud.CrudContextUtils;
import com.absir.aserv.crud.CrudSupply;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.JLog;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.CrudService;
import com.absir.aserv.system.service.EntityService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.AuthServiceUtils;
import com.absir.aserv.system.service.utils.InputServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.binder.BinderData;
import com.absir.binder.BinderResult;
import com.absir.binder.BinderUtils;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.parameter.ParameterResolverBinder;
import com.absir.server.value.Binder;
import com.absir.server.value.Body;
import com.absir.server.value.Mapping;
import com.absir.server.value.Nullable;
import com.absir.server.value.Param;
import com.absir.server.value.Server;
import com.absir.servlet.InputRequest;

@SuppressWarnings("unchecked")
@Base
@Server
public class Admin_entity extends AdminServer {

	/**
	 * CRUDSupply统一入口
	 * 
	 * @param entityName
	 * @param input
	 */
	protected ICrudSupply getCrudSupply(String entityName, Input input) {
		ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
		if (crudSupply == null) {
			throw new ServerException(ServerStatus.IN_404);
		}

		if (input != null) {
			JoEntity joEntity = new JoEntity(entityName, crudSupply.getEntityClass(entityName));
			input.setAttribute("joEntity", joEntity);
		}

		return crudSupply;
	}

	/**
	 * 列表页面
	 * 
	 * @param entityName
	 * @param jdbcPage
	 * @param input
	 */
	public void list(String entityName, @Binder JdbcPage jdbcPage, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		if (!crudSupply.support(Crud.COMPLETE)) {
			throw new ServerException(ServerStatus.IN_404);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
		InModel model = input.getModel();
		try {
			model.put("filter", AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user));
			model.put("update", true);

		} catch (ServerException e) {
			model.put("update", false);
		}

		model.put("insert", AuthServiceUtils.insertPermission(entityName, user));
		model.put("delete", AuthServiceUtils.deletePermission(entityName, user));
		jdbcPage = InputServiceUtils.getJdbcPage(entityName, jdbcPage, input);
		model.put("page", jdbcPage);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		String queue = InputServiceUtils.getOrderQueue(input);
		model.put("entities", EntityService.ME.list(entityName, crudSupply, user, null,
				InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), filter, null, input),
				queue, jdbcPage));
	}

	/**
	 * @param entityName
	 * @param input
	 */
	public void edit(String entityName, Input input) {
		edit(entityName, null, input);
	}

	/**
	 * 编辑页面
	 * 
	 * @param entityName
	 * @param id
	 * @param crudSupply
	 * @param input
	 */
	public void edit(String entityName, Object id, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		if (id == null && !crudSupply.support(Crud.CREATE)) {
			throw new ServerException(ServerStatus.IN_404);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = null;
		InModel model = input.getModel();
		try {
			if (id == null) {
				filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);

			} else {
				filter = AuthServiceUtils.updatePropertyFilter(entityName, crudSupply, user);
			}

			model.put("update", true);

		} catch (ServerException e) {
			if (!AuthServiceUtils.selectPermission(entityName, user)) {
				throw new ServerException(ServerStatus.ON_DENIED);
			}

			filter = new PropertyFilter();
			filter.exlcude("*");
			model.put("update", false);
		}

		model.put("insert", AuthServiceUtils.insertPermission(entityName, user));
		model.put("delete", AuthServiceUtils.deletePermission(entityName, user));
		model.put("create", id == null);
		JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
		model.put("multipart", CrudContextUtils.isMultipart(joEntity));
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		Object entity = edit(entityName, id, crudSupply, user);
		BinderData binderData = input.getBinderData();
		BinderResult binderResult = binderData.getBinderResult();
		binderResult.setValidation(true);
		binderResult.setPropertyFilter(filter);
		binderData.mapBind(BinderUtils.getDataMap(input.getParamMap()), entity);
		CrudContextUtils.crud(Crud.CREATE, null, joEntity, entity, user, filter);
		model.put("entity", entity);
	}

	/**
	 * 获取编辑实体
	 * 
	 * @param entityName
	 * @param id
	 * @param crudSupply
	 * @param user
	 * @return
	 */
	private Object edit(String entityName, Object id, ICrudSupply crudSupply, JiUserBase user) {
		Object entity = null;
		if (id == null) {
			return entity = crudSupply.create(entityName);

		} else {
			Serializable identifier = DynaBinderUtils.getParamId(id, crudSupply.getIdentifierType(entityName));
			if (identifier == null) {
				throw new ServerException(ServerStatus.IN_404);
			}

			entity = crudSupply.get(entityName, identifier, AccessServiceUtils.updateCondition(entityName, user, null));
			if (entity == null) {
				if (crudSupply.get(entityName, identifier, null) == null) {
					throw new ServerException(ServerStatus.ON_DELETED);

				} else {
					throw new ServerException(ServerStatus.ON_DENIED);
				}
			}

			return entity;
		}
	}

	/**
	 * @param entityName
	 * @param input
	 * @return
	 */
	public String save(String entityName, Input input) {
		return save(entityName, null, input);
	}

	/**
	 * 保存实体
	 * 
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public String save(String entityName, Object id, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		if (!crudSupply.support(id == null ? Crud.CREATE : Crud.UPDATE)) {
			throw new ServerException(ServerStatus.IN_404);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = null;
		try {
			filter = id == null ? AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user) : AuthServiceUtils
					.updatePropertyFilter(entityName, crudSupply, user);

		} catch (ServerException e) {
			JLog.log("admin", "save/" + (id == null ? entityName : (entityName + "/" + id)), input.getAddress(),
					user == null ? null : user.getUsername(), false);
			return "admin/entity/save.denied";
		}

		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_WRITE);
		Object entity = edit(entityName, id, crudSupply, user);
		if (id != null) {
			crudSupply.evict(entity);
		}

		boolean create = id == null;
		Map<String, Object> crudRecord = create ? null : CrudUtils.crudRecord(new JoEntity(entityName, entity.getClass()), entity,
				filter);
		BinderData binderData = input.getBinderData();
		BinderResult binderResult = binderData.getBinderResult();
		binderResult.setPropertyFilter(filter);
		String[] subtables = input.getParams("!subtables");
		if (subtables != null) {
			Map<String, Object> subtableMap = new HashMap<String, Object>();
			for (String subtable : subtables) {
				subtableMap.put(subtable, null);
			}

			binderData.mapBind(subtableMap, entity);
		}

		binderResult.setValidation(true);
		Map<String, Object> dataMap = ParameterResolverBinder.getPropertyMap(input);
		if (!create) {
			dataMap.remove(crudSupply.getIdentifierName(entityName));
		}

		binderData.mapBind(dataMap, entity);
		JoEntity joEntity = (JoEntity) input.getAttribute("joEntity");
		CrudContextUtils.crud(create ? Crud.CREATE : Crud.UPDATE, crudRecord, joEntity, entity, user, filter, binderResult, input);
		InModel model = input.getModel();
		model.put("entity", entity);
		if (binderResult.hashErrors()) {
			model.put("errors", binderResult.getPropertyErrors());
			return "admin/entity/save.error";
		}

		crudSupply.mergeEntity(entityName, entity, id == null);
		if (create) {
			model.put("create", true);
			model.put("id", crudSupply.getIdentifier(entityName, entity));
		}

		JLog.log("admin", "save/" + id == null ? entityName : (entityName + "/" + id), input.getAddress(), user == null ? null
				: user.getUsername(), true);
		return "admin/entity/save";
	}

	/**
	 * 删除实体
	 * 
	 * @param entityName
	 * @param id
	 * @param input
	 * @return
	 */
	public String delete(String entityName, @Param Object id, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, null);
		if (!crudSupply.support(Crud.DELETE)) {
			throw new ServerException(ServerStatus.IN_404);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		AuthServiceUtils.deletePropertyFilter(entityName, crudSupply, user);
		try {
			EntityService.ME.delete(entityName, crudSupply, user, id);

		} catch (Throwable e) {
			JLog.log("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user.getUsername(),
					false);
			return "admin/entity/delete.error";
		}

		JLog.log("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user.getUsername(), true);
		return "admin/entity/delete";
	}

	/**
	 * 批量删除
	 * 
	 * @param entityName
	 * @param ids
	 * @param input
	 * @return
	 */
	@Mapping(method = InMethod.POST)
	public String delete(String entityName, @Param String[] ids, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, null);
		JiUserBase user = SecurityService.ME.getUserBase(input);
		AuthServiceUtils.deletePropertyFilter(entityName, crudSupply, user);
		List<JLog> logs = new ArrayList<JLog>(ids.length);
		try {
			EntityService.ME.delete(entityName, crudSupply, user, ids);

		} catch (Throwable e) {
			for (String id : ids) {
				logs.add(new JLog("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user
						.getUsername(), false));
			}

			JLog.logs(logs);
			return "admin/entity/delete.error";
		}

		for (String id : ids) {
			logs.add(new JLog("admin", "delete/" + entityName + "/" + id, input.getAddress(), user == null ? null : user
					.getUsername(), true));
		}

		JLog.logs(logs);
		return "admin/entity/delete";
	}

	/**
	 * 导出Excel
	 * 
	 * @param entityName
	 * @param ids
	 * @param input
	 * @param response
	 * @throws IOException
	 */
	@Body
	public void export(String entityName, @Nullable @Param String[] ids, Input input, HttpServletResponse response)
			throws IOException {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		if (crudSupply instanceof CrudSupply) {
			throw new ServerException(ServerStatus.IN_404);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.selectPropertyFilter(entityName, crudSupply, user);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		List<Object> entities = ids == null ? EntityService.ME.list(entityName, crudSupply, user, null,
				InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), filter, null, input),
				InputServiceUtils.getOrderQueue(input), null) : EntityService.ME.list(entityName, crudSupply, user, null, ids);
		HSSFWorkbook workbook = XlsUtils.getWorkbook(entities, XlsUtils.XLS_BASE);
		response.addHeader("Content-Disposition", "attachment;filename=" + entityName + ".xls");
		workbook.write(response.getOutputStream());
	}

	/**
	 * 导入Excel
	 * 
	 * @param entityName
	 * @param xls
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public String importXls(String entityName, @Param FileItem xls, Input input) throws IOException {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		if (crudSupply instanceof CrudSupply) {
			throw new ServerException(ServerStatus.IN_404);
		}

		JiUserBase user = SecurityService.ME.getUserBase(input);
		PropertyFilter filter = AuthServiceUtils.insertPropertyFilter(entityName, crudSupply, user);
		List<?> entities = XlsUtils.getXlsList(new HSSFWorkbook(xls.getInputStream()), null, crudSupply.getEntityClass(entityName),
				XlsUtils.XLS_BASE, false);
		EntityService.ME.merge(entityName, crudSupply, user, entities, filter);
		JLog.log("admin", "importXls/" + entityName, input.getAddress(), user == null ? null : user.getUsername(), true);
		return "admin/entity/importXls";
	}

	/**
	 * 选择授权
	 * 
	 * @param entityName
	 * @param crudSupply
	 * @param input
	 */
	private void suggest(String entityName, ICrudSupply crudSupply, Input input) {
		if (crudSupply instanceof CrudSupply || !(input instanceof InputRequest)) {
			throw new ServerException(ServerStatus.IN_404);
		}

		if (((InputRequest) input).getSession(EntityStatics.suggest(entityName)) == null) {
			throw new ServerException(ServerStatus.ON_DENIED);
		}
	}

	/**
	 * 弹出列表
	 * 
	 * @param entityName
	 * @param input
	 */
	public void suggest(String entityName, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		suggest(entityName, crudSupply, input);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		input.getModel().put("entities", EntityStatics.suggest(entityName, input));
	}

	/**
	 * @param entityName
	 * @param input
	 */
	public void lookup(String entityName, Input input) {
		lookup(entityName, null, input);
	}

	/**
	 * 查找页面
	 * 
	 * @param entityName
	 * @param jdbcPage
	 * @param input
	 */
	@Mapping(method = InMethod.POST)
	public void lookup(String entityName, @Binder JdbcPage jdbcPage, Input input) {
		ICrudSupply crudSupply = getCrudSupply(entityName, input);
		suggest(entityName, crudSupply, input);
		JdbcCondition jdbcCondition = AccessServiceUtils.suggestCondition(entityName, SecurityService.ME.getUserBase(input),
				InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), null, null, input));
		jdbcPage = InputServiceUtils.getJdbcPage(entityName, jdbcPage, input);
		InModel model = input.getModel();
		model.put("page", jdbcPage);
		TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
		model.put("entities", crudSupply.list(entityName, jdbcCondition, InputServiceUtils.getOrderQueue(input), jdbcPage));
	}
}
