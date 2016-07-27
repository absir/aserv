package com.absir.aserv.system.service;

import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;

/**
 * Created by absir on 16/7/21.
 */
@Inject
public interface IEmailService {

    public static final IEmailService ME = BeanFactoryUtils.get(IEmailService.class);

    public boolean sendMail(String subject, String content, boolean html, String to);

    public boolean sendMailTos(String subject, String content, boolean html, String... tos);
}
