<!-- 对私借款详情页面 -->
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
			<h3 class="page-title">
				我的应用 <small>借款</small>
			</h3>
			<!-- END PAGE TITLE & BREADCRUMB-->
		</div>
	</div>
	<!-- END PAGE HEADER-->
	<!-- BEGIN MY APP-->
	<div class="app-set-fluid">
		<div class="tabbable tabbable-custom tabs-left">
			<!-- Only required for left/right tabs -->
			<ul class="nav nav-tabs tabs-left">
				<li class="active"><a href="manage/loan/privateLoanList.action">对私借款</a></li>
				<li><a href="manage/loan/publicLoanList.action">对公借款</a></li>
				<li><a href="manage/loan/setupLoanRule.action">借款设置</a></li>
				<li><a href="manage/loan/setupLoanUse.action">借款用途设置</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active">
					<!-- BENGIN DETAIL -->
				                    <div class="table-action-detial" id="loanPublishDetail">
				                    	<h5 class="set-hd-bar">
				                    		<strong>借款详情</strong>
				                    		<a href="manage/loan/privateLoanList.action" data-table="loanPublishTable" data-detail="loanPublishDetail" class="pull-right JbackTable" title="返回"><i class="icon-reply"></i></a>
											<a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
										</h5>
										<hr />
										<div class="avatar-fluid">
											<div class="avatar">
												<img alt="" src="${loanData.loan.avatar }" class="avatar">
											</div>
											<div class="info">
												<div class="name">${loanData.loan.userName }</div>
												<div class="pos">${loanData.loan.department }</div>
											</div>
										</div>
										<div class="well well-large table-action-detial-well">
											<p><strong>借款单号：</strong>${loanData.loan.loanNum }</p>
											<p><strong>所在部门：</strong>${loanData.loan.department }</p>
											<p><strong>借款人工号：</strong>${loanData.loan.userId }</p>
											<p><strong>借款人姓名：</strong>${loanData.loan.userName }</p>
											<p><strong>日期：</strong><fmt:formatDate value="${loanData.loan.applyDate }" pattern="yyyy-MM-dd"/></p>
											<p><strong>金额：</strong>${loanData.loan.amount }</p>
											<p><strong>大写金额：</strong>${loanData.loan.capitalAmount }</p>
											<p><strong>出差地区及事由：</strong>${loanData.loan.subject }&nbsp;</p>
											<p><strong>预计使用明细：</strong>${loanData.loan.details }&nbsp;</p>
											<p><strong>收款账号：</strong>${loanData.loan.receiveAccount }&nbsp;</p>
											
											
											<p><strong>相关材料扫描件：</strong></p>
											<ul class="imgs">
												<c:if test="${loanData.accessoryInfo != null}">
													<c:forEach items="${loanData.accessoryInfo}" var="item">
														<li><a data-original="${resPath}/01/resource/${item.fileName}" href="javascript:void(0)"><img src="${resPath}/01/imageZoom/${item.fileName}"></a></li>
<%-- 														<li><img src="${img.filePath }" alt="${img.fileName }" /></li> --%>
													</c:forEach>
												</c:if>
											</ul>
											<p><strong>当前状态：</strong>${loanData.loan.statusDisplay }&nbsp;</p>
											<p><strong>审核人：</strong>&nbsp;</p>
											<ul class="auditors">
												<c:if test="${loanData.shList != null}" >
									                <c:forEach items="${loanData.shList}" var="item">
									                	<li><img src="${item.avatar}" alt="${item.accountName}" /></li>
									                </c:forEach>
									             </c:if>
											</ul>
											<p><strong>批复结果：</strong></p>
											<ul class="rs">
												<c:if test="${loanData.shList != null}">
													<c:forEach items="${loanData.shList}" var="item">
														<c:if test="${item.dealResult == '1'}">
															<li>
																<div class="hd">${item.accountName}：<span class="pull-right time"><fmt:formatDate
																			value="${item.updateTime}" pattern="yyyy-MM-dd hh:MM" /></span></div>
																<p class="bd">同意申请,${item.remark}</p>
															</li>
														</c:if>
														<c:if test="${item.dealResult == '3'}">
															<li>
																<div class="hd">${item.accountName}：<span class="pull-right time"><fmt:formatDate
																			value="${item.updateTime}" pattern="yyyy-MM-dd hh:MM" /></span></div>
																<p class="bd">拒绝申请,${item.remark}</p>
															</li>
														</c:if>
													</c:forEach>
												</c:if>
											</ul>
										</div>
				                    </div>
				              <!-- END DETAIL -->
				</div>
			</div>
		</div>
	</div>
	<!-- END MY APP-->
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
