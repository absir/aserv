package com.absir.aserv.system.bean;

/**
 * Created by absir on 16/7/26.
 */

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.Length;

@MaEntity(parent = @MaMenu("网站设置"), name = "模版")
public class JTplConfigure extends JConfigureBase {

    //@JaSubField("模版设置")
    @JaLang("注册协议")
    @JaEdit(types = "html")
    private String registerAgreement = "本服务协议双方为本平台与本平台客户，本服务协议具有合同效力。您确认本服务协议后，本服务协议即在您和本网站之间产生法律效力。请您务必在注册之前认真阅读全部服务协议内容，如有任何疑问，可向本网站咨询。 无论您事实上是否在注册之前认真阅读了本服务协议，只要您点击协议正本下方的\"注册\"按钮并按照本网站注册程序成功注册为用户，您的行为仍然表示您同意并签署了本服务协议。";

    @JaLang("验证邮件")
    @JaEdit(types = "html")
    private String codeEmailSubject = "【{0}】邮箱验证";

    @JaLang("验证邮件")
    @JaEdit(types = "html")
    private String codeEmail = "【{0}】{1}验证码是:{2}，请您在5分钟内使用。如非本人操作，请忽略本短信息。";

    @JaLang("验证消息")
    @Length(max = 255)
    @JaEdit(types = "text")
    private String codeMessage = "【{0}】{1}验证码是:{2}，请您在5分钟内使用。如非本人操作，请忽略本短信息。";

    @Langs
    public String getRegisterAgreement() {
        return registerAgreement;
    }

    public void setRegisterAgreement(String registerAgreement) {
        this.registerAgreement = registerAgreement;
    }

    @Langs
    public String getCodeEmail() {
        return codeEmail;
    }

    public void setCodeEmail(String codeEmail) {
        this.codeEmail = codeEmail;
    }

    @Langs
    public String getCodeMessage() {
        return codeMessage;
    }

    public void setCodeMessage(String codeMessage) {
        this.codeMessage = codeMessage;
    }

}
