<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%
	request.removeAttribute("layout");
%>
<json:object>
	<json:property name="statusCode" value="300" />
	<json:property name="message">${empty message ? "操作失败" : message}</json:property>
</json:object>
