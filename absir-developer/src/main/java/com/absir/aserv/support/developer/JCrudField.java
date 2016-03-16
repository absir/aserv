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

@SuppressWarnings("serial")
public class JCrudField implements Serializable {

    private String name;

    private Class<?> type;

    private int include;

    private int exclude;

    private JCrud jCrud;

    private Crud[] cruds;

    private JoEntity joEntity;

    private JoEntity keyJoEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public int getInclude() {
        return include;
    }

    public void setInclude(int include) {
        this.include = include;
    }

    public int getExclude() {
        return exclude;
    }

    public void setExclude(int exclude) {
        this.exclude = exclude;
    }

    public JCrud getjCrud() {
        return jCrud;
    }

    public void setjCrud(JCrud jCrud) {
        this.jCrud = jCrud;
    }

    public Crud[] getCruds() {
        return cruds;
    }

    public void setCruds(Crud[] cruds) {
        this.cruds = cruds;
    }

    public JoEntity getJoEntity() {
        return joEntity;
    }

    public void setJoEntity(JoEntity joEntity) {
        this.joEntity = joEntity;
    }

    public JoEntity getKeyJoEntity() {
        return keyJoEntity;
    }

    public void setKeyJoEntity(JoEntity keyJoEntity) {
        this.keyJoEntity = keyJoEntity;
    }

    public boolean allowInclude(int include) {
        return include == 0 || (include & this.include) != 0;
    }

    public boolean allowExclude(int exclude) {
        return exclude == 0 || (exclude & this.exclude) == 0;
    }

    public boolean allow(int include, int exclude) {
        return allowInclude(include) && allowExclude(exclude);
    }
}
