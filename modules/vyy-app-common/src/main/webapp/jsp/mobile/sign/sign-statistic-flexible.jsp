<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head lang="en">
<title>签到</title>
<%@ include file="../../include/head.jsp"%>
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/bootstrap-datetimepicker.min.css">
<link rel="stylesheet" href="css/communal.css">
<link rel="stylesheet" href="css/signIn-viewDetail.css">
<style>
.loading {
	width: 134px;
	height: 94px;
	text-align: center;
	background: rgba(0, 0, 0, 0.6);
	color: #fff;
	border-radius: 4px;
	margin: auto;
	font-size: 14px;
	z-index: 2500;
	position: absolute;
}
/* 没有签到事项Start */
.noSignRecord {
	font-size: 14px;
	color: #787878;
	text-align: center;
	padding-left: 0 !important;
	top: 0px
}
/* 没有签到事项End */
.oneLoading>div:first-child {
	padding-top: 20px;
}
</style>
</head>
<body>

	<div class="viewDetailsWarp">
		<div id="cal"></div>
	</div>
	<script type="text/javascript">
	
	  window.sign_Info = {
			    // 打卡时间配置
			    "signStart": "${startHour}",//上班打卡不得晚于
			    "signEnd":"${endHour}",   //下班打卡不得早于
			    "workHours":"${workHours}"        //工作时长不得少于
			};
	</script>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/jCal.js"></script>
	<script src="js/mobile/sign/sign-statistic-flexible.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//获得浏览器的高度
			var windowH = window.innerHeight;
			var windowW = window.innerWidth;
			//设置发起签到模态框的top、left
			$("#loading").css({
				"top" : (windowH / 2 - 50),
				"left" : (windowW / 2 - 67),
				"z-index" : "100000"
			});
			$("#onLoading").css({
				"z-index" : "100000"
			});
		});
	</script>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>