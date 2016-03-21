<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>/">
<title>系统异常</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<style>
header {
	color: #ffbb01;
	font-size: 31px;
	margin: 140px auto auto 23px;
}

.hintInfo {
	font-size: 15px;
	color: #000;
	margin: 25px auto auto 75px;
}

.icon400 img {
	width: 100%;
	height: auto;
	margin-top: 66px;
}
</style>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
	$(function() {
		var i = 3;
		function clock() {
			i = i - 1;
			document.title = "页面将在" + i + "秒后自动关闭!";
			if (i > 0)
				setTimeout(clock, 1000);
			else {
				WeixinJSBridge.call('closeWindow');
			}
		}
		clock();
	});
</script>
</head>
<body>
	<div class="warp">
		<header>Whoops...</header>
		<div class="hintInfo">系统太累了，稍微休息一下吧。</div>
		<div class="icon400">
			<img src="images/xiaoniao404.png">
		</div>
	</div>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>
