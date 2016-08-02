package com.absir.aserv.developer;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.support.web.WebJetbrickSupply;
import com.absir.aserv.system.bean.JTplConfigure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.core.kernel.KernelLang;
import jetbrick.template.JetTemplate;

/**
 * Created by absir on 16/7/27.
 */
@Bean
public class Site implements KernelLang.GetTemplate<String, JetTemplate> {

    public static final Site ME = BeanFactoryUtils.get(Site.class);

    public static final JTplConfigure TPL = JConfigureUtils.getConfigure(JTplConfigure.class);

    public static final String SEND_SUCCESS = LangCodeUtils.get("发送成功", Site.class);

    public static final String SEND_FAIL = LangCodeUtils.get("发送失败", Site.class);

    public static final String SEND_IDLE = LangCodeUtils.get("发送过快", Site.class);

    public static final String CLOUD_NOT_SEND = LangCodeUtils.get("无法发送", Site.class);

    public static final String USERNAME_REGISTERED = LangCodeUtils.get("用户名已注册", Site.class);

    public static final String EMAIL_REGISTERED = LangCodeUtils.get("邮箱已注册", Site.class);

    public static final String MOBILE_REGISTERED = LangCodeUtils.get("手机号已注册", Site.class);

    public static final String REGISTER_OPERATION = LangCodeUtils.get("注册", Site.class);

    public static final String PASSWORD_OPERATION = LangCodeUtils.get("修改密码", Site.class);

    public static final String VERIFY_ERROR = LangCodeUtils.get("验证码错误", Site.class);

    @Override
    public JetTemplate getWith(String template) {
        return WebJetbrickSupply.getEngine().createTemplate(template);
    }

}
