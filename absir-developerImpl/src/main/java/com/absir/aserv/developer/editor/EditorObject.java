/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-30 下午4:48:08
 */
package com.absir.aserv.developer.editor;

import java.util.HashMap;
import java.util.Map;

import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.core.kernel.KernelString;
import com.absir.property.Property;
import com.absir.property.PropertyObject;

/**
 * @author absir
 * 
 */
public class EditorObject implements PropertyObject<EditorObject> {

	/** generated */
	private boolean generated;

	/** embedd */
	private boolean embedd;

	/** lang */
	private String lang;

	/** tag */
	private String tag;

	/** metas */
	private Map<Object, Object> metas;

	/** edit */
	private JaEdit edit;

	/** crud */
	private JaCrud crud;

	/** crudValue */
	private String crudValue;

	/** keyClass */
	private Class<?> keyClass;

	/** valueClass */
	private Class<?> valueClass;

	/** keyName */
	private String keyName;

	/** valueName */
	private String valueName;

	/**
	 * @return the generated
	 */
	public boolean isGenerated() {
		return generated;
	}

	/**
	 * @param generated
	 *            the generated to set
	 */
	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	/**
	 * @return the embedd
	 */
	public boolean isEmbedd() {
		return embedd;
	}

	/**
	 * @param embedd
	 *            the embedd to set
	 */
	public void setEmbedd(boolean embedd) {
		this.embedd = embedd;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @param metas
	 *            the metas to set
	 */
	public void setMeta(String name, String value) {
		if (metas == null) {
			metas = new HashMap<Object, Object>();
		}

		metas.put(name, value);
	}

	/**
	 * @param map
	 */
	public void setMetas(Map<?, ?> map) {
		if (map == null) {
			return;
		}

		if (metas == null) {
			metas = new HashMap<Object, Object>();
		}

		metas.putAll(map);
	}

	/**
	 * @return the edit
	 */
	public JaEdit getEdit() {
		return edit;
	}

	/**
	 * @param edit
	 *            the edit to set
	 */
	public void setEdit(JaEdit edit) {
		this.edit = edit;
	}

	/**
	 * @return the crud
	 */
	public JaCrud getCrud() {
		return crud;
	}

	/**
	 * @param crud
	 *            the crud to set
	 */
	public void setCrud(JaCrud crud) {
		this.crud = crud;
	}

	/**
	 * @return the crudValue
	 */
	public String getCrudValue() {
		return crudValue;
	}

	/**
	 * @param crudValue
	 *            the crudValue to set
	 */
	public void setCrudValue(String crudValue) {
		this.crudValue = crudValue;
	}

	/**
	 * @return the metas
	 */
	public Map<Object, Object> getMetas() {
		return metas;
	}

	/**
	 * @return the keyClass
	 */
	public Class<?> getKeyClass() {
		return keyClass;
	}

	/**
	 * @param keyClass
	 *            the keyClass to set
	 */
	public void setKeyClass(Class<?> keyClass) {
		this.keyClass = keyClass;
	}

	/**
	 * @return the valueClass
	 */
	public Class<?> getValueClass() {
		return valueClass;
	}

	/**
	 * @param valueClass
	 *            the valueClass to set
	 */
	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}

	/**
	 * @return the keyName
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * @param keyName
	 *            the keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * @return the valueName
	 */
	public String getValueName() {
		return valueName;
	}

	/**
	 * @param valueName
	 *            the valueName to set
	 */
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.property.PropertyObject#getPropertyData(java.lang.String,
	 * com.absir.property.Property)
	 */
	@Override
	public EditorObject getPropertyData(String name, Property property) {
		if (property.getAllow() < 0) {
			return null;
		}

		if (KernelString.isEmpty(lang)) {
			lang = name;
		}

		return this;
	}
}
