<%
	Element params = ((Element) request.getAttribute("input"));
	String paramsValue = params.attr("value");
	params.before("<c:set var=\"value\" value=\"" + paramsValue + "\"/>");
	params.attr("value", DeveloperCode.print("WebJsplUtils.paramsValue(pageContext.getAttribute(\"value\"))"));
%>
<%@ include file="text.jsp"%>