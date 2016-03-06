<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>
<%@page isErrorPage="true"%>
<%@ include file="/WEB-INF/jsp/common.jsp"%>
<c:set var="layout_body" scope="request">
	<h1>程序错误</h1>
	<p>
		<%
			StringWriter stringWriter = new StringWriter();
				exception.printStackTrace(new PrintWriter(stringWriter));
				out.println(stringWriter.toString().replace("\n", "\n</br>"));
		%>
	</p>
</c:set>
<jsp:include page="layout.jsp"></jsp:include>