/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class KernelList {

    public static final List EMPTY_LIST = new ArrayList();

    public static final Comparator<Orderable> COMPARATOR = new Comparator<Orderable>() {

        @Override
        public int compare(Orderable lhs, Orderable rhs) {
            return lhs.getOrder() - rhs.getOrder();
        }
    };

    public static final Comparator<Orderable> COMPARATOR_DESC = new Comparator<Orderable>() {

        @Override
        public int compare(Orderable lhs, Orderable rhs) {
            return rhs.getOrder() - lhs.getOrder();
        }
    };

    public static final Comparator COMMON_COMPARATOR = new Comparator() {

        @Override
        public int compare(Object lhs, Object rhs) {
            return getOrder(lhs) - getOrder(rhs);
        }
    };

    public static <T> T get(List<T> list, int index) {
        return get(list, null, index);
    }

    public static <T> T get(List<T> list, T defaultValue, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }

        return defaultValue;
    }

    public static <T> void addOnly(List<T> list, T element) {
        for (T el : list) {
            if (el == element) {
                return;
            }
        }

        list.add(element);
    }

    public static <T extends Orderable> void addOrder(List<T> list, T element) {
        int order = element.getOrder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (order < list.get(i).getOrder()) {
                list.add(i, element);
                return;
            }
        }

        list.add(element);
    }

    public static <T extends Orderable> void addOrderOnly(List<T> list, T element) {
        int order = element.getOrder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Orderable orderable = list.get(i);
            if (orderable == element) {
                return;
            }

            if (order < orderable.getOrder()) {
                list.add(i, element);
                return;
            }
        }

        list.add(element);
    }

    public static <T extends Orderable> void sortOrderable(List<T> list) {
        Collections.sort(list, COMPARATOR);
    }

    public static <T extends Orderable> void sortOrderableDesc(List<T> list) {
        Collections.sort(list, COMPARATOR_DESC);
    }

    public static int getOrder(Object element) {
        return element instanceof Orderable ? ((Orderable) element).getOrder() : 0;
    }

    public static void addOrderObject(List list, Object element) {
        int order = getOrder(element);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (order < getOrder(list.get(i))) {
                list.add(i, element);
                return;
            }
        }

        list.add(element);
    }

    public static void sortCommonObjects(List list) {
        Collections.sort(list, COMMON_COMPARATOR);
    }

    public static <T> List<T> getFilterSortList(Collection<T> collection, FilterTemplate<T> filterTemplate, Comparator<T> comparator) {
        if (filterTemplate == null && comparator == null) {
            return new ArrayList<T>(collection);
        }

        List<T> list = new ArrayList<T>(collection.size());
        try {
            for (T element : collection) {
                if (filterTemplate == null || filterTemplate.doWith(element)) {
                    list.add(element);
                }
            }

        } catch (BreakException e) {
        }

        if (comparator != null) {
            Collections.sort(list, comparator);
        }

        return list;
    }

    public interface Orderable {
        public int getOrder();
    }
}
