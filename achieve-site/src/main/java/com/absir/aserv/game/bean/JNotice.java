package com.absir.aserv.game.bean;

import com.absir.aserv.master.bean.base.JbServerTargetsO;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.Entity;

/**
 * Created by absir on 2017/2/28.
 */
@MaEntity(parent = {@MaMenu("节点管理")}, name = "通知")
@Entity
public class JNotice extends JbServerTargetsO {

    @JaLang("标题")
    private String subject;

    @JaLang("内容")
    private String content;

    @JaLang("前往面板")
    private int gotoPanel;

    @JaLang("子面板")
    private int subPanel;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGotoPanel() {
        return gotoPanel;
    }

    public void setGotoPanel(int gotoPanel) {
        this.gotoPanel = gotoPanel;
    }

    public int getSubPanel() {
        return subPanel;
    }

    public void setSubPanel(int subPanel) {
        this.subPanel = subPanel;
    }
}
