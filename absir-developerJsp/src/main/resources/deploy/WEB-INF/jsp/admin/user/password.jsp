<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%
	request.setAttribute("layout", "/WEB-INF/jsp/admin/layout.jsp");
%>
<div class="pageContent">
	<form action="${admin_route}/user/password" method="post"
		onsubmit="return validateCallback(this, dialogAjaxDone)">
		<div class="pageFormContent" layoutH="58">
			<div class="unit">
				<label>原密码：</label> <input type="password" name="password" size="30"
					class="required" />
			</div>
			<div class="unit">
				<label>新密码：</label> <input type="password" name="newPassword"
					size="30" class="required" />
			</div>
			<div class="unit">
				<label>确认密码：</label> <input type="password" size="30"
					class="required" equalto="input[name=newPassword]" />
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
