<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<title>报销</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/repExpense.css">
<link rel="stylesheet" href="css/newExpense.css">
</head>
<body>
	<div class="warp">
	<form>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${expenseInfo.expenseNum}</span>
				</div>
				<div class="listLeft listNameWidth5">报销单号：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${expenseInfo.theme}</span>
				</div>
				<div class="listLeft listNameWidth5">报销主题：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${expenseInfo.department}</span>
				</div>
				<div class="listLeft listNameWidth5">报销部门：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${expenseInfo.reason}</span>
				</div>
				<div class="listLeft listNameWidth5">报销事由：</div>
			</div>
			<!-- 报销费用Start -->
			<div class="reimModal">
				<div class="reimType">
					<div class="">报销费用：</div>
				</div>
				<c:if
					test="${listExpenseFee ne null and listExpenseFee.size() gt 0 }">
					<c:forEach items="${listExpenseFee}" var="item">
						<div class="feeList" data-id="${item.id }">
							<%-- <div>${item.categoryDisplay } ${item.money }元</div> --%>
							<div>${item.categoryDisplay }
								<fmt:formatNumber type='number' value='${item.money }'
									groupingUsed='false' />
								元
							</div>
						</div>
					</c:forEach>
				</c:if>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${expenseInfo.amount}</span> <b>元</b>
				</div>
				<div class="listLeft listNameWidth5">报销总额：</div>
			</div>
			<!-- 当前是财务人员 -->
		<c:if test="${entityAccount.personType eq  '2' }">
			
				<div class="formList clear">
					<div class="listRight">
						<input type="text" autofocus="autofocus" name="actualAmount"
							id="actualAmount" value="" class="listRightPadding5"
							placeholder="请填写" data-con="m" data-empty="请填写实际报销金额"
							data-error="实际报销金额格式错误"> <b>元</b>
					</div>
					<div class="listLeft listNameWidth5">实际报销：</div>
				</div>
			

		</c:if>


		<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${expenseInfo.annexCount}</span> <b>页</b>
				</div>
				<div class="listLeft listNameWidth5">附件数目：</div>
			</div>
			<div class="addCanningCopy" id="scanCopyList">
				<div class="canningTit whiteBg">报销单及发票扫描件：</div>
				<div class="canningCopyList">
					<div class="canningCopy">
						<c:if test="${ accessoryInfor != null}">
							<c:forEach items="${ accessoryInfor}" var="item">
								<div class="canningCopyImg" accessoryId="${item.id}">
									<a href="javascript:void(0)" data-original="${resPath}/01/resource/${item.fileName}"><img src="${resPath}/01/imageZoom/${item.fileName}"
										data-original="${resPath}/01/resource/${item.fileName}"></a>
								</div>
							</c:forEach>
						</c:if>
					</div>
				</div>
			</div>
			<div class="formList clear borderTop">
				<div class="listRight">
					<span class="listRightPadding5">${alreadyUser}</span>
				</div>
				<div class="listLeft listNameWidth5">已审核人：</div>
			</div>
			<!-- 如果当前不是财务人员 -->
			<c:choose>
				<c:when test="${entityAccount.personType !=  '2' }">
					<%-- <div class="formList clear">
		            <div class="listRight">
		                <span  class="listRightPadding8">${entityAccount.accountName}</span>
		            </div>
		            <div class="listLeft listNameWidth8">财务报销负责人：</div>
		     </div> --%>
					<!-- <div class="selectPeople " id="selectAuditorList">
	            <div class="selectPeopleTit whiteBg">下一级审核人(无审核人可以不选)：</div>
	            <div class="selectPeopleList">
	                <div class="addedPeopleList">
	                  <div class="addPeople" id="addPrincipal">
	                    <img src="images/addPeople.gif">
	                  </div>
	                </div>
	            </div>
	        </div> -->
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
			<!-- 批复结果 -->
			<div class="replyResults">
				<header>批复结果：</header>
				<c:if test="${shList != null}">
					<ul>
						<c:forEach items="${shList}" var="item">
							<c:if test="${item.dealResult == '1'}">
								<li>
									<div>
										<span>${item.accountName}：</span> <span><fmt:formatDate
												value="${item.updateTime}" pattern="yyyy年MM月dd日  HH:mm" /></span>
									</div> <span>同意申请,${item.remark}</span>
								</li>
							</c:if>
							<c:if test="${item.dealResult == '2'}">
								<li>
									<div>
										<span>${item.accountName}：</span> <span><fmt:formatDate
												value="${item.updateTime}" pattern="yyyy年MM月dd日  HH:mm" /></span>
									</div> <span>拒绝申请,${item.remark}</span>
								</li>
							</c:if>
						</c:forEach>
					</ul>
				</c:if>
			</div>
			<div class="btnGroup">
				<button type="button" class="warpBtn doubleBtn blurBg" id="passBtn"
					expenseId="${expenseInfo.id}" entityAccountId="${entityAccount.id}"
					data-url="mobile/expense/expenseAudit.action">通过</button>
				<button type="button" class="warpBtn doubleBtn yellowBg"
					id="returnBtn" expenseId="${expenseInfo.id}"
					entityAccountId="${entityAccount.id}"
					data-url="mobile/expense/expenseAudit.action">退回</button>
			</div>
		<input type="hidden"
			value="mobile/expense/expenseDetailView.action?id=${expenseInfo.id}&flag=1"
			id="go-url">
			</form>
	</div>
	<!-- 人员列表End -->
	<div class="membersListWarp myHide"></div>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/zoom_app.js"></script>
	<script src="js/eSelectPeople-1.1.js"></script>
	<script src="js/mobile/expense/rep-auditDetail.js"></script>
	<script src='js/jweixin-1.0.0.js'></script>
	<script src='js/eJweixin-1.0.0.js'></script>
	<script type="text/javascript">
   var signature = ${signature};
   var jsApiList = ['chooseImage','uploadImage','previewImage'];
   $(function(){
   	$.eWeixinJSUtil.init(signature,jsApiList);
   });
   </script>
	<!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>
