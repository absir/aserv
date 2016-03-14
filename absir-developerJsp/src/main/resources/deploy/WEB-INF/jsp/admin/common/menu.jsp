<%@page import="com.absir.appserv.menu.IMenuBean" %>
<%@page import="com.absir.appserv.menu.MenuContextUtils" %>
<%@ include file="/WEB-INF/jsp/common.jsp" %>
<%
    for (IMenuBean adminBean : MenuContextUtils.getMenuBeans("admin")) {
%>
<div class="accordionHeader">
    <h2>
        <span>Folder</span><%=adminBean.getName()%>
    </h2>
</div>
<div class="accordionContent">
    <ul class="tree treeFolder">
        <%
            for (IMenuBean menuBean : adminBean.getChildren()) {
        %>
        <li><a
                <%
                    if (menuBean.getUrl() == null)
                        out.print(" href=\"" + MenuContextUtils.getAdminRoute() + menuBean.getUrl() + "\" ref=\"" + menuBean.getRef() + "\"");
                %>><%=menuBean.getName()%>
        </a>
            <ul>
                <%
                    if (menuBean.getChildren() != null) {
                        for (IMenuBean menu : menuBean.getChildren()) {
                %>
                <li><a href="<%=menu.getUrl()%>" target="navTab"
                       rel="<%=menu.getRef()%>"><%=menu.getName()%>
                </a></li>
                <%
                        }
                    }
                %>
            </ul>
        </li>
        <%
            }
        %>
    </ul>
</div>
<%
    }
%>