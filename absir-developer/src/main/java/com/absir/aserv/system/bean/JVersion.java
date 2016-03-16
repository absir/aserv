/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月4日 下午7:37:02
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Entity;

@Entity
public class JVersion extends JbBean {

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "版本")
    private String version;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "版本名")
    private String versionName;

    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaLang(value = "版本文件")
    private String versionFile;

    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime")
    @JaLang(value = "创建时间")
    private long createTime;

    @JaLang(value = "描述")
    private String description;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionFile() {
        return versionFile;
    }

    public void setVersionFile(String versionFile) {
        this.versionFile = versionFile;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
