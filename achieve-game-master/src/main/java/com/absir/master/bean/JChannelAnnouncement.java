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

/**
 * @author absir
 */
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * @return the value
     */
    public List<DAnnouncement> getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(List<DAnnouncement> value) {
        this.value = value;
    }

}
