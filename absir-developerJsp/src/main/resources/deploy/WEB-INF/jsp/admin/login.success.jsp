<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<json:object>
	<json:property name="statusCode" value="200" />
	<json:property name="message" value="登录成功" />
	<json:property name="callbackType" value="closeCurrent" />
</json:object>