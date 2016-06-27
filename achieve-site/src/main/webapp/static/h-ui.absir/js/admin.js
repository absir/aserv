/**
 * Created by absir on 16/6/26.
 */
$(function () {
    $(document).on('click.ab_sel', '', function (e) {
        e.preventDefault();
        var $this = $(this);
        $group = $this.parent('ab_sel_grp');
        if ($group.attr('multi_sel')) {
            $this.toggleClass('ab_sel_select');

        } else {
            var select = $this.hasClass('ab_sel_select');
            $('.ab_sel_select', $group).removeClass('ab_sel_select');
            if (!select) {
                $this.addClass('ab_sel_select');
            }
        }
    });


});