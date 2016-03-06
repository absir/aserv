/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月9日 下午2:57:07
 */
package com.absir.aserv.system.bean;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.proxy.JiPass;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.dao.BeanDao;

/**
 * @author absir
 *
 */
@MaEntity(parent = { @MaMenu("附件管理") }, name = "上传")
@JaModel(desc = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JUpload extends JbBean implements JiPass, ICrudBean {

	@JaLang("文件路径")
	@JaEdit(groups = { JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST })
	@Column(unique = true)
	private String filePath;

	@JaLang(value = "文件类型")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	private String fileType;

	@JaLang(value = "关联用户", tag = "assocUser")
	@JaEdit(groups = JaEdit.GROUP_LIST)
	@JaName(value = "JUser")
	private long userId;

	@JaLang("创建时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	@JaCrud(value = "dateCrudFactory", cruds = { Crud.CREATE }, factory = DateCrudFactory.class)
	private long createTime;

	@JaLang("过期时间")
	@JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
	private long passTime;

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath
	 *            the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the passTime
	 */
	public long getPassTime() {
		return passTime;
	}

	/**
	 * @param passTime
	 *            the passTime to set
	 */
	public void setPassTime(long passTime) {
		this.passTime = passTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.aserv.crud.value.ICrudBean#proccessCrud(com.absir.aserv
	 * .system.bean.value.JaCrud.Crud, com.absir.aserv.crud.CrudHandler)
	 */
	@Override
	public void proccessCrud(Crud crud, CrudHandler handler) {
		if (crud == Crud.DELETE) {
			Session session = BeanDao.getSession();
			session.flush();
			try {
				session.delete(this);
				session.flush();
				UploadCrudFactory.ME.delete(filePath);

			} catch (Exception e) {
				session.cancelQuery();
				passTime = 0;
				session.merge(this);
			}
		}
	}
}
