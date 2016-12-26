/**
 * Created by absir on 16/6/26.
 */
function ab_humanTime(time, max) {
    if (time < (max || 1000)) {
        return time + 's';
    }

    if (time < 600) {
        return Math.floor(time / 60) + 'm' + (time % 60) + 's';
    }

    if (time < (max ? max * 60 : 60000)) {
        return Math.ceil(time / 60) + 'm';
    }

    if (time < 36000) {
        return Math.floor(time / 3600) + 'h' + Math.ceil((time % 3600) / 60) + 'm';
    }

    return Math.ceil(time / 3600) + 'h';
}

function ab_buttonIdle($btn, time) {
    var idle = $btn.data('idle');
    if (idle && idle.id) {
        clearInterval(idle.id);

    } else {
        idle = {};
        idle.value = $btn.text();
        $btn.data('idle', idle);
    }

    var next = function () {
        $btn.text(ab_humanTime(time));
        if (--time <= 0) {
            $btn.text(idle.value);
            clearInterval(idle.id);
            idle.id = 0;
            $btn.removeAttr("disabled");
        }
    };
    $btn.attr("disabled", "true");
    next();
    idle.id = setInterval(next, 1000);
}

function ab_getUP(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    return r != null ? unescape(r[2]) : null;
}

function ab_group($node, toggle) {
    var tog = $node.attr(toggle);
    if (tog) {
        var $group = $node.closest("[ab_group='ab_main'], .ab_main");
        return ab_groupSel($group, "[ab_group='" + tog + "'], ." + tog);
    }

    return $node.closest("[ab_group='" + toggle + "'], ." + toggle);
}

function ab_groupSel($group, sel) {
    return $group && $group.length ? $(sel, $group) : $(sel);
}

function ab_groupParam(sel, node) {
    $this = $(node ? node : this);
    $group = ab_group($this, 'ab_param_grp');
    return ab_groupSel($group, sel);
}

function ab_groupTrigger(sel, node, trigger) {
    var target = ab_groupParam(sel, node);
    trigger = trigger || 'click';
    return function () {
        target.trigger(trigger);
    }
}

function ab_getParam(sel, node, json) {
    if (!json) {
        if (sel[0] === '$') {
            json = true;
            sel = sel.substring(1);
        }
    }

    $param = ab_groupParam(sel, node);
    var len = $param.length;
    var vals;

    if (json) {
        vals = Array();
    }

    if (len == 0) {
        return undefined;

    } else if (len == 1) {
        var val = $param.val();
        if (!val) {
            val = $param.attr('value');
        }

        if (json) {
            vals.push(val);

        } else {
            return val;
        }

    } else {
        var params = "";
        $param.each(function () {
            var $t = $(this);
            var val = $t.val();
            if (!val) {
                val = $t.attr('value');
            }

            if (json) {
                vals.push(val);

            } else {
                if (params) {
                    params += "," + val;

                } else {
                    params = val;
                }
            }
        });

        if (!json) {
            return params;
        }
    }

    return vals && vals.length ? $.toJSON(vals) : undefined;
}

function ab_open(href) {
    window.open(href);
}

function ab_goBack() {
    if (ab_isHasFrame()) {
        removeIframe();

    } else {
        window.close();
    }
}

function ab_openHref(href, title) {
    if (ab_isHasFrame()) {
        creatIframe(href, title);

    } else {
        ab_open(href);
    }
}

function ab_safeHref(href, title) {
    if (ab_isHasFrame()) {
        creatIframe(href, title);

    } else {
        location.replace(href);
    }
}

function ab_isHasFrame() {
    var topWindow = $(window.parent.document);
    var iframe = topWindow.find('#iframe_box .show_iframe');
    return iframe && iframe.length;
}

function ab_ajax(url, callback, complete, dt) {
    var opts = url.constructor == Object ? url : {"url": url};
    callback = callback || function (data, dt) {
            return ab_ajaxCallback(data, undefined, undefined, undefined, dt);
        }
    opts.success = complete ? function (data) {
        if (dt) {
            data = callback(data, dt);
            if (data === undefined) {
                return;
            }

        } else {
            callback(data);
        }

        complete(data, dt);

    } : callback;
    opts.error = function () {
        layer.alert(ab_lang_map.request_fail, {icon: 3});
    }

    $.ajax(opts);
}

function ab_formData($form, excludes) {
    var data = {};
    var $is = $('[name]', $form);
    if ($is && $is.length) {
        $is.each(function () {
            var $i = $(this);
            var n = $i.attr('name');
            if (!excludes || !excludes[n]) {
                data[n] = $i.val();
            }
        });
    }

    return data;
}

function ab_formSetData($form, data, excludes) {
    for (var n in data) {
        if (!excludes || !excludes[n]) {
            var v = data[n];
            $v = $('[name="' + n + '"]', $form);
            if ($v && $v.length) {
                $v.val(v);

            } else {
                $form.append('<input type="hidden" name="' + n + '" value="' + v + '">');
            }
        }
    }
}

var ab_lang_map = {};
ab_lang_map.request_fail = "请求失败";
ab_lang_map.option_success = "操作成功";
ab_lang_map.option_fail = "操作失败";
ab_lang_map.option_uncomplete = "操作未完成";

function ab_ajaxCallbackBefore(json, $form, $tForm, callback) {
    try {
        var data = $.parseJSON(json);
        var url = data.url;
        if (url !== undefined && url !== '' && url !== 0) {
            if (!url || url === 1) {
                url = location.href;
            }

            var nav_url = function () {
                if (url === undefined) {
                    return;
                }

                var open = data.open;
                if (open) {
                    ab_safeHref(url, open);

                } else {
                    location.replace(url);
                }

                nav_url = undefined;
            }

            var time = parseInt(data.time);
            time = isNaN(time) ? 0 : time;
            if (time < 0) {
                nav_url();
                return;

            } else {
                if (!data.end) {
                    data.end = nav_url;
                }

                setTimeout(nav_url, time === 0 ? 3000 : time);
            }
        }

        $tForm = $tForm || $form;
        if ($tForm && data.errors) {
            if (callback && data.errors.verifyCode) {
                var $verifyCode = $('[name="verifyCode"]', $tForm);
                if (!($verifyCode && $verifyCode.length) || $verifyCode.attr('type') === 'hidden') {
                    if (Object.getOwnPropertyNames(data.errors).length == 1) {
                        if (!data.verifyUrl) {
                            data.verifyUrl = ab_com.verify || (ab_com.route + 'portal/verify');
                        }

                    } else {
                        delete data.errors.verifyCode;

                    }
                }
            }

            var v = $tForm.data('validator') || $tForm.validator();
            if (v) {
                v.showErrors(data.errors);
            }
        }

        if ($tForm && data.verifyUrl) {
            $.get(data.verifyUrl, {}, function (data) {
                var width = $(window).width() - 32;
                if (width > 320) {
                    width = 320;
                }

                var l = layer.open({
                    type: 1,
                    title: 0,
                    area: [width + 'px', 'auto'],
                    content: data,
                });
                var $layer = $('#layui-layer' + l);
                if ($layer && $layer.length) {
                    ab_init($layer);
                    if ($.fn.ab_toggles) {
                        $f = $('form', $layer);
                        if ($f && $f.length) {
                            var $is = $('[name]', $f);
                            if ($is && $is.length) {
                                var submitHandler = function (data) {
                                    ab_formSetData($form, data || ab_formData($form));

                                    layer.close(l);
                                    ab_ajaxSubmit($form, callback, $tForm);
                                };

                                if ($f.attr('action')) {
                                    $.fn.ab_toggles['form']($f, function (json, $form, $tForm, callback) {
                                        var data = ab_ajaxCallbackBefore(json, $form, $tForm, callback);
                                        if (data.ok) {
                                            submitHandler(data.verifies);

                                        } else {
                                            ab_ajaxCallbackData(data);
                                        }
                                    });

                                } else {
                                    $.fn.ab_toggles['validator']($f, submitHandler);
                                }
                            }
                        }
                    }
                }
            });

            return;
        }

        if (data.click) {
            var $click = $tForm ? $(data.click, $tForm) : $(data.click);
            if ($click && $click.length) {
                $click.click();
            }

        } else {
            if ($tForm) {
                var $verifyCode = $('.verifyCode', $tForm);
                if ($verifyCode && $verifyCode.length) {
                    $verifyCode.click();
                }
            }
        }

        if (data.idleTime) {
            var $btn = $tForm ? $(data.idleButton, $tForm) : $(data.idleButton);
            if ($btn && $btn.length) {
                ab_buttonIdle($btn, data.idleTime);
            }
        }

        if ($tForm && data.tip) {
            ab_init($tForm.prepend(data.tip).children(":first"));
        }

        return data;

    } catch (e) {
        layer.alert("Parse Json Error", {icon: 2});
        throw e;
    }
}

function ab_ajaxCallbackData(data, dt) {
    if (data) {
        var icon = data.icon;
        if (dt && (!icon || icon === 1)) {
            return data;
        }

        var message = data.message;
        if (!message) {
            switch (icon) {
                case 1:
                    message = ab_lang_map.option_success;
                    break;
                case 2:
                    message = ab_lang_map.option_fail;
                    break;
                default:
                    message = ab_lang_map.option_uncomplete;
                    break;
            }
        }

        var opts = {icon: icon};
        if (data.end) {
            opts.end = data.end;
        }

        layer.alert(message, opts);
    }
}

function ab_ajaxCallback(json, $form, $tForm, callback, dt) {
    return ab_ajaxCallbackData(ab_ajaxCallbackBefore(json, $form, $tForm, callback), dt);
}

function ab_ajaxSubmit($form, callback, $tForm) {
    callback = callback || ab_ajaxCallback;
    var load = $form.attr('load');
    if (load !== undefined) {
        load = layer.load(load);
    }

    var _callback = function (data) {
        if (load) {
            layer.close(load);
        }

        callback(data, $form, $tForm, callback);
    }

    $form.ajaxSubmit({
        //iframe: true,
        success: _callback,
        error: _callback
    });
};

function ab_submit($form, att, value, attrs) {
    if (att) {
        if (att.constructor === Object) {
            for (var key in att) {
                $input = $("[name='" + key + "']", $form);
                if ($input) {
                    $input.val(att[key])
                }
            }

        } else {
            $input = $("[name='" + att + "']", $form);
            if ($input) {
                $input.val(value)
            }
        }
    }

    if (attrs && attrs.constructor === Object) {
        for (var key in attrs) {
            var _val = $form.attr(key);
            $form.attr(key, attrs[key]);
            attrs[key] = _val;
        }

    } else {
        attrs = undefined;
    }

    $form.submit();
    if (attrs) {
        for (var key in attrs) {
            var val = attrs[key];
            if (val === undefined) {
                $form.removeAttr(key);

            } else {
                $form.attr(key, val);
            }
        }
    }
}

function ab_submitAttrs(attrs, node) {
    var $form = $(node ? node : this).closest('form');
    if ($form && $form.length) {
        ab_submit($form, null, null, attrs);
    }
}

function ab_submitOption(option, node) {
    var $form = $(node ? node : this).closest('form');
    if ($form && $form.length) {
        var $option = $("[name='!submitOption']", $form);
        if (!$option || $option.length == 0) {
            $option = $('<input name="!submitOption" type="hidden"/>');
            $form.append($option);
        }

        if ($option && $option.length) {
            $option.val(option);
            $form.submit();
            $option.val('');
        }
    }
}

function ab_init(ui) {

}

function ab_ajaxLoad($node, url) {
    ab_ajax(url, function (html) {
        ab_init($node.html(html));
    });
}

var ab_com = {};
$(function () {
    var $am = $('#ab_com');
    if ($am && $am.length) {
        var atts = $am[0].attributes;
        var len = atts.length;
        for (var i = 0; i < len; i++) {
            var att = atts[i];
            ab_com[att.name] = att.value;
        }
    }
});