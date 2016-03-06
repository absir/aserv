<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%
	request.setAttribute("layout", "/WEB-INF/jsp/admin/layout.jsp");
%>
<form id="pagerForm" method="post"
	action="${admin_route}/entity/list/${entityName}">
	<input type="hidden" name="pageIndex" value="${page.pageIndex}" /> <input
		type="hidden" name="pageSize" value="${page.pageSize}" /> <input
		type="hidden" name="orderField" value="${orderField}" /> <input
		type="hidden" name="orderDirection" value="${orderDirection}" /> <input
		type="hidden" name="searchConditions" value="${searchConditions}" />
</form>
<div layoutH="26">
	<div class="pageHeader">
		<form rel="pagerForm" method="post"
			action="${admin_route}/entity/list/${entityName}"
			onsubmit="return navTabSearch(this);">
			<input type="hidden" name="pageSize" value="${page.pageSize}" />
			<div class="searchBar">
				<div class="searchFormContent">
					<%
						RenderUtils.include("bean/search/" + request.getAttribute("entityName") + ".jsp", "search.jsp", pageContext, request, response);
					%>
				</div>
				<div class="subBar">
					<ul>
						<li><div class="buttonActive">
								<div class="buttonContent">
									<button type="submit">检索</button>
								</div>
							</div></li>
					</ul>
				</div>
			</div>
		</form>
	</div>
	<div class="pageContent">
		<div class="panelBar">
			<ul class="toolBar">
				<li><a class="add"
					href="${admin_route}/entity/edit/${entityName}" target="navTab"
					rel="${entityName}_edit"><span>添加</span></a></li>
				<li><a class="edit" warn="请选择一条纪录"
					href="${admin_route}/entity/edit/${entityName}/{id}"
					target="navTab" rel="${entityName}_edit"><span>修改</span></a></li>
				<li><a class="delete" title="确实要删除这些记录吗?"
					href="${admin_route}/entity/delete/${entityName}"
					target="selectedTodo" rel="ids"><span>批量删除</span></a></li>
				<li class="line">line</li>
				<li><a class="icon"
					href="${admin_route}/entity/export/${entityName}"
					title="实要导出全部记录吗?" target="dwzExport" targettype="navTab"><span>导出全部</span></a></li>
				<li><a class="icon"
					href="${admin_route}/entity/export/${entityName}"
					title="实要导出这些记录吗?" target="selectedToExport" rel="ids"><span>批量导出</span></a></li>
				<li class="line">line</li>
				<li><a class="icon"
					href="${admin_route}/entity/upload/${entityName}" target="dialog"><span>批量导入</span></a></li>
			</ul>
		</div>
		<table class="table" width="100%">
			<%
				RenderUtils.include("bean/list/" + request.getAttribute("entityName") + ".jsp", pageContext, request, response);
			%>
		</table>
	</div>
</div>
<div class="panelBar">
	<div class="pages">
		<span>显示</span> <select class="combox" name="numPerPage"
			value="${page.pageSize}"
			onchange="navTabPageBreak({numPerPage:this.value})">
			<option value="20">20</option>
			<option value="50">50</option>
			<option value="100">100</option>
			<option value="200">200</option>
		</select> <span>条，共${page.totalCount}条</span>
	</div>
	<div class="pagination" targetType="navTab"
		currentPage="${page.pageIndex}" numPerPage="${page.pageSize}"
		totalCount="${page.totalCount}" pageNumShown="10"></div>
</div>
