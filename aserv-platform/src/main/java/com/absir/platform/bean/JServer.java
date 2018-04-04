package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.platform.bean.base.JbPlatformGroup;
import org.hibernate.annotations.Type;
import tplatform.DServer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by absir on 2016/12/2.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "服务")
@Entity
public class JServer extends JbPlatformGroup {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JaLang("服务列表")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @JaEdit(types = "subtable")
    public DServer[] servers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DServer[] getServers() {
        return servers;
    }

    public void setServers(DServer[] servers) {
        this.servers = servers;
    }

}
