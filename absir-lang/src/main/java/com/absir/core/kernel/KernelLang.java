/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-15 上午11:18:46
 */
package com.absir.core.kernel;

import com.absir.core.base.Environment;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@SuppressWarnings({"rawtypes", "unchecked"})
public class KernelLang {

    public static final Object NULL_OBJECT = new Object();

    public static final Object[] NULL_OBJECTS = new Object[]{};

    public static final byte[] NULL_BYTES = new byte[]{};

    public static final String NULL_STRING = "";

    public static final String[] NULL_STRINGS = new String[]{};

    public static final Class[] NULL_CLASSES = new Class[]{};
    public static final NullListSet NULL_LIST_SET = new NullListSet();
    public static final Map<Object, Object> NULL_MAP = new Map() {

        @Override
        public void clear() {
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return (Set<Entry<Object, Object>>) (Set) NULL_LIST_SET;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Set<Object> keySet() {
            return NULL_LIST_SET;
        }

        @Override
        public Object put(Object key, Object value) {
            return NULL_LIST_SET;
        }

        @Override
        public void putAll(Map m) {
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Collection<Object> values() {
            return NULL_LIST_SET;
        }
    };
    public static final ListIterator NULL_LIST_ITERATOR = new ListIterator() {

        @Override
        public void add(Object e) {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public int nextIndex() {
            return -1;
        }

        @Override
        public Object previous() {
            return null;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public void remove() {
        }

        @Override
        public void set(Object e) {
        }
    };
    public static final char[] REG_CHARS = new char[]{'*', '{', '(', '['};

    public static Object[] getOptimizeObjects(Object[] ary) {
        return ary.length == 0 ? NULL_OBJECTS : ary;
    }

    public static String[] getOptimizeStrings(String[] ary) {
        return ary.length == 0 ? NULL_STRINGS : ary;
    }

    public static Class[] getOptimizeClasses(Class[] ary) {
        return ary.length == 0 ? NULL_CLASSES : ary;
    }

    public static List getOptimizeList(List list) {
        return list.isEmpty() ? NULL_LIST_SET : list;
    }

    public static Set getOptimizeSet(Set set) {
        return set.isEmpty() ? NULL_LIST_SET : set;
    }

    public static Map getOptimizeMap(Map map) {
        return map.isEmpty() ? NULL_MAP : map;
    }

    public static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }

    public static void ThrowableMutil(Throwable throwable, Throwable mutil) {
        if (mutil != null) {
            Throwable cause = null;
            while (true) {
                cause = throwable.getCause();
                if (cause == null || cause == throwable) {
                    KernelObject.declaredSet(throwable, "cause", mutil);
                    break;
                }

                throwable = cause;
            }
        }
    }

    public static enum MatcherType implements IMatcherType {

        EQUALS {
            @Override
            public boolean matchString(String match, String string) {
                return string.equals(match);
            }
        },

        STARTS_WITH {
            @Override
            public boolean matchString(String match, String string) {
                return string.startsWith(match);
            }
        },

        ENDS_WITH {
            @Override
            public boolean matchString(String match, String string) {
                return string.endsWith(match);
            }
        },

        CONTAINS {
            @Override
            public boolean matchString(String match, String string) {
                return string.contains(match);
            }
        };

        public static Entry<String, IMatcherType> getMatchEntry(String match) {
            return getMatchEntry(match, false);
        }

        public static Entry<String, IMatcherType> getMatchEntry(String match, boolean matchReg) {
            ObjectEntry<String, IMatcherType> matchEntry = new ObjectEntry<String, IMatcherType>();
            int last = match.length() - 1;
            if (last >= 0) {
                if (match.charAt(0) == '*') {
                    if (last > 0) {
                        if (match.charAt(last) == '*') {
                            if (last > 1) {
                                matchEntry.setValue(MatcherType.CONTAINS);
                                matchEntry.setKey(match.substring(1, last));
                            }

                        } else {
                            matchEntry.setValue(MatcherType.ENDS_WITH);
                            matchEntry.setKey(match.substring(1, last + 1));
                        }
                    }

                } else if (match.charAt(last) == '*') {
                    if (last > 0) {
                        matchEntry.setValue(MatcherType.STARTS_WITH);
                        matchEntry.setKey(match.substring(0, last));
                    }

                } else {
                    if (matchReg && KernelString.indexOf(match, REG_CHARS) >= 0) {
                        try {
                            Pattern pattern = Pattern.compile(match);
                            matchEntry.setValue(new MatcherTypeReg(pattern));
                            return matchEntry;

                        } catch (Throwable e) {
                            Environment.throwable(e);
                        }
                    }

                    matchEntry.setValue(MatcherType.EQUALS);
                    matchEntry.setKey(match);
                }
            }

            return matchEntry;
        }

        public static boolean isMatch(String match, Entry<String, IMatcherType> entry) {
            IMatcherType matcherType = entry.getValue();
            if (matcherType == null) {
                return true;
            }

            if (matcherType.matchString(entry.getKey(), match)) {
                return true;
            }

            return false;
        }

    }

    public static interface IMatcherType {

        public boolean matchString(String match, String string);
    }

    public static interface CloneTemplate<T> extends Cloneable {

        public T clone();

    }

    public static interface CallbackTemplate<T> {

        void doWith(T template);
    }

    public static interface CallbackTemplate2<T, K> {

        void doWith(T template, K k);
    }

    public static interface CallbackBreak<T> {

        void doWith(T template) throws BreakException;
    }

    public static interface FilterTemplate<T> {

        boolean doWith(T template) throws BreakException;
    }

    public static interface GetTemplate<T, K> {

        T getWith(K k);
    }

    public static interface GetTemplate2<T, K, K2> {

        T getWith(K k, K2 k2);
    }

    public static class MatcherTypeReg implements IMatcherType {

        protected Pattern pattern;

        MatcherTypeReg(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matchString(String match, String string) {
            return pattern.matcher(string).find();
        }
    }

    @SuppressWarnings("serial")
    public static class BreakException extends Exception {

    }

    @SuppressWarnings("serial")
    public static class CancelException extends RuntimeException {

    }

    @SuppressWarnings("serial")
    public static class CauseRuntimeException extends RuntimeException {

        public CauseRuntimeException(Throwable cause) {
            super(cause);
        }

        @Override
        public Throwable getCause() {
            Throwable cause = super.getCause();
            while (cause != null && cause != this && cause instanceof CauseRuntimeException) {
                cause = cause.getCause();
            }

            return cause;
        }

        @Override
        public String getMessage() {
            Throwable cause = getCause();
            return cause == null ? super.getMessage() : cause.getMessage();
        }
    }

    public static class ClassKey {

        Class<?> cls;

        Serializable key;

        int hashCode;

        public ClassKey(Class<?> cls, Serializable key) {
            this.cls = cls;
            this.key = key;
            hashCode = (KernelObject.hashCode(cls) & 0XFFFF0000) + (KernelObject.hashCode(key) & 0X0000FFFF);
        }

        public Class<?> getCls() {
            return cls;
        }

        public Serializable getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj.getClass() != ObjectTemplate.class)) {
                return false;
            }

            ClassKey classKey = (ClassKey) obj;
            return KernelObject.equals(cls, classKey.cls) && KernelObject.equals(key, classKey.key);
        }
    }

    public static class ObjectTemplate<T> {

        public T object;

        public ObjectTemplate() {
        }

        public ObjectTemplate(T object) {
            this.object = object;
        }

        @Override
        public int hashCode() {
            return KernelObject.hashCode(object);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj.getClass() != ObjectTemplate.class)) {
                return false;
            }

            return KernelObject.equals(object, ((ObjectTemplate) obj).object);
        }
    }

    public static class ObjectEntry<K, V> implements Entry<K, V> {

        K key;

        V value;

        public ObjectEntry() {
        }

        public ObjectEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V val = this.value;
            this.value = value;
            return val;
        }

        @Override
        public int hashCode() {
            return KernelObject.hashCode(key);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Entry)) {
                return false;
            }

            return KernelObject.equals(key, ((Entry) obj).getKey());
        }
    }

    public static class PropertyFilter {

        private int group;

        private Map<String, Entry<String, IMatcherType>> includes;

        private Map<String, Entry<String, IMatcherType>> excludes;

        private String propertyPath = "";

        public static final boolean isAllow(int include, int exclude, int group) {
            return group == 0 || ((exclude & group) == 0 && (include == 0 || (include & group) != 0));
        }

        public PropertyFilter newly() {
            PropertyFilter filter = new PropertyFilter();
            filter.group = group;
            filter.includes = includes;
            filter.excludes = excludes;
            return filter;
        }

        public void begin() {
            propertyPath = "";
        }

        public boolean allow(int include, int exclude) {
            return isAllow(include, exclude, group);
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public PropertyFilter include(String property) {
            if (includes == null) {
                includes = new HashMap<String, Entry<String, IMatcherType>>();

            } else if (includes.containsKey(property)) {
                return this;
            }

            includes.put(property, null);
            return this;
        }

        public void removeInclude(String property) {
            if (includes != null) {
                includes.remove(property);
            }
        }

        public PropertyFilter exclude(String property) {
            if (excludes == null) {
                excludes = new HashMap<String, Entry<String, IMatcherType>>();

            } else if (excludes.containsKey(property)) {
                return this;
            }

            excludes.put(property, null);
            return this;
        }

        public void removeExclude(String property) {
            if (excludes != null) {
                excludes.remove(property);
            }
        }

        public boolean isNonePath() {
            return includes == null && excludes == null;
        }

        private boolean match(Map<String, Entry<String, IMatcherType>> matchers) {
            for (Entry<String, Entry<String, IMatcherType>> matcher : matchers.entrySet()) {
                if (matcher.getKey() == null) {
                    return true;
                }

                Entry<String, IMatcherType> entry = matcher.getValue();
                if (entry == null) {
                    entry = MatcherType.getMatchEntry(matcher.getKey());
                    matcher.setValue(entry);
                }

                if (MatcherType.isMatch(propertyPath, entry)) {
                    return true;
                }
            }

            return false;
        }

        public boolean isMatch() {
            if (excludes != null) {
                if (match(excludes)) {
                    return false;
                }
            }

            if (includes != null) {
                return match(includes);
            }

            return true;
        }

        public boolean isMatch(String propertyName) {
            if (!KernelString.isEmpty(propertyName)) {
                if (KernelString.isEmpty(propertyPath)) {
                    propertyPath = propertyName;

                } else {
                    propertyPath = propertyPath + "." + propertyName;
                }
            }

            return isMatch();
        }

        public boolean isMatchPath(String propertyPath) {
            this.propertyPath = propertyPath;
            return isMatch();
        }

        public boolean isMatchPath(String propertyPath, String propertyName) {
            setPropertyPath(propertyPath);
            return isMatch(propertyName);
        }

        public String getPropertyPath() {
            return propertyPath;
        }

        public void setPropertyPath(String propertyPath) {
            this.propertyPath = propertyPath;
        }

        public Map<String, Entry<String, IMatcherType>> getIncludes() {
            return includes;
        }

        public Map<String, Entry<String, IMatcherType>> getExcludes() {
            return excludes;
        }
    }

    public static final class NullListSet implements List, Set {

        @Override
        public boolean add(Object e) {
            return false;
        }

        @Override
        public void add(int index, Object element) {
        }

        @Override
        public boolean addAll(Collection c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection c) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection c) {
            return false;
        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return -1;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Iterator<Object> iterator() {
            return NULL_LIST_ITERATOR;
        }

        @Override
        public int lastIndexOf(Object o) {
            return -1;
        }

        @Override
        public ListIterator<Object> listIterator() {
            return NULL_LIST_ITERATOR;
        }

        @Override
        public ListIterator<Object> listIterator(int index) {
            return NULL_LIST_ITERATOR;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public Object remove(int index) {
            return null;
        }

        @Override
        public boolean removeAll(Collection c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection c) {
            return false;
        }

        @Override
        public Object set(int index, Object element) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            return NULL_LIST_SET;
        }

        @Override
        public Object[] toArray() {
            return NULL_OBJECTS;
        }

        @Override
        public Object[] toArray(Object[] a) {
            return NULL_OBJECTS;
        }
    }
}
