package com.absir.aserv.system.service;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.in.Input;
import com.absir.server.value.Param;
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

    public String randomCode() {
        StringBuilder stringBuilder = new StringBuilder();
        HelperRandom.appendFormat(stringBuilder, HelperRandom.FormatType.NUMBER, HelperRandom.nextInt(999999));
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

    @Transaction
    public void registerCode(@Param int type, Input input) {

    }

}
