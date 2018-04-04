package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.platform.bean.base.JbPlatformGroup;
import org.hibernate.annotations.Type;
import tplatform.DAnnouncement;

import javax.persistence.*;

/**
 * Created by absir on 2016/12/1.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "公告")
@Entity
public class JAnnouncement extends JbPlatformGroup {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JaLang("公告列表")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @Embedded
    private DAnnouncement announcement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DAnnouncement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(DAnnouncement announcement) {
        this.announcement = announcement;
    }
}
