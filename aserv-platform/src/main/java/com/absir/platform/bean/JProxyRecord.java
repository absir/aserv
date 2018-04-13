package com.absir.platform.bean;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.core.base.Environment;
import com.absir.platform.service.ProxyService;
import com.absir.server.in.Input;

import javax.persistence.Entity;
import javax.persistence.Transient;

@MaEntity(parent = {@MaMenu("平台管理")}, name = "代理记录")
@Entity
public class JProxyRecord extends JbBean implements ICrudBean {

    @JaLang("转发地址")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    public String url;

    @JaLang("转发参数")
    public byte[] postData;

    @JaLang("再次发送")
    @Transient
    @JaEdit(editable = JeEditable.ENABLE)
    public boolean send;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getPostData() {
        return postData;
    }

    public void setPostData(byte[] postData) {
        this.postData = postData;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    @Override
    public void processCrud(JaCrud.Crud crud, CrudHandler handler, Input input) {
        if (handler.isPersist() && handler.getCrud() != JaCrud.Crud.DELETE && send) {
            try {
                ProxyService.ME.proxyRequest(url, postData, null);

            } catch (Exception e) {
                Environment.throwable(e);
            }
        }
    }
}
