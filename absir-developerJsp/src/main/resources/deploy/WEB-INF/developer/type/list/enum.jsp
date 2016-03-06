<%@ include file="./base.jsp"%>
<%
	EntityModel entityModel = (EntityModel) request.getAttribute("entityModel");
	String runtimeName = EntityStatics.getSharedRuntimeName(entityModel.getJoEntity().getEntityName(), field.getName());
	EntityStatics.setSharedObject(runtimeName, field.getMetas().get("values"), WebJsplUtils.getInput(request));

	String value = element.html();
	element.before("<c:set var=\"value\">" + value + "</c:set>");
	element.empty();
	element.appendChild(ScripteNode.node(DeveloperCode.print("WebJsplUtils.value(((java.util.Map)EntityStatics.getSharedObject(\"" + runtimeName
			+ "\", WebJsplUtils.getInput(request))).get(pageContext.getAttribute(\"value\")))")));
%>