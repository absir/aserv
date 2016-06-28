/**
 * Created by absir on 16/6/26.
 */
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

});