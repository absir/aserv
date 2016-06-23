<%@ include file="./base.jsp" %>
<%
    String value = element.html();

    element.before("<c:set var=\"value\" value=\"" + value + "\"/>");
    element.empty();
    element.appendChild(ScriptNode.node(DeveloperCode.print("WebJsplUtils.dateValue(pageContext.getAttribute(\"value\"))")));
%>