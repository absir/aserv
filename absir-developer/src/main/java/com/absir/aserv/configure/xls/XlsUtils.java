/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-24 下午12:05:11
 */
package com.absir.aserv.configure.xls;

import com.absir.aserv.configure.xls.value.XaWorkbook;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.core.kernel.KernelString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author absir
 *
 */
@SuppressWarnings({"unchecked"})
public abstract class XlsUtils {

    /**
     * XLS_BASE
     */
    public static final XlsBase XLS_BASE = new XlsBase();
    /**
     * classMapCallbacks
     */
    private static Map<Class<?>, List<CallbackTemplate<Class<?>>>> classMapCallbacks = new HashMap<Class<?>, List<CallbackTemplate<Class<?>>>>();

    /**
     * @param xlsClass
     * @param callback
     */
    public static void register(Class<?> xlsClass, CallbackTemplate<Class<?>> callback) {
        List<CallbackTemplate<Class<?>>> callbacks = classMapCallbacks.get(xlsClass);
        if (callbacks == null) {
            callbacks = new ArrayList<CallbackTemplate<Class<?>>>();
            classMapCallbacks.put(xlsClass, callbacks);
        }

        callbacks.add(callback);
    }

    /**
     * @param xlsClass
     * @param callback
     * @return
     */
    public static boolean unRegister(Class<?> xlsClass, CallbackTemplate<Class<?>> callback) {
        List<CallbackTemplate<Class<?>>> callbacks = classMapCallbacks.get(xlsClass);
        if (callbacks != null) {
            if (callbacks.remove(callback)) {
                if (callbacks.isEmpty()) {
                    classMapCallbacks.remove(xlsClass);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * @param xlsClass
     * @return
     */
    public static <T extends XlsBase> XlsDao<T, Serializable> getXlsDao(Class<T> xlsClass) {
        return getXlsDao(xlsClass, false);
    }

    /**
     * @param xlsClass
     * @param reload
     * @return
     */
    public static <T extends XlsBase> XlsDao<T, Serializable> getXlsDao(Class<T> xlsClass, boolean reload) {
        XlsDao<T, Serializable> xlsDao = reload ? null : XlsAccessorUtils.getXlsDao(xlsClass);
        if (xlsDao == null) {
            synchronized (xlsClass) {
                xlsDao = XlsAccessorUtils.getXlsDao(xlsClass);
                if (xlsDao == null) {
                    try {
                        reloadXlsDao(xlsClass);

                    } catch (IOException e) {
                        if (BeanFactoryUtils.getEnvironment() == Environment.DEVELOP) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    xlsDao = XlsAccessorUtils.getXlsDao(xlsClass);
                }
            }
        }

        return xlsDao;
    }

    /**
     * @param xlsClass
     * @throws IOException
     */
    public static <T extends XlsBase> void reloadXlsDao(Class<T> xlsClass) throws IOException {
        synchronized (xlsClass) {
            XlsBase xlsBase = KernelClass.newInstance(xlsClass);
            XaWorkbook xaWorkbook = xlsClass.getAnnotation(XaWorkbook.class);
            String workbook = xaWorkbook == null || KernelString.isEmpty(xaWorkbook.workbook()) ? xlsClass.getSimpleName()
                    : xaWorkbook.workbook();
            getXlsBeans(xlsBase.getHssfWorkbook(workbook), xaWorkbook == null ? null : xaWorkbook.sheets(), xlsClass, xlsBase);
            List<CallbackTemplate<Class<?>>> callbacks = classMapCallbacks.get(xlsClass);
            if (callbacks != null) {
                for (CallbackTemplate<Class<?>> callback : callbacks) {
                    callback.doWith(xlsClass);
                }
            }
        }
    }

    /**
     * @param xlsClass
     */
    public static <T extends XlsBase> void clearXlsDao(Class<T> xlsClass) {
        XlsAccessorUtils.clearXlsDao(xlsClass);
    }

    /**
     * @param xlsClass
     */
    public static <T extends XlsBase> XlsDao<T, ? extends Serializable> getReloadXlsDao(Class<T> xlsClass) {
        return getXlsDao(xlsClass, true);
    }

    /**
     * @param xlsClass
     * @param id
     * @return
     */
    public static <T extends XlsBase> T getXlsBean(Class<T> xlsClass, Serializable id) {
        return getXlsDao(xlsClass).get(id);
    }

    /**
     * @param xlsClass
     * @param id
     * @return
     */
    public static <T extends XlsBase> T findXlsBean(Class<T> xlsClass, Object id) {
        return getXlsDao(xlsClass).find(id);
    }

    /**
     * @param xlsClass
     * @return
     */
    public static <T extends XlsBase> Collection<T> getXlsBeans(Class<T> xlsClass) {
        XlsDao<T, Serializable> dao = getXlsDao(xlsClass);
        return dao == null ? null : dao.getAll();
    }

    /**
     * @param workbook
     * @param beanClass
     * @return
     */
    public static <T extends XlsBase> Collection<T> getXlsBeans(HSSFWorkbook workbook, Class<T> beanClass) {
        return getXlsBeans(workbook, null, beanClass);
    }

    /**
     * @param workbook
     * @param sheets
     * @param beanClass
     * @return
     */
    public static <T extends XlsBase> Collection<T> getXlsBeans(HSSFWorkbook workbook, int[] sheets, Class<T> beanClass) {
        return getXlsList(workbook, sheets, beanClass, KernelClass.newInstance(beanClass), false);
    }

    /**
     * @param workbook
     * @param sheets
     * @param beanClass
     * @param xlsBase
     * @return
     */
    public static <T extends XlsBase> Collection<T> getXlsBeans(HSSFWorkbook workbook, int[] sheets, Class<T> beanClass,
                                                                XlsBase xlsBase) {
        return getXlsList(workbook, sheets, beanClass, xlsBase, true);
    }

    /**
     * @param workbook
     * @param beanClass
     * @return
     */
    public static <T> List<T> getXlsList(HSSFWorkbook workbook, Class<T> beanClass) {
        return getXlsList(workbook, null, beanClass, XLS_BASE, false);
    }

    /**
     * @param workbook
     * @param sheets
     * @param beanClass
     * @param xlsBase
     * @param cacheable
     * @return
     */
    public static <T> List<T> getXlsList(HSSFWorkbook workbook, int[] sheets, Class<T> beanClass, XlsBase xlsBase, boolean cacheable) {
        if (xlsBase == null) {
            xlsBase = XLS_BASE;
        }

        return XlsAccessorUtils.getXlsBeans(workbook, sheets, beanClass, xlsBase, cacheable);
    }

    /**
     * @param beanClass
     * @return
     */
    public static HSSFWorkbook getWorkbook(Class<? extends XlsBase> beanClass) {
        return getWorkbook(null, beanClass, null, KernelClass.newInstance(beanClass));
    }

    /**
     * @param beans
     * @return
     */
    public static HSSFWorkbook getWorkbook(List<? extends XlsBase> beans) {
        return getWorkbook(null, beans);
    }

    /**
     * @param beanName
     * @param beans
     * @return
     */
    public static <T extends XlsBase> HSSFWorkbook getWorkbook(String beanName, Collection<T> beans) {
        T xlsBase = null;
        for (Object bean : beans) {
            if (bean != null) {
                xlsBase = (T) bean;
                break;
            }
        }

        if (xlsBase == null) {
            return null;
        }

        return getWorkbook(beanName, (Class<T>) xlsBase.getClass(), beans, xlsBase);
    }

    /**
     * @param beans
     * @param xlsBase
     * @return
     */
    public static HSSFWorkbook getWorkbook(Collection<Object> beans, XlsBase xlsBase) {
        return getWorkbook(null, beans, xlsBase);
    }

    /**
     * @param beanName
     * @param beans
     * @param xlsBase
     * @return
     */
    public static <T> HSSFWorkbook getWorkbook(String beanName, Collection<T> beans, XlsBase xlsBase) {
        Class<T> beanClass = null;
        for (Object bean : beans) {
            if (bean != null) {
                beanClass = (Class<T>) bean.getClass();
                break;
            }
        }

        if (beanClass == null) {
            return null;
        }

        return getWorkbook(beanName, beanClass, beans, xlsBase);
    }

    /**
     * @param beanName
     * @param beanClass
     * @param beans
     * @param xlsBase
     * @return
     */
    public static <T> HSSFWorkbook getWorkbook(String beanName, Class<T> beanClass, Collection<T> beans, XlsBase xlsBase) {
        if (xlsBase == null) {
            xlsBase = XLS_BASE;
        }

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        XlsAccessorUtils.writeHssfWorkbook(hssfWorkbook, beanClass, beans, xlsBase);
        return hssfWorkbook;
    }
}
