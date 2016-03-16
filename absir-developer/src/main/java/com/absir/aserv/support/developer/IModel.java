/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-8-8 下午6:45:54
 */
package com.absir.aserv.support.developer;

import com.absir.orm.value.JoEntity;

import java.util.List;

public interface IModel {

    public JoEntity getJoEntity();

    public String getCaption();

    public Long lastModified();

    public boolean isFilter();

    public DModel getModel();

    public List<JCrud> getjCruds();

    public IField getPrimary();

    public List<IField> getFields();

    public List<IField> getGroupFields(String group);

}
