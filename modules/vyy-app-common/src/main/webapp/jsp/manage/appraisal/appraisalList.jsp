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
<style>
th, td { white-space: nowrap; }
</style>
<body class="frame-body">
	<!-- BEGIN PAGE HEADER-->
    <div class="row-fluid">
        <div class="span12">
        <!-- BEGIN PAGE TITLE & BREADCRUMB-->
            <h3 class="page-title">我的应用 <small>员工评价</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN MY APP-->
    <div class="row-fluid v-box approval-box">
	  <!-- BEGIN table -->
	  <div class="order-table approval-table">
	     <div class="order-table-head order-search-box table-search-bar">
	      <div class="form-horizontal">
	           
	           <div class="control-group">
	               <div class="controls">
	                   <span><input type="text" class="m-wrap m-ctrl-medium date-picker" id="starttime"  placeholder="开始时间"></span>
	                   <span>至</span>
	                   <span><input type="text" class="m-wrap m-ctrl-medium date-picker" id="endtime" placeholder="结束时间"></span>
	               </div>
	           </div>
	           <div class="control-group">
	               <div class="controls">
	                   <input type="text" id="theme" placeholder="评价主题">
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
	                   <th>评价主题</th>
	                   <th>发起人工号</th>
	                   <th>发起人姓名</th>
	                   <th>发起日期</th>
	                   <th>当前状态</th>
	                   <th>是否有综合评价</th>
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
  <!-- END MY APP-->    
  <!-- BEGIN CORE PLUGINS -->
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/manage/bootstrap-datepicker.js"></script>
	<script type="text/javascript" src="js/datatables/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" src="js/manage/mcommon.js"></script>
	<script type="text/javascript" src="js/manage/appraisal/appraisal-manage.js"></script>
	<!-- END CORE PLUGINS -->   
	<script>
	    $(function(){
	    	appraisalList.init();
	    });
	</script>
</body>
</html>