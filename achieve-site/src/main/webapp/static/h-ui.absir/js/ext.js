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

$(function () {
    $("[ab_click]").each(function () {
        var $this = $(this);
        var confirm = $this.attr('ab_confirm');
        var noParam = $this.attr('ab_noParam');
        if (!noParam) {
            noParam = "请先选择对象";
        }

        var ab_click = $this.attr('ab_click');
        var ab_eval_params = new Array();
        var i = 0;
        var len = ab_click.length;
        while (i < len) {
            var pos = ab_click.indexOf("$P{", i);
            if (pos >= 0) {
                ab_eval_params.push(ab_click.substring(i, pos));
                i = pos;
                pos = ab_click.indexOf("}", i);
                if (pos >= 0) {
                    ab_eval_params.push(ab_click.substring(i + 3, pos));
                    i = pos + 1;

                } else {
                    break;
                }

            } else {
                ab_eval_params.push(ab_click.substring(i));
                break;
            }
        }

        len = ab_eval_params.length;
        $this.click(function () {
            var sel;
            var ab_click = ab_eval_params[0];
            for (var i = 1; i < len; i++) {
                var param = ab_getParam(ab_eval_params[i]);
                if (param === undefined) {
                    layer.alert(noParam, {icon: 2});
                    return;
                }

                ab_click += param;
                if (++i < len) {
                    ab_click += ab_eval_params[i];
                }
            }

            if (confirm) {
                layer.confirm(confirm, function (index) {
                    layer.close(index);
                    eval(ab_click);

                })

            } else {
                eval(ab_click);
            }
        });
    });

    $(".ab_sel").each(function () {
        var $this = $(this);
        $group = ab_group($this, 'ab_sel_grp');
        var multi = $group.attr('multi_sel');
        $this.click($group.attr('multi_sel') ? function () {
            $this.toggleClass('ab_sel_select');

        } : function () {
            var select = $this.hasClass('ab_sel_select');
            ab_group_sel($group, '.ab_sel_select').removeClass('ab_sel_select');
            if (!select) {
                $this.addClass('ab_sel_select');
            }
        });
    });

    if ($.fn.DataTable) {
        var $_fnDataTableExt = $.fn.DataTable.ext;
        var _pageButton = $_fnDataTableExt.renderer.pageButton._;
        var _fnBindAction = $_fnDataTableExt.internal._fnBindAction;
        $_fnDataTableExt.renderer.pageButton.formRender = function (settings, host, idx, buttons, page, pages) {
            var aPage = settings.aPage
            if (!aPage) {
                settings.aPage = aPage = {};
                var $table = $(settings.nTable);
                aPage.pageSize = $table.attr('pageSize');
                aPage.pageIndex = $table.attr('pageIndex');
                aPage.pageCount = $table.attr('pageCount');
                aPage.totalCount = $table.attr('totalCount');
                $group = ab_group($table, 'ab_form_grp');
                aPage.form = ab_group_sel($group, '.ab_pageForm');
            }

            page = aPage.pageIndex - 1;
            pages = aPage.pageCount;
            buttons = $_fnDataTableExt.pager[settings.sPaginationType](page, pages);
            _pageButton(settings, host, idx, buttons, page, pages);

            if (aPage.form) {
                var clickHandler = function (e) {
                    var evt = e.data;
                    console.log(evt);
                    var pageIndex = aPage.pageIndex;
                    if (evt === "previous") {
                        pageIndex--;

                    } else if (evt === "next") {
                        pageIndex++;

                    } else {
                        pageIndex = evt + 1;
                    }

                    ab_formSubmit(aPage.form, 'pageIndex', pageIndex);
                };

                var i = 0, j = -2, l = 0;
                var pb;
                $('a', host).each(function () {
                    while (true) {
                        if (j === -2) {
                            pb = buttons[i];
                            j = $.isArray(pb) ? (pb.length - 1) : -1;
                            if (j > 0) {
                                l = j;
                                j = 0;
                            }
                        }

                        var button;
                        if (j < 0) {
                            button = pb;
                            j = -2;
                            i++;

                        } else {
                            button = pb[j++];
                            if (j > l) {
                                j = -2;
                                i++;
                            }
                        }

                        if (button !== "ellipsis") {
                            break;
                        }
                    }

                    var $this = $(this);
                    $this.unbind();
                    _fnBindAction($this, button, clickHandler);
                });
            }
        }

        $(".ab_page_table").each(function () {
            var $this = $(this);
            var opts = {
                "processing": true,
                "bAutoWidth": false,
                "bSort": false,
                "renderer": "formRender",
                "aLengthMenu": [20, 50, 100, 200],
                "aoColumnDefs": [{
                    "bSortable": false,
                    "aTargets": [0]
                }],
            }

            opts.iDisplayLength = $this.attr('pageSize');
            var $dataTable = $this.DataTable(opts);
            var settings = $dataTable.context[0];
            $dataTable.on('length', function () {
                if (settings.aPage) {
                    ab_formSubmit(settings.aPage.form, 'pageSize', settings._iDisplayLength);
                }
            });
        });
    }

});