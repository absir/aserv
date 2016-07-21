package com.absir.aserv.system.service;

import com.absir.bean.core.BeanFactoryUtils;

/**
 * Created by absir on 16/7/21.
 */
public interface IEmailService {

    public static final IEmailService ME = BeanFactoryUtils.get(IEmailService.class);

    public boolean sendMail(String subject, String content, boolean html, String to);

    public boolean sendMail(String subject, String content, boolean html, String... tos);
}
