/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-8-8 下午5:17:28
 */
package com.absir.aserv.support.developer;

import java.io.Serializable;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
@SuppressWarnings("serial")
public class JCrud implements Serializable {

	/** value */
	protected String value;

	/** value */
	protected Class<?> factory;

	/** parameters */
	protected Object[] parameters;

	/** cruds */
	protected JaCrud.Crud[] cruds;

	/**
	 * 
	 */
	public JCrud() {
	}

	/**
	 * @param crud
	 */
	public JCrud(JaCrud crud) {
		setJaCrud(crud);
	}

	/**
	 * @param crud
	 */
	public void setJaCrud(JaCrud crud) {
		if (crud != null) {
			setJaCrud(crud.value(), crud.factory(), KernelArray.toArray(crud.parameters()), crud.cruds());
		}
	}

	/**
	 * @param value
	 * @param factory
	 * @param parameters
	 * @param cruds
	 */
	public void setJaCrud(String value, Class<?> factory, Object[] parameters, JaCrud.Crud[] cruds) {
		this.value = value;
		this.factory = factory;
		this.parameters = parameters;
		this.cruds = cruds;
	}

	/**
	 * @param crudValue
	 */
	public void setJaCrudValue(String crudValue) {
		if (!KernelString.isEmpty(crudValue)) {
			String[] params = crudValue.split(",");
			int length = params.length;
			if (length == 1) {
				value = params[0];

			} else if (length == 2) {
				value = params[0];
				factory = KernelClass.forName(params[1]);

			} else if (length > 2) {
				value = params[0];
				factory = KernelClass.forName(params[1]);
				length -= 2;
				parameters = new Object[length];
				for (int i = 0; i < length; i++) {
					parameters[i] = params[i + 2];
				}
			}
		}
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the factory
	 */
	public Class<?> getFactory() {
		return factory;
	}

	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * @return the cruds
	 */
	public JaCrud.Crud[] getCruds() {
		return cruds;
	}
}
