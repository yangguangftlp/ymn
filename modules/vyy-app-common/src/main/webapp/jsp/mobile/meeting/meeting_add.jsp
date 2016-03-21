<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
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
<link rel="stylesheet" href="css/common_bosum.css">
<link rel="stylesheet" href="css/contact.css">
<link rel="stylesheet" href="css/meetingRoom_Bosum.css">
</head>
<body>
	<div class="warp">
		<!-- 主题名称 -->
		<header class="h-bg">
			<div class="meetTheme headbg">${meetingRoom.roomName }</div>
			<div class="meetAddress">
				<%-- ${meetingInfo.roomName} --%>
				设备情况：${meetingRoom.equipment }，可容纳${meetingRoom.capacity }人
			</div>
		</header>
		<!-- 时间段选择 -->
		<div class="formList clear">
			<div class="listRight">
				<i id="yesterdayDt" class="fa fa-angle-left left" data-flag="0"
					style="width: 20px; height: 36px;"></i> <span id="mt-date"
					class="title mt-date">${dateBuilder }</span> <i id="tomorowDt"
					class="fa fa-angle-right right" data-flag="0"></i>
			</div>
			<div class="listRight choose-time">
				<ul>
				</ul>
			</div>
			<div class="listRight choose-sign">
				<ul>
					<li><span></span>未预定</li>
					<li><span></span>我的预定</li>
					<li><span></span>已预订</li>
				</ul>
			</div>
		</div>
		<!-- 会议主题 -->
		<div class="formList clear">
			<div class="listRight">
				<input class="listRightPadding5" name="theme" id="theme"
					placeholder="例如：需求讨论分析会议" data-con="*" data-empty="请填写会议主题"
					value="">
			</div>
			<div class="listLeft listNameWidth5">
				<span class="title">会议主题：</span>
			</div>
		</div>
		<!-- 是否重复预定 -->
		<div class="formList clear">
			<div class="listRight">
				<button id="switchOne" type="button" class="mwui-switch-btn">
					<span change="0" class="off"></span><input type="hidden"
						name="show_icon" value="0" />
				</button>
				<input type="hidden" class="listRightPadding7 reservation"
					name="reservation" id="reservation" value="0">
			</div>
			<div class="listLeft listNameWidth7">
				<span class="title">是否重复预定：</span>
			</div>
		</div>
		<!-- 是否通知参会人员 -->
		<div class="formList clear">
			<div class="listRight">
				<button id="switchTow" type="button" class="mwui-switch-btn switch">
					<span change="0" class="off"></span><input type="hidden"
						name="show_icon" value="0" />
				</button>
				<input type="hidden" class="listRightPadding12 reservation"
					name="sendtopp" id="sendtopp" value="0">
			</div>
			<div class="listLeft listNameWidth12">
				<span class="title">是否接收参会人与会通知：</span>
			</div>
		</div>
		<!-- 参会人员 -->
		<div class="selectPeople" id="selectAuditor">
			<div class="selectPeopleTit">参会人员</div>
			<div class="selectPeopleList">
				<div class="addedPeopleList">
					<div class="addPeople" id="addPeople">
						<img class="index_up" src="images/addPeople.gif">
					</div>
				</div>
			</div>
		</div>
		<!-- <div class="borderTop borderBottom inform">
            <i class="fa fa-check-circle-o informBtn"></i>
            <label>微信企业号通知参会人员</label>
        </div> -->
		<div class="btnGroup">
			<button type="button" class="singleBtn warpBtn blurBg" id="passBtn"
				data-url="">确认</button>
			<!-- <button type="button" class="singleBtn warpBtn lightGrayBg" id="cancelBtn" data-url="">取消</button> -->
		</div>
		<input type="hidden" name="informRadio" id="informRadio" value="true">
		<input type="hidden" name="thisDate" id="thisDate" value="">
		<input type="hidden" name="thisEndTime" id="thisEndTime" value="">
	</div>
	<!-- 人员列表End -->
	<div class="membersListWarp myHide"></div>
	<footer class="copyright">© 微移云技术支持</footer>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/eSelectPeople-1.1.js"></script>
	<script src="js/toast.js"></script>
	<script src='js/jweixin-1.0.0.js'></script>
	<script src='js/eJweixin-1.0.0.js'></script>
	<script src="js/mobile/meeting/chooseSeat.js"></script>
	<script src="js/mobile/meeting/theme.js"></script>
	<script type="text/javascript">
	</script>
	
</body>
</html>
