<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<json:object>
	<json:property name="statusCode" value="200" />
	<json:property name="message" value="保存成功" />
	<c:choose>
		<c:when test="${create}">
			<json:property name="callbackType" value="forward" />
			<json:property name="forwardUrl"
				value="${admin_route}/entity/edit/${entityName}/${id}" />
		</c:when>
		<c:otherwise>
			json:property name="callbackType" value="refresh" />
		</c:otherwise>
	</c:choose>
</json:object>
