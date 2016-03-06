<%@ include file="../../common/edit.jsp"%>
<%
	Scenario.pop("search", request);
	String name = input.attr("name");
	String cname = name + " >=";
	input.attr("name", cname);
	input.attr("value", "${searchConditionMap['" + cname + "']}");
	DeveloperUtils.includeExist("edit", field.getTypes(), pageContext, request, response);

	cname = name + " <=";
%>

<c:set var="node">
	<input name="<%=cname%>" type="text" size="20"
		value="<%="${searchConditionMap['" + cname + "']}"%>">
</c:set>
<%
	request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
	DeveloperUtils.includeExist("edit", field.getTypes(), pageContext, request, response);
%>