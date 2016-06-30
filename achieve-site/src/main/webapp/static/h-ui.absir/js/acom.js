/**
 * Created by absir on 16/6/26.
 */
function ab_group($node, toggle) {
    var tog = $node.attr(toggle);
    if (tog) {
        var $group = $node.parents("[ab_toggle='ab_main'], .ab_main");
        if ($group && $group.length > 0) {
            return ab_group_sel($group, "[ab_toggle='" + tog + "'], ." + tog);
        }
    }

    return $node.parents("[ab_toggle='" + toggle + "'], ." + toggle);
}

function ab_group_sel($group, sel) {
    return $group && $group.length > 0 ? $(sel, $group) : $(sel);
}

function ab_formSubmit($form, att, value, attrs) {
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

function ab_isHasFrame() {
    var topWindow = $(window.parent.document);
    var iframe = topWindow.find('#iframe_box .show_iframe');
    return iframe && iframe.length > 0;
}

function ab_open(href) {
    window.open(href);
}

function ab_openHref(href, title) {
    if (ab_isHasFrame()) {
        creatIframe(href, title);

    } else {
        ab_open(href);
    }
}

function ab_getParam(sel, node, json) {
    $this = $(node ? node : this);
    $group = ab_group($this, 'ab_param_grp');
    if (!json) {
        if (sel[0] === '$') {
            json = true;
            sel = sel.substring(1);
        }
    }

    $param = ab_group_sel($group, sel);
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

function ab_ajaxUrl(url) {
    var opts = url.constructor == Object ? url : {"url": url};
    opts.success = ab_ajaxCallback;
    opts.error = function () {
        layer.alert("请求失败", {icon: 3});
    }

    $.ajax(opts);
}

function ab_ajaxCallback(json) {
    try {
        var data = $.evalJSON(json);
        var url = data.url;
        if (url !== undefined) {
            if (!url) {
                url = location.href;
            }

            location.replace(url);
        }

        var icon = data.code;
        var message = data.message;
        if (!message) {
            switch (icon) {
                case 0:
                    message = "操作成功";
                    break;
                case 2:
                    message = "操作失败";
                    break;
                default:
                    message = "操作未完成";
                    break;
            }
        }

        layer.alert(message, {icon: icon});

    } catch (e) {
        layer.alert("Parse Json Error", {icon: 2});
        //throw e;
    }
}