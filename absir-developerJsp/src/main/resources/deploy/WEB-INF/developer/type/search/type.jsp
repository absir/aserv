<%@ include file="../edit/base.jsp"%>
<%
	if (Scenario.pop("search", request) != null) {
		if (KernelClass.getMatchNumberClass(field.getType()) != null) {
	input.attr("size", "10");
	String name = input.attr("name");
	String cname = name + " >=";
	input.attr("name", cname);
	input.attr("value", "${searchConditionMap['" + cname + "']}");

	cname = name + " <=";
%>
<c:set var="node">
	<input name="<%=cname%>" type="text" size="10"
		value="<%="${searchConditionMap['" + cname + "']}"%>">
</c:set>
<%
	ScripteNode.append(element, pageContext.getAttribute("node").toString());

		} else {
	DeveloperUtils.includeExist("edit", field.getTypes(), pageContext, request, response);
		}
	}
%>