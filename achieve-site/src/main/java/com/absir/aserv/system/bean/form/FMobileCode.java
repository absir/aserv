package com.absir.aserv.system.bean.form;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.validator.value.NotEmpty;
import com.absir.validator.value.Regex;

import java.util.regex.Pattern;

/**
 * Created by absir on 16/7/21.
 */
public class FMobileCode {

    public static final String MOBILE_REGX = "^\\d*$";

    public static final Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_REGX);

    @JaLang("手机")
    @Regex(value = MOBILE_REGX)
    @NotEmpty
    public String mobile;

    @JaLang("验证码")
    @NotEmpty
    @JaEdit(types = "code")
    public String code;

}
