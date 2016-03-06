/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-15 上午10:39:07
 */
package com.absir.aserv.configure.conf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.binder.BinderUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;

/**
 * @author absir
 * 
 */
@SuppressWarnings("unchecked")
public abstract class ConfigureUtils {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureUtils.class);

	/** Configure_Class_Map_Bean */
	private static Map<Class<? extends ConfigureBase>, ConfigureBase> Configure_Class_Map_Bean = new HashMap<Class<? extends ConfigureBase>, ConfigureBase>();

	/**
	 * @param file
	 * @return
	 */
	public static Map<String, Object> readPropertyMap(File file) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		BeanConfigImpl.readProperties(BeanFactoryUtils.getBeanConfig(), propertyMap, file, null);
		return BinderUtils.getDataMap(propertyMap);
	}

	/**
	 * @param cls
	 * @return
	 */
	public <T extends ConfigureBase> T getConfigure(Class<T> cls) {
		T configure = (T) Configure_Class_Map_Bean.get(cls);
		if (configure == null) {
			synchronized (cls) {
				configure = (T) Configure_Class_Map_Bean.get(cls);
				if (configure == null) {
					configure = KernelClass.newInstance(cls);
					try {
						DynaBinder.INSTANCE.mapBind(readPropertyMap(configure.getConfigureFile()), configure);

					} catch (Exception e) {
						LOGGER.error("get configure " + cls + " error", e);
					}

					Configure_Class_Map_Bean.put(cls, configure);
				}
			}
		}

		return configure;
	}

	/**
	 * @param cls
	 */
	public <T extends ConfigureBase> void clearConfigure(Class<T> cls) {
		synchronized (cls) {
			Configure_Class_Map_Bean.remove(cls);
		}
	}
}
