<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>微移云管理系统</title>
<%@ include file="../../include/layout.jsp"%>
<style>
th, td { white-space: nowrap; }
</style>
</head>
<body class="frame-body">
	<!-- BEGIN PAGE HEADER-->
    <div class="row-fluid">
        <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
            <h3 class="page-title">我的应用 <small>报销</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN MY APP-->
    <div class="app-set-fluid">
        <div class="tabbable tabbable-custom tabs-left">
            <!-- Only required for left/right tabs -->
            <ul class="nav nav-tabs tabs-left">
                <li class="active"><a href="manage/expense/expenseList.action">报销</a></li>
                <li><a href="manage/expense/setupCostCategory.action">报销费用类别设置</a></li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane active">
                    <div class="row-fluid">
		           			<!-- BEGIN table -->
			                <div class="order-table expense-table">
			                   <div class="order-table-head order-search-box table-search-bar">
			                    <div  class="form-horizontal">
			                         <div class="control-group">
			                             <div class="controls">
			                                 <span><input type="text" class="m-wrap m-ctrl-medium date-picker" id="starttime"  placeholder="开始时间"></span>
			                                 <span>至</span>
			                                 <span><input type="text" class="m-wrap m-ctrl-medium date-picker" id="endtime" placeholder="结束时间"></span>
			                             </div>
			                         </div>
			                         <div class="control-group">
			                             <div class="controls">
			                                 <input type="text" id="userName" placeholder="员工姓名" />
			                             </div>
			                         </div>
			                         <div class="control-group">
			                             <div class="controls">
			                                 <select id="status">
			                                 	<option value="">请选择</option>
												<option value="4">待报销</option>
												<option value="5">已报销</option>
												<option value="-2">报销退回</option>
												<option value="-1">审核退回</option>
			                                 </select>
			                             </div>
			                         </div>
			                         <div class="control-group">
			                             <div class="controls">
			                                 <input type="text" id="expenseNum" placeholder="报销单号">
			                             </div>
			                         </div>
			                         <div class="control-group">
			                             <div class="controls">
			                                 <button type="button" id="queryBtn"  class="btn orange"><i class="icon-search"></i>&nbsp;查询</button>
	                   						 <button id="exportBtn" class="btn"><i class="icon-download-alt"></i>&nbsp;导出excel</button>
			                             </div>
			                       	</div>
			                    </div>
			                   </div>
			                   <div class="v-table">
			                   		<table class="table  table-hover"  id="ck_table" width="100%">
			                        	<thead>
			                             <tr>
			                                 <th>报销单号</th>
			                                 <th>报销人工号</th>
			                                 <th>报销人姓名</th>
			                                 <th>报销事由</th>
			                                 <th>报销部门</th>
			                                 <th>报销总额</th>
			                                 <th>实际报销</th>
			                                 <th>申请时间</th>
			                                 <th>审核状态</th>
			                                 <th>操作</th>
			                             </tr>
			                         </thead>
			                         <tbody>
			                             
			                         </tbody>
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
                
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/manage/bootstrap-datepicker.js"></script>
	<script type="text/javascript" src="js/datatables/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" src="js/manage/mcommon.js"></script>
	<script type="text/javascript" src="js/manage/expense/expense-manage.js"></script>
	<script>
	    $(function(){
	    	expenseList.init();
	    });
	</script>
</body>
</html>