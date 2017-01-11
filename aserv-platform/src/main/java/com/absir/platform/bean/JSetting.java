package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.platform.bean.base.JbPlatform;
import tplatform.DFromSetting;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by absir on 2016/12/2.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "设置")
@Entity
public class JSetting extends JbPlatform {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JaLang("设置")
    private DFromSetting fromSetting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DFromSetting getFromSetting() {
        return fromSetting;
    }

    public void setFromSetting(DFromSetting fromSetting) {
        this.fromSetting = fromSetting;
    }

}
