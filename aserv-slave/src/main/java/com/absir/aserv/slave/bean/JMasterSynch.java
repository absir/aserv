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

    @JaLang("提交查看")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String postView;

    @JaLang("压缩")
    private boolean varints;

    @JaLang("更新时间")
    @JaEdit(groups = JaEdit.GROUP_LIST, types = "dateTime", editable = JeEditable.LOCKED)
    private long updateTime;

    @JaLang("已经同步")
    @JaEdit(groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKED)
    @JaColum(indexs = @Index(columnList = "synched"))
    private boolean synched;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMasterIndex() {
        return masterIndex;
    }

    public void setMasterIndex(int masterIndex) {
        this.masterIndex = masterIndex;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public byte[] getPostData() {
        return postData;
    }

    public void setPostData(byte[] postData) {
        this.postData = postData;
    }

    public boolean isVarints() {
        return varints;
    }

    public void setVarints(boolean varints) {
        this.varints = varints;
    }

    public String getPostView() {
        return postView;
    }

    public void setPostView(String postView) {
        this.postView = postView;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isSynched() {
        return synched;
    }

    public void setSynched(boolean synched) {
        this.synched = synched;
    }

}
