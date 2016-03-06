<%@ include file="../../common/edit.jsp"%>
<%
	input.removeAttr("size");
	input.attr("type", "checkbox");
	String value = input.attr("value");
	input.attr("value", "true");
	element.before("<c:set var=\"value\" value=\"" + value + "\"/>");
	input.attr("${value ? \"checked\" : \"\"}", "true");
	element.after("<input type=\"hidden\" name=\"" + input.attr("name")+ "\" value=\"false\">");
%>