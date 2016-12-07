package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.platform.bean.base.JbPlatform;

import javax.persistence.*;

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
    private SettingEntry settingEntry;

    @Embeddable
    public static class SettingEntry {

        @JaLang("认证")
        private boolean auth;

        @JaLang("消息")
        @JaEdit(types = "text")
        private String message;

        @JaLang("强制更新")
        private boolean force;

        @JaLang("下载地址")
        private String downloadUrl;

        @JaLang("CDN地址")
        private String cdnUrl;

        @JaLang("其他地址")
        private String otherUrl;

        public boolean isAuth() {
            return auth;
        }

        public void setAuth(boolean auth) {
            this.auth = auth;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isForce() {
            return force;
        }

        public void setForce(boolean force) {
            this.force = force;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getCdnUrl() {
            return cdnUrl;
        }

        public void setCdnUrl(String cdnUrl) {
            this.cdnUrl = cdnUrl;
        }

        public String getOtherUrl() {
            return otherUrl;
        }

        public void setOtherUrl(String otherUrl) {
            this.otherUrl = otherUrl;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SettingEntry getSettingEntry() {
        return settingEntry;
    }

    public void setSettingEntry(SettingEntry settingEntry) {
        this.settingEntry = settingEntry;
    }

}
