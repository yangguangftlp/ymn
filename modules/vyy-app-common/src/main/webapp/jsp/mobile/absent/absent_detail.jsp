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
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/leave.css">

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
	<!-- 加载样式 -->
	<!-- <div class="screenModal myHide" id="onLoading">
		<div class="oneLoading loading" id="loading">
			<div>
				<img src="images/smallLoading.gif">
			</div>
			<div>加载中...</div>
		</div>
	</div> -->
	<div class="detailWarp">
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

			<c:if test="${awaitUser != null}">
				<div class="selectPeople  borderBottom">
					<div class="selectPeopleTit whiteBg">待审核人：</div>
					<div class="selectPeopleList">
						<div class="addedPeopleList">
							<div>
								<img src="${awaitUser.avatar}" id="awaitUser"
									uId="${awaitUser.accountId}" absentId="${absentInfo.id}"
									absentTypeName="${absentTypeName}"> <span>${awaitUser.accountName}</span>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<%-- <c:if test="${applyResult != null}">
				<div class="formList clear">
					<div class="listRight">
						<c:if test="${applyResult == '1'}">
							<span class="listRightPadding5">通过</span>
						</c:if>
						<c:if test="${applyResult == '2'}">
							<span class="listRightPadding5">未通过，原因:${applyRemark}</span>
						</c:if>
					</div>
					<div class="listLeft listNameWidth5">批复结果：</div>
				</div>
			</c:if> --%>
			<div class="formList clear">
	            <div class="listRight">
	                <span class="listRightPadding5">${stautsDisplay}</span>
	            </div>
	            <div class="listLeft listNameWidth5">当前状态：</div>
	        </div>
			 <!-- 批复结果 -->
		    <div class="replyResults">
		        <header>批复结果：</header>
		         <c:if test="${shList != null}" >
		       	 <ul>
			        <c:forEach items="${shList}" var="item">
			        		       <c:if test="${item.dealResult == '1'}">
		        <li>
	                <div><span>${item.accountName}：</span>
	                <span><fmt:formatDate value="${item.updateTime}" pattern="yyyy年MM月dd日  HH:mm"/></span></div>
	                <span>同意申请,${item.remark}</span>
	            </li>
	            </c:if>
	           <c:if test="${item.dealResult == '2'}">
		        <li>
	                <div><span>${item.accountName}：</span>
	                <span><fmt:formatDate value="${item.updateTime}" pattern="yyyy年MM月dd日  HH:mm"/></span></div>
	                <span>拒绝申请,${item.remark}</span>
	            </li>
	            </c:if>
			       </c:forEach>
		        </ul>
		     	</c:if>
		    </div>
			<c:if test="${userType != null && userType == '0' && absentInfo.status != '3'}">
				<div class="btnGroup">
					<c:if test="${absentInfo.status ne '-1'}">
					<button type="button" class="doubleBtn warpBtn blurBg" id="urgeBtn">催办</button>
					</c:if>
					<!--增加撤回功能按钮 -->
					<c:if test="${absentInfo.status == '1'}">
						<button type="button" class="doubleBtn warpBtn greenBg" id="withdrawBtn" absentId="${absentInfo.id}">撤回</button>
					</c:if>
				</div>
			</c:if>
		</section>
		<!-- 催办成功模态框Start -->
		<!-- <div class="screenModal myHide">
			<div class="promptModal"></div>
		</div> -->
		<!-- 催办成功模态框End -->
	</div>
	<input data-flag="${flag}" type="hidden" id="flag">
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/mobile/absent/absent-detail.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
        //获得浏览器的高度
        var windowH=window.innerHeight;
        var windowW=window.innerWidth;
        //设置发起签到模态框的top、left
        $("#loading").css({"top":(windowH/2-50),"left":(windowW/2-67),"z-index":"200000"});
        $("#onLoading").css({"z-index":"100000"});
	});
</script>
<script src="js/public-plug-in.js"></script>
<!-- <script type="text/javascript">
   var flag = '${flag}';
   $(function(){
   	if(!!flag && flag == '1')
   	{
   		setTimeout(function () {
   		 	window.addEventListener("popstate", function(e) {
   		 		//如果当前是从审核页面过来的 页面将会关闭
	   		 	try{
	   		 	 	WeixinJSBridge.call('closeWindow'); 
		          }catch (e) {
		        		window.location.href="absent/auditRecordView.action";
				}
   		 	  }, false);
   		      window.history.pushState({title: "title",url: "absent/auditRecordView.action"}, "title", "absent/auditRecordView.action");
   		  	}, 100);
   	   }
   });
</script> -->
<!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>