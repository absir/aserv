<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<json:object>
	<json:property name="statusCode" value="300" />
	<spring:hasBindErrors name="${entityName}">
	<!-- ${errors} -->
		<json:property name="message" value="保存数据出错:" />
	</spring:hasBindErrors>
	<json:property name="navTabId" value="" />
	<json:property name="rel" value="" />
	<json:property name="callbackType" value="" />
	<json:property name="forwardUrl" value="" />
	<json:property name="confirmMsg" value="" />
</json:object>