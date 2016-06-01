/**
 * Created by absir on 16/2/4.
 */
$.extend($.expr[':'],
    {
        CurrentNavtab: function (el, i, m) {
            return el === $.CurrentNavtab[0];
        },

        CurrentDialog: function (el, i, m) {
            return el === $.CurrentDialog[0];
        },
    });

$(function () {
    $.fn.tab.Constructor.prototype.show = function () {
        var $this = this.element
        var $ul = $this.closest('ul:not(.dropdown-menu)')
        var selector = $this.data('target')

        if (!selector) {
            selector = $this.attr('href')
            selector = selector && selector.replace(/.*(?=#[^\s]*$)/, '') // strip for ie7
        }

        if ($this.parent('li').hasClass('active')) return

        var $previous = $ul.find('.active:last a')
        var hideEvent = $.Event('hide.bs.tab', {
            relatedTarget: $this[0]
        })
        var showEvent = $.Event('show.bs.tab', {
            relatedTarget: $previous[0]
        })

        $previous.trigger(hideEvent)
        $this.trigger(showEvent)

        if (showEvent.isDefaultPrevented() || hideEvent.isDefaultPrevented()) return

        var $target
        if (selector === '#') {
            var index = $this.parent().index()
            $target = $ul.siblings('.tab-content').find('.tab-pane').eq(index);

        } else {
            $target = $(selector)
        }

        this.activate($this.closest('li'), $ul)
        this.activate($target, $target.parent(), function () {
            $previous.trigger({
                type: 'hidden.bs.tab',
                relatedTarget: $this[0]
            })
            $this.trigger({
                type: 'shown.bs.tab',
                relatedTarget: $previous[0]
            })
        })
    }

    $(document).on('click.itemDetail', '[data-toggle="ab_add_item"]', function (e) {
        e.preventDefault();
        var $this = $(this);
        var $num = $this.parent().find('.num-add');
        var num = $num ? $num.val() : 1;
        var $table = $this.closest('table');
        var $archetype = $table.find('.archetype');
        var $tbody = $table.find('tbody');
        var html = $archetype.prop("outerHTML");
        html = html.replace('<!--archetype', '');
        html = html.replace('archetype-->', '');
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

            $tr.initui();
        }
    });

    $(document).on('click.itemDetail', '[data-toggle="ab_remove_item"]', function (e) {
        e.preventDefault();
        var $this = $(this);
        var $tr = $this.closest('tr');
        $tr.remove();
    });

    $(document).on('click.ab_submit_option', '[data-submit]', function (e) {
        e.preventDefault();
        var $this = $(this);
        var $form = $this.closest('form');
        var $option = $form.find('[name="!submitOption"]');
        $option.val($this.attr('data-submit'));
        $form.submit();
        $option.val($this.attr(''));
    });

    $(document).on('click.ab_submit_option', '[data-toggle="tab"]', function (e) {
        e.preventDefault();
    });

    String.prototype.replacePlh = function ($box) {
        if (!$box) {
            var $object = $(event.srcElement);
            if ($object.data('unitBox')) {
                $box = $object.closest('.unitBox');
            }

            $box = $box || $(document)
        }

        return this.replace(/{\/?[^}]*}/g, function ($1) {
            var $input = $box.find($1.replace(/[{}]+/g, ''))

            return $input && $input.val() ? $input.val() : $1
        })
    }

    var thatDialogOrNavtab = function () {
        return $.CurrentDialog || $.CurrentNavtab;
    }

    var Bjuiajax = $.fn.bjuiajax.Constructor;
    var Bajaxdone = Bjuiajax.prototype.ajaxdone;
    Bjuiajax.prototype.ajaxdone = function (json) {
        if (json.refresh) {
            if ($.CurrentDialog) {
                setTimeout(function () {
                    BJUI.dialog('refresh')
                }, 100)

            } else if ($.CurrentNavtab) {
                setTimeout(function () {
                    BJUI.navtab('refresh')
                }, 100)
            }
        }

        Bajaxdone(json);
    }

    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1,               //月份
            "d+": this.getDate(),                    //日
            "h+": this.getHours(),                   //小时
            "m+": this.getMinutes(),                 //分
            "s+": this.getSeconds(),                 //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    $(document).on('bjui.afterInitUI', function (e) {
        var $target = $(e.target);
        $target.find('.dateTime').each(function () {
            var $this = $(this);
            var $input = $this.find('.dateTimeInput');
            var time = $input.val();
            if (time) {
                var $picker = $this.find('[data-toggle="datepicker"]');
                if ($picker) {
                    var pattern = $picker.attr('data-pattern');
                    $picker.val(new Date(parseInt(time)).Format(pattern ? pattern : 'yyyy-MM-dd'));
                    var changed = function (e) {
                        e.preventDefault();
                        var date = $picker.val();
                        if (date) {
                            $input.val(new Date(date.replace(/-/g, "/")).getTime());
                        }
                    };

                    $picker.change(changed);
                    $picker.on('afterchange.bjui.datepicker', changed);
                }
            }
        });
    });
});