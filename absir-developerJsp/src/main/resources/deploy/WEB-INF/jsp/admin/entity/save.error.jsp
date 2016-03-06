<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<json:object>
	<json:property name="statusCode" value="300" />
	<json:property name="message" value="保存失败" />
	<json:object name="errors">
		<c:forEach var="error" items="${errors}">
			<json:property name="${error.propertyPath}" value="${error.errorMessage}" />
		</c:forEach>
	</json:object>
</json:object>
