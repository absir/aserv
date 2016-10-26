package com.absir.aserv.system.service;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.developer.Site;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.system.asset.Asset_verify;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.bean.form.FEmailCode;
import com.absir.aserv.system.bean.form.FMobileCode;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeUserType;
import com.absir.aserv.system.configure.JSiteConfigure;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.JUserDao;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.lang.ILangMessage;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.binder.BinderData;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelMap;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.route.invoker.InvokerResolverErrors;
import com.absir.validator.ValidatorNotEmpty;
import com.absir.validator.value.Confirm;
import com.absir.validator.value.Length;
import com.absir.validator.value.NotEmpty;
import com.absir.validator.value.Regex;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/7/23.
 */
@Bean
public class PortalService {

    public static final PortalService ME = BeanFactoryUtils.get(PortalService.class);

    public static final String EMAIL_TAG = "email";

    public static final String MESSAGE_TAG = "message";

    public static final String REGISTER_TAG = "register";

    public static final String LOGIN_TAG = "login";

    public static final String PASSWORD_TAG = "password";

    public static final String BIND_EMAIL_TAG = "bindEmail";

    public static final String BIND_MOBILE_TAG = "bindMobile";

    public static final String SYSTEM_OPERATION = LangCodeUtils.get("系统操作", PortalService.class);

    private static final Map<String, String> TAG_MAP_OPERATION = new HashMap<String, String>();

    static {
        TAG_MAP_OPERATION.put(BIND_EMAIL_TAG, LangCodeUtils.get("绑定邮箱", PortalService.class));
        TAG_MAP_OPERATION.put(BIND_MOBILE_TAG, LangCodeUtils.get("绑定手机", PortalService.class));
    }

    public static final String getOperation(String tag, ILangMessage input) {
        tag = TAG_MAP_OPERATION.get(tag);
        if (tag == null) {
            tag = SYSTEM_OPERATION;
        }

        return input == null ? tag : input.getLangMessage(tag);
    }

    public static void resolverIdleTime(long idleTime, long sendTime, Input input) {
        InModel model = input.getModel();
        if (sendTime == 0) {
            model.put("message", input.getLang(Site.SEND_SUCCESS));
            model.put("idleTime", idleTime / 1000);

        } else {
            model.put("icon", 2);
            model.put("message", input.getLang(sendTime == -2 ? Site.CLOUD_NOT_SEND : sendTime == -1 ? Site.SEND_FAIL : Site.SEND_IDLE));
            if (sendTime > 0) {
                model.put("idleTime", sendTime / 1000);
            }
        }

        model.put("idleButton", "[ab_toggle='subForm']");
    }

    public static int getRegisterType(int type) {
        if (!Pag.CONFIGURE.hasAllowUserRegister()) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        //默认注册类型
        if (type == 0) {
            type = Pag.CONFIGURE.getDefaultRegisterType();
        }

        if (type == 1) {
            if (!Pag.CONFIGURE.isAllowUsernameRegister()) {
                type = 2;
            }

        } else if (type == 3) {
            if (!Pag.CONFIGURE.hasAllowMessageRegister()) {
                type = 2;
            }

        } else {
            type = 2;
        }

        if (type == 2 && !Pag.CONFIGURE.hasAllowEmailRegister()) {
            if (Pag.CONFIGURE.hasAllowMessageRegister()) {
                type = 3;

            } else if (Pag.CONFIGURE.isAllowUsernameRegister()) {
                type = 1;

            } else {
                throw new ServerException(ServerStatus.ON_DENIED);
            }
        }

        return type;
    }

    /*
    * 获取验证等级
    */
    public static int getVerifyLevel(int level, JUser user) {
        if (level == 3 && KernelString.isEmpty(user.getMobile())) {
            level = 2;
        }

        if (level == 2 && KernelString.isEmpty(user.getEmail())) {
            level = 1;
        }

        return level;
    }

    public void throwExceptionMessage(String message, boolean langCode, Input input) {
        if (message != null) {
            input.getModel().put("message", langCode ? input.getLangMessage(message) : message);
        }

        throw new ServerException(ServerStatus.IN_FAILED);
    }

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
     * -1验证码不存在 -2验证码过期 -3验证码错误 -4 等级太低 0正常
     */
    @Transaction(readOnly = true)
    public int verifyId(String id, String tag, String code, int level, boolean delete) {
        Session session = BeanDao.getSession();
        JVerifier verifier = BeanDao.loadReal(session, JVerifier.class, id, LockMode.PESSIMISTIC_WRITE);
        if (verifier == null || !KernelObject.equals(verifier.getTag(), tag)) {
            return -1;
        }

        long contextTime = ContextUtils.getContextTime();
        if (verifier.getPassTime() != 0 && verifier.getPassTime() < contextTime) {
            return -2;
        }

        if (!KernelObject.equals(verifier.getValue(), code)) {
            return -3;
        }

        if (level > 0) {
            if (verifier.getIntValue() < level) {
                return -4;
            }
        }

        if (delete) {
            BeanService.ME.delete(verifier);
        }

        return 0;
    }

    @Transaction(readOnly = true)
    public int verifyEmailOrMobile(String emailOrMobile, String tag, String code, int level, boolean delete) {
        return verifyId(emailOrMobile + "@Code", tag, code, level, delete);
    }

    public boolean setOperationVerify(String address, String tag, Input input) {
        JSiteConfigure.OperationVerify verify = Pag.CONFIGURE.getOperationVerify(tag);
        if (VerifierService.isOperationCount(input.getAddress(), verify == null ? "" : verify.tag, verify == null ? Pag.CONFIGURE.getOperationVerifyCount() : verify.maxCount)) {
            if (input != null) {
                input.getModel().put("verify", true);
            }

            return true;
        }

        return false;
    }

    public boolean doneOperationVerify(String address, String tag, Input input) {
        JSiteConfigure.OperationVerify verify = Pag.CONFIGURE.getOperationVerify(tag);
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

    public void sendRegisterCode(int type, Input input) {
        type = getRegisterType(type);
        if (!(type == 2 || type == 3)) {
            throw new ServerException(ServerStatus.ON_DENIED);
        }

        String emailOrMobile = ME.getEmailOrMobile(type, input);
        if (type == 2) {
            if (ME.findUser(emailOrMobile, type) != null) {
                InvokerResolverErrors.onError("email", Site.EMAIL_REGISTERED, null, null);
            }

        } else {
            if (ME.findUser(emailOrMobile, type) != null) {
                InvokerResolverErrors.onError("mobile", Site.MOBILE_REGISTERED, null, null);
            }
        }

        ME.sendEmailOrMobileCode(type, emailOrMobile, REGISTER_TAG, Site.REGISTER_OPERATION, input);
    }

    public void register(int type, String securityName, Input input) {
        if (type == 1) {
            if (!Asset_verify.verifyInput(input)) {
                InvokerResolverErrors.onError("verifyCode", Site.VERIFY_ERROR, null, null);
            }
        }

        BinderData binderData = input.getBinderData();
        binderData.getBinderResult().setValidation(true);
        FRegister register = binderData.bind(input.getParamMap(), null, FRegister.class);
        JUser user = new JUser();
        if (type == 1) {
            FUsername username = binderData.bind(input.getParamMap(), null, FUsername.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            user.setUsername(username.username);


        } else if (type == 2) {
            FEmailCode emailCode = binderData.bind(input.getParamMap(), null, FEmailCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            if (ME.verifyEmailOrMobile(emailCode.email, REGISTER_TAG, emailCode.code, 0, true) != 0) {
                InvokerResolverErrors.onError("code", Site.VERIFY_ERROR, null, null);
            }

            user.setEmail(emailCode.email);

        } else {
            FMobileCode mobileCode = binderData.bind(input.getParamMap(), null, FMobileCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            if (ME.verifyEmailOrMobile(mobileCode.mobile, REGISTER_TAG, mobileCode.code, 0, true) != 0) {
                InvokerResolverErrors.onError("code", Site.VERIFY_ERROR, null, null);
            }

            user.setMobile(mobileCode.mobile);
        }

        user.setCreateTime(ContextUtils.getContextTime());
        user.setPasswordBase(register.password);
        user.setUserType(type == 1 ? JeUserType.USER_VALIDATING : JeUserType.USER_NORMAL);
        user.setActivation(!Pag.CONFIGURE.isRegisterUserNoActive());
        try {
            CrudServiceUtils.merge("JUser", null, user, true, null, null);

        } catch (ConstraintViolationException e) {
            if (type == 1) {
                InvokerResolverErrors.onError("username", Site.USERNAME_REGISTERED, null, null);

            } else if (type == 2) {
                InvokerResolverErrors.onError("email", Site.EMAIL_REGISTERED, null, null);

            } else {
                InvokerResolverErrors.onError("mobile", Site.MOBILE_REGISTERED, null, null);
            }
        }

        if (securityName != null) {
            SecurityService.ME.loginUser(securityName, user, input);
        }
    }

    public void password(JUser user, Input input) {
        BinderData binderData = input.getBinderData();
        binderData.getBinderResult().setValidation(true);
        String oldPassword = input.getParam("oldPassword");
        if (KernelString.isEmpty(oldPassword)) {
            binderData.getBinderResult().addPropertyError("oldPassword", input.getLangMessage(ValidatorNotEmpty.NOT_EMPTY), null);
        }

        FRegister register = binderData.bind(input.getParamMap(), null, FRegister.class);
        InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
        ME.doneOperationVerify(input.getAddress(), PASSWORD_TAG, input);

        if (!PasswordCrudFactory.getPasswordEncrypt(oldPassword, user.getSalt(), user.getSaltCount()).equals(user.getPassword())) {
            InvokerResolverErrors.onError("oldPassword", Site.PASSWORD_ERROR, null, null);
        }

        user.setPasswordBase(register.password);
        CrudServiceUtils.merge("JUser", null, user, false, null, null);
    }

    public String getEmailOrMobile(int type, Input input) {
        BinderData binderData = input.getBinderData();
        binderData.getBinderResult().setValidation(true);
        binderData.getBinderResult().getPropertyFilter().exclude("code");
        if (type == 2) {
            FEmailCode emailCode = binderData.bind(input.getParamMap(), null, FEmailCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            return emailCode.email;

        } else {
            FMobileCode mobileCode = binderData.bind(input.getParamMap(), null, FMobileCode.class);
            InvokerResolverErrors.checkError(binderData.getBinderResult(), null);
            return mobileCode.mobile;
        }
    }

    public void sendEmailOrMobileCode(int type, String emailOrMobile, String tag, String operation, Input input) {
        long idleTime;
        long sendTime;
        if (type == 2) {
            ME.doneOperationVerify(input.getAddress(), EMAIL_TAG, input);
            idleTime = Pag.CONFIGURE.getEmailIdleTime();
            sendTime = ME.sendEmailCode(emailOrMobile, tag, Site.TPL.getCodeEmailSubject(), Site.TPL.getCodeEmail(), idleTime, operation, input);

        } else {
            ME.doneOperationVerify(input.getAddress(), MESSAGE_TAG, input);
            idleTime = Pag.CONFIGURE.getMessageIdleTime();
            sendTime = ME.sendMessageCode(emailOrMobile, tag, Site.TPL.getCodeMessage(), idleTime, operation, input);
        }

        resolverIdleTime(idleTime, sendTime, input);
    }

    /**
     * 需要用户登录
     */
    public JUser verifyUser(Input input) {
        JiUserBase userBase = SecurityService.ME.getUserBase(input);
        if (userBase == null || !(userBase instanceof JUser)) {
            ME.throwExceptionMessage(Site.LOGIN_FAILURE, true, input);
        }

        return (JUser) userBase;
    }

    /**
     * 需要用户验证确认
     */
    public void verifyUser(int level, String tag, Input input) {
        JUser user = verifyUser(input);
        String verifyUser = input.getParam("_verifyUser");
        if (KernelString.isEmpty(verifyUser) || ME.verifyId("verifyUser@" + user.getId(), tag, verifyUser, level, true) != 0) {
            InModel model = input.getModel();
            model.put("verifyUrl", MenuContextUtils.getSiteRoute() + "user/verify/" + level + "?tag=" + tag);
            throw new ServerException(ServerStatus.ON_SUCCESS);
        }
    }

    /**
     * 设置用户验证
     */
    public JVerifier setVerifyUser(Long userId, String tag, int level) {
        JVerifier verifier = VerifierService.createVerifier("verifyUser@" + userId, tag, randomCode(), level, 600000);
        BeanService.ME.merge(verifier);
        return verifier;
    }

    public void sendVerifyCode(JUser user, int level, String tag, int type, Input input) {
        if (type == 3 && (!Pag.CONFIGURE.hasMessage() || KernelString.isEmpty(user.getMobile()))) {
            InvokerResolverErrors.onError("type", Site.COULD_NOT_USE, null, null);

        } else if (type == 2 && (!Pag.CONFIGURE.hasEmail() || KernelString.isEmpty(user.getEmail()))) {
            InvokerResolverErrors.onError("type", Site.COULD_NOT_USE, null, null);

        } else {
            InvokerResolverErrors.onError("type", Site.COULD_NOT_USE, null, null);
        }

        level = getVerifyLevel(level, user);
        if (type < level) {
            InvokerResolverErrors.onError("type", Site.COULD_NOT_USE, null, null);
        }

        sendEmailOrMobileCode(type, type == 2 ? user.getEmail() : user.getMobile(), tag, getOperation(tag, input), input);
    }

    /**
     * 验证操作验证
     */
    public void verifyLevel(JUser user, int level, String tag, int type, String value, Input input) {
        if (KernelString.isEmpty(value)) {
            InvokerResolverErrors.onError("value", ValidatorNotEmpty.NOT_EMPTY, null, null);
        }

        if (type == 1) {
            if (!Asset_verify.verifyInput(input)) {
                InvokerResolverErrors.onError("verifyCode", Site.VERIFY_ERROR, null, null);
            }

            if (!PasswordCrudFactory.getPasswordEncrypt(value, user.getSalt(), user.getSaltCount()).equals(user.getPassword())) {
                InvokerResolverErrors.onError("value", Site.PASSWORD_ERROR, null, null);
            }

        } else {
            String emailOrMobile = type == 2 ? user.getEmail() : user.getMobile();
            if (ME.verifyEmailOrMobile(emailOrMobile, tag, value, 0, false) != 0) {
                InvokerResolverErrors.onError("code", Site.VERIFY_ERROR, null, null);
            }
        }

        InModel model = input.getModel();
        model.put("ok", 1);
        model.put("verifies", KernelMap.newMap("_verifyUser", ME.setVerifyUser(user.getId(), tag, level).getValue()));
    }

    public void username(JUser user, String username, Input input) {
        if (!KernelString.isEmpty(user.getUsername())) {
            ME.throwExceptionMessage(Site.USERNAME_HAD, true, input);
        }

        user.setUsername(username);
        try {
            CrudServiceUtils.merge("JUser", null, user, true, null, null);

        } catch (ConstraintViolationException e) {
            InvokerResolverErrors.onError("username", Site.USERNAME_REGISTERED, null, null);
        }
    }

    public void sendOperationCode(int type, int level, String tag, Input input) {
        JUser user = verifyUser(input);
        level = getVerifyLevel(level, user);
        String emailOrMobile = ME.getEmailOrMobile(type, input);
        verifyUser(level, tag, input);
        ME.sendEmailOrMobileCode(type, emailOrMobile, REGISTER_TAG, Site.REGISTER_OPERATION, input);
    }

    public static class FUsername {

        @JaLang("用户名")
        @NotEmpty
        @Regex(value = "^[^\\d@][^@]{4,16}$", lang = "请输入首位不是数字,不含有@的4-16位字符")
        public String username;
    }

    public static class FRegister {

        @JaLang("密码")
        @JaEdit(types = "passwordType")
        @NotEmpty
        @Length(min = 6, max = 16)
        public String password;

        @JaLang("确认密码")
        @JaEdit(types = "passwordType")
        @NotEmpty
        @Confirm("newPassword")
        public String confirmPassword;
    }

}
