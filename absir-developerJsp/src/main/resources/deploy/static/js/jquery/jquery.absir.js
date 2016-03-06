/**
 * @author absir
 */
function abSelectValue(_box) {
	$("select", _box).each(function() {
		var $this = $(this)[0];
		var value = $this.getAttribute("value");
		if (value != null) {
			$this.removeAttribute("value");
			$("option[value='" + value + "']", $this).attr("selected", true);
		}
	});
}

function abInitUI(_box){
	abSelectValue(_box);
}

$(function() {
	abInitUI(false);
});