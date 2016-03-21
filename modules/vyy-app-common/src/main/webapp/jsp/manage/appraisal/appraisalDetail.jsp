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
<!--             <h3 class="page-title">评价详情 </h3> -->
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BENGIN DETAIL -->
	<div class="table-action-detial">
	     <h5 class="set-hd-bar">
	         <strong>评价详情</strong>
	         <a href="javascript:window.history.back(-1)" id="Jback" class="pull-right" title="返回"><i class="icon-reply"></i></a>
			 <a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
		</h5>
		<hr />
		<input type="hidden" id="id" value="${data.appraisal.id }" />
		<div class="a-title">${data.appraisal.theme }</div>
		<div class="well well-large table-action-detial-well">
			<p><strong>发起人：</strong>${data.appraisal.userName }&nbsp;</p>
			<p><strong>评价指标：</strong>&nbsp;</p>
			<c:if test="${data.problemTemplate != null}">
				<dl>
				<c:forEach items="${data.problemTemplate}" var="item" varStatus="status">
                	<dt>${status.count}、${item.quota}(${item.scores}分)</dt>
                	<dd><p>${item.standard}</p></dd>
                </c:forEach>
                </dl>
            </c:if>
		
			<p><strong>当前状态：</strong>&nbsp;</p>
			<p><strong>评价结果：</strong>&nbsp;</p>
			<div class="order-table v-table">
				<table class="table table-hover" id="ck_table" width="100%">
					<thead>
						<tr>
							<th>排名</th>
							<th>姓名</th>
							<th>平均得分</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
				
			</div>
		</div>
	</div>
	<!-- END DETAIL -->   
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/manage/bootstrap-datepicker.js"></script>
	<script type="text/javascript" src="js/datatables/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" src="js/manage/mcommon.js"></script>
	<script type="text/javascript" src="js/manage/appraisal/appraisal-manage.js"></script>
	<script>
	    $(function(){
	    	appraisalDetail.init();
	    });
	</script>
</body>
</html>