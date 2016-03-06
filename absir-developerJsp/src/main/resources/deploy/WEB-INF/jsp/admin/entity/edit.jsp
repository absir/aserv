<%@page import="com.absir.aserv.support.developer.RenderUtils"%>
<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%
	request.setAttribute("layout", "/WEB-INF/jsp/admin/layout.jsp");
%>
<div class="pageContent">
	<form class="pageForm required-validate" method="post"
		action="${admin_route}/entity/save/${entityName}${empty id ? "" : "/".concat(id)}" ${multipart ? "enctype=\"multipart/form-data\"" : ""}
		onsubmit="return ${multipart ? "iframeCallback" : "validateCallback"}(this, navTabAjaxErrors);">
		<%
			RenderUtils.include("bean/edit/" + request.getAttribute("entityName") + ".jsp", pageContext, request, response);
		%>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">保存</button>
						</div>
					</div></li>
				<li>
					<div class="button">
						<div class="buttonContent">
							<button type="reset">取消</button>
						</div>
					</div>
				</li>
			</ul>
		</div>
	</form>
	<c:if test="${!create}">
		<form id="pagerForm"></form>
	</c:if>
</div>