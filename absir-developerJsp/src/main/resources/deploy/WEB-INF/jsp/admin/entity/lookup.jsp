<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%
	request.setAttribute("layout", "/WEB-INF/jsp/admin/layout.jsp");
	request.setAttribute("suggest", request.getParameter("suggest"));
%>
<c:if test="${empty suggest}">
	<c:set var="suggest" value="${primary}" />
</c:if>
<form id="pagerForm" method="post"
	action="${admin_route}/entity/lookup/${entityName}?suggest=${suggest}">
	<input type="hidden" name="pageIndex" value="${page.pageIndex}" /> <input
		type="hidden" name="pageSize" value="${page.pageSize}" /> <input
		type="hidden" name="orderField" value="${orderField}" /> <input
		type="hidden" name="orderDirection" value="${orderDirection}" /> <input
		type="hidden" name="searchConditions" value="${searchConditions}" />
</form>
<div layoutH="26">
	<div class="pageHeader">
		<form rel="pagerForm" method="post"
			action="${admin_route}/entity/lookup/${entityName}?suggest=${suggest}"
			onsubmit="return dwzSearch(this, 'dialog');">
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
						<li><div class="button">
								<div class="buttonContent">
									<button type="button" multLookup="ids" warn="请选择数据">选择带回</button>
								</div>
							</div></li>
					</ul>
				</div>
			</div>
		</form>
	</div>
	<div class="pageContent">
		<table class="table" width="100%" targetType="dialog" layoutH="138">
			<%
				RenderUtils.include("bean/lookup/" + request.getAttribute("entityName") + ".jsp", pageContext, request, response);
			%>
		</table>
	</div>
</div>
<div class="panelBar">
	<div class="pages">
		<span>显示</span> <select class="combox" name="numPerPage"
			value="${page.pageSize}"
			onchange="dwzPageBreak({targetType:'dialog', data:{numPerPage:this.value}})">
			<option value="20">20</option>
			<option value="50">50</option>
			<option value="100">100</option>
			<option value="200">200</option>
		</select> <span>条，共${page.totalCount}条</span>
	</div>
	<div class="pagination" targetType="dialog"
		currentPage="${page.pageIndex}" numPerPage="${page.pageSize}"
		totalCount="${page.totalCount}" pageNumShown="10"></div>
</div>