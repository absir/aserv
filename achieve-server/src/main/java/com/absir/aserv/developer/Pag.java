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
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.RenderUtils;
import com.absir.aserv.system.configure.JSiteConfigure;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.helper.HelperLang;
import com.absir.aserv.system.helper.HelperLong;
import com.absir.aserv.system.server.ServerDiyView;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.lang.LangBundle;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.orm.value.JoEntity;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.servlet.InDispathFilter;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
@Inject
public class Pag {

    /**
     * CONFIGURE
     */
    public static final JSiteConfigure CONFIGURE = JConfigureUtils.getConfigure(JSiteConfigure.class);
    /**
     * PAG_LANG
     */
    private static final IPagLang PAG_LANG = BeanFactoryUtils.get(IPagLang.class);
    /**
     * NAME_TAG
     */
    private static final String NAME_TAG = Pag.class.getName() + "@NAME_TAG";
    /**
     * NAME_TAGS
     */
    private static final String NAME_TAGS = Pag.class.getName() + "@NAME_TAGS";
    /**
     * forEntity
     */
    private static Map<String, Object> forEntity;

    /**
     * forEntityMap
     */
    private static Map<String, Object> forEntityMap;

    /**
     * forEntityMap
     */
    private static Map<String, Object> forEntityMapValue;

    static {
        forEntity = new HashMap<String, Object>();
        forEntityMap = new HashMap<String, Object>();
        forEntityMapValue = new HashMap<String, Object>();
        //forEntityMap.put("key", "#for_key#");
        //forEntityMapValue.put("key", "#for_key#");
        forEntityMapValue.put("value", new HashMap<String, Object>());
    }

    /**
     * @return
     */
    public static boolean isDebug() {
        return BeanFactoryUtils.getEnvironment().compareTo(Environment.DEBUG) <= 0;
    }

    /**
     * @return
     */
    public static Input getInput() {
        OnPut onPut = OnPut.get();
        return onPut == null ? null : onPut.getInput();
    }

    /**
     * @param request
     * @return
     */
    public static Input getInput(ServletRequest request) {
        return InDispathFilter.getInput(request);
    }

    /**
     * @return
     */
    public static boolean isI18n() {
        return LangBundle.ME == null ? false : LangBundle.isI18n();
    }

    /**
     * @return
     */
    public static Locale locale() {
        return LangBundle.isI18n() ? LangBundle.ME.getLocale() : getInput().getLocale();
    }

    /**
     * @param name
     * @return
     */
    public static String lang(String name) {
        Input input = getInput();
        return input == null ? name : input.getLang(name);
    }

    /**
     * @param name
     * @param request
     * @return
     */
    public static String lang(String name, ServletRequest request) {
        return lang(name);
    }

    /**
     * @param lang
     * @return
     */
    public static String getLang(String lang) {
        return getLang(lang, true);
    }

    /**
     * @param lang
     * @return
     */
    public static String getLang(String lang, boolean echo) {
        return getLang(HelperLang.getCaptionLang(lang), lang, echo);
    }

    /**
     * @param lang
     * @param lang
     * @param echo
     * @return
     */
    public static String getLang(String name, String lang, boolean echo) {
        LangBundle.ME.setResourceLang(name, lang);
        return getLangRequest(name, lang, echo);
    }

    /**
     * @param name
     * @return
     */
    public static String getLangName(String name) {
        return getLangName(name, true);
    }

    /**
     * @param name
     * @param echo
     * @return
     */
    public static String getLangName(String name, boolean echo) {
        return getLangRequest(name, lang(name), echo);
    }

    /**
     * @param name
     * @param lang
     * @param echo
     * @return
     */
    protected static String getLangRequest(String name, String lang, boolean echo) {
        if (LangBundle.isI18n()) {
            name = KernelString.transferred(name);
            name = PAG_LANG == null ? "Pag.lang(" + name + ")" : PAG_LANG.getPagLang(name);
            return echo ? IRender.ME.echo(name) : name;

        } else {
            return echo ? lang : KernelString.transferred(lang);
        }
    }

    /**
     * @return
     */
    public static JSiteConfigure configure() {
        return CONFIGURE;
    }

    /**
     * @param name
     * @return
     */
    public static JConfigureBase configure(String name) {
        return (JConfigureBase) JConfigureSupply.ME.create(name);
    }

    /**
     * @param cls
     * @return
     */
    public static <T extends JConfigureBase> T getConfigure(Class<T> cls) {
        return JConfigureUtils.getConfigure(cls);
    }

    /**
     * @param name
     * @return
     */
    public static List<OMenuBean> menu(String name) {
        return MenuContextUtils.getMenuBeans(name);
    }

    /**
     * @param include
     * @return
     * @throws IOException
     */
    public static String include(String include) throws IOException {
        return getInclude(include, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    /**
     * @param generate
     * @param include
     * @return
     * @throws IOException
     */
    public static String include(String generate, String include) throws IOException {
        return getIncludeGen(generate, include, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    /**
     * @param include
     * @param renders
     * @return
     * @throws IOException
     */
    public static String getInclude(String include, Object... renders) throws IOException {
        return getIncludeGen(include, include, renders);
    }

    /**
     * @param generate
     * @param include
     * @param renders
     * @return
     * @throws IOException
     */
    public static String getIncludeGen(String generate, String include, Object... renders) throws IOException {
        RenderUtils.generate(generate, include, renders);
        return IRender.ME.include(generate);
    }

    /**
     * @param generate
     * @throws IOException
     */
    public static void includeGen(String generate) throws IOException {
        includeGen(generate, generate);
    }

    /**
     * @param generate
     * @param include
     * @return
     * @throws IOException
     */
    public static void includeGen(String generate, String include) throws IOException {
        RenderUtils.generate(generate, include, ServerDiyView.getRenders(OnPut.get().getInput()));
    }

    /**
     * @param obj
     * @return
     */
    public static String value(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * @param obj
     * @return
     */
    public static String dateValue(Object obj) {
        return dateValue(obj, 0);
    }

    /**
     * @param obj
     * @param type
     * @return
     */
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

    /**
     * @param obj
     * @return
     */
    public static String ipValue(Object obj) {
        long ip = KernelDyna.to(obj, long.class);
        return HelperLong.longIPV4(ip);
    }

    /**
     * @param obj
     * @return
     */
    public static String enumValue(Object obj) {
        if (obj != null) {
            if (obj.getClass().isEnum()) {
                return ((Enum<?>) obj).name();
            }
        }

        return value(obj);
    }

    /**
     * @param obj
     * @return
     */
    public static String enumLang(Object obj) {
        if (obj != null) {
            if (obj.getClass().isEnum()) {
                Enum<?> e = (Enum<?>) obj;
                return namesLang(CrudUtils.getEnumMetaMap(e.getClass()).get(e.name()));
            }
        }

        return value(obj);
    }

    /**
     * @param names
     * @return
     */
    public static String namesLang(String[] names) {
        return isI18n() || names.length < 2 ? lang(names[0]) : names[1];
    }

    /**
     * @param obj
     * @return
     */
    public static String param(Object obj) {
        return DynaBinder.to(obj, String.class);
    }

    /**
     * @param obj
     * @return
     */
    public static Object[] params(Object obj) {
        return params(obj, false);
    }

    /**
     * @param obj
     * @param serializable
     * @return
     */
    public static Object[] params(Object obj, boolean serializable) {
        return obj == null ? null : DynaBinder.to(obj, serializable ? String[].class : Object[].class);
    }

    /**
     * @param params
     * @param param
     * @return
     */
    public static boolean isParams(Object[] params, Object param) {
        return params != null && KernelArray.contain(params, param);
    }

    /**
     * @param obj
     * @return
     */
    public static String paramsValue(Object obj) {
        if (obj != null) {
            if (obj.getClass().isArray()) {
                return KernelString.implode(DynaBinder.to(obj, Object[].class), ",");
            }
        }

        return value(obj);
    }

    /**
     * @param obj
     * @return
     */
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

    /**
     * @param input
     * @param tag
     * @return
     */
    public static Map<String, Object> getNameTag(Input input, String tag) {
        Object nameTag;
        if (tag == null) {
            nameTag = input.getModel().get(NAME_TAG);

        } else {
            nameTag = input.getModel().get(NAME_TAGS);
            if (nameTag != null) {
                nameTag = nameTag instanceof Map ? ((Map<String, Object>) nameTag).get(tag) : null;
            }
        }

        return nameTag == null || !(nameTag instanceof Map) ? null : (Map<String, Object>) nameTag;
    }

    /**
     * @param input
     * @param name
     * @param tag
     * @param value
     */
    public static void setNameTag(Input input, String name, String tag, String value) {
        Object nameTag;
        Map<String, Object> nameTagMap;
        if (tag == null) {
            nameTag = input.getModel().get(NAME_TAG);
            if (nameTag == null || !(nameTag instanceof Map)) {
                nameTagMap = new LinkedHashMap<String, Object>();

            } else {
                nameTagMap = (Map<String, Object>) nameTag;
                input.getModel().put(NAME_TAG, nameTagMap);
            }

        } else {
            nameTag = input.getModel().get(NAME_TAGS);
            if (nameTag == null || !(nameTag instanceof Map)) {
                nameTagMap = new HashMap<String, Object>();

            } else {
                nameTagMap = (Map<String, Object>) nameTag;
                input.getModel().put(NAME_TAGS, nameTagMap);
            }

            if (nameTag == nameTagMap) {
                nameTag = nameTagMap.get(tag);
                if (nameTag != null && nameTag instanceof Map) {
                    nameTagMap = (Map<String, Object>) nameTag;
                    nameTag = null;
                }
            }

            if (nameTag != null) {
                nameTag = new LinkedHashMap<String, Object>();
                nameTagMap.put(tag, nameTag);
                nameTagMap = (Map<String, Object>) nameTag;
                nameTag = null;
            }
        }

        if (nameTag != nameTagMap || !nameTagMap.containsKey(name)) {
            nameTagMap.put(name, value);
        }
    }

    /**
     * @return
     */
    public static String getForIndex() {
        return "#for_index#";
    }


    /**
     * @param map
     * @return
     */
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

    /**
     * @param entity
     * @param group
     * @return
     */
    public static boolean isEmptyFieldGroup(JoEntity entity, String group) {
        String[] fields = CrudUtils.getGroupFields(entity, group);
        return fields == null || fields.length == 0;
    }

    /**
     * @param entity
     * @param group
     * @return
     */
    public static String[] getEntityGroupField(JoEntity entity, String group) {
        return CrudUtils.getGroupFields(entity, group);
    }


    /**
     * @param path
     * @return
     */
    public static String getPath(String path) {
        if (KernelString.isEmpty(path)) {
            return path;
        }

        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }

        return HelperFileName.getPath(path);
    }

    /**
     * @param path
     * @return
     */
    public static String uploadUrl(String path) {
        return UploadCrudFactory.getUploadUrl() + path;
    }

    /**
     * @param path
     * @return
     */
    public static boolean isEmptyUpload(String path) {
        return UploadCrudFactory.ME.isEmpty(path);
    }

    /**
     * @author absir
     */
    public static interface IPagLang {

        /**
         * @param transferredName
         * @return
         */
        public String getPagLang(String transferredName);
    }
}
