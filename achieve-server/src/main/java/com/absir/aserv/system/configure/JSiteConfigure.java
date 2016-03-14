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
import com.absir.aserv.system.bean.value.JaLang;

/**
 * @author absir
 */
@MaEntity(parent = @MaMenu("网站设置"), name = "全局")
public class JSiteConfigure extends JConfigureBase {

    @JaLang("网站名称")
    private String sitename = "achieve server";

    @JaLang("允许注册")
    private boolean allowRegister = true;

    @JaLang("验证时间")
    private long verifyTime = 3600000;

    @JaLang("注册用户禁用")
    private boolean registerUserDisable;

    @JaLang("关键字")
    private String[] keywords = new String[]{"achieve", "server", "java", "web", "cms", "framework"};

    @JaLang("描述")
    private String discription = "achieve server is a java stack type web development framework, make as blog, bussiness, cms, game server";

    @JaLang("上传大小")
    private long uploadSize = 2000000;

    @JaLang("上传扩展名")
    private String uploadExtension = "gif|jpg|jpeg|png|txt|doc|xls|zip|rar";

    /**
     * @return the sitename
     */
    @Langs
    public String getSitename() {
        return sitename;
    }

    /**
     * @param sitename the sitename to set
     */
    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    /**
     * @return the allowRegister
     */
    public boolean isAllowRegister() {
        return allowRegister;
    }

    /**
     * @param allowRegister the allowRegister to set
     */
    public void setAllowRegister(boolean allowRegister) {
        this.allowRegister = allowRegister;
    }

    /**
     * @return the verifyTime
     */
    public long getVerifyTime() {
        return verifyTime;
    }

    /**
     * @param verifyTime the verifyTime to set
     */
    public void setVerifyTime(long verifyTime) {
        this.verifyTime = verifyTime;
    }

    /**
     * @return the registerUserDisable
     */
    public boolean isRegisterUserDisable() {
        return registerUserDisable;
    }

    /**
     * @param registerDisable the registerUserDisable to set
     */
    public void setRegisterUserDisable(boolean registerDisable) {
        this.registerUserDisable = registerUserDisable;
    }

    /**
     * @return the keywords
     */
    @Langs
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the discription
     */
    @Langs
    public String getDiscription() {
        return discription;
    }

    /**
     * @param discription the discription to set
     */
    public void setDiscription(String discription) {
        this.discription = discription;
    }

    /**
     * @return the uploadSize
     */
    public long getUploadSize() {
        return uploadSize;
    }

    /**
     * @param uploadSize the uploadSize to set
     */
    public void setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
    }

    /**
     * @return the uploadExtension
     */
    public String getUploadExtension() {
        return uploadExtension;
    }

    /**
     * @param uploadExtension the uploadExtension to set
     */
    public void setUploadExtension(String uploadExtension) {
        this.uploadExtension = uploadExtension;
    }
}
