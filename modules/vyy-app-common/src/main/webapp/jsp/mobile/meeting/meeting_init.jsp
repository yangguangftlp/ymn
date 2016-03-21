<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<base href="${contextPath}/">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache, must-revalidate">
<meta http-equiv="expires" content="0">
<link rel="stylesheet" href="css/global.css">
<title>预订会议室</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global_bosum.css">
<link rel="stylesheet" href="css/common_bosum.css">
<link rel="stylesheet" href="css/meetingRoom_Bosum.css">

</head>
<body>
	<div class="warp">
		<header id="showdate" class="meetTheme headbg showdate border-none"></header>
		<section class="meetRoom">
			<div class="formList clear border-none">
				<div class="listRight">
					<i id="previousweek" class="fa fa-angle-left left previousweek"
						data-flag="0"></i>
					<!--显示日期-->
					<table id="mytable" class="mytable" cellspacing="0" border="0"
						align="center" width="90%">
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</table>
					<i id="nextweek" class="fa fa-angle-right right nextweek"
						data-flag="0"></i>
				</div>
			</div>
			<div class="listRight choose-sign filter-m">
				<ul>
					<li><div>显示全部</div> <i class="fa fa-meh-o"></i></li>
					<li mode="1"><div>容纳人数</div> <i class="fa fa-long-arrow-up"></i></li>
					<li data-url=""><i class="fa fa-filter"></i> 筛选</li>
				</ul>
			</div>
			<div class="meetRoomCon"></div>
		</section>
		<!-- 无可用会议室 -->
		<div id="noRecord" class="noRecord" style="display: none">无可用会议室...</div>
		<!-- 点击获取日期隐藏域 -->
		<input id="save_Date" type="hidden" value="" /> <input
			id="JmeetRoomConPage" type="hidden" value="1" />
	</div>
	<footer class="copyright">© 微移云技术支持</footer>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/mobile/meeting/doListView.js"></script>
	<script src="js/mobile/meeting/meetingDateCs.js"></script>
	<script src="js/mobile/meeting/conditionBosum.js"></script>
	<!-- <script src="js/meetingRoom/judgeScroll.js"></script>  -->
	<script src="js/toast.js"></script>
	<script type="text/javascript">
		$(function() {
			meetingDateCs.init().bindEvent();
		});
	</script>
</body>
</html>
