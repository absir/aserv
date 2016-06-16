/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月16日 上午10:38:20
 */
package com.absir.context.lang;

import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.cron.CronFixDelayRunnable;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelMap;
import com.absir.core.kernel.KernelString;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
@Base
@Bean
public class LangBundle {

    public static final LangBundle ME = BeanFactoryUtils.get(LangBundle.class);

    private static boolean i18n;

    private static boolean strict;

    @Value(value = "lang.resouce", defaultValue = "${classPath}lang/")
    protected String langResource;

    protected long reloadTime;

    protected CronFixDelayRunnable reloadRunnable;

    protected Locale locale;

    protected Integer localeCode;

    protected Map<Integer, Locale> codeMaplocale;

    protected Map<String, String> resourceBundle;

    protected Map<String, String> resourceLangs = new HashMap<String, String>();

    protected Map<Locale, Map<String, String>> localeMapResourceBunlde = new HashMap<Locale, Map<String, String>>();

    public static Locale getLocaleFromString(String locale) {
        if (locale != null) {
            String[] locales = locale.split("_");
            locale = locales[0];
            int length = locales.length;
            String country = length > 1 ? locales[1] : "";
            String variant = length > 2 ? locales[2] : "";
            return new Locale(locale, country, variant);
        }

        return null;
    }

    public static boolean isI18n() {
        return i18n;
    }

    public static boolean isStrictTag() {
        return i18n || LangBundle.strict;
    }

    @Inject
    protected void setI18n(@Value(value = "lang.i18n", defaultValue = "0") boolean i18n, @Value(value = "lang.strict", defaultValue = "0") boolean strict) {
        LangBundle.i18n = i18n;
        LangBundle.strict = strict;
    }

    /**
     * 初始化国际化资源
     */
    @Inject
    protected void initBundle() {
        Long reload = BeanFactoryUtils.getBeanConfigValue("lang.reload", Long.class);
        setReloadTime(reload == null ? BeanFactoryUtils.getEnvironment().compareTo(Environment.DEVELOP) <= 0 ? -1 : 0
                : reload);
        locale = getLocaleFromString(BeanFactoryUtils.getBeanConfigValue("lang.locale", String.class));
        if (locale == null) {
            locale = Locale.getDefault();
        }

        String[] locales = BeanFactoryUtils.getBeanConfigValue("lang.locales", String[].class);
        if (locales != null) {
            codeMaplocale = new LinkedHashMap<Integer, Locale>();
            for (String locale : locales) {
                String[] codes = locale.split(":", 2);
                if (codes.length == 2) {
                    codeMaplocale.put(DynaBinder.to(codes[0], Integer.class), getLocaleFromString(codes[1]));
                }
            }

            if (codeMaplocale.isEmpty()) {
                codeMaplocale = null;

            } else {
                localeCode = KernelMap.getKey(codeMaplocale, locale);
                if (localeCode == null) {
                    localeCode = 0;
                    codeMaplocale.put(localeCode, locale);

                } else {
                    if (codeMaplocale.size() == 1) {
                        codeMaplocale = null;
                    }
                }
            }
        }
    }

    /**
     * 内置国际化资源写入
     */
    @Stopping
    public void stopping() {
        if (!resourceLangs.isEmpty()) {
            String var = locale.getLanguage();
            if (!KernelString.isEmpty(var)) {
                String resource = langResource + var;
                var = locale.getCountry();
                if (!KernelString.isEmpty(var)) {
                    resource += '_' + var;
                    var = locale.getVariant();
                    if (!KernelString.isEmpty(var)) {
                        resource += '_' + var;
                    }
                }

                BeanConfigImpl.writeProperties(resourceLangs, new File(resource + "/general.properties"));
            }
        }
    }

    public void setReloadTime(long reloadTime) {
        if (this.reloadTime != reloadTime) {
            this.reloadTime = reloadTime;
            if (reloadTime <= 0) {
                if (reloadRunnable != null) {
                    reloadRunnable.setFixDelay(0);
                    reloadRunnable = null;
                }

            } else {
                reloadRunnable = reloadRunnable == null ? new CronFixDelayRunnable(new Runnable() {

                    @Override
                    public void run() {
                        resourceBundle = null;
                        localeMapResourceBunlde.clear();
                    }

                }, reloadTime) : reloadRunnable.transformCronFixDelayRunnable(reloadTime);
                ContextUtils.getScheduleFactory().addRunnables(reloadRunnable);
            }
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public Locale getLocale(Integer code) {
        if (code != null && codeMaplocale != null) {
            Locale locale = codeMaplocale.get(code);
            if (locale != null) {
                return locale;
            }
        }

        return locale;
    }

    public Integer getLocaleCode() {
        return localeCode;
    }

    public boolean isLocaleCode(Integer code) {
        return code == null || code == 0 || code.equals(localeCode);
    }

    public Integer getLocaleCode(Locale locale) {
        if (locale == null || codeMaplocale == null) {
            return null;
        }

        Integer code = 0;
        float max = 0;
        float similar;
        String localeStr = locale.toString();
        for (Entry<Integer, Locale> entry : codeMaplocale.entrySet()) {
            similar = KernelString.similar(localeStr, entry.getValue().toString());
            if (similar >= 1.0f) {
                return entry.getKey();
            }

            if (similar > max) {
                max = similar;
                code = entry.getKey();
            }
        }

        return code == 0 ? localeCode : code;
    }

    public Map<Integer, Locale> getCodeMaplocale() {
        return codeMaplocale;
    }

    public Map<String, String> getResourceBundle() {
        if (reloadTime < 0) {
            return getResourceBundle(locale);
        }

        if (resourceBundle == null) {
            resourceBundle = getResourceBundle(locale);
        }

        return resourceBundle;
    }

    public void setResourceLang(String name, String lang) {
        Map<String, String> resourceBundle = getResourceBundle();
        if (!resourceBundle.containsKey(name)) {
            resourceLangs.put(name, lang);
            resourceBundle.put(name, lang);
        }
    }

    public Map<String, String> getResourceBundle(Locale locale) {
        Map<String, String> resourceBundle = localeMapResourceBunlde.get(locale);
        if (resourceBundle != null) {
            return resourceBundle;
        }

        String var = locale.getLanguage();
        if (!KernelString.isEmpty(var)) {
            String resource = langResource + var;
            File resourceFile = new File(resource);
            if (resourceFile.exists()) {
                resourceBundle = new HashMap<String, String>();
                BeanConfigImpl.readDirProperties(null, (Map<String, Object>) (Object) resourceBundle, resourceFile,
                        null);
                if (reloadTime >= 0) {
                    localeMapResourceBunlde.put(locale, resourceBundle);
                }

                var = locale.getCountry();
                if (!KernelString.isEmpty(var)) {
                    resource += '_' + var;
                    resourceFile = new File(resource);
                    if (resourceFile.exists()) {
                        BeanConfigImpl.readDirProperties(null, (Map<String, Object>) (Object) resourceBundle,
                                resourceFile, null);
                        var = locale.getVariant();
                        if (!KernelString.isEmpty(var)) {
                            resource += '_' + var;
                            resourceFile = new File(resource);
                            if (resourceFile.exists()) {
                                BeanConfigImpl.readDirProperties(null, (Map<String, Object>) (Object) resourceBundle,
                                        resourceFile, null);
                            }
                        }
                    }
                }
            }
        }

        if (locale == this.locale) {
            if (resourceBundle == null) {
                resourceBundle = new HashMap<String, String>();
            }

            this.resourceBundle = resourceBundle;
            for (Entry<String, String> entry : resourceLangs.entrySet()) {
                if (!resourceBundle.containsKey(entry.getKey())) {
                    resourceBundle.put(entry.getKey(), entry.getValue());
                }
            }

            return resourceBundle;
        }

        if (resourceBundle == null) {
            resourceBundle = getResourceBundle();
        }

        return resourceBundle;
    }

    public void clearResouceBundle() {
        resourceBundle = null;
        localeMapResourceBunlde.clear();
    }

    public String getLangResource(String lang, Locale locale) {
        return getLangResource(lang, getResourceBundle(locale), locale);
    }

    public String getLangResource(String lang, Map<String, String> resourceBundle, Locale locale) {
        String value = resourceBundle.get(lang);
        if (value == null) {
            value = lang;
        }

        return value;
    }
}
