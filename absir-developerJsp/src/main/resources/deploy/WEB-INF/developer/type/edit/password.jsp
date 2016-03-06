<%@ include file="../../common/edit.jsp"%>
<%
	input.attr("name", input.attr("name") + "@");
	input.attr("type", "password");
	input.removeAttr("value");
%>