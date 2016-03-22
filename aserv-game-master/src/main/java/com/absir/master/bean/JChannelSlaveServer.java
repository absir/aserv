/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年4月13日 下午3:37:15
 */
package com.absir.master.bean;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.orm.value.JaColum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;

@MaEntity(parent = {@MaMenu("节点管理")}, name = "渠道", value = @MaMenu(order = -126))
@JaModel(desc = true)
@Entity
public class JChannelSlaveServer extends JbBean {

    @JaLang("名称")
    private String name;

    @JaLang("服务")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ManyToOne
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private JSlaveServer server;

    @JaLang("渠道")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    @JaColum(indexs = @Index(columnList = "channel"))
    private String channel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSlaveServer getServer() {
        return server;
    }

    public void setServer(JSlaveServer server) {
        this.server = server;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
