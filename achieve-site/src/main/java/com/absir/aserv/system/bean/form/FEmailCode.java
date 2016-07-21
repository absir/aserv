package com.absir.aserv.system.bean.form;

import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.Email;
import com.absir.validator.value.NotEmpty;

/**
 * Created by absir on 16/7/21.
 */
public class FEmailCode {

    @JaLang("邮箱")
    @Email
    @NotEmpty
    public String email;

    @JaLang("验证码")
    @NotEmpty
    public String code;
}
