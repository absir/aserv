<%@ include file="../../../common/option.jsp"%>
<thead>
	<tr>
		<th width="22"><input type="checkbox" group="ids"
			class="checkboxCtrl"></th>
		<%
			Scenario.set("list", request);
			element = document.appendElement("tr");
		%>
		<%
			for (IField field : entityModel.getGroupFields("list")) {
		%>
		<%
			// 显示列表头
				identifier = "<th class=\"" + field.getName();
				if (!generator.append(identifier, element)) {
		%>
		<c:set var="node">
			<%=identifier + "\">" + WebJsplUtils.getInput(request).getLang(field.getCaption()) + "</th>"%>
		</c:set>
		<%
			node = ScripteNode.append(element, pageContext.getAttribute("node").toString()).get(0);
					Object width = field.getMetas().get("width");
					if (width != null) {
						node.attr("width", width.toString());
					}

					if (field.isCanOrder()) {
						node.attr("orderField", field.getName());
						((Element) node).addClass("${orderFieldMap." + field.getName() + "}");
					}
				}
				// 显示列表头结束
			}
		%>
		<%=element.html() + "\r\n"%>
	</tr>
</thead>
<tbody>
	<%="<c:forEach items=\"${entities}\" var=\"entity\">"%>
	<%=DeveloperCode.script("pageContext.setAttribute(\"id\", EntityStatics.getPrimary(pageContext.getAttribute(\"entity\"), \"" + entityModel.getPrimary().getName() + "\"));")%>
	<tr target="id" rel="\${id}">
		<%
			element = document.appendElement("tr");
			// 显示列表ID
			identifier = "<td><input name=\"ids\"";
			if (!generator.append(identifier, element)) {
		%>
		<%=identifier%>
		type="checkbox" value="\${id}"<%="></td>"%>
		<%
			}
			// 显示列表ID结束
		%>
		<%
			for (IField field : entityModel.getGroupFields("list")) {
		%>
		<%
			// 显示列表内容
				identifier = "<td class=\"" + field.getName();
				if (!generator.append(identifier, element)) {
					request.setAttribute("field", field);
		%>
		<c:set var="node">
			<%=identifier + "\">${entity." + field.getName() + "}</td>"%>
		</c:set>
		<%
			// 适配字段特性
					request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
					DeveloperUtils.includeExist("list", field.getTypes(), pageContext, request, response);
				}
				// 显示页面内容结束
			}
		%>
		<%
			// 适配实体特性
			request.setAttribute("element", element);
			DeveloperUtils.includeExist("list", entityModel.getJoEntity().getEntityName(), pageContext, request, response);
			Scenario.pop(request);
		%>
		<%=element.html() + "\r\n"%>
	</tr>
	<%="</c:forEach>"%>
</tbody>
