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
		           		<!-- BENGIN DETAIL -->
					    <div class="table-action-detial">
			              	<h5 class="set-hd-bar">
			              		<strong>报销详情</strong>
			              		<a href="manage/expense/expenseList.action" id="Jback" class="pull-right" title="返回"><i class="icon-reply"></i></a>
								<a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
							</h5>
							<hr />
							<div class="avatar-fluid">
								<div class="avatar">
									<img alt="" src="${expenseData.avatar}" class="avatar">
								</div>
								<div class="info">
									<div class="name">${expenseData.expenseInfo.userName }</div>
									<div class="pos">${expenseData.expenseInfo.department }</div>
								</div>
							</div>
							<div class="well well-large table-action-detial-well">
								<p><strong>报销单号：</strong>${expenseData.expenseInfo.expenseNum }&nbsp;</p>
								<p><strong>报销主题：</strong>${expenseData.expenseInfo.theme }&nbsp;</p>
								<p><strong>报销部门：</strong>${expenseData.expenseInfo.department }&nbsp;</p>
								<p><strong>报销事由：</strong>${expenseData.expenseInfo.reason }&nbsp;</p>
								<p><strong>报销费用：</strong>
									<c:forEach items="${expenseData.listExpenseFee }" var="expense">
										${expense.categoryDisplay }${expense.money }元<br/>
									</c:forEach>&nbsp;
								</p>
								<p><strong>报销总额：</strong>${expenseData.expenseInfo.amount}元&nbsp;</p>
								<c:if test="${expenseData.expenseInfo.status == '5' }">
									<p><strong>实际报销：</strong>${expenseData.expenseInfo.actualCost}元&nbsp;</p>
								</c:if>
								<p><strong>报销单及发票扫描件：</strong></p>
								<ul class="imgs">
									<c:if test="${expenseData.accessoryInfor != null}">
										<c:forEach items="${expenseData.accessoryInfor}" var="item">
											<li><a data-original="${resPath}/01/resource/${item.fileName}" href="javascript:void(0)"><img src="${resPath}/01/imageZoom/${item.fileName}"></a></li>
<%-- 														<li><img src="${img.filePath }" alt="${img.fileName }" /></li> --%>
										</c:forEach>
									</c:if>
								</ul>
								<p><strong>当前状态：</strong>${expenseData.stautsDisplay}&nbsp;&nbsp;</p>
								<p><strong>批复结果：</strong></p>
								<ul class="rs">
									<c:if test="${expenseData.shList != null}">
										<c:forEach items="${expenseData.shList}" var="item">
											<c:if test="${item.dealResult == '1'}">
												<li>
													<div class="hd">${item.accountName}：<span class="pull-right time"><fmt:formatDate
																value="${item.updateTime}" pattern="yyyy-MM-dd HH:mm" /></span></div>
													<p class="bd">同意申请,${item.remark}</p>
												</li>
											</c:if>
											<c:if test="${item.dealResult == '2'}">
												<li>
													<div class="hd">${item.accountName}：<span class="pull-right time"><fmt:formatDate
																value="${item.updateTime}" pattern="yyyy-MM-dd HH:mm" /></span></div>
													<p class="bd">拒绝申请,${item.remark}</p>
												</li>
											</c:if>
										</c:forEach>
									</c:if>
								</ul>
<!-- 								<p><strong>审批进展：</strong></p> -->
<!-- 								<ul class="rs"> -->
<%-- 									<c:if test="${expenseData.entityProgressList ne null and expenseData.entityProgressList.size() gt 0 }"> --%>
<%-- 					                      <c:forEach items="${expenseData.entityProgressList }" var="item"> --%>
<!-- 					                   	  <li> -->
<!-- 												<div class="hd"> -->
<!-- 												<span class="time"> -->
<%-- 												<fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd hh:MM:dd"/> --%>
<!-- 												</span> -->
<!-- 												</div> -->
<%-- 												<p class="bd">${entityProgress.content }</p> --%>
<!-- 										   </li> -->
<%-- 					                       </c:forEach> --%>
<%-- 					                </c:if> --%>
<!-- 								</ul> -->
								
							</div>
			 			 </div>
			             <!-- END DETAIL -->	
       				 </div>
                </div>
            </div>
        </div>
    </div>
    <!-- END MY APP-->
	<!-- BEGIN CORE PLUGINS -->
	<script src="js/manage/jquery-1.10.1.min.js" type="text/javascript"></script>
	<script src="js/manage/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="js/manage/zoom.js"></script>
	<script type="text/javascript">
		$("body").zoom({
			parentName : ".imgs",
			tagName : "a",
		});
	</script>
	</body>
	</html>