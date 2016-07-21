/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年8月28日 下午1:19:17
 */
package com.absir.aserv.system.configure;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaSubField;
import com.absir.aserv.system.service.IEmailService;
import com.absir.aserv.system.service.IMessageService;
import com.absir.validator.value.Length;
import com.absir.validator.value.Range;

@MaEntity(parent = @MaMenu("网站设置"), name = "全局")
public class JSiteConfigure extends JConfigureBase {

    @JaLang("网站名称")
    private String siteName = "achieve server";

    @JaLang("关键字")
    private String[] keywords = new String[]{"achieve", "server", "java", "web", "cms", "framework"};

    @JaLang("描述")
    private String description = "achieve server is a java stack type web development framework, make as blog, bussiness, cms, game server";

    @JaLang("上传大小")
    private long uploadSize = 2000000;

    @JaLang("上传扩展名")
    private String uploadExtension = "gif|jpg|jpeg|png|txt|doc|xls|zip|rar";

    @JaLang("使用短信")
    private boolean allowUseMessage;

    @JaSubField("注册设置")
    @JaLang("默认注册类型")
    //1 用户名注册 2 邮件注册 3 邮件注册
    @Range(min = 1, max = 3)
    private int defaultRegisterType = 2;

    @JaLang("用户名注册")
    private boolean allowUsernameRegister;

    @JaLang("邮件注册")
    private boolean allowEmailRegister;

    @JaLang("短信注册")
    private boolean allowMessageRegister;

    @JaLang("手动激活")
    private boolean registerUserNoActive;

    @JaLang("验证密码修改")
    private boolean allowConfirmPasswordModify;

    @JaLang("邮件密码修改")
    private boolean allowEmailPasswordModify;

    @JaSubField("模版设置")
    @JaLang("注册协议")
    @JaEdit(types = "html")
    private String registerAgreement = "本服务协议双方为本平台与本平台客户，本服务协议具有合同效力。您确认本服务协议后，本服务协议即在您和本网站之间产生法律效力。请您务必在注册之前认真阅读全部服务协议内容，如有任何疑问，可向本网站咨询。 无论您事实上是否在注册之前认真阅读了本服务协议，只要您点击协议正本下方的\"注册\"按钮并按照本网站注册程序成功注册为用户，您的行为仍然表示您同意并签署了本服务协议。";

    @JaLang("验证消息模版")
    @Length(max = 255)
    @JaEdit(types = "text")
    private String codeMessageTpl = "";

    @JaLang("验证邮件模版")
    @JaEdit(types = "html")
    private String codeEmailTpl = "";

    @Langs
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Langs
    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    @Langs
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public void setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
    }

    public String getUploadExtension() {
        return uploadExtension;
    }

    public void setUploadExtension(String uploadExtension) {
        this.uploadExtension = uploadExtension;
    }

    public boolean isAllowUseMessage() {
        return allowUseMessage;
    }

    public void setAllowUseMessage(boolean allowUseMessage) {
        this.allowUseMessage = allowUseMessage;
    }

    public int getDefaultRegisterType() {
        return defaultRegisterType;
    }

    public void setDefaultRegisterType(int defaultRegisterType) {
        this.defaultRegisterType = defaultRegisterType;
    }

    public boolean isAllowUsernameRegister() {
        return allowUsernameRegister;
    }

    public void setAllowUsernameRegister(boolean allowUsernameRegister) {
        this.allowUsernameRegister = allowUsernameRegister;
    }

    public boolean isAllowEmailRegister() {
        return allowEmailRegister;
    }

    public void setAllowEmailRegister(boolean allowEmailRegister) {
        this.allowEmailRegister = allowEmailRegister;
    }

    public boolean isAllowMessageRegister() {
        return allowMessageRegister;
    }

    public void setAllowMessageRegister(boolean allowMessageRegister) {
        this.allowMessageRegister = allowMessageRegister;
    }

    public boolean isRegisterUserNoActive() {
        return registerUserNoActive;
    }

    public void setRegisterUserNoActive(boolean registerUserNoActive) {
        this.registerUserNoActive = registerUserNoActive;
    }

    public boolean isAllowConfirmPasswordModify() {
        return allowConfirmPasswordModify;
    }

    public void setAllowConfirmPasswordModify(boolean allowConfirmPasswordModify) {
        this.allowConfirmPasswordModify = allowConfirmPasswordModify;
    }

    public boolean isAllowEmailPasswordModify() {
        return allowEmailPasswordModify;
    }

    public void setAllowEmailPasswordModify(boolean allowEmailPasswordModify) {
        this.allowEmailPasswordModify = allowEmailPasswordModify;
    }

    public boolean hasMessage() {
        return allowUseMessage && IMessageService.ME != null;
    }

    public boolean hasEmail() {
        return IEmailService.ME != null;
    }

    public boolean hasAllowUserRegister() {
        return allowUsernameRegister || (allowEmailRegister && hasEmail()) || (allowMessageRegister && hasMessage());
    }

    @Langs
    public String getRegisterAgreement() {
        return registerAgreement;
    }

    public void setRegisterAgreement(String registerAgreement) {
        this.registerAgreement = registerAgreement;
    }

    @Langs
    public String getCodeMessageTpl() {
        return codeMessageTpl;
    }

    public void setCodeMessageTpl(String codeMessageTpl) {
        this.codeMessageTpl = codeMessageTpl;
    }

    @Langs
    public String getCodeEmailTpl() {
        return codeEmailTpl;
    }

    public void setCodeEmailTpl(String codeEmailTpl) {
        this.codeEmailTpl = codeEmailTpl;
    }

}
