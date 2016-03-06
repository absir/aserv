<%@include file="../../../common/option.jsp"%>
<%
	Scenario.set("mapped", request);
	IField field = (IField) request.getAttribute("field");
	// 如果实体已经持久化，则可以联动管理
%>
<%="<c:if test=\"${entity." + entityModel.getPrimary().getName() + " != null}\">"%>
<div class="panelBar">
	<ul class="toolBar">
		<li><a class="add"
			href="${admin_route}/entity/edit/<%=field.getEntityName() %>?<%=field.getMappedBy() %>=<%=DeveloperCode.print("EntityStatics.urlPrimary(\"" +entityModel.getJoEntity().getEntityName()+ "\", pageContext.findAttribute(\"entity\") , \""+entityModel.getPrimary().getName()+"\")") %>"
			target="dialog" rel="s_id"><span>添加</span></a></li>
		<li><a class="edit"
			href="${admin_route}/entity/edit/<%=field.getEntityName() %>/{id}"
			target="dialog" warn="请选择一条纪录"><span>修改</span></a></li>
		<li><a title="确实要删除这些记录吗?" target="selectedTodo" rel="s_id"
			href="${admin_route}/entity/delete/<%=field.getEntityName() %>"
			class="delete"><span>批量删除</span></a></li>
		<li class="line">line</li>
	</ul>
</div>
<table class="table" width="100%">
	<%
		// 实现实体列表
			String filepath = "/WEB-INF/jsp/admin/entity/bean/list/" + field.getEntityName() + ".jsp";
			DeveloperUtils.generate(filepath, "/WEB-INF/jsp/admin/entity/list.jsp", pageContext, request, response);
			Scenario.pop(request);
	%>
	<%="<jsp:include page=\"" + filepath + "\" />"%>
</table>
<%="</c:if>"%>