/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-10 下午1:43:31
 */
package com.absir.bean.basis;

import com.absir.core.base.Environment;

import java.lang.reflect.Type;

public interface BeanConfig {

    public String getClassPath();

    public String getResourcePath();

    public Environment getEnvironment();

    public void setEnvironment(Environment environment);

    public boolean isOutEnvironmentDenied();

    public Object getValue(String name);

    public void setValue(String name, Object obj);

    public Object getConfigValue(String name);

    public String getExpression(String expression);

    public String getExpression(String expression, boolean strict);

    public <T> T getExpressionObject(String expression, String beanName, Class<T> toClass);

    public Object getExpressionObject(String expression, String beanName, Type toType);

    public <T> T getExpressionValue(String expression, String beanName, Class<T> toClass);

    public <T> T getExpressionDefaultValue(String expression, String beanName, Class<T> toClass);

    public Object getExpressionDefaultValue(String expression, String beanName, Type toType);

    public String getClassPath(String filename);

    public String getResourcePath(String filename);

    public String getResourcePath(String filename, String nullPrefix);
}
