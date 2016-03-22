package com.absir.aserv.slave.bean;

import com.absir.aserv.game.bean.JbPlayer;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by absir on 16/3/22.
 */
@MappedSuperclass
public abstract class JbPlayerL extends JbPlayer {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
