/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月27日 下午12:18:52
 */
package com.absir.aserv.system.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaModel;
import com.absir.aserv.system.service.BeanService;
import com.absir.context.core.ContextUtils;
import com.absir.orm.value.JaColum;

import javax.persistence.Entity;
import javax.persistence.Index;
import java.util.Collection;

@MaEntity(parent = {@MaMenu("系统管理")}, name = "日志")
@JaModel(desc = true)
@Entity
public class JLog extends JbBean {

    @JaLang("名称")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    @JaColum(indexs = @Index(name = "name", columnList = "name"))
    private String name;

    @JaLang("动作")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String action;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    private long createTime;

    @JaLang("IP")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String ip;

    @JaLang("用户名")
    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    private String username;

    @JaLang("成功")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean success;

    public JLog() {
    }

    public JLog(String name, String action, String ip, String username, boolean success) {
        this.name = name;
        this.action = action;
        createTime = ContextUtils.getContextTime();
        this.ip = ip;
        this.username = username;
        this.success = success;
    }

    public static void log(JLog log) {
        BeanService.ME.persist(log);
    }

    public static JLog log(String name, String action, String ip, String username, boolean success) {
        JLog log = new JLog(name, action, ip, username, success);
        log(log);
        return log;
    }

    public static void logs(Collection<JLog> logs) {
        BeanService.ME.persists(logs);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
