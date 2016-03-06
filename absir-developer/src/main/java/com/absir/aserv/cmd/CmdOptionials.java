/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-6-17 上午10:15:32
 */
package com.absir.aserv.cmd;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public class CmdOptionials {

	/** opitional */
	private Opitional<?> opitional;

	/** keyMapOpitional */
	private Map<String, Opitional<?>> keyMapOpitional = new LinkedHashMap<String, Opitional<?>>();

	/** optionialStoredFile */
	private File optionialStoredFile;

	/**
	 * @param argCmd
	 * @return
	 */
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

	/**
	 * @param storedFile
	 */
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

	/**
	 * @param key
	 * @param optionalClass
	 * @param defaultValue
	 * @return
	 */
	public <T> Opitional<T> putOpitional(String key, Class<? extends T> optionalClass, T defaultValue) {
		return putOpitional(key, optionalClass, defaultValue, true);
	}

	/**
	 * @param key
	 * @param optionalClass
	 * @param defaultValue
	 * @param stored
	 * @return
	 */
	public <T> Opitional<T> putOpitional(String key, Class<? extends T> optionalClass, T defaultValue, boolean stored) {
		Opitional<T> opitional = new Opitional<T>(optionalClass, defaultValue, stored);
		keyMapOpitional.put(key, opitional);
		return opitional;
	}

	/**
	 * @param key
	 * @param optionalClass
	 * @param defaultValue
	 * @param activeValue
	 * @return
	 */
	public <T> OpitionalValue<T> putOpitionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue) {
		return putOpitionalValue(key, optionalClass, defaultValue, activeValue, false);
	}

	/**
	 * @param key
	 * @param optionalClass
	 * @param defaultValue
	 * @param activeValue
	 * @return
	 */
	public <T> OpitionalValue<T> putOpitionalValue(String key, Class<? extends T> optionalClass, T defaultValue, T activeValue,
			boolean stored) {
		OpitionalValue<T> opitionalValue = new OpitionalValue<T>(optionalClass, defaultValue, stored, activeValue);
		keyMapOpitional.put(key, opitionalValue);
		return opitionalValue;
	}

	/**
	 * @author absir
	 * 
	 */
	public static class Opitional<T> {

		/** type */
		protected Class<? extends T> type;

		/** value */
		protected T value;

		/** stored */
		protected boolean stored;

		/** modified */
		protected boolean modified;

		/**
		 * @param optionalClass
		 * @param defaultValue
		 * @param stored
		 */
		protected Opitional(Class<? extends T> optionalClass, T defaultValue, boolean stored) {
			this.type = optionalClass;
			this.value = defaultValue;
			this.stored = stored;
		}

		/**
		 * @return the type
		 */
		public Class<? extends T> getType() {
			return type;
		}

		/**
		 * @return the value
		 */
		public T getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(T value) {
			this.value = value;
		}

		/**
		 * @return the stored
		 */
		public boolean isStored() {
			return stored;
		}

		/**
		 * @param stored
		 *            the stored to set
		 */
		public void setStored(boolean stored) {
			this.stored = stored;
		}

		/**
		 * @return the modified
		 */
		public boolean isModified() {
			return modified;
		}

		/**
		 * @param modified
		 *            the modified to set
		 */
		public void setModified(boolean modified) {
			this.modified = modified;
		}

		/**
		 * @param opitionalValue
		 */
		public void setOpitionalValue(Object opitionalValue) {
			modified = true;
			value = DynaBinder.to(opitionalValue, type);
		}
	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static class OpitionalValue<T> extends Opitional<T> {

		/** activeValue */
		private T activeValue;

		/**
		 * @param optionalClass
		 * @param defaultValue
		 * @param stored
		 * @param activeValue
		 */
		protected OpitionalValue(Class<? extends T> optionalClass, T defaultValue, boolean stored, T activeValue) {
			super(optionalClass, defaultValue, stored);
			this.activeValue = activeValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.absir.aserv.cmd.CmdOptionials.Opitional#setOpitionalValue(java
		 * .lang.Object)
		 */
		@Override
		public void setOpitionalValue(Object opitionalValue) {
			modified = true;
			value = opitionalValue == null ? activeValue : DynaBinder.to(opitionalValue, type);
		}
	}
}
