/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年5月6日 上午9:28:38
 */
package com.absir.aserv.slave.bean;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.orm.value.JaColum;

import javax.persistence.*;

/**
 * @author absir
 *
 */
@Entity
public class JMasterSynch extends JbBase {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    private String id;

    @JaLang("主服务器编号")
    private int masterIndex;

    @JaLang("链接")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String uri;

    @JaLang("提交数据")
    @Lob
    @Column(length = 10240)
    private byte[] postData;

    @JaLang("更新时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime", editable = JeEditable.LOCKED)
    private long updateTime;

    @JaLang("已经同步")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    @JaColum(indexs = @Index(columnList = "synched"))
    private boolean synched;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the masterIndex
     */
    public int getMasterIndex() {
        return masterIndex;
    }

    /**
     * @param masterIndex
     *            the masterIndex to set
     */
    public void setMasterIndex(int masterIndex) {
        this.masterIndex = masterIndex;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri
     *            the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the postData
     */
    public byte[] getPostData() {
        return postData;
    }

    /**
     * @param postData
     *            the postData to set
     */
    public void setPostData(byte[] postData) {
        this.postData = postData;
    }

    /**
     * @return the updateTime
     */
    public long getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     *            the updateTime to set
     */
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the synched
     */
    public boolean isSynched() {
        return synched;
    }

    /**
     * @param synched
     *            the synched to set
     */
    public void setSynched(boolean synched) {
        this.synched = synched;
    }
}
