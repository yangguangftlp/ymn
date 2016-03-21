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
<!--             <h3 class="page-title">我的应用 <small>审批</small></h3> -->
        <!-- END PAGE TITLE & BREADCRUMB-->
        </div>
    </div>
    <!-- END PAGE HEADER-->
    <!-- BENGIN DETAIL -->
	<div class="table-action-detial">
	     <h5 class="set-hd-bar">
	         <strong>个人评价详情</strong>
	         <a href="javascript:window.history.back(-1)" id="Jback" class="pull-right" title="返回"><i class="icon-reply"></i></a>
			 <a href="javascript:location.reload();" class="pull-right" title="刷新"><i class="icon-refresh"></i></a>
		</h5>
		<hr />
		<div class="avatar-fluid">
			<div class="avatar">
				<img alt="${data.user.name }" src="${data.user.avatar}" class="avatar">
			</div>
			<div class="info pull-left" style="margin-left:15px">
				<div class="name">${data.user.name }</div>
				<div class="pos">${data.department}</div>
			</div>
			<div class="pull-left total-wrap">
				<div class="pos">总得分：<b>${data.total}</b></div>
			</div>
		</div>
		<div class="well well-large table-action-detial-well">
			<c:forEach items="${data.problemTemplateMapList }" var="item">
				<c:if test="${item.standard ne null }">
					<p><strong>${item.px}.${item.quota}<c:if test="${item.standard ne null}">(${item.scores}分)</c:if>
					</strong>&nbsp;</p>
					<c:if test="${item.scoreData ne null }">
						<div class="a-desc">
							<c:forEach items="${item.scoreData}" var="childItem"> 
              					 ${childItem.score}分——${childItem.uCount}人&emsp;
           						 </c:forEach>
						</div>
					</c:if>
				</c:if>
				<c:if test="${item.standard eq null }">
					<c:set value="${item}" var="problemTemplate" />
				</c:if>
			</c:forEach>
			<c:if test="${data.appraisalInfo.overallMerit and problemTemplate ne null}">
				<p><strong>综合评价：</strong> </p>
					<c:if test="${data.opinionMapList ne null and data.opinionMapList.size() gt 0 }">
						<ul class="fb-info-list">
							<c:forEach items="${data.opinionMapList }" var="item">
								<li>
									<p>${item.raterName }：</p>
									<p class="text">
										<label class="text-primary">${item.opinion }</p>
								</li>
							</c:forEach>
						</ul>
					</c:if>
			</c:if>
		</div>
	</div>
	<!-- END DETAIL -->   
</body>
</html>