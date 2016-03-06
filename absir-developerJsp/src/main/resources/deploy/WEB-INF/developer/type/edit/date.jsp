<%@ include file="../../common/edit.jsp"%>
<%
	String value = input.attr("value");
	element.before("<c:set var=\"value\" value=\"" + value + "\"/>");
	boolean search = "search".equals(Scenario.get(request));
	input.attr("value", DeveloperCode.print("WebJsplUtils.dateValue(pageContext.getAttribute(\"value\"), " + (search ? -1 : 0) + ")"));
	if (search || field.getEditable() != JeEditable.LOCKED) {
		input.addClass("date");
		Object dateFmt = field.getMetas().get("dateFmt");
		input.attr("dateFmt", dateFmt == null ? "yyyy-MM-dd HH:mm:ss" : dateFmt.toString());
		input.after("<a class=\"inputDateButton\" href=\"javascript:;\">选择</a>");
	}
%>
