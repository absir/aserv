package com.absir.aserv.system.service;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.JUserDao;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.context.core.ContextUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.LockMode;
import org.hibernate.Session;

import java.text.MessageFormat;

/**
 * Created by absir on 16/7/23.
 */
@Bean
public class PortalService {

    public static final PortalService ME = BeanFactoryUtils.get(PortalService.class);

    public static final String REGISTER_TAG = "register";

    public static final String PASSWORD_TAG = "password";

    @Transaction(readOnly = true)
    public JUser findUser(String name, int type) {
        if (type == 1) {
            return (JUser) BeanService.ME.selectQuerySingle("SELECT o FROM JUser o WHERE o.username = ?", name);

        } else if (type == 2) {
            return (JUser) BeanService.ME.selectQuerySingle("SELECT o FROM JUser o WHERE o.email = ?", name);

        } else if (type == 3) {
            return (JUser) BeanService.ME.selectQuerySingle("SELECT o FROM JUser o WHERE o.mobile = ?", name);
        }

        return JUserDao.ME.findByRefUsername(name);
    }

    public String randomCode() {
        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.NUMBER, HelperRandom.nextInt(999999), 0, 6);
        return stringBuilder.toString();
    }

    /*
     * -2 系统不支持 -1 发送失败 0 发送成功>0 已经发送了
     */
    @Transaction
    public long sendMessageCode(String mobile, String tag, String content, long idleTime, String operation, ILangMessage langMessage) {
        if (!Pag.CONFIGURE.hasMessage()) {
            return -2;
        }

        Session session = BeanDao.getSession();
        String id = mobile + "@Code";
        if (idleTime > 0) {
            long time = VerifierService.getOperationIdleTime(session, id, true);
            if (time > 0) {
                return time;
            }
        }

        String code = randomCode();
        content = MessageFormat.format(content, Pag.CONFIGURE.getSiteName(), langMessage == null ? operation : langMessage.getLangMessage(operation), code);
        if (!IMessageService.ME.sendMessage(content, mobile)) {
            return -1;
        }

        VerifierService.doneOperation(session, id, idleTime, tag, code, null);
        return 0;
    }

    @Transaction
    public long sendEmailCode(String email, String tag, String subject, String content, long idleTime, String operation, ILangMessage langMessage) {
        if (!Pag.CONFIGURE.hasEmail()) {
            return -2;
        }

        Session session = BeanDao.getSession();
        String id = email + "@Code";
        if (idleTime > 0) {
            long time = VerifierService.getOperationIdleTime(session, id, true);
            if (time > 0) {
                return time;
            }
        }

        String code = randomCode();
        subject = MessageFormat.format(subject, Pag.CONFIGURE.getSiteName());
        content = MessageFormat.format(content, Pag.CONFIGURE.getSiteName(), langMessage == null ? operation : langMessage.getLangMessage(operation), code);
        if (!IEmailService.ME.sendMail(subject, content, true, email)) {
            return -1;
        }

        VerifierService.doneOperation(session, id, idleTime, tag, code, null);
        return 0;
    }

    /*
     * -1验证码不存在 -2验证码过期 0正常
     */
    @Transaction(readOnly = true)
    public int verifyCode(String emailOrMobile, String tag) {
        String id = emailOrMobile + "@Code";
        Session session = BeanDao.getSession();
        JVerifier verifier = session.load(JVerifier.class, id, LockMode.PESSIMISTIC_WRITE);
        if (verifier == null || !verifier.getTag().equals(tag)) {
            return -1;
        }

        long contextTime = ContextUtils.getContextTime();
        if (verifier.getPassTime() != 0 && verifier.getPassTime() < contextTime) {
            return -2;
        }

        BeanService.ME.delete(verifier);
        return 0;
    }


}
