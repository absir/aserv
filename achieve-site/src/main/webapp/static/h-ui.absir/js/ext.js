/**
 * Created by absir on 16/6/26.
 */
function ab_formSubmit($form, atts, value) {
    if ($.isArray(atts)) {
        for (var key in atts) {
            $input = $("[name='" + key + "']", $form);
            if ($input) {
                $input.val(atts[key])
            }
        }

    } else {
        $input = $("[name='" + atts + "']", $form);
        if ($input) {
            $input.val(value)
        }
    }

    $form.submit();
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

$(function () {
    $(".ab_sel").click(function () {
        var $this = $(this);
        $group = $this.parents('.ab_sel_grp');
        if ($group.attr('multi_sel')) {
            $this.toggleClass('ab_sel_select');

        } else {
            var select = $this.hasClass('ab_sel_select');
            ($group ? $('.ab_sel_select', $group) : $('.ab_sel_select')).removeClass('ab_sel_select');
            if (!select) {
                $this.addClass('ab_sel_select');
            }
        }
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
                $group = $table.parents('.ab_form_grp');
                aPage.form = $group ? $('.ab_pageForm') : $('.ab_pageForm', $group);
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