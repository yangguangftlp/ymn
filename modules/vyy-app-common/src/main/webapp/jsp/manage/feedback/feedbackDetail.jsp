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
            <h3 class="page-title">我的应用 <small>问题反馈</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
     <!-- BEGIN MY APP-->
                <div class="app-set-fluid">
                <input type="hidden" value="${ id}" id="id" />
                <input type="hidden" id="userId" />
                			 <!-- BENGIN DETAIL -->
				                    <div class="table-action-detial app-manage-feedback-detail">
				                    	<h5 class="set-hd-bar">
				                    		<strong>问题处理</strong>
				                    		<a href="manage/feedback/feedbackList.action" id="Jback" class="pull-right" title="返回"><i class="icon-reply"></i></a>
											<a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
										</h5>
										<hr />
										<div class="well well-large table-action-detial-well">
											<p><strong>问题内容：</strong><label id="problem"></label></p>
											<p><strong>修改建议：</strong><label id="suggest"></label></p>
											<p><strong>页面截图：</strong></p>
											<ul class="imgs">
<!-- 												<li><img src="images/shuiwu.png" alt="" /></li> -->
<!-- 												<li><img src="images/shuiwu.png" alt="" /></li> -->
<!-- 												<li><img src="images/shuiwu.png" alt="" /></li> -->
											</ul>
											<p><strong>修改建议：</strong></p>
											<ul class="fb-info-list">
											</ul>
											<p><strong>处理回复：</strong></p>
											<form class="form-horizontal" action="">
												<div class="control-group">
													<div class="controls">
														<textarea rows="3" id="content" class="span8 m-wrap"></textarea>
													</div>
												</div>
												<div class="form-actions">
													<button class="btn orange" id="send" type="button">发送</button>
													<button class="btn" id="remind" type="button">提醒</button>                            
												</div>
											</form>
										</div>
										
										
										
				                    </div>
				                    
				                    <!-- END DETAIL -->
                </div>
                <!-- END MY APP-->               
	<!-- BEGIN CORE PLUGINS -->
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/manage/mcommon.js"></script>
	<script type="text/javascript" src="js/manage/feedback/feedback-manage.js"></script>
	<script type="text/javascript" src="js/manage/zoom.js"></script>
	<!-- END CORE PLUGINS -->   
	<script>
	    $(function(){
	    	feedbackDetail.init({
	    		resPath : "${resPath}"
	    	});
	    });
	</script>
</body>
</html>