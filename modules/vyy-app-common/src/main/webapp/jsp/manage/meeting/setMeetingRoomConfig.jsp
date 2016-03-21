<!-- 会议室设置页面 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>微移云管理系统</title>
<%@ include file="../../include/layout.jsp"%>
</head>
<body class="frame-body">
	<!-- BEGIN PAGE HEADER-->
	<div class="row-fluid">
		<div class="span12">
			<!-- BEGIN PAGE TITLE & BREADCRUMB-->
			<h3 class="page-title">
				我的应用 <small>会议室</small>
			</h3>
			<!-- END PAGE TITLE & BREADCRUMB-->
		</div>
	</div>
	<!-- END PAGE HEADER-->
	<!-- BEGIN MY APP-->
                <div class="app-set-fluid">
                    <!-- app manage -->
                    <div class="v-box row-fluid">
                                <div class="order-table-head">
	                               <a href="#" class="btn" data-target="#setMeetingRoomTime" data-toggle="modal">起止时间设置</a>
	                               <a href="#" data-target="#addMeetingRoom" data-toggle="modal" class="btn">添加会议室</a>
	                               <a href="javascript:void(0)" id="delMRoom" class="btn">删除会议室</a>
	                           </div>
	                           <div class="v-table">
		                            <table class="table  table-hover" id="ck_table" width="100%">
		                                <thead>
		                                    <tr>
		                                    	<th><input type="checkbox" class="group-checkable" ></th>
		                                        <th>会议室名称</th>
		                                        <th>地点</th>
		                                        <th>容纳人数</th>
		                                        <th>设备情况</th>
		                                    </tr>
		                                </thead>
		                                <tbody>
		                                   
		                                </tbody>
		                            </table>
		                       </div>
                        </div>  
                </div>
                <!-- END MY APP-->       
				<!-- BEGIN MODAL-->
            <div id="setMeetingRoomTime" class="modal fade modal-absent-type" aria-hidden="true" style="display:none">
                <div class="modal-dialog">
                      <div class="modal-content">
                         <div class="modal-header">
                            <button type="button" class="close"data-dismiss="modal" aria-hidden="true"> </button>
                            <h4 class="modal-title" id="myModalLabel">起止时间设置 <small>（默认时间：8:30-21:00）</small></h4>
                         </div>
                         <div class="modal-body row-fluid sign-holiday-content">
                            <form action="" class="form-horizontal">
                            	 <div class="control-group">
                                    <label class="control-label must">开始时间</label>
                                    <div class="controls">
										<select name="start" id="start">
											 <option value="00:00:00">00:00</option>
											 <option value="00:30:00">00:30</option>
											 <option value="01:00:00">01:00</option>
											 <option value="01:30:00">01:30</option>
											 <option value="02:00:00">02:00</option>
											 <option value="02:30:00">02:30</option>
											 <option value="03:00:00">03:00</option>
											 <option value="03:30:00">03:30</option>
											 <option value="04:00:00">04:00</option>
											 <option value="04:30:00">04:30</option>
											 <option value="05:00:00">05:00</option>
											 <option value="05:30:00">05:30</option>
											 <option value="06:00:00">06:00</option>
											 <option value="06:30:00">06:30</option>
											 <option value="07:00:00">07:00</option>
											 <option value="07:30:00">07:30</option>
											 <option value="08:00:00">08:00</option>
											 <option value="08:30:00">08:30</option>
											 <option value="09:00:00">09:00</option>
											 <option value="09:30:00">09:30</option>
											 <option value="10:00:00">10:00</option>
											 <option value="10:30:00">10:30</option>
											 <option value="11:00:00">11:00</option>
											 <option value="11:30:00">11:30</option>
											 <option value="12:00:00">12:00</option>
											 <option value="12:30:00">12:30</option>
											 <option value="13:00:00">13:00</option>
											 <option value="13:30:00">13:30</option>
											 <option value="14:00:00">14:00</option>
											 <option value="14:30:00">14:30</option>
											 <option value="15:00:00">15:00</option>
											 <option value="15:30:00">15:30</option>
											 <option value="16:00:00">16:00</option>
											 <option value="16:30:00">16:30</option>
											 <option value="17:00:00">17:00</option>
											 <option value="17:30:00">17:30</option>
											 <option value="18:00:00">18:00</option>
											 <option value="18:30:00">18:30</option>
											 <option value="19:00:00">19:00</option>
											 <option value="19:30:00">19:30</option>
											 <option value="20:00:00">20:00</option>
											 <option value="20:30:00">20:30</option>
											 <option value="21:00:00">21:00</option>
											 <option value="21:30:00">21:30</option>
											 <option value="22:00:00">22:00</option>
											 <option value="22:30:00">22:30</option>
											 <option value="23:00:00">23:00</option>
											 <option value="23:30:00">23:30</option>
										</select>
									</div>
                                </div>
                                <!-- END　ROW -->
                                <div class="control-group">
                                    <label class="control-label must">结束时间</label>
                                    <div class="controls">
										<select name="end" id="end">
											 <option value="00:00:00">00:00</option>
											 <option value="00:30:00">00:30</option>
											 <option value="01:00:00">01:00</option>
											 <option value="01:30:00">01:30</option>
											 <option value="02:00:00">02:00</option>
											 <option value="02:30:00">02:30</option>
											 <option value="03:00:00">03:00</option>
											 <option value="03:30:00">03:30</option>
											 <option value="04:00:00">04:00</option>
											 <option value="04:30:00">04:30</option>
											 <option value="05:00:00">05:00</option>
											 <option value="05:30:00">05:30</option>
											 <option value="06:00:00">06:00</option>
											 <option value="06:30:00">06:30</option>
											 <option value="07:00:00">07:00</option>
											 <option value="07:30:00">07:30</option>
											 <option value="08:00:00">08:00</option>
											 <option value="08:30:00">08:30</option>
											 <option value="09:00:00">09:00</option>
											 <option value="09:30:00">09:30</option>
											 <option value="10:00:00">10:00</option>
											 <option value="10:30:00">10:30</option>
											 <option value="11:00:00">11:00</option>
											 <option value="11:30:00">11:30</option>
											 <option value="12:00:00">12:00</option>
											 <option value="12:30:00">12:30</option>
											 <option value="13:00:00">13:00</option>
											 <option value="13:30:00">13:30</option>
											 <option value="14:00:00">14:00</option>
											 <option value="14:30:00">14:30</option>
											 <option value="15:00:00">15:00</option>
											 <option value="15:30:00">15:30</option>
											 <option value="16:00:00">16:00</option>
											 <option value="16:30:00">16:30</option>
											 <option value="17:00:00">17:00</option>
											 <option value="17:30:00">17:30</option>
											 <option value="18:00:00">18:00</option>
											 <option value="18:30:00">18:30</option>
											 <option value="19:00:00">19:00</option>
											 <option value="19:30:00">19:30</option>
											 <option value="20:00:00">20:00</option>
											 <option value="20:30:00">20:30</option>
											 <option value="21:00:00">21:00</option>
											 <option value="21:30:00">21:30</option>
											 <option value="22:00:00">22:00</option>
											 <option value="22:30:00">22:30</option>
											 <option value="23:00:00">23:00</option>
											 <option value="23:30:00">23:30</option>
										</select>
									</div>
                                </div>
                                <!-- END　ROW -->
                             </form>
                         </div>
                         <div class="modal-footer">
                            <button type="button" id="setting" class="btn btn-default orange">保存</button>
                            <button type="button" id="default" class="btn btn-primary ">恢复默认</button>
                         </div>
                      </div><!-- /.modal-content -->
                </div>
            </div>
        <!-- END MODAL -->
        <!-- BEGIN MODAL-->
            <div id="addMeetingRoom" class="modal fade modal-absent-type" aria-hidden="true" style="display:none">
                <div class="modal-dialog">
                      <div class="modal-content">
                         <div class="modal-header">
                            <button type="button" class="close"data-dismiss="modal" aria-hidden="true"> </button>
                            <h4 class="modal-title" id="myModalLabel">添加会议室</h4>
                         </div>
                         <div class="modal-body row-fluid">
                            <form action="" class="form-horizontal">
                            	 <div class="control-group">
                                    <label class="control-label must">会议室名称</label>
                                    <div class="controls">
										<input type="text" class="m-wrap"  id="roomName">
									</div>
                                </div>
                                <!-- END　ROW -->
                                <div class="control-group">
                                    <label class="control-label must">会议室地点</label>
                                    <div class="controls">
										<input type="text" class="m-wrap"  id="address">
									</div>
                                </div>
                                <!-- END　ROW -->
                                <div class="control-group">
                                    <label class="control-label must">容纳人数</label>
                                    <div class="controls">
										<input type="text" class="m-wrap"  id="capacity">
									</div>
                                </div>
                                <!-- END　ROW -->
                                <div class="control-group">
                                    <label class="control-label must">设备情况</label>
                                    <div class="controls">
										<input type="text" class="m-wrap"  id="equipment">
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
	<!-- BEGIN CORE PLUGINS -->
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/datatables/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" src="js/manage/mcommon.js"></script>
	<script type="text/javascript" src="js/manage/meeting/meeting-manage.js"></script>
	<!-- END CORE PLUGINS -->
	<script>
		$(function() {
			meeting.init({
				seData:["${startTime}","${endTime}"]
			});
		});
	</script>
</body>
</html>
