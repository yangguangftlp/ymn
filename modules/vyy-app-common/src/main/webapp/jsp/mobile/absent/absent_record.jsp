<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<title>请假</title>
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/common.css">
</head>
<body class="whiteBg">
<!-- 加载样式 -->
	<div class="warp">
		<!-- <ul class="clear nav threeColumns">
			<li class="navSelected" flag="0">待审核</li>
			<li flag="0">审核中</li>
			<li flag="0">已审核</li>
			<div class="line"></div>
		</ul> -->
		<nav class="newSearchNav">
			<header
				class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
				<b></b> <input type="checkbox"> <span><i
					class="fa fa-angle-down projectIcon"></i></span>
			</header>
			<div class="whiteBg newSearchBottom switchCon myHide">
				<ul>
					<li class="newSearchBottom">待审核<!--<i class="fa fa-check mainColor"></i>--></li>
					<li class="newSearchBottom">审核中</li>
					<li>已审核</li>
					<!-- <li>全部状态</li> -->
				</ul>
			</div>

		</nav>
		<section>
			<ul>
			</ul>
		</section>
	</div>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/recordList.js"></script>
	<script src="js/mobile/absent/absent-record.js"></script>
	<!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>