<%@ include file="../../common/edit.jsp"%>
<c:set var="node">
	<%
		String value = field.getName() + "Value";
			out.println(DeveloperCode.set("${" + DeveloperCode.unExpression(input.attr("value")) + "[0]}", value));
			input.attr("value", "${" + value + "}");
			String expression = "pageContext.findAttribute(\"" + value + "\")";
			expression = "EntityStatics.find(\"" + field.getEntityName() + "\", " + expression + ", WebJsplUtils.getInput(request))";
			expression = "pageContext.setAttribute(\"" + value + "\" ," + expression + ");";
			out.println(DeveloperCode.script(expression));
	%>
</c:set>
<%
	element.before(ScripteNode.node((String) pageContext.getAttribute("node")));
%>