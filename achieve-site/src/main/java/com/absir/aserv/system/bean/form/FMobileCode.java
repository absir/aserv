package com.absir.aserv.system.bean.form;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.NotEmpty;
import com.absir.validator.value.Regex;

/**
 * Created by absir on 16/7/21.
 */
public class FMobileCode {

    @JaLang("手机")
    @Regex(value = "^\\d*$")
    @NotEmpty
    public String mobile;

    @JaLang("验证码")
    @NotEmpty
    @JaEdit(types = "code")
    public String code;

}
