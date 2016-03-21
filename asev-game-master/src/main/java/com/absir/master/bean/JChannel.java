/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午2:29:14
 */
package com.absir.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;

@MaEntity(parent = {@MaMenu("平台管理")}, name = "渠道", value = @MaMenu(order = -128))
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JChannel extends JbBase {

    @JaLang(value = "渠道", tag = "channel")
    @Id
    @NotEmpty
    private String id;

    @JaLang("名称")
    private String name;

    @JaLang("公告别名")
    private String announcementAlias;

    @JaLang("服务别名")
    private String serverAlias;

    @JaLang("版本")
    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnouncementAlias() {
        return announcementAlias;
    }

    public void setAnnouncementAlias(String announcementAlias) {
        this.announcementAlias = announcementAlias;
    }

    public String getServerAlias() {
        return serverAlias;
    }

    public void setServerAlias(String serverAlias) {
        this.serverAlias = serverAlias;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
