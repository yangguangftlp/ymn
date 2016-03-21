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
            <h3 class="page-title">我的应用 <small>审批</small></h3>
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BENGIN DETAIL -->
	<div class="table-action-detial">
	     <h5 class="set-hd-bar">
	         <strong>审批详情</strong>
	         <a href="manage/approval/approvalList.action" id="Jback" class="pull-right" title="返回"><i class="icon-reply"></i></a>
			 <a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
		</h5>
		<hr />
		<div class="avatar-fluid">
			<div class="avatar">
				<img alt="${approvalData.approvalInfo.userName }" src="${approvalData.avatar}" class="avatar">
			</div>
			<div class="info">
				<div class="name">${approvalData.approvalInfo.userName }</div>
				<div class="pos">${approvalData.approvalInfo.department }</div>
			</div>
		</div>
		<div class="well well-large table-action-detial-well">
			<p><strong>流程名称：</strong>${approvalData.approvalInfo.flowName }</p>
			<p><strong>归属部门：</strong>${approvalData.approvalInfo.department }</p>
			<p><strong>审批内容：</strong>${approvalData.approvalInfo.content }</p>
			<p><strong>合同编号：</strong>${approvalData.approvalInfo.contractNumber }&nbsp;</p>
			<p><strong>合作方：</strong>${approvalData.approvalInfo.partner }&nbsp;</p>
			<p><strong>备注：</strong>${approvalData.approvalInfo.remark }&nbsp;</p>
			<p><strong>相关材料扫描件：</strong> </p>
			<ul class="imgs">
				<c:if test="${approvalData.accessoryInfor != null}">
					<c:forEach items="${approvalData.accessoryInfor}" var="item">
						<li><a data-original="${resPath}/01/resource/${item.fileName}" href="javascript:void(0)"><img src="${resPath}/01/imageZoom/${item.fileName}"></a></li>
					</c:forEach>
				</c:if>
			</ul>
			<p><strong>抄送人：</strong>${approvalData.ccUser }&nbsp;</p>
			<p><strong>当前状态：</strong>${approvalData.stautsDisplay }&nbsp;</p>
			<p><strong>审核人：</strong></p>
			<ul class="auditors">
				<c:if test="${approvalData.auditorList != null}" >
	                <c:forEach items="${approvalData.auditorList}" var="item">
	                	<li><img src="${item.avatar}" alt="${item.accountName}" /></li>
	                </c:forEach>
	             </c:if>
			</ul>
			<p><strong>批复结果：</strong></p>
			<ul class="rs">
				<c:if test="${approvalData.shList != null}">
					<c:forEach items="${approvalData.shList}" var="item">
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
											value="${item.updateTime}" pattern="yyyy-MMdd HH:mm" /></span></div>
								<p class="bd">拒绝申请,${item.remark}</p>
							</li>
						</c:if>
					</c:forEach>
				</c:if>
			</ul>
		</div>
	</div>
	<!-- END DETAIL -->   
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