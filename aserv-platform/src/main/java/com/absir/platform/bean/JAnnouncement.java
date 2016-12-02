package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.platform.bean.base.JbPlatform;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by absir on 2016/12/1.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "公告")
@Entity
public class JAnnouncement extends JbPlatform {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JaLang("公告列表")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @JaEdit(types = "subtable")
    public JAnnouncement.AnnouncementEntry[] announcementList;

    public static class AnnouncementEntry {

        @JaLang("标题")
        private String title;

        @JaLang("内容")
        @JaEdit(types = "text")
        private String content;

        @JaName("附件")
        @JaCrud(factory = UploadCrudFactory.class, parameters = {"jpg,png,zip"})
        private String attach;

        @JaLang("排序")
        private int ordinal;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAttach() {
            return attach;
        }

        public void setAttach(String attach) {
            this.attach = attach;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnnouncementEntry[] getAnnouncementList() {
        return announcementList;
    }

    public void setAnnouncementList(AnnouncementEntry[] announcementList) {
        this.announcementList = announcementList;
    }
}
