package com.absir.aserv.system.service;

import com.absir.bean.core.BeanFactoryUtils;

/**
 * Created by absir on 16/7/21.
 */
public interface IMessageService {

    public static final IMessageService ME = BeanFactoryUtils.get(IMessageService.class);

    public boolean sendMessage(String content, String to);

    public boolean sendMessage(String content, String... tos);
}
