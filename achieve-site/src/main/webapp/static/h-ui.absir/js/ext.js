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

function ab_openHref(href, title) {
    if (ab_isHasFrame()) {
        creatIframe(href, title);

    } else {
        window.open(href);
    }
}

function ab_getParam(sel, node) {
    $this = $(node ? node : this);
    $group = ab_group($this, 'ab_param_grp');
    $param = ab_group_sel($group, sel);
    var len = $param.length;
    if (len == 0) {
        return undefined;

    } else if (len == 1) {
        var val = $param.val();
        return val ? val : $param.attr('value');

    } else {
        var params = "";
        $param.each(function () {
            var $t = $(this);
            var val = $t.val();
            if (!val) {
                val = $t.attr('value');
            }
            if (params) {
                params += "," + val;

            } else {
                params = val;
            }
        });

        return params;
    }
}

$(function () {
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