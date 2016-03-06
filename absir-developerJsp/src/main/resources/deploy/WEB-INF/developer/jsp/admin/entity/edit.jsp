<%@include file="../../../common/option.jsp"%>
<div class="pageFormContent" layoutH="56">
	<%
		Scenario.set("edit", request);
			element = document.appendElement("div");
			String readonly = "${create ? \"\" : \" readonly\"}";
			for (IField field : entityModel.getPrimaries()) {
		// 编辑实体主键
		identifier = "name=\"" + field.getName() + "\"";
		if (!generator.append(identifier, element)) {
			if (field.isGenerated()) {
				// 自增长主键
	%>
	<p>
		<label><%=WebJsplUtils.getInput(request).getLang(field.getCaption())%>:</label> <input <%=identifier%>
			type="text" size="30" readonly="readonly"
			value="<%="${entity." + field.getName() + "}"%>">
	</p>
	<%
		} else {
				// 一般主键
				request.setAttribute("field", field);
	%>
	<c:set var="node">
		<p>
			<label><%=WebJsplUtils.getInput(request).getLang(field.getCaption())%>:</label> <input <%=identifier%>
				type="text" size="30"
				value="<%="${entity." + field.getName() + "}"%>">
		</p>
	</c:set>
	<%
		// 适配字段特性
		nodes = ScripteNode.append(element, pageContext.getAttribute("node").toString());
		((Element) nodes.get(0)).getElementsByTag("input").get(0).attr(readonly, "true");
		request.setAttribute("nodes", nodes);
		DeveloperUtils.includeExist("edit", field.getTypes(), pageContext, request, response);
			}

		}
		// 编辑实体主键结束
			}

			// 编辑其他字段
			List<IField> subtableFields = new ArrayList<IField>();
			Map<String, List<IField>> subtableSubFields = new HashMap<String, List<IField>>();
			for (IField field : entityModel.getFields()) {
		if (field.getTypes().size() > 0 && "subtable".equals(field.getTypes().get(0))) {
			// 关联实体字段
			subtableFields.add(field);

		} else {
			// 关联实体索引字段
			String subField = (String) field.getMetas().get("subField");
			if (subField != null) {
		List<IField> fields = subtableSubFields.get(subField);
		if (fields == null) {
			fields = new ArrayList<IField>();
			subtableSubFields.put(subField, fields);
		}

		fields.add(field);
		continue;
			}

			// 编辑一般字段
			identifier = "name=\"" + field.getName() + "\"";
			if (!generator.append(identifier, element)) {
		request.setAttribute("field", field);
	%>
	<c:set var="node">
		<p>
			<label><%=WebJsplUtils.getInput(request).getLang(field.getCaption())%>:</label> <input <%=identifier%>
				type="text" size="30" value="<%="$"%>{entity.<%=field.getName()%>}">
		</p>
	</c:set>
	<%
		request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
		DeveloperUtils.includeExist("edit", field.getTypes(), pageContext, request, response);
			}
		}

		// 适配字段特性
		request.setAttribute("element", element);
		DeveloperUtils.includeExist("edit", entityModel.getJoEntity().getEntityName(), pageContext, request, response);
			}
			// 编辑一般字段结束
	%>
	<%=element.html() + "\r\n"%>
	<div class="divider"></div>
	<%
		// 编辑关联实体
			if (subtableFields.size() > 0) {
		Object entity = request.getAttribute("entity");
		request.setAttribute("subtable", true);
	%>
	<%="<c:set value=\"${entity}\" var=\"entityContext\"/>"%>
	<div class="tabs">
		<div class="tabsHeader">
			<div class="tabsHeaderContent">
				<ul>
					<%
						// 编辑关联实体遍历
																for (IField field : subtableFields) {
																	identifier = "<li id=\"" + field.getName() + "\"";
																	if (!generator.print(identifier, out)) {
					%>
					<%=identifier + ">"%><a href="javascript:;"><span><%=WebJsplUtils.getInput(request).getLang(field.getCaption())%></span></a><%="</li>"%>
					<%
						}
																}
					%>
				</ul>
			</div>
		</div>
		<div class="tabsContent">
			<%
				//List<String> fieldNames = new ArrayList<String>();
								for (IField field : subtableFields) {
									identifier = "<div id=\"" + field.getName() + "\"";
									if (!generator.print(identifier, out)) {
										int subtable = KernelDyna.to(field.getMetas().get("subtable"), int.class);
			%>
			<%="<c:set value=\"${entity." + field.getName() + "}\" var=\"entities\"/>"%>
			<%=identifier + " class=\"pageContent\">"%>
			<%
				if (field.getMappedBy() == null) {
											element = document.appendElement("tr");
			%>
			<input name="!subtables" type="hidden" value="<%=field.getName()%>" />
			<table class="list nowrap itemDetail" width="100%"
				addButton="<%=subtable > 0 || !(field.getCrudField().getKeyJoEntity() == null && field.getValueField() == null) ? "" : "添加纪录"%>">
				<thead>
					<tr>
						<%
							if (field.getCrudField().getKeyJoEntity() == null && field.getValueField() == null) {
																								if (field.getCrudField().getJoEntity() == null) {
						%>
						<th>内容</th>
						<%
							// 关联实体数组(简单类型)
																									identifier = "name=" + field.getName() + "[${i.index}]";
																									if (!generator.append(identifier, element)) {
						%>
						<c:set var="node">
							<td><input <%=identifier%> type="text" style="width: 80%"
								value="<%="${entity}"%>"></td>
						</c:set>
						<%
							request.setAttribute("field", field);
																										request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
																										DeveloperUtils.includeExist("subtable", field.getTypes(), pageContext, request, response);
																									}
																									// 关联实体数组结束(简单类型)

																								} else {
																									for (IField subField : DeveloperModel.forEntityNameClass(field.getCrudField().getJoEntity(), request).getEntityModel().getFields()) {
						%>
						<th><%=WebJsplUtils.getInput(request).getLang(subField.getCaption())%></th>
						<%
							// 关联实体数组(复杂类型)
																										identifier = "name=" + field.getName() + "[${i.index}]." + subField.getName();
																										if (!generator.append(identifier, element)) {
						%>
						<c:set var="node">
							<td><input <%=identifier%> type="text" style="width: 80%"
								value="<%="$"%>{entity.<%=subField.getName()%>}"></td>
						</c:set>
						<%
							request.setAttribute("field", subField);
																											request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
																											DeveloperUtils.includeExist("subtable", subField.getTypes(), pageContext, request, response);
																										}
																										// 关联实体数组结束(复杂类型)

																										// 适配实体特性
																										request.setAttribute("element", element);
																										DeveloperUtils.includeExist("subtable", field.getEntityName(), pageContext, request, response);
																									}
																								}

																							} else if (field.getCrudField().getKeyJoEntity() == null) {
						%>
						<th><%=field.getMetas().containsKey("key") ? field.getMetas().get("key") : "键值"%></th>
						<%
							// 关联实体字典(键类型)
																								identifier = "class=\"itemKey " + field.getName() + "\"";
																								if (!generator.append(identifier, element)) {
						%>
						<c:set var="node">
							<td><input <%=identifier%> type="text" readonly="readonly"
								value="<%="${entity.key}"%>"></td>
						</c:set>
						<%
							request.setAttribute("field", field);
																									request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
																									DeveloperUtils.includeExist("subtable", field.getTypes(), pageContext, request, response);
																								}
																								
																								// 关联实体字典结束(键类型)
																								if (field.getCrudField().getJoEntity() == null) {
						%>
						<th>内容</th>
						<%
							// 关联实体字典(简单值类型)
																									identifier = "name=\"" + field.getName() + "['${entity.key}']\"";
																									if (!generator.append(identifier, element)) {
						%>
						<c:set var="node">
							<td><input <%=identifier%> type="text" style="width: 80%"
								value="<%="$"%>{entity.value}"></td>
						</c:set>
						<%
							request.setAttribute("field", field.getValueField());
																										request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
																										DeveloperUtils.includeExist("subtable", field.getValueField().getTypes(), pageContext, request, response);
																									}
																									// 关联实体字典结束(简单值类型)

																								} else {
																									for (IField subField : DeveloperModel.forEntityNameClass(field.getCrudField().getJoEntity(), request).getEntityModel().getFields()) {
						%>
						<th><%=WebJsplUtils.getInput(request).getLang(subField.getCaption())%></th>
						<%
							// 关联实体字典(复杂值类型)
																										identifier = "name=" + field.getName() + "['${entity.key}']." + subField.getName();
																										if (!generator.append(identifier, element)) {
						%>
						<c:set var="node">
							<td><input <%=identifier%> type="text" style="width: 80%"
								value="<%="$"%>{entity.value.<%=subField.getName()%>}" /></td>
						</c:set>
						<%
							request.setAttribute("field", subField);
																											request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
																											DeveloperUtils.includeExist("subtable", subField.getTypes(), pageContext, request, response);
																										}
																										// 关联实体字典结束(复杂值类型)

																										// 适配实体特性
																										request.setAttribute("element", element);
																										DeveloperUtils.includeExist("subtable", field.getValueEntityName(), pageContext, request, response);
																									}
						%>
						<%
							}
															}
															// 关联实体索引字段
															List<IField> subFields = subtableSubFields.get(field.getName());
															if (subFields != null) {
																for (IField subField : subFields) {
						%>
						<th><%=WebJsplUtils.getInput(request).getLang(subField.getCaption())%></th>
						<c:set var="node">
							<td><input type="radio" name="<%=subField.getName()%>"
								value="<%="${i.index}"%>" $selected /></td>
						</c:set>
						<%
							node = ScripteNode.append(element, pageContext.getAttribute("node").toString()).get(0);
																	node.remove();
																	element.appendChild(ScripteNode.node(node.outerHtml().replace("$selected=\"\"", "${entityContext." + subField.getName() + "==i.index ? \"checked\" : \"\"}")));
																}
															}
															// 关联实体索引字段结束
						%>
						<th class="option" width="60">操作</th>
						<c:set var="node">
							<td><%=subtable > 0 ? "" : "<a href=\"javascript:void(0)\" class=\"btnDel\">删除</a>"%></td>
						</c:set>
						<%
							request.setAttribute("nodes", ScripteNode.append(element, pageContext.getAttribute("node").toString()));
						%>
					</tr>
					<tr class="archetype">
						<c:set var="node">
							<%
								request.setAttribute("element", element);
																		request.setAttribute("field", field);
																		WebJsplUtils.renderInclude("/WEB-INF/developer/caches/subtable/" + entityModel.getJoEntity().getEntityName() + "." + field.getName() + ".jsp", "subtable.jsp",
																				pageContext, request, response);
							%>
						</c:set>
						<%=pageContext.getAttribute("node")%>
					</tr>
				</thead>
				<tbody>
				<%
					// 生成关联实体模版

											if (subtable > 0) {
												out.println("<c:set value=\"0\" var=\"index\"/>");
											}
				%>
				<%="<c:forEach items=\"${entities}\" var=\"entity\" varStatus=\"i\">"%>
				<tr>
					<%=element.html()%>
				</tr>
				<%
					if (subtable > 0) {
												out.println("<c:set value=\"${index + 1}\" var=\"index\"/>");
											}
				%>
				<%="</c:forEach>"%>
				<%
					if (subtable > 0) {
				%>
				<%="<c:forEach begin=\"${index}\" end=\"" + --subtable + "\" var=\"i\">"%>
				<tr><%=pageContext.getAttribute("node")%></tr>
				<%="</c:forEach>"%>
				<%
					}
											// 生成关联实体模版结束
				%>
				</tbody>
			</table>
			<%
				} else {
									// 管理关联实体
									request.setAttribute("field", field);
			%>
			<jsp:include page="mapped.jsp" />
			<%
				// 管理关联实体结束
								}
								out.println("</div>");
							}
			%>
	<%
		}
		//编辑关联实体遍历结束
		//关联实体字段隐藏提交
	%>
	<%-- 	if (fieldNames.size() > 0) {
	<input type="hidden" name="!subtables"
		value="<%=KernelString.implode(fieldNames, ",")%>">
		} --%>
	<%
		pageContext.removeAttribute("entity");
		request.setAttribute("entity", entity);%>
		  </div>
		</div>
					<div class="tabsFooter">
			<div class="tabsFooterContent"></div>
		</div>
			<%}
			// 编辑关联实体结束
			Scenario.pop(request);
	%>
</div>