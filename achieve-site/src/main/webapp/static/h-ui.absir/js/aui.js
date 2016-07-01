/**
 * Created by absir on 16/6/30.
 */
$(function () {
    var abToggle = {};
    $.fn.ab_toggle = abToggle;
    $.fn.ab_toggle_fun = function (ui) {
        (ui ? $("[ab_toggle]", $(ui)) : $("[ab_toggle]")).each(function () {
            var $this = $(this);
            var name = $this.attr('ab_toggle');
            if (name) {
                var toggle = abToggle[name];
                if (toggle && toggle.constructor == Function) {
                    toggle($this);
                }
            }
        });
    };

    setTimeout($.fn.ab_toggle_fun, 1);

    abToggle['sel'] = function ($this) {
        $group = ab_group($this, 'ab_selGroup');
        var multi = $group.attr('ab_multi');
        $this.click(multi ? function () {
            $this.toggleClass('ab_sel_select');

        } : function () {
            var select = $this.hasClass('ab_sel_select');
            ab_groupSel($group, '.ab_sel_select').removeClass('ab_sel_select');
            if (!select) {
                $this.addClass('ab_sel_select');
            }
        });
    };

    abToggle['click'] = function ($this) {
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
    };

    if ($.fn.iCheck) {
        abToggle[iCheck] = function ($this) {
            var $div = $this.parent().append('<div class="check-box"></div>').children("div");
            $this.remove();
            $div.append($this);
            $div.append('&nbsp;');
            $div.iCheck({
                checkboxClass: 'icheckbox',
                radioClass: 'iradio-blue',
                increaseArea: '20%'
            });
        };
    }

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
                $group = ab_group($table, 'ab_pageGroup');
                aPage.form = ab_groupSel($group, '.ab_pageForm');
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

                    ab_submit(aPage.form, 'pageIndex', pageIndex);
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

        abToggle['tableForm'] = function ($this) {
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
                    ab_submit(settings.aPage.form, 'pageSize', settings._iDisplayLength);
                }
            });
        };
    }
});