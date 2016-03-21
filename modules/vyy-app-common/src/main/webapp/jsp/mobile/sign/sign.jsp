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
<link rel="stylesheet" href="css/signIn-index.css">
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
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
	z-index: 1000;
	position: absolute;
}

.oneLoading>div:first-child {
	padding-top: 20px;
}
</style>
</head>
<body>
	<!-- 加载样式 -->
	<div class="oneLoading loading myHide" id="onLoading">
		<div>
			<img src="images/smallLoading.gif">
		</div>
		<div>加载中...</div>
	</div>
	<!-- 签到事项 -->
	<%-- <c:choose>
		<c:when test="${signListMap!= null && fn:length(signListMap) > 0}">
			<c:forEach items="${signListMap}" var="item">
				<div class="signInWarp" signId = "${item.id}">
					<header>${item.theme}</header>
					<div class="signInTime"><fmt:formatDate value="${item.beginTime}" pattern="HH:mm"/>~<fmt:formatDate value="${item.endTime}" pattern="HH:mm"/></div>
					<i class="fa fa-angle-right"></i>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<!-- 没有签到事项 -->
			<div class="noSignIn">今天没有签到事项......</div>
		</c:otherwise>
	</c:choose> --%>
	<script type="text/javascript">
		$(function() {
			$
					.ajax({
						url : 'mobile/sign/getWaitSignRecord.action',
						type : 'post',
						beforeSend : function() {
							$("#onLoading").show();
						},
						complete : function() {
							$("#onLoading").hide();
						},
						success : function(data, textStatus) {
							var $body = $("body");
							if (typeof(data) != "undefined" && !!data && data.length > 0) {
								var html = "";
								for (var i = 0, size = data.length; i < size; i++) {
									html += "<div class='signInWarp' signid ='"+data[i].id+"' >";
									html += "<header>" + data[i].theme
											+ "</header>";
									html += "<div class='signInTime'><em><i class='fa fa-sun-o'></i></em>"
											+ data[i].sbeginTime
											+ "<br/><em><i class='fa fa-moon-o'></i></em>"
											+ data[i].sendTime + "</div>";
									html += "<i class='fa fa-angle-right'></i>";
									html += "</div>";
								}
								$body.append(html);
								$(".signInWarp")
										.on(
												"click",
												function() {
													window.location.href = "mobile/sign/signDetailView.action?id="
															+ $(this).attr(
																	"signid");
												});
							} else {
								$body
										.append("<div class='noSignIn'>今天没有签到事项......</div>");
							}
						},
						error : function(XMLHttpRequest, textStatus,
								errorThrown) {
							alert("系统错误，请联系管理员！")
						}
					});

		});
		$(document).ready(function() {
			//获得浏览器的高度
			var windowH = window.innerHeight;
			var windowW = window.innerWidth;
			//设置发起签到模态框的top、left
			$("#onLoading").css({
				"top" : (windowH / 2 - 50),
				"left" : (windowW / 2 - 67)
			});
		});
	</script>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>