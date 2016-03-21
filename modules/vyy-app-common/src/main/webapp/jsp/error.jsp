<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>/">
<title>微移云</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common_bosum.css">
<style>
body {
	background: #fff
}
img {
	margin-top: 5em
}
.warp {
	text-align: center
}

header {
	color: #707070;
	font-size: 28px;
	padding: 0 10px;
	margin: 1em auto auto auto;
}

.hintInfo {
	font-size: 20px;
	color: #a0a0a0;
	padding: 0 20px;
	margin: 1em auto auto auto;
}

.service {
	font-size: 16px;
	color: #a0a0a0;
	padding: 0 30px;
	margin: 1em auto auto auto;
}
</style>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/jweixin-1.0.0.js"></script>
</head>
<body>
	<div class="warp">
		<img src="images/errorIcon.png" width="13%" />
		<header>${reason}</header>
		<div class="hintInfo">如有问题、请联系客服:</div>
		<div class="service">客服电话：025-85635699</div>
	</div>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>
