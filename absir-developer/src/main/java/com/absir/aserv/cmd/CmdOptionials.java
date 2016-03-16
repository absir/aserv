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
public class CmdOptionials {

    private Opitional<?> opitional;

    private Map<String, Opitional<?>> keyMapOpitional = new LinkedHashMap<String, Opitional<?>>();

    private File optionialStoredFile;

    public boolean proccessArgCmd(String argCmd) {
        int length = argCmd.length();
        if (length == 0) {
            return false;
        }

        if (opitional == null || argCmd.charAt(0) == '-') {
            if (argCmd.charAt(0) == '-') {
                if (length == 1) {
                    return false;
                }

                argCmd = argCmd.substring(1);
            }

            Opitional<?> opitional = keyMapOpitional.get(argCmd);
            if (opitional == null) {
                this.opitional = null;
                return false;
            }

            if (opitional instanceof OpitionalValue) {
                OpitionalValue<Object> opitionalValue = (OpitionalValue<Object>) opitional;
                opitionalValue.setValue(opitionalValue.activeValue);
                this.opitional = opitionalValue;
            }

        } else {
            opitional.setOpitionalValue(KernelString.unTransferred(argCmd));
            opitional = null;
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
            Opitional<?> opitional;
            for (Entry<String, Object> entry : optionalMap.entrySet()) {
                opitional = keyMapOpitional.get(entry.getKey());
                if (opitional != null && !opitional.isModified() && opitional.isStored()) {
                    opitional.setOpitionalValue(entry.getValue());
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
            Opitional<?> opitional;
            StringBuilder stringBuilder = new StringBuilder();
            for (Entry<String, Opitional<?>> entry : keyMapOpitional.entrySet()) {
                opitional = entry.getValue();
                if (opitional.isStored() && opitional.isModified()) {
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append('=');
                    stringBuilder.append(DynaBinder.to(opitional.getValue(), String.class));
                    stringBuilder.append("\r\n");
                }
            }

            HelperFile.writeStringToFile(optionialStoredFile, stringBuilder.toString());
        }
    }

    public <T> Opitional<T> putOpitional(String key, Class<? extends T> optionalClass, T defaultValue) {
        return putOpitional(key, optionalClass, defaultValue, true);
    }

    public <T> Opitional<T> putOpitional(String key, Class<? extends T> optionalClass, T defaultValue, boolean stored) {
        Opitional<T> opitional = new Opitional<T>(optionalClass, defaultValue, stored);
        keyMapOpitional.put(key, opitional);
        return opitional;
    }

    public <T> OpitionalValue<T> putOpitionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue) {
        return putOpitionalValue(key, optionalClass, defaultValue, activeValue, false);
    }

    public <T> OpitionalValue<T> putOpitionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue,
                                                   boolean stored) {
        OpitionalValue<T> opitionalValue = new OpitionalValue<T>(optionalClass, defaultValue, stored, activeValue);
        keyMapOpitional.put(key, opitionalValue);
        return opitionalValue;
    }

    public static class Opitional<T> {

        protected Class<? extends T> type;

        protected T value;

        protected boolean stored;

        protected boolean modified;

        protected Opitional(Class<? extends T> optionalClass, T defaultValue, boolean stored) {
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

    public static class OpitionalValue<T> extends Opitional<T> {

        private T activeValue;

        protected OpitionalValue(Class<? extends T> optionalClass, T defaultValue, boolean stored, T activeValue) {
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
