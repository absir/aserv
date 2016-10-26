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
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.service.IEmailService;
import com.absir.aserv.system.service.IMessageService;
import com.absir.core.kernel.KernelString;
import com.absir.validator.value.Range;

import java.util.Map;

@MaEntity(parent = @MaMenu("网站设置"), name = "全局")
public class JSiteConfigure extends JConfigureBase {

    @JaLang("网站名称")
    private String siteName = "achieve server";

    @JaLang("网站标题")
    private String title = siteName;

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

    @JaLang("邮件发送间隔")
    private long emailIdleTime = 300000;

    @JaLang("短信发送间隔")
    private long messageIdleTime = 300000;

    @JaLang("操作验证时间")
    private long operationVerifyTime = 3600000;

    @JaLang("操作验证次数")
    private int operationVerifyCount;

    @JaLang("操作验证")
    private Map<String, OperationVerify> operationVerifyMap;

    @JaSubField("注册设置")
    @JaLang("默认注册类型")
    //1 用户名注册 2 邮件注册 3 邮件注册
    @Range(min = 1, max = 3)
    private int defaultRegisterType = 2;

    @JaLang("用户名注册")
    private boolean allowUsernameRegister;

    @JaLang("邮箱注册")
    private boolean allowEmailRegister;

    @JaLang("短信注册")
    private boolean allowMessageRegister;

    @JaLang("手动激活")
    private boolean registerUserNoActive;

    @JaLang("验证密码修改")
    private boolean allowConfirmPasswordModify;

    @JaLang("邮件密码修改")
    private boolean allowEmailPasswordModify;

    @Langs
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Langs
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getEmailIdleTime() {
        return emailIdleTime;
    }

    public void setEmailIdleTime(long emailIdleTime) {
        this.emailIdleTime = emailIdleTime;
    }

    public long getMessageIdleTime() {
        return messageIdleTime;
    }

    public void setMessageIdleTime(long messageIdleTime) {
        this.messageIdleTime = messageIdleTime;
    }

    public long getOperationVerifyTime() {
        return operationVerifyTime;
    }

    public void setOperationVerifyTime(long operationVerifyTime) {
        this.operationVerifyTime = operationVerifyTime;
    }

    public int getOperationVerifyCount() {
        return operationVerifyCount;
    }

    public void setOperationVerifyCount(int operationVerifyCount) {
        this.operationVerifyCount = operationVerifyCount;
    }

    public Map<String, OperationVerify> getOperationVerifyMap() {
        return operationVerifyMap;
    }

    public void setOperationVerifyMap(Map<String, OperationVerify> operationVerifyMap) {
        this.operationVerifyMap = operationVerifyMap;
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

    public boolean hasAllowUsernameRegister() {
        return allowUsernameRegister;
    }

    public boolean hasAllowEmailRegister() {
        return allowEmailRegister && hasEmail();
    }

    public boolean hasAllowMessageRegister() {
        return allowMessageRegister && hasMessage();
    }

    public boolean hasAllowUserRegister() {
        return hasAllowUsernameRegister() || hasAllowEmailRegister() || hasAllowMessageRegister();
    }

    public JSiteConfigure.OperationVerify getOperationVerify(String tag) {
        Map<String, JSiteConfigure.OperationVerify> map = operationVerifyMap;
        if (map != null) {
            JSiteConfigure.OperationVerify operationVerify = map.get(tag);
            if (operationVerify != null) {
                if (!KernelString.isEmpty(operationVerify.alias)) {
                    JSiteConfigure.OperationVerify verify = map.get(operationVerify.alias);
                    if (verify != null) {
                        if (verify.tag != operationVerify.alias) {
                            verify.tag = operationVerify.alias;
                        }

                        return verify;
                    }
                }

                if (operationVerify.tag != tag) {
                    operationVerify.tag = tag;
                }
            }

            return operationVerify;
        }

        return null;
    }

    public static class OperationVerify {

        @JaLang("别名")
        public String alias;

        @JaLang("空闲时间")
        public long idleTime;

        @JaLang("最大次数")
        public int maxCount;

        @JaEdit(editable = JeEditable.DISABLE)
        public String tag;
    }
}
