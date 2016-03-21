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
<link rel="stylesheet" href="css/sign-launched.css">
<link rel="stylesheet" href="css/mobiscroll.css">
<link rel="stylesheet" href="css/contact.css">
<style type="text/css">
/* 第一种提示 */
.onePrompt {
	width: 90%;
	position: absolute;
	top: -30px;
	left: 5%;
	background: rgba(0, 0, 0, 0.4);
	color: #fff;
	text-align: center;
	height: 30px;
	line-height: 30px;
	border-radius: 0px 0px 4px 4px;
}

.onePrompt i {
	color: #fff;
	margin-right: 5px;
	font-size: 14px;
}

.twoPrompt {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	background: rgba(0, 0, 0, 0.4);
	z-index: 5000;
	font-size: 14px;
}

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
	<div class="twoPrompt myHide">
		<div class="onePrompt">
			<i class="fa fa-exclamation-circle"></i><span id="showMsg">主题不能为空</span>
		</div>
	</div>
	<!-- 加载样式 -->
	<div class="screenModal myHide" id="onLoading">
		<div class="oneLoading loading" id="loading">
			<div>
				<img src="images/smallLoading.gif">
			</div>
			<div>加载中...</div>
		</div>
	</div>
	<div class="signLaunchedWarp">
		<form action="" method="post">
			<div class="signList clear">
				<div class="signListRight">
					<input type="text" id="signTheme" name="signTheme" data-con="*"
						data-empty="主题不能为空" placeholder="请填写" id="signTheme">
				</div>
				<div class="signListLeft">签到主题：</div>
			</div>
			<div class="signList clear">
				<div class="signListRight">
					<div class="controls input-append">
						<input type="text" id="signStartTime" name="signStartTime"
							data-con="*" data-empty="请选择开始时间" readonly> <label
							class="add-on" for="signStartTime"><i
							class="icon-th fa fa-clock-o"></i></label>
					</div>
				</div>
				<div class="signListLeft">开始时间：</div>
			</div>
			<div class="signList clear">
				<div class="signListRight">
					<div class="controls input-append">
						<input type="text" id="signEndTime" name="signEndTime"
							data-con="*" data-empty="请选择结束时间" readonly> <label
							class="add-on" for="signEndTime"><i
							class="icon-th fa fa-clock-o"></i></label>
					</div>
				</div>
				<div class="signListLeft">结束时间：</div>
			</div>
			<div class="signInPeople">
				<input type="hidden" id="people" name="people" data-con="*"
					data-empty="请选择签到人员" />
				<div class="signInPeopleTit">签到人员</div>
				<div class="signInPeopleList">
					<div class="addedPeopleList">
						<div class="addPeople">
							<img src="images/addPeople.gif">
						</div>
					</div>
				</div>
			</div>
			<button type="button" class="signLaunchedBtn">发起签到</button>
		</form>
		<!-- 发起成功模态框Start -->
		<div class="modalSignLaunched myHide">发起成功</div>
		<!-- 发起成功模态框End -->
	</div>
	<!-- 人员列表Start -->
	<div class="membersListWarp myHide"></div>
	<!-- 人员列表End -->
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/mobiscroll.js"></script>
	<script src="js/mobiscroll_zh.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/eSelectPeople-1.1.js"></script>
	<script src="js/mobile/sign/sign-launched.js"></script>
	<script>
		$(function() {
			function showTip(msg) {
				$(".twoPrompt").show();
				$("#showMsg").html(msg);
				$(".onePrompt").animate({
					top : "0px"
				}, 500, function() {
					setTimeout(function() {
						$(".onePrompt").animate({
							top : "-30px"
						}, function() {
							$(".twoPrompt").hide();
						});
					}, 1000);
				});
			}
			//发起校验
			function check() {
				var signTheme = $("#signTheme").val();
				var signStartTime = $("#signStartTime").val();
				var signStartEnd = $("#signEndTime").val();
				//添加时间校验不能大于当前时间
				var datNow = new Date();
				var dateValue = Date.parse(signStartTime);

				if (dateValue < datNow) {
					showTip("开始时间必须在当前时间之后！");
					return false;
				}
				dateValue = Date.parse(signStartEnd);
				if (dateValue < datNow) {
					showTip("结束时间必须在当前时间之后！");
					return false;
				}
				if (signStartTime >= signStartEnd) {
					showTip("开始时间不能大于等于结束时间！");
					return false;
				}
				return true;
			}
			//发起签到成功
			$(".signLaunchedBtn")
					.click(
							function() {
								//获取人员信息
								var data = [];
								$(".addedPeopleList > div[uId]")
										.each(
												function() {
													data
															.push({
																"uId" : $(this)
																		.attr(
																				"uId"),
																"uAvatar" : $(
																		this)
																		.attr(
																				"uAvatar"),
																"uName" : $(
																		this)
																		.attr(
																				"uName"),
																"entityType" : "0"
															});
												});
								$("#people").val(data.join(""));
								//表单验证
								$("form")
										.verification(
												{
													callback : function() {
														if (!check()) {
															return;
														}
														//成功之后
														$
																.ajax({
																	url : 'mobile/sign/launchSign.action',
																	type : 'post',
																	data : {
																		launchSignInfo : JSON
																				.stringify({
																					beginTime : $(
																							"#signStartTime")
																							.val(),
																					endTime : $(
																							"#signEndTime")
																							.val(),
																					theme : $(
																							"#signTheme")
																							.val(),
																					userInfo : data
																				})
																	},
																	success : function(
																			data,
																			textStatus) {
																		if (data.status == 0) {
																			//成功之后
																			$(
																					".modalSignLaunched")
																					.removeClass(
																							"myHide");
																			setTimeout(
																					function() {
																						$(
																								".modalSignLaunched")
																								.stop()
																								.animate(
																										{
																											opacity : 0
																										},
																										300,
																										function() {
																											$(
																													".modalSignLaunched")
																													.addClass(
																															"myHide");
																										});
																					},
																					300);
																			window.location.href = "mobile/sign/myLaunchSignDetailView.action?id="
																					+ data.value;
																		}
																	},
																	error : function(
																			XMLHttpRequest,
																			textStatus,
																			errorThrown) {
																		alert(JSON
																				.stringify(errorThrown));
																	}
																});
													}
												});

							});
		});
	</script>
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