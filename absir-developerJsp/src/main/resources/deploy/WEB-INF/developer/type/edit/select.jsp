<%@ include file="../../common/edit.jsp"%>
<%
	DeveloperModel model = DeveloperModel.forEntityName(
			field.getEntityName(), request);
	String entityName = field.getEntityName();
%>
<c:set var="scripte">
	<%=DeveloperCode.script("pageContext.setAttribute(\"selects\", EntityStatics.suggest(\""
								+ entityName + "\", WebJsplUtils.getInput(request)));")%>
	<%="<c:set var=\"options\">"%>
	<%="<option value=\"\">请选择</option>"%>
	<%="<c:forEach items=\"${selects}\" var=\"select\" varStatus=\"status\">"%>
	<%=model.suggest("${select}")%>
	<%="<option value=\"${id}\">${value}</option>"%>
	<%="</c:forEach>"%>
	<%="</c:set>"%>
	<%=model.suggest(input.attr("value"))%>
</c:set>
<%
	element.before(ScripteNode.node((String) pageContext
	.findAttribute("scripte")));
	input.append("${options}");

	input.tagName("select");
	input.addClass("combox");
	input.removeAttr("type");
	input.removeAttr("size");
	input.attr("value", "${id}");
%>