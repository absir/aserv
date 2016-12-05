package com.absir.platform.bean;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaColum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Index;

/**
 * Created by absir on 2016/12/5.
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JPlatformFrom extends JbBean {

    @JaLang("平台")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaColum(indexs = {@Index(columnList = "platform,channel,packageName,versionCode,from")})
    private String platform;

    @JaLang("渠道")
    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    private String channel;

    @JaLang("包名")
    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    private String packageName;

    @JaLang("版本号")
    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    private int versionCode;

    @JaLang("来源")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String formInfo;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getFormInfo() {
        return formInfo;
    }

    public void setFormInfo(String formInfo) {
        this.formInfo = formInfo;
    }
}
