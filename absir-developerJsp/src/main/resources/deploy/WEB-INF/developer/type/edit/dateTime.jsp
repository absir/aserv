<%
	String dateTime = ((Element) request.getAttribute("input")).attr("value");
%>
<%@ include file="date.jsp"%>
<%
	input.before(ScripteNode.node("<input name=\"" + input.attr("name") + "\" class=\"dateTime\" value=\"${value}\" type=\"hidden\" size=\"30\"/>"));
	input.removeAttr("name");
%>
