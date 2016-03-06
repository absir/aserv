<%@ include file="../../common/edit.jsp"%>
<c:set var="node">
	<p>
		<input type="file" name="<%=field.getName() + "_file"%>" />
	</p>
</c:set>
<%
	element.after((String) pageContext.findAttribute("node"));
%>