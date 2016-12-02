/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月19日 上午10:32:29
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.*;
import com.absir.aserv.system.crud.DateCrudFactory;
import com.absir.validator.value.NotEmpty;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@MaEntity(parent = {@MaMenu("系统配置"), @MaMenu("常用配置")}, name = "字典")
@Entity
public class JDict extends JbBase implements JiOpenValue<String> {

    @JaLang(value = "键", tag = "key")
    @Id
    @NotEmpty
    @JaEdit(groups = {JaEdit.GROUP_SUG})
    private String id;

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("值")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    private String value;

    @JaLang("更新时间")
    @JaEdit(types = "dateTime", editable = JeEditable.LOCKED, groups = JaEdit.GROUP_LIST)
    @JaCrud(value = "dateCrudFactory", cruds = {JaCrud.Crud.CREATE, JaCrud.Crud.UPDATE}, factory = DateCrudFactory.class)
    private long updateTime;

    /**
     * @author absir 扩展存储
     */
    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> metaMap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String forValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Map<String, String> getMetaMap() {
        return metaMap;
    }

    public void setMetaMap(Map<String, String> metaMap) {
        this.metaMap = metaMap;
    }

    public Object getMetaMap(String key) {
        return metaMap == null ? null : metaMap.get(key);
    }

    public void setMetaMap(String key, String value) {
        if (metaMap == null) {
            metaMap = new HashMap<String, String>();
        }

        metaMap.put(key, value);
    }

}
