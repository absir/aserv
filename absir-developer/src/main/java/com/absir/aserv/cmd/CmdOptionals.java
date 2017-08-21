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

    private Map<String, Optional<?>> keyMapOpitional = new LinkedHashMap<String, Optional<?>>();

    private File optionialStoredFile;

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

            Optional<?> optional = keyMapOpitional.get(argCmd);
            if (optional == null) {
                this.optional = null;
                return false;
            }

            if (optional instanceof OptionalValue) {
                OptionalValue<Object> opitionalValue = (OptionalValue<Object>) optional;
                opitionalValue.setValue(opitionalValue.activeValue);
                this.optional = opitionalValue;
            }

        } else {
            optional.setOpitionalValue(KernelString.unTransferred(argCmd));
            optional = null;
        }

        return true;
    }

    public void loadOptionialStoredFile(File storedFile) {
        if (optionialStoredFile == null) {
            optionialStoredFile = storedFile;
        }

        if (storedFile.exists()) {
            Map<String, Object> optionalMap = new HashMap<String, Object>();
            BeanConfigImpl.readProperties(BeanFactoryUtils.getBeanConfig(), optionalMap, storedFile, null);
            Optional<?> optional;
            for (Entry<String, Object> entry : optionalMap.entrySet()) {
                optional = keyMapOpitional.get(entry.getKey());
                if (optional != null && !optional.isModified() && optional.isStored()) {
                    optional.setOpitionalValue(entry.getValue());
                }
            }
        }
    }

    /**
     * 保存设置参数
     *
     * @throws Throwable
     */
    public void saveOptionialStoredFile() throws Throwable {
        if (optionialStoredFile != null) {
            Optional<?> optional;
            StringBuilder stringBuilder = new StringBuilder();
            for (Entry<String, Optional<?>> entry : keyMapOpitional.entrySet()) {
                optional = entry.getValue();
                if (optional.isStored() && optional.isModified()) {
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append('=');
                    stringBuilder.append(DynaBinder.to(optional.getValue(), String.class));
                    stringBuilder.append("\r\n");
                }
            }

            HelperFile.writeStringToFile(optionialStoredFile, stringBuilder.toString());
        }
    }

    public <T> Optional<T> putOpitional(String key, Class<? extends T> optionalClass, T defaultValue) {
        return putOpitional(key, optionalClass, defaultValue, true);
    }

    public <T> Optional<T> putOpitional(String key, Class<? extends T> optionalClass, T defaultValue, boolean stored) {
        Optional<T> optional = new Optional<T>(optionalClass, defaultValue, stored);
        keyMapOpitional.put(key, optional);
        return optional;
    }

    public <T> OptionalValue<T> putOpitionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue) {
        return putOpitionalValue(key, optionalClass, defaultValue, activeValue, false);
    }

    public <T> OptionalValue<T> putOpitionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue,
                                                  boolean stored) {
        OptionalValue<T> opitionalValue = new OptionalValue<T>(optionalClass, defaultValue, stored, activeValue);
        keyMapOpitional.put(key, opitionalValue);
        return opitionalValue;
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

        public void setOpitionalValue(Object opitionalValue) {
            modified = true;
            value = DynaBinder.to(opitionalValue, type);
        }
    }

    public static class OptionalValue<T> extends Optional<T> {

        private T activeValue;

        protected OptionalValue(Class<? extends T> optionalClass, T defaultValue, boolean stored, T activeValue) {
            super(optionalClass, defaultValue, stored);
            this.activeValue = activeValue;
        }

        @Override
        public void setOpitionalValue(Object opitionalValue) {
            modified = true;
            value = opitionalValue == null ? activeValue : DynaBinder.to(opitionalValue, type);
        }
    }
}
