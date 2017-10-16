/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-6-17 上午10:15:32
 */
package com.absir.aserv.cmd;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelString;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class CmdOptionals {

    private Optional<?> optional;

    private Map<String, Optional<?>> keyMapOptional = new LinkedHashMap<String, Optional<?>>();

    private File optionalStoredFile;

    public boolean processArgCmd(String argCmd) {
        int length = argCmd.length();
        if (length == 0) {
            return false;
        }

        if (optional == null || argCmd.charAt(0) == '-') {
            if (argCmd.charAt(0) == '-') {
                if (length == 1) {
                    return false;
                }

                argCmd = argCmd.substring(1);
            }

            Optional<?> optional = keyMapOptional.get(argCmd);
            if (optional == null) {
                this.optional = null;
                return false;
            }

            if (optional instanceof OptionalValue) {
                OptionalValue<Object> optionalValue = (OptionalValue<Object>) optional;
                optionalValue.setValue(optionalValue.activeValue);
                this.optional = optionalValue;
            }

        } else {
            optional.setOptionalValue(KernelString.unTransferred(argCmd));
            optional = null;
        }

        return true;
    }

    public void loadOptionalStoredFile(File storedFile) {
        if (optionalStoredFile == null) {
            optionalStoredFile = storedFile;
        }

        if (storedFile.exists()) {
            Map<String, Object> optionalMap = new HashMap<String, Object>();
            BeanConfigImpl.readProperties(BeanFactoryUtils.getBeanConfig(), optionalMap, storedFile, null);
            Optional<?> optional;
            for (Entry<String, Object> entry : optionalMap.entrySet()) {
                optional = keyMapOptional.get(entry.getKey());
                if (optional != null && !optional.isModified() && optional.isStored()) {
                    optional.setOptionalValue(entry.getValue());
                }
            }
        }
    }

    /**
     * 保存设置参数
     *
     * @throws Throwable
     */
    public void saveOptionalStoredFile() throws Throwable {
        if (optionalStoredFile != null) {
            Optional<?> optional;
            StringBuilder stringBuilder = new StringBuilder();
            for (Entry<String, Optional<?>> entry : keyMapOptional.entrySet()) {
                optional = entry.getValue();
                if (optional.isStored() && optional.isModified()) {
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append('=');
                    stringBuilder.append(DynaBinder.to(optional.getValue(), String.class));
                    stringBuilder.append("\r\n");
                }
            }

            HelperFile.writeStringToFile(optionalStoredFile, stringBuilder.toString());
        }
    }

    public <T> Optional<T> putOptional(String key, Class<? extends T> optionalClass, T defaultValue) {
        return putOptional(key, optionalClass, defaultValue, true);
    }

    public <T> Optional<T> putOptional(String key, Class<? extends T> optionalClass, T defaultValue, boolean stored) {
        Optional<T> optional = new Optional<T>(optionalClass, defaultValue, stored);
        keyMapOptional.put(key, optional);
        return optional;
    }

    public <T> OptionalValue<T> putOptionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue) {
        return putOptionalValue(key, optionalClass, defaultValue, activeValue, false);
    }

    public <T> OptionalValue<T> putOptionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue,
                                                 boolean stored) {
        OptionalValue<T> optionalValue = new OptionalValue<T>(optionalClass, defaultValue, stored, activeValue);
        keyMapOptional.put(key, optionalValue);
        return optionalValue;
    }

    public static class Optional<T> {

        protected Class<? extends T> type;

        protected T value;

        protected boolean stored;

        protected boolean modified;

        protected Optional(Class<? extends T> optionalClass, T defaultValue, boolean stored) {
            this.type = optionalClass;
            this.value = defaultValue;
            this.stored = stored;
        }

        public Class<? extends T> getType() {
            return type;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public boolean isStored() {
            return stored;
        }

        public void setStored(boolean stored) {
            this.stored = stored;
        }

        public boolean isModified() {
            return modified;
        }

        public void setModified(boolean modified) {
            this.modified = modified;
        }

        public void setOptionalValue(Object optionalValue) {
            modified = true;
            value = DynaBinder.to(optionalValue, type);
        }
    }

    public static class OptionalValue<T> extends Optional<T> {

        private T activeValue;

        protected OptionalValue(Class<? extends T> optionalClass, T defaultValue, boolean stored, T activeValue) {
            super(optionalClass, defaultValue, stored);
            this.activeValue = activeValue;
        }

        @Override
        public void setOptionalValue(Object optionalValue) {
            modified = true;
            value = optionalValue == null ? activeValue : DynaBinder.to(optionalValue, type);
        }
    }
}
