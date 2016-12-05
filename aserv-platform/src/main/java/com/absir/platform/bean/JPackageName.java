package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiOpen;
import com.absir.validator.value.NotEmpty;

import javax.persistence.Entity;

/**
 * Created by absir on 2016/12/1.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "包名")
@Entity
public class JPackageName extends JbBeanS implements JiOpen {

    @NotEmpty
    @JaLang("名称")
    private String name;

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("备注")
    @JaEdit(types = "text")
    private String mark;

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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

}
