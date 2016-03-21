<!-- 考勤列表页面 -->
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/layout.jsp"%>
<title>微移云管理系统</title>
<style>
th, td { white-space: nowrap; }
</style>
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
					<li class="active"><a  href="manage/sign/attendanceList.action">签到列表</a></li>
					<li><a  href="manage/sign/setupAttendanceRule.action">考勤设置</a></li>
					<li><a href="manage/sign/workdayList.action">排除或增加日期设置</a></li>
				</ul>
				<div class="tab-content">
					<div id="tab_2_1" class="tab-pane active" style="display:block">
						<div class="row-fluid">
							<!-- BEGIN table -->
							<div class="order-table">
								<div class="order-table-head order-search-box table-search-bar">
									<div class="form-horizontal">
										<div class="control-group">
<!-- 											<label for="" class="control-label">时间区间：</label> -->
											<div class="controls">
												<span><input type="text"
													class="m-wrap m-ctrl-medium date-picker" placeholder="开始时间" id="starttime"></span>
												<span>至</span> <span><input type="text"
													class="m-wrap m-ctrl-medium date-picker" placeholder="结束时间" id="endtime"></span>
											</div>
										</div>
										<div class="control-group">
<!-- 											<label for="" class="control-label">员工：</label> -->
											<div class="controls">
												<input name="userName" id="userName" type="text"
													placeholder="员工姓名">
											</div>
										</div>
										<div class="control-group">
											<div class="controls">
												<button type="button" id="queryBtn"  class="btn orange"><i class="icon-search"></i>&nbsp;查询</button>
	                   							<button type="button" id="exportBtn" class="btn"><i class="icon-download-alt"></i>&nbsp;导出excel</button>
<!-- 	                   							<button type="button" id="refreshBtn" class="btn"><i class="icon-refresh"></i>&nbsp;全部</button> -->
											</div>
										</div>
										
									</div>
								</div>
								<div class="v-table">
									<table class="table table-hover" id="ck_table" width="100%">
										<thead>
											<tr>
												<th>员工工号</th>
												<th>员工姓名</th>
												<th>签到地点</th>
												<th>签到时间</th>
												<th>签到/签退</th>
												<th>备注</th>
											</tr>
										</thead>
										
									</table>
									
								</div>
							</div>
							<!-- END table -->
						</div>
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
<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="js/datatables/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" src="js/manage/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="js/manage/mcommon.js"></script>
<script type="text/javascript" src="js/manage/sign/sign-manage.js"></script>
<script>
	$(function() {
		signList.init();
		//start end date
	});
</script>
</body>
</html>
