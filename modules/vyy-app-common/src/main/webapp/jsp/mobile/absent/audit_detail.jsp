<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<title>请假</title>
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/leave.css">
<link rel="stylesheet" href="css/contact.css">

<style type="text/css">
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
/* 没有签到事项End */
.oneLoading>div:first-child {
	padding-top: 20px;
}
</style>
</head>
<body>

	<div class="reviewDetailWarp">
		<header class="warpHeader"> ${absentInfo.userName}申请
			${absentTypeName} ${absentTime}</header>
		<section>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${absentInfo.position}</span>
				</div>
				<div class="listLeft">职&emsp;&emsp;位：</div>
			</div>
			<c:if test="${absentInfo.reason != null && !empty absentInfo.reason}">
				<div class="formList clear">
					<div class="listRight">
						<span class="listRightPadding5">${absentInfo.reason}</span>
					</div>
					<div class="listLeft listNameWidth5">请假说明：</div>
				</div>
			</c:if>

			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${absentInfo.department}</span>
				</div>
				<div class="listLeft listNameWidth5">部&emsp;&emsp;门：</div>
			</div>

			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatDate
							value="${absentInfo.beginTime}" pattern="yyyy-MM-dd HH:mm" /></span>
				</div>
				<div class="listLeft listNameWidth5">开始时间：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatDate
							value="${absentInfo.endTime}" pattern="yyyy-MM-dd HH:mm" /></span>
				</div>
				<div class="listLeft listNameWidth5">结束时间：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"> <c:choose>
							<c:when test="${ccUser != null}">${ccUser}</c:when>
							<c:otherwise>无</c:otherwise>
						</c:choose>
					</span>
				</div>
				<div class="listLeft listNameWidth5">抄&nbsp;送&nbsp;人&nbsp;：</div>
			</div>

			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"> <c:choose>
							<c:when test="${audited != null}">${audited}</c:when>
							<c:otherwise>无</c:otherwise>
						</c:choose>
					</span>
				</div>
				<div class="listLeft listNameWidth5">已审核人：</div>
			</div>
<!-- 			<div class="selectPeople">
                <div class="selectPeopleTit whiteBg">下一级审核人(没有可不选)：</div>
                <div class="selectPeopleList">
                    <div class="addedPeopleList">
                       
                    </div>
                    <div class="addPeople" id="addAuditor">
                        <img src="images/addPeople.gif">
                    </div>
                </div>
            </div> -->
			<!-- 批复结果 -->
		    <div class="replyResults">
		        <header>批复结果：</header>
		         <c:if test="${shList != null}" >
					<ul>
						<c:forEach items="${shList}" var="item">
							<c:if test="${item.dealResult == '1'}">
								<li>
									<div>
										<span>${item.accountName}：</span> <span><fmt:formatDate
												value="${item.updateTime}" pattern="yyyy年MM月dd日  HH:mm" /></span>
									</div> <span>同意申请,${item.remark}</span>
								</li>
							</c:if>
							<c:if test="${item.dealResult == '2'}">
								<li>
									<div>
										<span>${item.accountName}：</span> <span><fmt:formatDate
												value="${item.updateTime}" pattern="yyyy年MM月dd日  HH:mm" /></span>
									</div> <span>拒绝申请,${item.remark}</span>
								</li>
							</c:if>
						</c:forEach>
					</ul>
				</c:if>
		    </div>
			<div class="btnGroup">
				<button type="button" class="warpBtn doubleBtn" id="passBtn" absentId="${absentInfo.id}" entityId="${entityId}">通过</button>
				<button type="button" class="warpBtn doubleBtn" id="returnBtn" absentId="${absentInfo.id}" entityId="${entityId}">退回</button>
			</div>
		</section>
	</div>
	<!-- 人员列表End -->
    <div class="membersListWarp myHide"></div>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/mobile/absent/audit-detail.js"></script>
	<script src="js/eSelectPeople-1.1.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//获得浏览器的高度
			var windowH = window.innerHeight;
			var windowW = window.innerWidth;
			//设置发起签到模态框的top、left
			$("#loading").css({
				"top" : (windowH / 2 - 50),
				"left" : (windowW / 2 - 67),
				"z-index" : "200000"
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