<!-- 应用使用排行页面 -->
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
            <h3 class="page-title">我的应用 <small>使用排行</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BEGIN MY APP-->
                <div class="app-set-fluid">
                    <div class="tabbable tabbable-custom tabs-left">
                        <!-- Only required for left/right tabs -->
                        <ul class="nav nav-tabs tabs-left">
                            <li><a href="#">人员使用排行</a></li>
                            <li class="active"><a href="#">应用使用排行</a></li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active">
                                <div class="row-fluid">
				                    <!-- BEGIN table -->
				                      <div class="order-table expense-table">
				                           <div class="order-table-head order-search-box table-search-bar">
					                           <form action="" class="form-horizontal">
					                                <div class="control-group">
					                                    <div class="controls">
					                                        <select>
					                                        	<option value="0">周排行</option>
					                                        	<option value="1">月排行</option>
					                                        	<option value="2">总排行</option>
					                                        </select>
					                                    </div>
					                                </div>
					                                <div class="control-group">
					                                    <div class="controls">
					                                        <input type="text" placeholder="应用名称">
					                                    </div>
					                                </div>
					                                <div class="control-group">
					                                    <div class="controls">
					                                        <input type="submit" value="查询"  class="btn orange">
					                                    </div>
				                               	 	</div>
					                           </form>
				                           </div>
				                           <div class="v-table">
					                            <table class="table  table-hover">
					                                <thead>
					                                    <tr>
					                                        <th>排名</th>
					                                        <th>LOGO</th>
					                                        <th>应用名称</th>
					                                        <th>使用次数</th>
					                                    </tr>
					                                </thead>
					                                <tbody>
					                                    <tr>
					                                        <td>1</td>
					                                        <td><img src="images/apps/sign.png" width="30"/></td>
					                                        <td>签到</td>
					                                        <td>1221</td>
					                                    </tr>
					                                    <tr>
					                                        <td>2</td>
					                                        <td><img src="images/apps/absent.png" width="30"/></td>
					                                        <td>请假</td>
					                                        <td>1121</td>
					                                    </tr>
					                                    <tr>
					                                        <td>3</td>
					                                        <td><img src="images/apps/approve.png" width="30"/></td>
					                                        <td>审批</td>
					                                        <td>1001</td>
					                                    </tr>
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
                
<!-- BEGIN CORE PLUGINS -->
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap-datepicker.js" type="text/javascript"></script>
	<script src="js/manage/app.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->   
<script>
    $(function(){
        App.init(); // initlayout and core plugins
        TableManaged.init();
    });
</script>
</body>
</html>