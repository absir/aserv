package com.absir.aserv.developer;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.support.web.WebJetbrickSupply;
import com.absir.aserv.system.bean.JTplConfigure;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.core.kernel.KernelLang;
import com.absir.core.util.UtilAbsir;
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

    public static final String SEND_IDLE = LangCodeUtils.get("刚发送过", Site.class);

    public static final String CLOUD_NOT_SEND = LangCodeUtils.get("无法发送", Site.class);

    public static final String USERNAME_REGISTERED = LangCodeUtils.get("用户名已注册", Site.class);

    public static final String EMAIL_REGISTERED = LangCodeUtils.get("邮箱已注册", Site.class);

    public static final String MOBILE_REGISTERED = LangCodeUtils.get("手机号已注册", Site.class);

    public static final String REGISTER_OPERATION = LangCodeUtils.get("注册", Site.class);

    public static final String VERIFY_ERROR = LangCodeUtils.get("验证码错误", Site.class);

    public static final String REGISTER_SUCCESS = LangCodeUtils.get("注册成功", Site.class);

    public static final String USER_NOT_EXIST = LangCodeUtils.get("用户不存在", Site.class);

    public static final String PASSWORD_ERROR = LangCodeUtils.get("密码错误", Site.class);

    public static final String MODIFY_SUCCESS = LangCodeUtils.get("修改成功", Site.class);

    public static final String LOGIN_FAILURE = LangCodeUtils.get("登录失效", Site.class);

    public static final String COULD_NOT_USE = LangCodeUtils.get("无法使用", Site.class);

    public static final String LOGIN_SUCCESS = LangCodeUtils.get("登录成功", Site.class);

    public static final String USERNAME_HAD = LangCodeUtils.get("不必绑定用户名", Site.class);

    public static final String EMAIL_ERROR = LangCodeUtils.get("请输入有效的邮箱", Site.class);

    public static final String MOBILE_ERROR = LangCodeUtils.get("请输入有效的手机号", Site.class);

    public static final String EMAIL_OR_MOBILE_ERROR = LangCodeUtils.get("请输入有效的邮箱或手机号", Site.class);

    public static final String LOGIN_LAST_ERROR_TIMES = LangCodeUtils.get("再登录{0}次,账户会锁定一段时间", Site.class);

    public static final String LOGIN_LAST_ERROR_TIME = LangCodeUtils.get("帐号锁定中,解锁等待时间{0}", Site.class);

    public static final String DAY = LangCodeUtils.get("天", Site.class);

    public static final String HOUR = LangCodeUtils.get("时", Site.class);

    public static final String MINUTE = LangCodeUtils.get("分", Site.class);

    public static final String SECOND = LangCodeUtils.get("秒", Site.class);

    public static String getHumanTime(int time, int format, ILangMessage langMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean found = false;
        int fTime;
        String fCode;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    fTime = UtilAbsir.DAY_SHORT;
                    fCode = DAY;
                    break;
                case 1:
                    fTime = 3600;
                    fCode = HOUR;
                    break;
                case 2:
                    fTime = 60;
                    fCode = MINUTE;
                    break;
                default:
                    fTime = 1;
                    fCode = SECOND;
                    break;
            }

            if (found || time >= fTime) {
                stringBuilder.append(time < fTime ? 0 : fTime == 1 ? time : (int) Math.floor(time / fTime));
                stringBuilder.append(langMessage == null ? fCode : langMessage.getLangMessage(fCode));
                if (time >= fTime) {
                    if (fTime != 1) {
                        time %= fTime;
                    }
                }

                if (format > 0) {
                    if (--format <= 0) {
                        break;
                    }
                }
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public JetTemplate getWith(String template) {
        return WebJetbrickSupply.getEngine().createTemplate(template);
    }

}
