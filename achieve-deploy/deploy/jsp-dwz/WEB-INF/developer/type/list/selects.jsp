<%@ include file="./base.jsp" %>
<%
    DeveloperModel model = DeveloperModel.forEntityName(field.getEntityName(), request);
    element.before(ScriptNode.node(model.suggests(element.html())));
    element.html("${values}");
%>