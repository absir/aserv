/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月27日 下午4:53:49
 */
package com.absir.aserv.system.configure;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.service.utils.EmailServiceUtils;

@MaEntity(parent = {@MaMenu("接口配置")}, name = "邮件")
public class JEmailConfigure extends JConfigureBase implements ICrudBean {

    @JaLang("发送服务器")
    private String smtp = "smtp.qq.com";

    @JaLang("端口")
    private int port = 25;

    @JaLang("安全连接")
    private boolean starttls;

    @JaLang(value = "发送名", tag = "fromName")
    private String from;

    @JaLang("匿名")
    private boolean anyone;

    @JaLang("用户名")
    private String username;

    @JaLang("密码")
    private String password;

    public String getSmtp() {
        return smtp;
    }

    public int getPort() {
        return port;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public String getFrom() {
        return from;
    }

    public boolean isAnyone() {
        return anyone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void processCrud(Crud crud, CrudHandler handler) {
        if (crud == Crud.UPDATE) {
            EmailServiceUtils.ME.clearSession();
        }
    }
}
