<%@ include file="../../common/edit.jsp" %>
<c:set var="options">
    <%="<option value=\"\">请选择</option>"%>
    <c:forEach items="${field.metas.values}" var="value">
        <c:set var="val" value="${value.value}"/>
        <option value="${value.key}"><%=WebJsplUtils.getLangName((String) pageContext.getAttribute("val"))%>
        </option>
    </c:forEach>
</c:set>
<%
    input.tagName("select");
    input.addClass("combox");
    input.removeAttr("type");
    input.removeAttr("size");

    input.append((String) pageContext.getAttribute("options"));
    String value = input.attr("value");
    element.before(ScripteNode.node("<c:set value=\"" + value + "\" var=\"value\"/>"));
    input.attr("value", DeveloperCode.print("WebJsplUtils.enumValue(pageContext.getAttribute(\"value\"))"));
%>