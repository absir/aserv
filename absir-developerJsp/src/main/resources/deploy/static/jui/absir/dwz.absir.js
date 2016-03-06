/**
 * @author absir
 */
function dwzRedefine() {
	var abInitUI = window.abInitUI;
	window.abInitUI = function() {
	}

	var initUI = window.initUI;
	window.initUI = function(_box) {
		abInitUI(_box);
		initUI(_box);

		if ($.fn.datepicker) {
			$('input.dateTime', _box).each(function() {
				$this = $(this);
				$date = $this.next();
				$datefmt = $date.attr('datefmt');
				if ($datefmt == undefined) {
					$datefmt = 'yyyy-MM-dd HH:mm:ss';
				}

				(function($this, $date, $datefmt) {
					$date.change(function() {
						var val = $date.val();
						$this.val(val ? val.parseDate($datefmt).getTime() : "");
					});
				})($this, $date, $datefmt);
			});
		}
	}

	DWZ.ajaxHtml = function(json) {
		alert(json);
	}
}

function navTabAjaxErrors(json) {
	if (json.errors instanceof Object) {
		$('form', navTab.getCurrentPanel()).validate().showErrors(json.errors);
	}

	navTabAjaxDone(json)
}

dwzRedefine();
