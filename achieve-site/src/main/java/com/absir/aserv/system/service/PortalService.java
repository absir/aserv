package com.absir.aserv.system.service;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.developer.Site;
import com.absir.aserv.system.asset.Asset_verify;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.configure.JSiteConfigure;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.JUserDao;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.in.Input;
import com.absir.server.route.invoker.InvokerResolverErrors;
import org.hibernate.LockMode;
import org.hibernate.Session;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by absir on 16/7/23.
 */
@Bean
public class PortalService {

    public static final PortalService ME = BeanFactoryUtils.get(PortalService.class);

    public static final String EMAIL_REGISTER_TAG = "emailRegister";

    public static final String MESSAGE_REGISTER_TAG = "messageRegister";

    public static final String REGISTER_TAG = "register";

    public static final String LOGIN_TAG = "login";

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
        JVerifier verifier = VerifierService.getOperationVerifier(session, id, idleTime, true);
        if (verifier == null) {
            return VerifierService.getOperationIdleTime(session, verifier, id);
        }

        String code = randomCode();
        VerifierService.doneOperation(session, verifier, tag, code, 1);
        content = MessageFormat.format(content, Pag.CONFIGURE.getSiteName(), langMessage == null ? operation : langMessage.getLangMessage(operation), code);
        if (!IMessageService.ME.sendMessage(content, mobile)) {
            return -1;
        }

        return 0;
    }

    @Transaction
    public long sendEmailCode(String email, String tag, String subject, String content, long idleTime, String operation, ILangMessage langMessage) {
        if (!Pag.CONFIGURE.hasEmail()) {
            return -2;
        }

        Session session = BeanDao.getSession();
        String id = email + "@Code";
        JVerifier verifier = VerifierService.getOperationVerifier(session, id, idleTime, true);
        if (verifier == null) {
            return VerifierService.getOperationIdleTime(session, verifier, id);
        }

        String code = randomCode();
        VerifierService.doneOperation(session, verifier, tag, code, 1);
        subject = MessageFormat.format(subject, Pag.CONFIGURE.getSiteName());
        content = MessageFormat.format(content, Pag.CONFIGURE.getSiteName(), langMessage == null ? operation : langMessage.getLangMessage(operation), code);
        if (!IEmailService.ME.sendMail(subject, content, true, email)) {
            return -1;
        }

        return 0;
    }

    /*
     * -1验证码不存在 -2验证码过期 0正常
     */
    @Transaction(readOnly = true)
    public int verifyCode(String emailOrMobile, String tag) {
        String id = emailOrMobile + "@Code";
        Session session = BeanDao.getSession();
        JVerifier verifier = BeanDao.loadReal(session, JVerifier.class, id, LockMode.PESSIMISTIC_WRITE);
        if (verifier == null || !KernelObject.equals(verifier.getTag(), tag)) {
            return -1;
        }

        long contextTime = ContextUtils.getContextTime();
        if (verifier.getPassTime() != 0 && verifier.getPassTime() < contextTime) {
            return -2;
        }

        BeanService.ME.delete(verifier);
        return 0;
    }

    public static JSiteConfigure.OperationVerify getOperationVerify(String tag) {
        Map<String, JSiteConfigure.OperationVerify> map = Pag.CONFIGURE.getOperationVerifyMap();
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

    public boolean setOperationVerify(String address, String tag, Input input) {
        JSiteConfigure.OperationVerify verify = getOperationVerify(tag);
        if (VerifierService.isOperationCount(input.getAddress(), verify == null ? "" : verify.tag, verify == null ? Pag.CONFIGURE.getOperationVerifyCount() : verify.maxCount)) {
            if (input != null) {
                input.getModel().put("verify", true);
            }

            return true;
        }

        return false;
    }

    public boolean doneOperationVerify(String address, String tag, Input input) {
        JSiteConfigure.OperationVerify verify = getOperationVerify(tag);
        tag = verify == null ? "" : verify.tag;
        long idleTime = verify == null ? Pag.CONFIGURE.getOperationVerifyTime() : verify.idleTime;
        int maxCount = verify == null ? Pag.CONFIGURE.getOperationVerifyCount() : verify.maxCount;
        if (VerifierService.isOperationCount(input.getAddress(), tag, maxCount)) {
            if (input != null) {
                if (!Asset_verify.verifyInput(input)) {
                    InvokerResolverErrors.onError("verifyCode", Site.VERIFY_ERROR, null, null);
                }
            }

            return true;
        }

        return VerifierService.doneOperationCount(address, tag, idleTime, maxCount);
    }

}
