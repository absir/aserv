package com.absir.platform.bean;

import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaClasses;
import com.absir.platform.bean.base.JbPlatform;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by absir on 2016/12/2.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "服务")
@Entity
public class JServer extends JbPlatform {

    @JaLang("服务列表")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @JaEdit(types = "subtable")
    public JServer.ServerEntry[] serverList;
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServerEntry[] getServerList() {
        return serverList;
    }

    public void setServerList(ServerEntry[] serverList) {
        this.serverList = serverList;
    }

    public static class ServerEntry {

        @NotEmpty
        @JaClasses(JSlaveServer.class)
        @JaLang(value = "节点服务", tag = "slaveServer")
        private long id;

        @NotEmpty
        @JaLang("名称")
        private String name;

        @JaLang("服务地址")
        private String sAddress;

        @JaLang("端口")
        private int port;

        @JaLang("下载地址")
        private String dAddress;

        @JaLang("权重")
        private int weight;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getsAddress() {
            return sAddress;
        }

        public void setsAddress(String sAddress) {
            this.sAddress = sAddress;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getdAddress() {
            return dAddress;
        }

        public void setdAddress(String dAddress) {
            this.dAddress = dAddress;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

    }
}
