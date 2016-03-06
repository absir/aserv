<%@ include file="../../../common/common.jsp"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.absir.aserv.developer.model.*"%>
<%=DeveloperCode.importClass(List.class.getName())%>
<%=DeveloperCode.importClass(ArrayList.class.getName())%>
<%
	Scenario.set("suggest", request);
	String entityName = (String) request.getAttribute("entityName");
	EntityModel entityModel = (EntityModel) request.getAttribute("entityModel");
	out.println(DeveloperCode.scriptBegin());
	out.println("if(request.getAttribute(\"" + entityName + "$suggests\") != null) return;");
	out.println("request.setAttribute(\"" + entityName + "$primary" + "\", \"" + entityModel.getPrimary().getName() + "\");");
	out.println(DeveloperCode.define("List", "suggests", "new ArrayList()"));
	out.println("request.setAttribute(\"" + entityName + "$suggests\", suggests);");
	if (entityModel.getGroupFields(JaEdit.GROUP_SUGGEST) != null) {
		for (IField field : entityModel.getGroupFields(JaEdit.GROUP_SUGGEST)) {
	out.println("suggests.add(\"" + field.getName() + "\");");
		}
	}

	out.println(DeveloperCode.scriptEnd());
	Scenario.pop(request);
%>