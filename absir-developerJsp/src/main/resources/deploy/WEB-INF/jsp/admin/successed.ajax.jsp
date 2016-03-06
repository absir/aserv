<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%
	request.removeAttribute("layout");
%>
<json:object>
	<json:property name="statusCode" value="200" />
	<json:property name="message">${empty message ? "操作成功" : message}</json:property>
	<json:property name="callbackType" value="closeCurrent" />
</json:object>
