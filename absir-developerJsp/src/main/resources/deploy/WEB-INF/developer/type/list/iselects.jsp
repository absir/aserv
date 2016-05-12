<%@ include file="./base.jsp" %>
<%
    String value = element.html();
    element.before(ScriptNode.node("<c:set value=\"" + value + "\" var=\"value\"/>\r\n"));
    element.before(DeveloperCode.script("pageContext.setAttribute(\"value\", EntityStatics.list(\"" + field.getEntityName()
            + "\", pageContext.findAttribute(\"value\")));"));
    DeveloperModel model = DeveloperModel.forEntityName(field.getEntityName(), request);
    element.before(ScriptNode.node(model.suggests("${value}")));
    element.html("${values}");
%>
