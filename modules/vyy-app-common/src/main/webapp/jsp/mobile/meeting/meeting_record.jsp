<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head lang="en">
<base href="${contextPath}/">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<title>会议室预订</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global_bosum.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/common_bosum.css">
<link rel="stylesheet" href="css/meetingRoom_Bosum.css">
</head>
<body class="whiteBg">
    <div class="warp">
        <nav class="newSearchNav">
			<header
				class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
				<b><!--全部状态--></b> <input type="checkbox"> <span><i
					class="fa fa-angle-down projectIcon"></i></span>
			</header>
			<div class="whiteBg newSearchBottom switchCon myHide">
				<ul>
					<li class="newSearchBottom">待参加</li>
					<li>已结束</li>
					<!-- <li>全部状态</li> -->
				</ul>
			</div>

		</nav>
        <section>
            <ul id="meetings"></ul>
        </section>
    </div>
	<footer class="copyright">© 微移云技术支持</footer>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/toast.js"></script>
	<script src='js/jweixin-1.0.0.js'></script>
	<script src='js/eJweixin-1.0.0.js'></script>
	<script src="js/recordList.js"></script>
	<script src="js/mobile/meeting/queryRecord.js"></script>
</body>
</html>