package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.proxy.IPassClear;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by absir on 16/8/17.
 */
@MaEntity(parent = {@MaMenu("任务配置")}, name = "任务")
@Entity
public class JTask extends JTaskBase implements IPassClear {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @NotEmpty
    @JaLang("纪录编号")
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
