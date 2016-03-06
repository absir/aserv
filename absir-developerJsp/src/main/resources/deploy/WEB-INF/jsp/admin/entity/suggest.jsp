<%@ include file="/WEB-INF/jsp/common.jsp"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%
	request.setAttribute("suggest", request.getParameter("suggest"));
	RenderUtils.include("/WEB-INF/jsp/bean/suggest/" + request.getAttribute("entityName") + ".jsp", pageContext, request, response);
%>
<c:set var="primary" value="${entityName}${\"$primary\"}" />
<c:set var="primary" value="${requestScope[primary]}" />
<c:set var="suggests" value="${entityName}$suggests" />
<c:set var="suggests" value="${requestScope[suggests]}" />
<c:if test="${empty suggest}">
	<c:set var="suggest" value="${primary}" />
</c:if>
<json:array var="record" items="${records}">
	<json:object>
		<json:property name="${suggest}" value="${record[primary]}" />
		<json:property name="${suggest}$suggest">
			<c:forEach var="field" items="${suggests}" varStatus="status">
				<c:if test="${status.index > 0 }">.</c:if>${record[field]}
			</c:forEach>
		</json:property>
	</json:object>
</json:array>