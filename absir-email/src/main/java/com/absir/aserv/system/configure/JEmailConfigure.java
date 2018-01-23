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
import com.absir.aserv.crud.ICrudSubmit;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.service.EmailService;
import com.absir.aserv.system.service.utils.EmailServiceUtils;
import com.absir.core.base.Environment;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.validator.value.Length;

import javax.mail.MessagingException;

@MaEntity(parent = {@MaMenu("接口配置")}, name = "邮件")
public class JEmailConfigure extends JConfigureBase implements ICrudBean, ICrudSubmit<JEmailConfigure.MailSubmit> {

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
    @JaEdit(types = "passwordType")
    private String password;

    @JaLang("测试邮箱")
    private String testMail;

    @JaLang("测试主题")
    private String testSubject;

    @Length(max = 1024)
    @JaLang("测试内容")
    @JaEdit(types = "text")
    private String testContent;

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isAnyone() {
        return anyone;
    }

    public void setAnyone(boolean anyone) {
        this.anyone = anyone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTestMail() {
        return testMail;
    }

    public void setTestMail(String testMail) {
        this.testMail = testMail;
    }

    public String getTestSubject() {
        return testSubject;
    }

    public void setTestSubject(String testSubject) {
        this.testSubject = testSubject;
    }

    public String getTestContent() {
        return testContent;
    }

    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    @Override
    public void processCrud(Crud crud, CrudHandler handler, Input input) {
        if (handler.isPersist()) {
            EmailServiceUtils.ME.clearSession();
        }
    }

    @Override
    public String submitOption(MailSubmit option, InModel model) {
        if (option == MailSubmit.MAIL_TEST) {
            try {
                EmailService.ME.sendMimeMessage(EmailService.ME.createMimeMessage(testMail), testSubject, testContent, true);

            } catch (MessagingException e) {
                Environment.throwable(e);
                model.put("icon", 2);
                model.put("message", ICrudSubmit.SEND_FAIL);
            }
        }

        return null;
    }


    public static enum MailSubmit {

        @JaLang("发送测试")
        MAIL_TEST,
    }
}
