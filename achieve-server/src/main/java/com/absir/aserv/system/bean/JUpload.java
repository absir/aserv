/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月9日 下午2:57:07
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.proxy.JiPass;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.orm.value.JaColum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Index;

@MaEntity(parent = {@MaMenu("附件管理")}, name = "上传")
@JaModel(desc = true)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JUpload extends JbBean implements JiPass, ICrudBean {

    @JaLang("目录路径")
    @JaColum(indexs = @Index(columnList = "dirPath,filename", unique = true))
    private String dirPath;

    @JaLang("文件名称")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String filename;

    /**
     * NULL为文件夹
     */
    @JaLang(value = "文件类型")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String fileType;

    @JaLang("图片")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean imaged;

    @JaLang("文件大小")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private long fileSize;

    @JaLang(value = "关联用户", tag = "assocUser")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaName(value = "JUser")
    private long userId;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {Crud.CREATE}, factory = DateCrudFactory.class)
    private long createTime;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long passTime;

    public String getDirPath() {
        if (dirPath == null) {
            dirPath = "";
        }

        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isImaged() {
        return imaged;
    }

    public void setImaged(boolean imaged) {
        this.imaged = imaged;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    @Override
    public void proccessCrud(Crud crud, CrudHandler handler) {
        UploadCrudFactory.ME.crud(this, crud, handler);
    }
}
