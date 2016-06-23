<%@ include file="../../common/edit.jsp" %>
<%
    element.tagName("td");
    element.removeClass("p");
    element.previousSibling().remove();
%>