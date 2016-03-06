<%@ include file="../../common/edit.jsp"%>
<c:set var="node">
	<%
		String value = field.getName() + "Value";
			out.println(DeveloperCode.set(input.attr("value"), value));
			input.attr("value", "${" + value + "}");
			String expression = "pageContext.findAttribute(\"" + value + "\")";
			expression = "EntityStatics.list(\"" + field.getEntityName() + "\", " + expression + ", WebJsplUtils.getInput(request))";
			expression = "pageContext.setAttribute(\"" + value + "\" ," + expression + ");";
			out.println(DeveloperCode.script(expression));
	%>
</c:set>
<%
	element.before(ScripteNode.node((String) pageContext.getAttribute("node")));
%>