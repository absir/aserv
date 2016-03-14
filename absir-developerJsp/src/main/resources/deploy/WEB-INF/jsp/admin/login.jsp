<%@ include file="/WEB-INF/jsp/common.jsp" %>
<%
    request.setAttribute("layout", "/WEB-INF/jsp/layout.jsp");
%>
<c:set var="page_title" scope="request">${app_name}登录后台</c:set>
<c:set var="page_header" scope="request">
</c:set>
<style type="text/css">
    * {
        font-family: "Verdana", "Tahoma", "Lucida Grande", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
    }

    body {
        background: url(images/loginbg_01.jpg) no-repeat center center fixed;
        -webkit-background-size: cover;
        -moz-background-size: cover;
        -o-background-size: cover;
        background-size: cover;
    }

    a:link {
        color: #285e8e;
    }

    .main_box {
        position: absolute;
        top: 50%;
        left: 50%;
        margin-top: -260px;
        margin-left: -300px;
        padding: 30px;
        width: 600px;
        height: 460px;
        background: #FAFAFA;
        background: rgba(255, 255, 255, 0.5);
        border: 1px #DDD solid;
        border-radius: 5px;
        -webkit-box-shadow: 1px 5px 8px #888888;
        -moz-box-shadow: 1px 5px 8px #888888;
        box-shadow: 1px 5px 8px #888888;
    }

    .main_box .setting {
        position: absolute;
        top: 5px;
        right: 10px;
        width: 10px;
        height: 10px;
    }

    .main_box .setting a {
        color: #FF6600;
    }

    .main_box .setting a:hover {
        color: #555;
    }

    .login_logo {
        margin-bottom: 20px;
        height: 45px;
        text-align: center;
    }

    .login_logo img {
        height: 45px;
    }

    .login_msg {
        text-align: center;
        font-size: 16px;
    }

    .login_form {
        padding-top: 20px;
        font-size: 16px;
    }

    .login_box .form-control {
        display: inline-block;
        *display: inline;
        zoom: 1;
        width: auto;
        font-size: 18px;
    }

    .login_box .form-control.x319 {
        width: 319px;
    }

    .login_box .form-control.x164 {
        width: 164px;
    }

    .login_box .form-group {
        margin-bottom: 20px;
    }

    .login_box .form-group label.t {
        width: 120px;
        text-align: right;
        cursor: pointer;
    }

    .login_box .form-group.space {
        padding-top: 15px;
        border-top: 1px #FFF dotted;
    }

    .login_box .form-group img {
        margin-top: 1px;
        height: 32px;
        vertical-align: top;
    }

    .login_box .m {
        cursor: pointer;
    }

    .bottom {
        text-align: center;
        font-size: 12px;
    }
</style>
<div class="main_box">
    <div class="login_box">
        <div>
            <c:if test="${error != null}">
                <div class="alert alert-danger alert-dismissable" style="color: red;">
                    <c:choose>
                        <c:when test='${error.serverStatus == "NO_VERIFY"}'>
                            验证码错误
                        </c:when>
                        <c:when test="${error.exceptionData == null}">
                            登录错误过多，请稍后
                        </c:when>
                        <c:otherwise>
                            用户密码错误
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
        <h3 class="text-center">欢迎使用 ${app_name}</h3>

        <form class="login_form" role="form" method="post">
            <div class="form-group">
                <label for="j_username" class="t">用户名：</label>
                <input id="j_username" name="username" type="text" class="form-control x319 in" autocomplete="off" required>
            </div>
            <div class="form-group">
                <label for="j_password" class="t">密　码：</label>
                <input id="j_password" name="password" type="password" class="form-control x319 in" required>
            </div>
            <div class="form-group">
                <label for="j_captcha" class="t">验证码：</label>
                <input id="j_captcha" name="verifycode" type="text" class="form-control x164 in" required>
                <img src="${site_route}/asset/verify" onclick="javascript:this.src=this.src+'?t='+Math.random()" alt="点击更换" title="点击更换" class="m">
            </div>
            <div class="form-group">
                <label class="t"></label>
                <label for="j_remember" class="m"><input id="j_remember" name="remeber" type="checkbox" value="true">&nbsp;记住登陆账号!</label>
            </div>
            <div class="form-group space">
                <label class="t"></label>　　　
                <input type="submit" id="login_ok" value="&nbsp;登&nbsp;录&nbsp;" class="btn btn-primary btn-lg">&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="reset" value="&nbsp;重&nbsp;置&nbsp;" class="btn btn-default btn-lg">
            </div>
            <p class="text-center">
                <a href="${SITE_ROUTE}/forget.html">
                    <small>忘记密码了？</small>
                </a> | <a href="${SITE_ROUTE}/register.html">注册一个新账号</a>
            </p>
        </form>
    </div>
</div>