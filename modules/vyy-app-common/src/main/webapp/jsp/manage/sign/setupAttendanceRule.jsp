<!-- 设置考勤规则页面 -->
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/layout.jsp"%>
<title>微移云管理系统</title>
</head>
<body class="frame-body">
<!-- BEGIN PAGE -->
<div class="page-content">
	<!-- BEGIN PAGE CONTAINER-->
	<div class="container-fluid">
		<!-- BEGIN PAGE HEADER-->
		<div class="row-fluid">
			<div class="span12">
				<!-- BEGIN PAGE TITLE & BREADCRUMB-->
				<h3 class="page-title">
					我的应用 <small>签到</small>
				</h3>
				<!-- END PAGE TITLE & BREADCRUMB-->
			</div>
		</div>
		<!-- BEGIN MY APP-->
		<div class="app-set-fluid">
			<div class="tabbable tabbable-custom tabs-left">
				<!-- Only required for left/right tabs -->
				<ul class="nav nav-tabs tabs-left">
					<li><a  href="manage/sign/attendanceList.action">签到列表</a></li>
					<li class="active"><a  href="manage/sign/setupAttendanceRule.action">考勤设置</a></li>
					<li><a href="manage/sign/workdayList.action">排除或增加日期设置</a></li>
				</ul>
				<div class="tab-content">
					<div id="tab_2_2" class="tab-pane" style="display:block">
						<!-- app manage -->
						<form id="attendanceRuleForm"
							class="form-horizontal form-set-sign" action="#">
							<input id="id" type="hidden" />
							<h5 class="set-hd-bar">
								<strong>考勤规则设置</strong>
<!-- 								<a class="pull-right" title='返回'><i -->
<!-- 									class="icon-reply"></i> </a> -->
									<a class="pull-right" href="javascript:location.reload();"><i class="icon-refresh" title='刷新' ></i></a>
							</h5>
							<hr>
							<h5>
								<strong>星期</strong>
							</h5>
							<div class="control-group">
								<div class="controls">
									<!-- $('#test').is(':checked') $('#test').prop("checked")-->
									<label class="checkbox"> <input id="mon" class="checkbox" type="checkbox" />
										星期一
									</label> <label class="checkbox"> <input class="checkbox" id="tues"
										type="checkbox" />
										星期二
									</label> <label class="checkbox"> <input class="checkbox" id="wed"
										type="checkbox" />
										星期三
									</label> <label class="checkbox"> <input class="checkbox" id="thur"
										type="checkbox" />
										星期四
									</label> <label class="checkbox"> <input class="checkbox" id="fri"
										type="checkbox" />
										星期五
									</label> <label class="checkbox"> <input class="checkbox" id="sat"
										type="checkbox" />
										星期六
									</label> <label class="checkbox"> <input class="checkbox" id="sun"
										type="checkbox" />
										星期日
									</label>
								</div>
							</div>
							<h5>
								<strong>标准工作时间</strong>
							</h5>
							<div class="controls">
								<label class="checkbox">早 <select id="startHour">
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
								</select> ： <select id="startMinute">
										<option value="00">00</option>
										<option value="10">10</option>
										<option value="20">20</option>
										<option value="30">30</option>
										<option value="40">40</option>
										<option value="50">50</option>
										</select> 迟到 <select id="delayMinutes">
										<option value="0">0</option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="10">10</option>
								</select>分钟，不算迟到
								</label>
							</div>
							<div class="controls">
								<label class="checkbox">晚 <select id="endHour">
										<option value="16">16</option>
										<option value="17">17</option>
										<option value="18">18</option>
										<option value="19">19</option>
										<option value="20">20</option>

								</select> ： <select id="endMinute">
										<option value="00">00</option>
										<option value="10">10</option>
										<option value="20">20</option>
										<option value="30">30</option>
										<option value="40">40</option>
										<option value="50">50</option>

								</select>
								</label>
							</div>
							<div class="form-actions">
								<button id="setupBtn" type="button" class="btn orange">
									<i class="icon-ok"></i> 保存
								</button>
								<button type="button" class="btn" id="defaultBtn">恢复默认</button>
								<span class="ml-10">默认时间：09：00-18：00</span>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<!-- END MY APP-->

	</div>
</div>

<!-- BEGIN CORE PLUGINS -->
<script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
<script type="text/javascript" src="js/manage/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="js/manage/mcommon.js"></script>
<script type="text/javascript" src="js/manage/sign/sign-manage.js"></script>
<script>
	$(function() {
		setupAttendance.init({
			settingObj:'${attendanceRuleJson}'
		});
	});
</script>
</body>
</html>