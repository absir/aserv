<%@ include file="./base.jsp" %>
<%
    DeveloperModel model = DeveloperModel.forEntityName(field.getEntityName(), request);
    element.before(ScriptNode.node(model.suggest(element.html())));
    element.html("${value}");
%>
