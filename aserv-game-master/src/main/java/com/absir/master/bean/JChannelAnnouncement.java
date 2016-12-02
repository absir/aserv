/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月18日 下午4:30:59
 */
package com.absir.master.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiOpenValue;
import com.absir.master.bean.dto.DAnnouncement;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@MaEntity(parent = {@MaMenu("平台管理")}, name = "公告", value = @MaMenu(order = -127))
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JChannelAnnouncement extends JbBase implements JiOpenValue<List<DAnnouncement>> {

    @JaLang(value = "渠道", tag = "channel")
    @Id
    @NotEmpty
    private String id;

    @JaLang("名称")
    private String name;

    @JaLang("开启")
    private boolean open;

    @JaLang("公告")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    private List<DAnnouncement> value;

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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<DAnnouncement> forValue() {
        return value;
    }

    public void setValue(List<DAnnouncement> value) {
        this.value = value;
    }

}
