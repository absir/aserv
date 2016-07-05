/**
 * Created by absir on 16/6/26.
 */
function ab_group($node, toggle) {
    var tog = $node.attr(toggle);
    if (tog) {
        var $group = $node.parents("[ab_group='ab_main'], .ab_main");
        return ab_groupSel($group, "[ab_group='" + tog + "'], ." + tog);
    }

    return $node.parents("[ab_group='" + toggle + "'], ." + toggle);
}

function ab_groupSel($group, sel) {
    return $group && $group.length > 0 ? $(sel, $group) : $(sel);
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

    $param = ab_groupSel($group, sel);
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

function ab_openHref(href, title) {
    if (ab_isHasFrame()) {
        creatIframe(href, title);

    } else {
        ab_open(href);
    }
}

function ab_isHasFrame() {
    var topWindow = $(window.parent.document);
    var iframe = topWindow.find('#iframe_box .show_iframe');
    return iframe && iframe.length > 0;
}

function ab_ajax(url, callback) {
    var opts = url.constructor == Object ? url : {"url": url};
    opts.success = callback || ab_ajaxCallback;
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

var $_ab_ajax_iframe;

function ab_ajaxSubmit(form, callback) {
    var $document = $(document.documentElement);
    var $iframe = $_ab_ajax_iframe;
    if (!$iframe) {
        $_ab_ajax_iframe = $iframe = $document.append('<iframe id="ab_ajax_iframe" name="ab_ajax_iframe" src="about:blank" style="display:none"></iframe>').children().last();
    }

    var $form = $(form);
    if ($form.attr('ab_ajax')) {
        throw new Error("submit in ajax");
    }

    $form.attr('ab_ajax', 1);
    form = $form[0];
    var target = form.target;
    form.target = "ab_ajax_iframe";
    $iframe.bind("load", function (event) {
        $iframe.unbind("load");
        $document.trigger("ajaxStop");
        var src = $iframe.attr('src');
        if (src == "javascript:'%3Chtml%3E%3C/html%3E';" || // For Safari
            src == "javascript:'<html></html>';") { // For FF, IE
            return;
        }

        var iframe = $iframe[0];
        var doc = iframe.contentDocument || iframe.document;

        // fixing Opera 9.26,10.00
        if (doc.readyState && doc.readyState != 'complete') return;
        // fixing Opera 9.64
        if (doc.body && doc.body.innerHTML == "false") return;

        var response;
        if (doc.XMLDocument) {
            // response is a xml document Internet Explorer property
            response = doc.XMLDocument;
        } else if (doc.body) {
            try {
                response = $iframe.contents().find("body").text();
                // response = jQuery.parseJSON(response);
            } catch (e) { // response is html document or plain text
                response = doc.body.innerHTML;
            }
        } else {
            // response is a xml document
            response = doc;
        }

        (callback || ab_ajaxCallback)(response);
    });

    $form.trigger('submit');
    $form.removeAttr('ab_ajax');
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

function ab_submitOption(option, node) {
    var $form = $(node ? node : this).parents('form');
    if ($form && $form.length > 0) {
        var $option = $("[name='!submitOption']", $form);
        if ($option && $option.length > 0) {
            $option.val(option);
            $form.submit();
            $option.val('');
        }
    }
}