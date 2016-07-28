/**
 * Created by absir on 16/6/26.
 */
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
    return $group && $group.length > 0 ? $(sel, $group) : $(sel);
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

    return vals && vals.length > 0 ? $.toJSON(vals) : undefined;
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
    return iframe && iframe.length > 0;
}

function ab_ajax(url, callback, complete) {
    var opts = url.constructor == Object ? url : {"url": url};
    callback = callback || ab_ajaxCallback;
    opts.success = complete ? function (data) {
        callback(data), complete(data)
    } : callback;
    opts.error = function () {
        layer.alert("请求失败", {icon: 3});
    }

    $.ajax(opts);
}

var ab_lang_map = {};
ab_lang_map.option_success = "操作成功";
ab_lang_map.option_fail = "操作失败";
ab_lang_map.option_uncomplete = "操作未完成";

function ab_ajaxCallback(json, $form) {
    try {
        var data = $.evalJSON(json);
        var url = data.url;
        if (url !== undefined && url !== '' && url !== 0) {
            if (!url || url === 1) {
                url = location.href;
            }

            var open = data.open;
            if (open) {
                ab_safeHref(url, open);

            } else {
                location.replace(url);
            }

            return;
        }

        if ($form && data.errors) {
            $form.data('validator').showErrors(data.errors);
        }

        if (data.click) {
            var $click = $form ? $(data.click, $form) : $(data.click);
            if ($click) {
                $click.click();
            }
        }

        var icon = data.icon;
        var message = data.message;
        if (!message) {
            switch (icon) {
                case 0:
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

        layer.alert(message, {icon: icon});

    } catch (e) {
        layer.alert("Parse Json Error", {icon: 2});
        throw e;
    }
}

function ab_ajaxSubmit($form, callback, $tForm) {
    $form.ajaxSubmit({
        //iframe: true,
        success: function (data) {
            (callback || ab_ajaxCallback)(data, $tForm || $form);
        },
        error: function (data) {
            (callback || ab_ajaxCallback)(data, $tForm || $form);
        }
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
    if ($form && $form.length > 0) {
        ab_submit($form, null, null, attrs);
    }
}

function ab_submitOption(option, node) {
    var $form = $(node ? node : this).closest('form');
    if ($form && $form.length > 0) {
        var $option = $("[name='!submitOption']", $form);
        if (!$option || $option.length == 0) {
            $option = $('<input name="!submitOption" type="hidden"/>');
            $form.append($option);
        }

        if ($option && $option.length > 0) {
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