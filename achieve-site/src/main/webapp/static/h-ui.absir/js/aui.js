/**
 * Created by absir on 16/6/30.
 */
function ab_evalParams(expr) {
    var evalParams = new Array();
    var i = 0;
    var len = expr.length;
    while (i < len) {
        var pos = expr.indexOf("$P{", i);
        if (pos >= 0) {
            evalParams.push(expr.substring(i, pos));
            i = pos;
            pos = expr.indexOf("}", i);
            if (pos >= 0) {
                evalParams.push(expr.substring(i + 3, pos));
                i = pos + 1;

            } else {
                break;
            }

        } else {
            evalParams.push(expr.substring(i));
            break;
        }
    }

    return evalParams;
}

function ab_evalRequire(evalParams, noParam) {
    var len = evalParams.length;
    var require = evalParams[0];
    for (var i = 1; i < len; i++) {
        var param = ab_getParam(evalParams[i]);
        if (param === undefined) {
            layer.alert(noParam, {icon: 2});
            return undefined;
        }

        require += param;
        if (++i < len) {
            require += evalParams[i];
        }
    }

    return require;
}

ab_lang_map.first_select_target = "请先选择对象";
ab_lang_map.format_error = "格式错误";
ab_lang_map.confirm_error = "内容不一致";

$(function () {
        $.fn.ab_eval = function (expr) {
            eval(expr);
        };

        var _ab_init_ = ab_init;
        ab_init = function (ui) {
            if (_ab_init_) {
                _ab_init_(ui);
            }

            $.fn.ab_toggle_fun(ui);
        }

        var abToggles = {};
        $.fn.ab_toggles = abToggles;
        var abValidates = {};
        $.fn.ab_validates = abValidates;
        if ($.validator) {
            $.validator.addMethod('ab_validate', function (value, element, param) {
                if (param && param.length > 1) {
                    var validate = param[0];
                    if (validate && typeof validate == 'function') {
                        return this.optional(element) || validate(value, element);
                    }
                }

                return true;

            }, '{1}');
        }

        $.fn.ab_toggle_fun = function (ui) {
            (ui ? $("[ab_toggle]", $(ui)) : $("[ab_toggle]")).each(function () {
                var $this = $(this);
                var name = $this.attr('ab_toggle');
                if (name) {
                    var toggle = abToggles[name];
                    if (toggle && toggle.constructor == Function) {
                        toggle($this);
                    }
                }
            });
        };

        setTimeout($.fn.ab_toggle_fun, 1);

        abToggles['sel'] = function ($this) {
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

        abToggles['click'] = function ($this) {
            var confirm = $this.attr('ab_confirm');
            var noParam = $this.attr('ab_noParam');
            if (!noParam) {
                noParam = ab_lang_map.first_select_target;
            }

            var evalParams = ab_evalParams($this.attr('ab_click'));
            $this.click(function () {
                var require = ab_evalRequire(evalParams, noParam);
                if (require == undefined) {
                    return;
                }

                if (confirm) {
                    layer.confirm(confirm, function (index) {
                        layer.close(index);
                        $this.ab_eval(require);
                    });

                } else {
                    $this.ab_eval(require);
                }
            });
        };

        abToggles['check'] = function ($this) {
            $input = $('[type=hidden]', $this.parent());
            if ($input && $input.length) {
                $this.change(function () {
                    $input.attr('value', $this.prop('checked') ? 1 : 0);
                });
            }
        };

        if ($.fn.iCheck) {
            abToggles['iCheck'] = function ($this) {
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

            abToggles['tableForm'] = function ($this) {
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

                $('[ab_order]', $this).click(function () {
                    var $this = $(this);
                    ab_submit(settings.aPage.form, {
                        "orderField": $this.attr('ab_order'),
                        "orderDirection": $(this).hasClass('sorting_asc') ? 'desc' : 'asc'
                    });
                });
            };
        }

        var UEI = 0;
        if (typeof(UE) === "object") {
            abToggles['UE'] = function ($this) {
                var id = $this.attr('id');
                if (!id) {
                    id = "__ab_ue_" + (++UEI);
                    $this.attr('id', id);
                }

                UE.getEditor(id, {autoHeightEnabled: false});
            }
        }

        abToggles['validator'] = function ($this, submitHandler) {
            var opt = {success: "valid"};
            if (submitHandler) {
                opt.submitHandler = submitHandler;
            }

            $inputs = $('[ab_validate],[error]', $this);
            if ($inputs && $inputs.length) {
                var rules = {};
                var messages = {};
                $inputs.each(function () {
                    var $input = $(this);
                    var validate = abValidates[$input.attr('ab_validate')];
                    if (validate) {
                        var name = $input.attr('name');
                        if (name) {
                            var param = validate($this, $input, name);
                            if (param) {
                                rules[name] = {
                                    ab_validate: param
                                };
                            }
                        }

                    } else {
                        var error = $input.attr('error');
                        if (error) {
                            var name = $input.attr('name');
                            if (name) {
                                messages[name] = error;
                            }
                        }
                    }
                });

                if (!$.isEmptyObject(rules)) {
                    opt.rules = rules;
                }

                if (!$.isEmptyObject(messages)) {
                    opt.messages = messages;
                }
            }

            $this.validate(opt);
        };

        abToggles['reset'] = function ($this) {
            setTimeout(function () {
                $this[0].reset();
            }, 1);
        };

        abToggles['form'] = function ($this, callback) {
            function submitHandler() {
                if ($this.attr('ab_ajax')) {
                    return true;
                }

                try {
                    ab_ajaxSubmit($this, callback || $this.attr('ab_callback'));

                } catch (e) {
                    console.error(e);
                }

                return false;
            };

            if ($this.attr('ab_reset')) {
                abToggles['reset']($this);
            }

            if ($.fn.validate && $this.attr('ab_validator')) {
                abToggles['validator']($this, submitHandler);

            } else {
                $this.submit(submitHandler);
            }
        };

        abToggles['resize'] = function ($this) {
            var resize = $this.attr('resize');
            var minSize = $this.attr('minSize');
            var maxSize = $this.attr('maxSize');
            var offSize = $this.attr('offSize') || 0;

            function reszieFun() {
                var width = $(window).width();
                var toSize = (width < minSize || width > maxSize) ? (width - offSize) : undefined;
                if (resize === 'style') {
                    if (toSize) {
                        $this.css('width', null);

                    } else {
                        $this.css('width', toSize + "px");
                    }

                } else {
                    if (toSize) {
                        $this.attr(resize, width);

                    } else {
                        $this.removeAttr(resize);
                    }
                }
            };

            $(window).bind('resize', reszieFun);
            reszieFun();
        };

        abToggles['addItem'] = function ($this) {
            var $num = $this.parent().find('.num-add');
            var $table = $this.closest('table');
            var $archetype = $table.find('.archetype');
            var $tbody = $table.find('tbody');
            var html = $archetype.prop("outerHTML");
            html = html.replace('<!--archetype', '');
            html = html.replace('archetype-->', '');
            $this.click(function () {
                var num = $num ? $num.val() : 1;
                while (num > 0) {
                    num--;
                    var $tr = $(html).appendTo($tbody);
                    $tr.removeClass('archetype');
                    $tr.find('[aname]').each(function () {
                        var $this = $(this);
                        var name = $this.attr('aname');
                        name = name.replace('#for_index#', '');
                        $this.attr('name', name);
                        $this.removeAttr('aname');
                    });

                    ab_init($tr);
                }
            });
        };

        abToggles['removeItem'] = function ($this) {
            $this.click(function () {
                var $tr = $this.closest('tr');
                $tr.remove();
            })
        };

        abToggles['upItem'] = function ($this) {
            $this.click(function () {
                var $tr = $this.closest('tr');
                var $pre = $tr.prev();
                if ($pre || $pre.is('tr')) {
                    $tr.insertBefore($pre);
                }
            })
        };

        abToggles['checkAll'] = function ($this) {
            var target = $this.attr('target');
            if (target) {
                $this.change(function () {
                    $param = ab_groupParam("[name='" + target + "']", $this);
                    $param.prop('checked', $this.prop('checked'));
                });
            }
        };

        abToggles['stop'] = function ($this) {
            $this.click(function (e) {
                e.stopPropagation();
            });
        };

        abToggles['open'] = function ($this) {
            var href = $this.attr('_href');
            var title = $this.attr('title');
            $this.click(function (e) {
                ab_openHref(href, title);
            });
        };

        abToggles['subForm'] = function ($this) {
            var $form = $this.closest('form');
            if ($form && $form.length) {
                var $group = ab_group($this, 'subForm');
                if ($group && $group.length) {
                    var $inputs = $('[name]', $group);
                    if ($inputs && $inputs.length) {
                        var tExclude = $this.attr('exclude');
                        var gExclude = $group.attr('exclude');
                        var exclude = ',' + (tExclude ? (tExclude + ',') : '') + (gExclude ? (gExclude + ',') : '');
                        if (exclude.length > 1) {
                            var $includes = new Array();
                            $inputs.each(function () {
                                var $input = $(this);
                                if (exclude.indexOf(',' + $input.attr('name') + ',') < 0) {
                                    $includes.push($input);
                                }
                            });

                            $inputs = $($includes);
                        }

                        var validator = $form.data('validator');
                        $this.bind('click', function () {
                            if (validator) {
                                var errors = false;
                                $inputs.each(function () {
                                    if (!validator.element('[name=' + $(this).attr('name') + ']')) {
                                        errors = true;
                                    }
                                });

                                if (errors) {
                                    return;
                                }
                            }

                            $nForm = $('<form></form>');
                            var atts = $group[0].attributes;
                            var len = atts.length;
                            for (var i = 0; i < len; i++) {
                                var att = atts[i];
                                $nForm.attr(att.name, att.value);
                            }

                            if (!$nForm.attr('action')) {
                                var action = $this.attr('action');
                                if (!action) {
                                    action = $form.attr('action');
                                }

                                if (action) {
                                    $nForm.attr('action', action);
                                }
                            }

                            $inputs.each(function () {
                                $nForm.append($(this).clone());
                            });

                            ab_ajaxSubmit($nForm, $group.attr('ab_callback'), $form);
                        });
                    }
                }
            }
        };

        abToggles['close'] = function ($this) {
            var $target = ab_group($this, 'ab_close');
            if ($target) {
                $this.bind('click', function () {
                    $target.remove();
                });
            }
        };

        abValidates['pattern'] = function ($form, $input, name) {
            var pattern = $input.attr('pattern');
            pattern = '/' + pattern + '/g';
            if (pattern) {
                var error = $input.attr('error');
                if (!error) {
                    error = ab_lang_map.format_error;
                }

                return [function (value, element) {
                    return eval(pattern).test(value);

                }, error];
            }
        };

        abValidates['confirm'] = function ($form, $input, name) {
            var confirm = $input.attr('confirm');
            if (confirm) {
                var $confirm = $('[name="' + confirm + '"]', $form);
                if ($confirm && $confirm.length) {
                    var error = $input.attr('error');
                    if (!error) {
                        error = ab_lang_map.confirm_error;
                    }

                    return [function (value, element) {
                        return value === $confirm.val();

                    }, error];
                }
            }
        };

    }
);