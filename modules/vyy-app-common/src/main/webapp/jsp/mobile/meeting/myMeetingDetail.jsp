<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
<script src="js/jquery-1.8.3.min.js"></script>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global_bosum.css">
<link rel="stylesheet" href="css/common_bosum.css">
<link rel="stylesheet" href="css/meetingRoom_Bosum.css">
</head>
<body>
	<div class="warp roomNameDetail">
		<header>
			<div class="meetTheme">${theme}</div>
			<div class="meetAddress">${roomName}</div>
		</header>
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5">${date}</span>
			</div>
			<div class="listLeft listNameWidth5">
				<i class="fa fa-calendar icon"></i> <span class="title">日期：</span>
			</div>
		</div>
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5" id="startAndEndSpan"></span>
			</div>
			<div class="listLeft listNameWidth5">
				<i class="fa fa-clock-o icon"></i> <span class="title">时间：</span>
			</div>
		</div>
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5">${timeLengthStr}&nbsp;小时</span>
			</div>
			<div class="listLeft listNameWidth5">
				<i class="fa fa-bell-o icon"></i> <span class="title">时长：</span>
			</div>
		</div>
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5">${adress}</span>
			</div>
			<div class="listLeft listNameWidth5">
				<i class="fa fa-map-marker icon"></i> <span class="title">地点：</span>
			</div>
		</div>
		<div class="formList clear ">
			<div class="listRight">
				<span class="listRightPadding5">${meetingStatus}</span>
			</div>
			<div class="listLeft listNameWidth5">
				<span class="title">会议状态：</span>
			</div>
		</div>
		<div class="selectPeople" id="selectAuditor">
			<div class="selectPeopleTit whiteBg">参会人员：共邀请 ${eaListSize}人</div>
			<div class="selectPeopleList">
				<div class="addedPeopleList">
					<c:if test="${eaList != null && eaList.size() > 0}">
		                <c:forEach items="${eaList}" var="item">
		                	<div>
			                    <img src="${item.avatar}">
			                    <span>${item.accountName}</span>
			                    <c:if test="${item.dealResult=='1'}">
			                    	<em class="joined"></em>
		                    	</c:if>
			                </div>
		                </c:forEach>
              		</c:if>
				</div>
			</div>
		</div>
		<div class="btnGroup">
			<c:if
				test="${ meetingStatusDigit==1 && dealResult==0 }">
				<button type="button" class="singleBtn warpBtn blurBg"
					id="affirmBtn" data-url="">确认参加</button>
			</c:if>
			<%-- <c:if
				test="${ meetingStatusDigit==1 && dealResult==1 }">
				<button type="button" class="singleBtn warpBtn grayBg"
					id="affirmBtn2" data-url="" disabled="disabled">已确认参加</button>
			</c:if> --%>
			<c:if
				test="${currentUserType==0 && meetingStatusDigit==1 }">
				<button type="button" class="singleBtn warpBtn yellowBg"
					id="cancelBtn" data-url="">取消会议</button>
			</c:if>
		</div>
	</div>
	<input type="hidden" value="${startAndEnd}" id="startAndEnd"/>
	<input type="hidden" value="${meetingId}" id="meetingId" />
	<script src="js/toast.js"></script>
	<script src="js/mobile/meeting/detail.js"></script>
</body>
</html>
