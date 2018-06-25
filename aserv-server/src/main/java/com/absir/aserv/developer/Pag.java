/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月30日 上午10:49:12
 */
package com.absir.aserv.developer;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.configure.JConfigureSupply;
import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.menu.OMenuBean;
import com.absir.aserv.support.DeveloperBreak;
import com.absir.aserv.support.developer.IDeveloper;
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.RenderUtils;
import com.absir.aserv.system.asset.Asset_verify;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeVotePermission;
import com.absir.aserv.system.configure.JSiteConfigure;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.helper.HelperLang;
import com.absir.aserv.system.helper.HelperLong;
import com.absir.aserv.system.server.ServerDiyView;
import com.absir.aserv.system.service.AuthService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.client.helper.HelperJson;
import com.absir.context.lang.LangBundle;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JePermission;
import com.absir.orm.value.JoEntity;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.servlet.InDispatchFilter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SuppressWarnings("unchecked")
@Inject
public class Pag {

    public static final JSiteConfigure CONFIGURE = JConfigureUtils.getConfigure(JSiteConfigure.class);

    private static final IPagLang PAG_LANG = BeanFactoryUtils.get(IPagLang.class);

    private static final String NAME_TAG = Pag.class.getName() + "@NAME_TAG";

    private static final String NAME_TAGS = Pag.class.getName() + "@NAME_TAGS";

    public static final AuthService AUTH = AuthService.ME;

    public static boolean rolePermissions(JiUserBase user) {
        return AUTH.permissions("JMenuPermission", user, JeVotePermission.INSERTABLE, JeVotePermission.UPDATEABLE, JeVotePermission.DELETEABLE)
                && AUTH.permissions("JMaMenu", user, JeVotePermission.INSERTABLE, JeVotePermission.UPDATEABLE, JeVotePermission.DELETEABLE);
    }

    private static Map<String, Object> forEntity;

    private static Map<String, Object> forEntityMap;

    private static Map<String, Object> forEntityMapValue;

    static {
        forEntity = new HashMap<String, Object>();
        forEntityMap = new HashMap<String, Object>();
        forEntityMapValue = new HashMap<String, Object>();
        //forEntityMap.put("key", "#for_key#");
        //forEntityMapValue.put("key", "#for_key#");
        forEntityMapValue.put("value", new HashMap<String, Object>());
    }

    public static void devI(int devTime) {
        if (IDeveloper.ME != null) {
            int newTime = IDeveloper.ME.getDeveloperNewTime();
            if (newTime > 0) {
                if (devTime < newTime) {
                    throw new DeveloperBreak();
                }
            }
        }
    }

    public static void dev(long devTime) {
        if (IDeveloper.ME != null) {
            devI(UtilAbsir.shortTime(devTime));
        }
    }

    public static String treeName(int depth, Object name) {
        if (depth <= 0) {
            return String.valueOf(name);
        }


        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < depth; i++) {
            //stringBuilder.append('─');
            stringBuilder.append('　');
        }

        stringBuilder.append('└');
        stringBuilder.append(name);
        return stringBuilder.toString();
    }

    public static boolean isDebug() {
        return BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0;
    }

    public static void println(Object value) {
        System.out.println(value);
    }

    public static Input getInput() {
        OnPut onPut = OnPut.get();
        return onPut == null ? null : onPut.getInput();
    }

    public static Input getInput(ServletRequest request) {
        return InDispatchFilter.getInput(request);
    }

    public static boolean isI18n() {
        return LangBundle.ME == null ? false : LangBundle.isI18n();
    }

    public static Locale locale() {
        return LangBundle.isI18n() ? LangBundle.ME.getLocale() : getInput().getLocale();
    }

    public static String lang(String code) {
        Input input = getInput();
        return input == null ? code : input.getLang(code);
    }

    public static String lang(String code, ServletRequest request) {
        return lang(code);
    }

    public static String getLang(String lang) {
        return getLang(lang, true);
    }

    public static String getLang(String lang, boolean echo) {
        return getLang(HelperLang.getLangCode(lang), lang, echo);
    }

    public static String getLang(String code, String lang, boolean echo) {
        if (LangBundle.isI18n()) {
            LangBundle.ME.setResourceLang(code, lang);
        }

        return getLangRequest(code, lang, echo);
    }

    public static String getCaptionLang(String caption) {
        return getCaptionLang(caption, true);
    }

    public static String getCaptionLang(String caption, boolean echo) {
        return getLangRequest(caption, caption, echo);
    }

    protected static String getLangRequest(String code, String lang, boolean echo) {
        if (LangBundle.isI18n()) {
            code = KernelString.transferred(code);
            code = PAG_LANG == null ? "Pag.lang(" + code + ")" : PAG_LANG.getPagLang(code);
            return echo ? IRender.ME.echo(code) : code;

        } else {
            return echo ? lang : KernelString.transferred(lang);
        }
    }

    public static JSiteConfigure configure() {
        return CONFIGURE;
    }

    public static JConfigureBase configure(String name) {
        return (JConfigureBase) JConfigureSupply.ME.create(name);
    }

    public static <T extends JConfigureBase> T getConfigure(Class<T> cls) {
        return JConfigureUtils.getConfigure(cls);
    }

    public static List<OMenuBean> menu(String name) {
        return MenuContextUtils.getMenuBeans(name);
    }

    public static void _include(String include) throws IOException {
        _include(include, include);
    }

    public static void _include(String include, String includeGen) throws IOException {
        _include(include, includeGen, null);
    }

    public static void _include(String include, String includeGen, String entityAtt) throws IOException {
        Input input = OnPut.get().getInput();
        Object[] renders = ServerDiyView.getRenders(OnPut.get().getInput());
        try {
            IRender.ME.include(includeGen, renders);
            return;

        } catch (Exception e) {
            if (!ServerDiyView.ME.isDeveloperNotExist(e, input)) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                }

                throw new IOException(e);
            }

            if (entityAtt != null) {
                Object entity = input.getModel().get(entityAtt);
                if (entity != null) {

                }
            }

            RenderUtils.generate(include, includeGen);
        }

        IRender.ME.include(includeGen, renders);
    }

    public static String include(String include) throws IOException {
        return getInclude(include, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    public static String include(String include, String includeGen) throws IOException {
        return getIncludeGen(include, includeGen, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    public static String getInclude(String include, Object... renders) throws IOException {
        return getIncludeGen(include, include, renders);
    }

    public static String getIncludeGen(String include, String includeGen, Object... renders) throws IOException {
        RenderUtils.generate(include, includeGen, renders);
        return IRender.ME.include(include);
    }

    public static void includeGen(String include) throws IOException {
        includeGen(include, include);
    }

    public static void includeGen(String include, String includeGen) throws IOException {
        RenderUtils.generate(include, includeGen, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    public static void generateTpl(String include, String tpl) throws IOException {
        RenderUtils.generateTpl(include, tpl, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    public static String transferred(String str) {
        return KernelString.transferred(str);
    }

    public static String value(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String dateValue(Object obj) {
        return dateValue(obj, 0);
    }

    public static String dateValue(Object obj, int type) {
        Date date = KernelDyna.toDate(obj);
        if (type >= 0) {
            if (date == null) {
                date = new Date();
            }

        } else {
            type = -type - 1;
        }

        return value(KernelDyna.toString(date, type));
    }

    public static String dateFormat(Object obj, String dateFormat) {
        Date date = KernelDyna.toDate(obj);
        return date == null ? "" : KernelDyna.getSimpleDateFormat(dateFormat).format(date);
    }

    public static String ipValue(Object obj) {
        long ip = KernelDyna.to(obj, long.class);
        return HelperLong.longIPV4(ip);
    }

    public static String enumValue(Object obj) {
        if (obj != null) {
            if (obj.getClass().isEnum()) {
                return ((Enum<?>) obj).name();
            }
        }

        return value(obj);
    }

    public static String enumLang(Object obj) {
        if (obj != null) {
            if (obj.getClass().isEnum()) {
                Enum<?> e = (Enum<?>) obj;
                return captionLang(CrudUtils.getEnumMetaMap(e.getClass()).get(e.name()));
            }
        }

        return value(obj);
    }

    public static String captionLang(String caption) {
        return isI18n() ? lang(caption) : caption;
    }

    public static String param(Object obj) {
        return DynaBinder.to(obj, String.class);
    }

    public static Object[] params(Object obj) {
        return params(obj, false);
    }

    public static Object[] params(Object obj, boolean serializable) {
        return obj == null ? null : DynaBinder.to(obj, serializable ? String[].class : Object[].class);
    }

    public static boolean isParams(Object[] params, Object param) {
        return params != null && KernelArray.contain(params, param);
    }

    public static String paramsValue(Object obj) {
        if (obj != null) {
            if (obj.getClass().isArray()) {
                return KernelString.implode(DynaBinder.to(obj, Object[].class), ',');

            } else if (obj instanceof Collection) {
                return KernelString.implode((Collection) obj, ',');
            }
        }

        return value(obj);
    }

    public static String paramsTextValue(Object obj) {
        if (obj != null) {
            if (obj.getClass().isArray()) {
                return KernelString.implode(DynaBinder.to(obj, Object[].class), "\r\n");

            } else if (obj instanceof Collection) {
                return KernelString.implode((Collection) obj, "\r\n");
            }
        }

        return value(obj);
    }

    public static Map<?, ?> mapValue(Object obj) {
        if (obj != null) {
            if (obj instanceof Collection) {
                return KernelCollection.toMap((Collection<?>) obj);

            } else if (obj instanceof Map) {
                return (Map<?, ?>) obj;
            }
        }

        return null;
    }

    public static Map<String, Object> getNameTag(Input input, String name) {
        Object nameTag = input.getModel().get(NAME_TAG);
        if (name != null) {
            if (nameTag == null || !(nameTag instanceof Map)) {
                nameTag = null;

            } else {
                nameTag = ((Map<String, Object>) nameTag).get(name);
            }
        }

        return nameTag == null || !(nameTag instanceof Map) ? null : (Map<String, Object>) nameTag;
    }

    public static void setNameTag(Input input, String name, String value) {
        setNameTag(input, name, value, value);
    }

    public static void setNameTag(Input input, String name, String tag, String value) {
        Object nameTag;
        Map<String, Object> nameTagMap;
        nameTag = input.getModel().get(NAME_TAG);
        if (nameTag == null || !(nameTag instanceof Map)) {
            nameTagMap = new HashMap<String, Object>();
            input.getModel().put(NAME_TAG, nameTagMap);

        } else {
            nameTagMap = (Map<String, Object>) nameTag;
        }

        if (name != null) {
            nameTag = nameTagMap.get(name);
            if (nameTag == null || !(nameTag instanceof Map)) {
                nameTag = new LinkedHashMap<String, Object>();
                nameTagMap.put(name, nameTag);
            }

            nameTagMap = (Map<String, Object>) nameTag;
        }

        nameTagMap.put(tag, value);
    }

    public static String getForIndex() {
        return "#for_index#";
    }

    public static Map<String, Object> getForEntityMap(int map) {
        switch (map) {
            case 1:
                return forEntity;
            case 2:
                return forEntityMap;
            case 3:
                return forEntityMapValue;
            default:
                return null;
        }
    }

    public static boolean isEmptyFieldGroup(JoEntity entity, String group) {
        String[] fields = CrudUtils.getGroupFields(entity, group);
        return fields == null || fields.length == 0;
    }

    public static String[] getEntityGroupField(JoEntity entity, String group) {
        return CrudUtils.getGroupFields(entity, group);
    }

    public static String getPath(String path) {
        if (KernelString.isEmpty(path)) {
            return path;
        }

        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }

        return HelperFileName.getPath(path);
    }

    public static String uploadUrl(String path) {
        return UploadCrudFactory.ME.getUploadUrl(path);
    }

    public static boolean isEmptyUpload(String path) {
        return UploadCrudFactory.ME.isEmpty(path);
    }

    public static void forEntity(HttpServletRequest request) {
        request.setAttribute("entity", forEntity);
    }

    public static String verifyShow(String attrs, int width, int height, Input input) {
        return Asset_verify.ME.show(attrs, width, height, input);
    }

    public static String hiddenEmail(String email) {
        if (KernelString.isEmpty(email)) {
            return email;
        }

        return KernelString.hiddenString(2, email.indexOf('@') - 1, email);
    }

    public static String hiddenMobile(String mobile) {
        if (KernelString.isEmpty(mobile)) {
            return mobile;
        }

        return KernelString.hiddenString(mobile.length() - 8, 2, mobile);
    }

    public static UploadCrudFactory.MultipartUploader getUploader(Object[] params) {
        return UploadCrudFactory.getMultipartUploader(params);
    }

    public static String getUploaderExtensions(Object[] params) {
        UploadCrudFactory.MultipartUploader uploader = getUploader(params);
        return uploader == null || uploader.getExtensions() == null || uploader.getExtensions().length == 0 ? null : KernelString.implode(uploader.getExtensions(), ',');
    }

    public static String json(Object obj) throws IOException {
        return HelperJson.encode(obj);
    }

    public static boolean suggest(String entityName) {
        return SessionFactoryUtils.entityPermission(entityName, JePermission.SUGGEST);
    }

    public static String iencrypt(HttpServletRequest request) throws NoSuchAlgorithmException {
        return SecurityService.ME.getIEncryptKey(request, true);
    }

    public static interface IPagLang {

        public String getPagLang(String transferredName);
    }
}
