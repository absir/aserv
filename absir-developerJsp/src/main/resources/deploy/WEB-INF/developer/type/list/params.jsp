<%@ include file="./base.jsp"%>
<%
String value = element.html();

element.before("<c:set var=\"value\" value=\"" + value + "\"/>");
element.empty();
element.appendChild(ScripteNode.node(DeveloperCode.print("WebJsplUtils.paramsValue(pageContext.getAttribute(\"value\"))")));
%>