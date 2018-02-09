package com.absir.group.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
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
 * Created by absir on 16/8/19.
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@MaEntity(parent = {@MaMenu("集群管理")}, name = "节点")
@Entity
public class JNode extends JbBean {

    @JaLang("名称")
    private String name;

    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    @JaLang("群组")
    @JaColum(indexs = {@Index(columnList = "", unique = false)})
    private int group;

    @JaLang("上线时间")
    @JaEdit(types = "startTime", groups = JaEdit.GROUP_SUGGEST)
    private long onlineTime;

    @JaLang("离线时间")
    @JaEdit(types = "startTime", groups = JaEdit.GROUP_SUGGEST)
    private long offlineTime;

    @JaLang("节点地址")
    private String nodeAddress;

    @JaLang("秘钥")
    @JaEdit(groups = JaEdit.GROUP_SUGGEST)
    private String secretKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public long getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(long offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
