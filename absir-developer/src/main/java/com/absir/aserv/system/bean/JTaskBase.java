package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JeEditable;
import com.absir.aserv.system.bean.value.JiActive;
import com.absir.aserv.task.TaskService;
import com.absir.bean.lang.ILangMessage;
import com.absir.binder.BinderResult;
import com.absir.core.base.Environment;
import com.absir.data.helper.HelperDatabind;
import com.absir.validator.IValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import java.io.IOException;

/**
 * Created by absir on 16/8/15.
 */
@MappedSuperclass
public abstract class JTaskBase implements JiActive, IValidator {

    @JaLang(value = "任务名称", tag = "taskName")
    private String name;

    @JaEdit(editable = JeEditable.LOCKNONE)
    @Lob
    @Column(length = 10240)
    private byte[] taskData;

    @JaLang("任务数据")
    @JaEdit(types = "text")
    private transient String taskJson;

    @JaLang("开始时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long beginTime;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long passTime;

    @JaLang("启动标示")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String startTag;

    @JaEdit(editable = JeEditable.LOCKABLE, types = "dateTime", groups = JaEdit.GROUP_LIST)
    @JaLang("启动时间")
    private long startTime;

    @JaLang("剩余重试次数")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private int leftRetryCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getTaskData() {
        return taskData;
    }

    public void setTaskData(byte[] taskData) {
        this.taskData = taskData;
    }

    @JsonIgnore
    public String getTaskJson() {
        if (taskJson == null) {
            Class<?>[] paramTypes = TaskService.ME.getParamTypes(name);
            if (paramTypes == null || paramTypes.length == 0) {
                taskJson = "";

            } else {
                try {
                    taskJson = HelperDatabind.JSON.writeAsStringArray(HelperDatabind.JSON.readArray(taskData, paramTypes));

                } catch (IOException e) {
                    Environment.throwable(e);
                    taskJson = "";
                }
            }
        }

        return taskJson;
    }

    public void setTaskJson(String taskJson) {
        this.taskJson = taskJson;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getPassTime() {
        return passTime;
    }

    public void setPassTime(long passTime) {
        this.passTime = passTime;
    }

    public String getStartTag() {
        return startTag;
    }

    public void setStartTag(String startTag) {
        this.startTag = startTag;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getLeftRetryCount() {
        return leftRetryCount;
    }

    public void setLeftRetryCount(int leftRetryCount) {
        this.leftRetryCount = leftRetryCount;
    }

    @Override
    public void validatorResult(String propertyPath, BinderResult result, ILangMessage langMessage) {
        Class<?>[] paramTypes = TaskService.ME.getParamTypes(name);
        if (paramTypes == null) {
            result.addPropertyError(propertyPath == "" ? "name" : (propertyPath + ".name"), langMessage == null ? TaskService.TASK_NOT_FOUND : langMessage.getLangMessage(TaskService.TASK_NOT_FOUND), null);
            return;
        }

        if (paramTypes.length > 0) {
            try {
                Object[] params = HelperDatabind.JSON.readArray(taskJson, paramTypes);
                taskData = HelperDatabind.PACK.writeAsBytesArray(params);

            } catch (Exception e) {
                result.addPropertyError(propertyPath == "" ? "taskJson" : (propertyPath + ".taskJson"), langMessage == null ? TaskService.TASK_PARAM_ERROR : langMessage.getLangMessage(TaskService.TASK_PARAM_ERROR), null);
            }
        }
    }
}
