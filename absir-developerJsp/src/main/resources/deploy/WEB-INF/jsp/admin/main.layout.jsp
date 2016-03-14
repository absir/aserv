<%@ include file="/WEB-INF/jsp/common.jsp" %>
<%
    request.setAttribute("layout", "/WEB-INF/jsp/layout.jsp");
%>
<c:set var="page_title" scope="request">${app_name}管理后台</c:set>
<c:set var="page_header" scope="request">
    <c:set value="${site_route}/static/jui/" var="jui"/>
    <link href="${jui}themes/default/style.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${jui}themes/css/core.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${jui}themes/css/print.css" rel="stylesheet"
          type="text/css" media="print"/>
    <link href="${jui}uploadify/css/uploadify.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${jui}themes/css/ieHack.css" rel="stylesheet"
          type="text/css" media="screen"/>

    <c:set value="${site_route}/static/js/jquery/" var="jquery"/>
    <script src="${jquery}jquery-1.7.2.js" type="text/javascript"></script>
    <script src="${jquery}jquery.bgiframe.js" type="text/javascript"></script>
    <script src="${jquery}jquery.cookie.js" type="text/javascript"></script>
    <script src="${jquery}jquery.validate.js" type="text/javascript"></script>
    <script src="${jquery}jquery.absir.js" type="text/javascript"></script>

    <script src="${jui}js/speedup.js" type="text/javascript"></script>
    <script src="${jui}xheditor/xheditor-1.2.1.min.js"
            type="text/javascript"></script>
    <script src="${jui}uploadify/scripts/jquery.uploadify.min.js"
            type="text/javascript"></script>
    <script src="${jui}bin/dwz.ESC.js" type="text/javascript"></script>

    <script src="${jui}/absir/dwz.absir.js" type="text/javascript"></script>
    <link href="${jui}absir/dwz.absir.css" rel="stylesheet" type="text/css"
          media="screen"/>

    <!-- svg图表 supports Firefox 3.0+, Safari 3.0+, Chrome 5.0+, Opera 9.5+ and Internet Explorer 6.0+ -->
    <script src="${jui}chart/raphael.js" type="text/javascript"></script>
    <script src="${jui}chart/g.raphael.js" type="text/javascript"></script>
    <script src="${jui}chart/g.bar.js" type="text/javascript"></script>
    <script src="${jui}chart/g.line.js" type="text/javascript"></script>
    <script src="${jui}chart/g.pie.js" type="text/javascript"></script>
    <script src="${jui}chart/g.dot.js" type="text/javascript"></script>

    <script type="text/javascript">
        $(function () {
            DWZ.init("${jui}server.frag.xml", {
                loginUrl: "${admin_route}/login/ajax",
                loginTitle: "登录",
                statusCode: {
                    ok: 200,
                    error: 300,
                    timeout: 301
                },
                pageInfo: {
                    pageNum: "pageIndex",
                    numPerPage: "pageSize",
                    orderField: "orderField",
                    orderDirection: "orderDirection"
                },
                debug: false,
                callback: function () {
                    initEnv();
                    $("#themeList").theme({
                        themeBase: "${jui}themes"
                    });
                }
            });
        });
    </script>
</c:set>

<div id="layout">
    <div id="header">
        <div class="headerNav">
            <a class="logo" href="${admin_route}">${app_name}</a>
            <ul class="nav">
                <!-- <li id="switchEnvBox"><a href="javascript:">（<span>上海</span>）切换城市</a></li> -->
                <li><a href="${admin_route}/user/password" target="dialog"
                       ref="user">修改密码</a></li>
                <li><a href="#"><sec:authentication
                        property="principal.username"/></a></li>
                <li><a href="${admin_route}/login/out">退出</a></li>
            </ul>
            <ul class="themeList" id="themeList">
                <li theme="default">
                    <div class="selected">蓝色</div>
                </li>
                <li theme="green">
                    <div>绿色</div>
                </li>
                <li theme="purple">
                    <div>紫色</div>
                </li>
                <li theme="silver">
                    <div>银色</div>
                </li>
                <li theme="azure">
                    <div>天蓝</div>
                </li>
            </ul>
        </div>
    </div>

    <div id="leftside">
        <div id="sidebar_s">
            <div class="collapse">
                <div class="toggleCollapse">
                    <div></div>
                </div>
            </div>
        </div>
        <div id="sidebar">
            <div class="toggleCollapse">
                <h2>主菜单</h2>

                <div>收缩</div>
            </div>

            <div class="accordion" fillSpace="sidebar">
                <%@ include file="common/menu.jsp" %>
            </div>
        </div>
    </div>

    <div id="container">
        <div id="navTab" class="tabsPage">
            <div class="tabsPageHeader">
                <div class="tabsPageHeaderContent">
                    <!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
                    <ul class="navTab-tab">
                        <li tabid="main" class="main"><a href="javascript:;"><span><span
                                class="home_icon">我的主页</span></span></a></li>
                    </ul>
                </div>
                <div class="tabsLeft">left</div>
                <!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
                <div class="tabsRight">right</div>
                <!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
                <div class="tabsMore">more</div>
            </div>
            <ul class="tabsMoreList">
                <li><a href="javascript:;">我的主页</a></li>
            </ul>
            <div class="navTab-panel tabsPageContent layoutBox">
                <div class="page unitBox">
                    <div class="accountInfo">${body_content}</div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="footer">
    Copyright &copy; 2013 <a>天乙无道</a>.${app_version}
</div>