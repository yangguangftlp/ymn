<!-- 排除或增加日期设置页面 -->
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
					<li><a  href="manage/sign/setupAttendanceRule.action">考勤设置</a></li>
					<li class="active"><a href="manage/sign/workdayList.action">排除或增加日期设置</a></li>
				</ul>
				<div class="tab-content">
					<div id="tab_2_1" class="tab-pane active" style="display:block">
						<div class="row-fluid">
							<!-- BEGIN table -->
							<div class="order-table">
								<div class="order-table-head order-search-box table-search-bar">
									<h5 class="set-hd-bar clearfix">
										<a title="刷新" href="javascript:location.reload();" class="pull-right"><i class="icon-refresh"></i></a>
										<a class="pull-right"><i title="添加" data-toggle="modal" data-target="#addSignType" class="icon-plus"></i></a>
<!-- 										<a class="pull-right"><i title="导入" class="icon-download"></i></a> -->
									</h5>
								</div>
								<div class="v-table">
									<table class="table  table-hover" id="ck_table" width="100%">
										<thead>
											<tr>
		                                        <th>开始日期</th>
		                                        <th>结束日期</th>
		                                        <th>类型</th>
		                                        <th>备注</th>
		                                        <th>操作</th>
		                                    </tr>
										</thead>
										
									</table>
									
								</div>
								<p class="app-manage-sign-tip"><a href="#"  data-target="#signHoliday" data-toggle="modal">&gt;&gt;查看2016年法定节假日安排</a></p>
							</div>
							<!-- END table -->
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- END MY APP-->
		<!-- BEGIN MODAL-->
        <div id="signHoliday" class="modal fade modal-absent-type" aria-hidden="true" style="display:none">
            <div class="modal-dialog">
                  <div class="modal-content">
                     <div class="modal-header">
                        <button type="button" class="close"data-dismiss="modal" aria-hidden="true"> </button>
                        <h4 class="modal-title" id="myModalLabel">2016年法定节假日</h4>
                     </div>
                     <div class="modal-body row-fluid sign-holiday-content">
                        <h3 class="title">国务院办公厅关于2016年部分节假日安排的通知</h3>
                        <p class="sub-title">国办发明电〔2015〕18号</p>
                        <p>各省、自治区、直辖市人民政府，国务院各部委、各直属机构：</p>
                        <p>经国务院批准，现将2016年元旦、春节、清明节、劳动节、端午节、中秋节和国庆节放假调休日期的具体安排通知如下。</p>
                        <p><strong>一、元旦：</strong>1月1日放假，与周末连休。</p>
                        <p><strong>二、春节：</strong>2月7日至13日放假调休，共7天。2月6日（星期六）、2月14日（星期日）上班。</p>
                        <p><strong>三、清明节：</strong>4月4日放假，与周末连休。</p>
                        <p><strong>四、劳动节：</strong>5月1日放假，5月2日（星期一）补休。</p>
                        <p><strong>五、端午节：</strong>6月9日至11日放假调休，共3天。6月12日（星期日）上班。</p>
                        <p><strong>六、中秋节：</strong>9月15日至17日放假调休，共3天。9月18日（星期日）上班。</p>
                        <p><strong>七、国庆节：</strong>10月1日至7日放假调休，共7天。10月8日（星期六）、10月9日（星期日）上班。</p>
                        <p>节假日期间，各地区、各部门要妥善安排好值班和安全、保卫等工作，遇有重大突发事件，要按规定及时报告并妥善处置，确保人民群众祥和平安度过节日假期。</p>
                        <p class="inscribe">国务院办公厅<br/>2015年12月10日</p>
                     </div>
                     <div class="modal-footer">
                        <button type="button" class="btn btn-primary " data-dismiss="modal">关闭</button>
                     </div>
                  </div><!-- /.modal-content -->
            </div>
        </div>
        <!-- END MODAL -->
        <!-- BEGIN MODAL-->
        <div id="addSignType" class="modal fade modal-absent-type" aria-hidden="true" style="display:none">
            <div class="modal-dialog">
                 <div class="modal-content">
                     <div class="modal-header">
                        <button type="button" class="close"data-dismiss="modal" aria-hidden="true"> </button>
                        <h4 class="modal-title" id="myModalLabel">添加</h4>
                     </div>
                     <div class="modal-body row-fluid">
                        <form action="" class="form-horizontal">
                            <div class="control-group">
                                <label class="control-label must">开始时间</label>
                                <div class="controls">
									<div class="input-append date form_datetime">
										<input type="text" class="m-wrap date-picker" readonly="readonly" id="startDay" size="16">
										<span class="add-on"><i class="icon-calendar"></i></span>
									</div>
								</div>
                			</div>
                			<!-- END　ROW -->
			                <div class="control-group">
			                    <label class="control-label must">结束时间</label>
			                    <div class="controls">
									<div class="input-append date form_datetime">
										<input type="text" class="m-wrap date-picker" readonly="readonly" id="endDay"  size="16">
										<span class="add-on"><i class="icon-calendar"></i></span>
									</div>
								</div>
			                </div>
			                <!-- END　ROW -->
			                <div class="control-group">
			                    <label class="control-label must">类型</label>
			                    <div class="controls">
			                        <select name="attendOrNot" id="attendOrNot">
			                        	<option value="0">排除考勤日期</option>
			                        	<option value="1">增加考勤日期</option>
			                        </select>
			                    </div>
			                </div>
			                <!-- END　ROW -->
			                <div class="control-group">
			                    <label class="control-label ">备注</label>
			                    <div class="controls">
			                        <input type="text" class="m-wrap"id="remark" >
			                    </div>
			                </div>
			                <!-- END　ROW -->
             			</form>
      				 </div>
			         <div class="modal-footer">
			            <button type="button" id="save" class="btn btn-default orange">保存</button>
			            <button type="button" class="btn btn-primary " data-dismiss="modal">取消</button>
			         </div>
      			</div><!-- /.modal-content -->
            </div>
        </div>
        <!-- END MODAL -->
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
		workdayList.init();
	});
</script>
</body>
</html>
