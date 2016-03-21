<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head lang="en">
<title>签到</title>
<%@ include file="../../include/head.jsp"%>
<link rel="stylesheet" href="css/communal.css">
<link rel="stylesheet" href="css/signIn-mySignTheme.css">
</head>
<body>
	<div class="mySignTheme">

		<c:choose>
			<c:when test="${signInfo != null }">
				<header>
					<div class="themeTit">签到主题：${signInfo.theme}</div>
					<div class="themeTime">
						<span>${time}</span>
                		<span>${date}</span>
					</div>
				</header>
				<div class="themeDetail">
					<div class="formList clear">
						<div class="listRight">
							<span>${signInfo.sbeginDate}</span>
						</div>
						<div class="listLeft">开始时间：</div>
					</div>
					<div class="formList clear">
						<div class="listRight">
							<span>${signInfo.sendDate}</span>
						</div>
						<div class="listLeft">结束时间：</div>
					</div>
					<%-- <div class="peopleList signedPeople clear">
						<div class="signInRight">
							<span>${shouldSignUserInfo}</span>
						</div>
						<div class="signInLeft">应签到人员：</div>
					</div> --%>
					<div class="formList signedPeople clear">
						<div class="listRight">
							<span>${signUserInfo}</span>
						</div>
						<div class="listLeft listNameWidth6">已签到人员：</div>
					</div>
					<div class="formList unSignedPeople clear">
						<div class="listRight">
							<span>${noSignUserInfo}</span>
						</div>
						<div class="listLeft listNameWidth6">未签到人员：</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>

			</c:otherwise>
		</c:choose>
	</div>
	<script src="js/jquery-1.8.3.min.js"></script>
<!-- 	<script src="js/signIn-mySignTheme.js"></script> -->
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>