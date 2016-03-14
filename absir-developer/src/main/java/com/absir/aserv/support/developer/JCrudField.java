/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-10 上午10:43:54
 */
package com.absir.aserv.support.developer;

import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.orm.value.JoEntity;

import java.io.Serializable;

/**
 * @author absir
 */
@SuppressWarnings("serial")
public class JCrudField implements Serializable {

    /**
     * name
     */
    private String name;

    /**
     * type
     */
    private Class<?> type;

    /**
     * include
     */
    private int include;

    /**
     * exclude
     */
    private int exclude;

    /**
     * jCrud
     */
    private JCrud jCrud;

    /**
     * cruds
     */
    private Crud[] cruds;

    /**
     * joEntity
     */
    private JoEntity joEntity;

    /**
     * valueJoEntity
     */
    private JoEntity keyJoEntity;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * @return the include
     */
    public int getInclude() {
        return include;
    }

    /**
     * @param include the include to set
     */
    public void setInclude(int include) {
        this.include = include;
    }

    /**
     * @return the exclude
     */
    public int getExclude() {
        return exclude;
    }

    /**
     * @param exclude the exclude to set
     */
    public void setExclude(int exclude) {
        this.exclude = exclude;
    }

    /**
     * @return the jCrud
     */
    public JCrud getjCrud() {
        return jCrud;
    }

    /**
     * @param jCrud the jCrud to set
     */
    public void setjCrud(JCrud jCrud) {
        this.jCrud = jCrud;
    }

    /**
     * @return the cruds
     */
    public Crud[] getCruds() {
        return cruds;
    }

    /**
     * @param cruds the cruds to set
     */
    public void setCruds(Crud[] cruds) {
        this.cruds = cruds;
    }

    /**
     * @return the joEntity
     */
    public JoEntity getJoEntity() {
        return joEntity;
    }

    /**
     * @param joEntity the joEntity to set
     */
    public void setJoEntity(JoEntity joEntity) {
        this.joEntity = joEntity;
    }

    /**
     * @return the keyJoEntity
     */
    public JoEntity getKeyJoEntity() {
        return keyJoEntity;
    }

    /**
     * @param keyJoEntity the keyJoEntity to set
     */
    public void setKeyJoEntity(JoEntity keyJoEntity) {
        this.keyJoEntity = keyJoEntity;
    }

    /**
     * @param include
     * @return
     */
    public boolean allowInclude(int include) {
        return include == 0 || (include & this.include) != 0;
    }

    /**
     * @param exclude
     * @return
     */
    public boolean allowExclude(int exclude) {
        return exclude == 0 || (exclude & this.exclude) == 0;
    }

    /**
     * @param include
     * @param exclude
     * @return
     */
    public boolean allow(int include, int exclude) {
        return allowInclude(include) && allowExclude(exclude);
    }
}
