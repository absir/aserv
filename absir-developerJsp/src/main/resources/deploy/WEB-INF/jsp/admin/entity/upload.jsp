<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%
	request.setAttribute("layout", "/WEB-INF/jsp/admin/layout.jsp");
%>
<div class="pageContent">
	<form method="post" enctype="multipart/form-data" 
		action="${admin_route}/entity/importXls/${entityName}"
		onsubmit="return iframeCallback(this, dialogAjaxDone)">
		<div class="pageFormContent" layoutH="58">
			<div class="unit">
				<label>导入文件：</label> <input type="file" name="xls" class="required" />
			</div>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">提交</button>
						</div>
					</div></li>
				<li><div class="button">
						<div class="buttonContent">
							<button type="button" class="close">取消</button>
						</div>
					</div></li>
			</ul>
		</div>
	</form>
</div>
