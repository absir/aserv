/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月9日 下午2:57:07
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBeanSL;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * @author absir
 */
@Entity
public class JUploadCite extends JbBeanSL {

    @JaLang(value = "上传内容", tag = "uploadContent")
    @ManyToOne(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private JUpload upload;

    @JaLang(value = "更新时间")
    @JaEdit(types = "dateTime")
    private long updateTime;

    /**
     * @return the upload
     */
    public JUpload getUpload() {
        return upload;
    }

    /**
     * @param upload the upload to set
     */
    public void setUpload(JUpload upload) {
        this.upload = upload;
    }


    /**
     * @return
     */
    public long getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
