<%@ include file="../../common/edit.jsp"%>
<%
	String value = input.attr("value");
	input.before("<c:set var=\"value\" value=\"" + value + "\"/>");
	input.attr("value", DeveloperCode.print("WebJsplUtils.paramsValue(pageContext.getAttribute(\"value\"))"));
%>