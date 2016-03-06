<%@ include file="common.jsp"%>
<%
	EntityModel entityModel = (EntityModel)request.getAttribute("entityModel");
	DeveloperGenerator generator = DeveloperGenerator.getDeveloperGenerator(request);
	
	//String entityName = (String)request.getAttribute("entityName");
	//IField field;
	String identifier;
	
	Document document = new Document("");
	document.outputSettings().escapeMode(ScripteNode.NONE);

	Element element;
	List<Node> nodes;
	Node node;
%>
<%=DeveloperCode.include("/WEB-INF/jsp/common.jsp")%>
<%=DeveloperCode.importClass("com.absir.core.kernel.*")%>
<%=DeveloperCode.importClass("com.absir.aserv.system.service.statics.*")%>
<%generator.print("<%importClass>", out);%>