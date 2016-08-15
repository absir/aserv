package com.absir.aserv.task;

import com.absir.aop.AopProxyUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.IMethodInject;
import com.absir.bean.inject.InjectMethod;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelString;
import com.absir.data.helper.HelperDatabind;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 16/8/15.
 */
@Base
@Bean
public class TaskService implements IMethodInject<String> {

    public static final TaskService ME = BeanFactoryUtils.get(TaskService.class);

    private Map<String, TaskMethod> taskMethodMap;

    public static class TaskMethod {

        public Object beanObject;

        public Method method;

        public Class<?>[] paramTypes;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public String getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method) {
        JaTask task = BeanConfigImpl.getMethodAnnotation(method, JaTask.class, true);
        return task == null ? null : task.value();
    }

    public String getTaskName(Object beanObject, Method method) {
        Class<?> beanClass = beanObject == null ? method.getDeclaringClass() : AopProxyUtils.getBeanType(beanObject);
        return beanClass.getSimpleName() + "." + method.getName();
    }

    @Override
    public void setInjectMethod(String inject, Method method, Object beanObject, InjectMethod injectMethod) {
        if (KernelString.isEmpty(inject)) {
            inject = getTaskName(beanObject, method);
        }

        addTaskMethod(inject, beanObject, injectMethod.getMethod());
    }

    public void addTaskMethod(String name, Object beanObject, Method method) {
        addTaskMethodReplace(name, beanObject, method, false);
    }

    public void addTaskMethodReplace(String name, Object beanObject, Method method, boolean replace) {
        if (KernelString.isEmpty(name)) {
            throw new RuntimeException("task method name empty");
        }

        TaskMethod taskMethod = new TaskMethod();
        taskMethod.beanObject = beanObject;
        taskMethod.method = method;
        taskMethod.paramTypes = method.getParameterTypes();

        synchronized (this) {
            if (taskMethodMap == null) {
                taskMethodMap = new HashMap<String, TaskMethod>();
            }

            if (!replace) {
                if (taskMethodMap.containsKey(name)) {
                    throw new RuntimeException("task method [" + name + "]" + method + " => " + taskMethodMap.get(name).method);
                }
            }

            taskMethodMap.put(name, taskMethod);
        }
    }

    public boolean invokeTask(String name, byte[] dataParams) throws IOException, InvocationTargetException, IllegalAccessException {
        TaskMethod taskMethod = taskMethodMap == null ? null : taskMethodMap.get(name);
        if (taskMethod != null) {
            Object[] params = HelperDatabind.readArray(dataParams, (Type[]) taskMethod.paramTypes);
            return invokeTaskMethod(name, taskMethod, params);
        }

        return false;
    }

    public boolean invokeTask(String name, Object... params) throws InvocationTargetException, IllegalAccessException {
        return invokeTaskMethod(name, null, params);
    }

    public boolean invokeTaskMethod(String name, TaskMethod taskMethod, Object... params) throws InvocationTargetException, IllegalAccessException {
        if (taskMethod == null) {
            taskMethod = taskMethodMap == null ? null : taskMethodMap.get(name);
        }

        if (taskMethod != null) {
            taskMethod.method.invoke(taskMethod.beanObject, params);
            return true;
        }

        return false;
    }
}
