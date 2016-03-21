<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<title>问题反馈</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
</head>
<body class="whiteBg">
	<div class="warp">
		<nav class="newSearchNav">
			<header
				class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
				<b></b> <input type="checkbox"> <span><i
					class="fa fa-angle-down projectIcon"></i></span>
			</header>
			<div class="whiteBg newSearchBottom switchCon myHide">
				<ul>
					<li class="newSearchBottom">未解决</li>
					<li>已解决</li>
					<!-- <li>全部状态</li> -->
				</ul>
			</div>

		</nav>
		<section>
			<ul>
			</ul>
		</section>
	</div>
	<!-- 跳转链接 -->
	<input type="hidden" id="go-url" value="mobile/feedback/feedbackDetail.action">
	<input type="hidden" id="go-ts-url" value="mobile/feedback/feedbackDetail.action">

	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/recordList.js"></script>
	<script src="js/mobile/feedback/problemRecord.js"></script>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>