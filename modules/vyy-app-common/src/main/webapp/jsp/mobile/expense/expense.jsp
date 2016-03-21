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
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<title>报销</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/repExpense.css">
<link rel="stylesheet" href="css/newExpense.css">
<link rel="stylesheet" href="css/contact.css">
</head>
<body>
	<div class="warp">
		<form action="" method="post">
			<div class="formList clear">
				<div class="listRight">
					<input type="text" name="repExpTheme" id="repExpTheme"
						class="listRightPadding5" placeholder="例如：市场调研费报销" data-con="*"
						data-empty="请填写报销主题" value="${expenseInfo.theme}">
				</div>
				<div class="listLeft listNameWidth5">报销主题：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<input type="text" name="department" id="department"
						class="listRightPadding5" placeholder="请填写部门" data-con="*"
						data-empty="请填写部门" value="${expenseInfo.department}" readonly>
				</div>
				<div class="listLeft listNameWidth5">报销部门：</div>
			</div>
			<!-- 新的报销费用 -->
			
			<div class="stepwiseListModal">
	            <div class="stepwiseListHeader">
	                <div class="">报销费用：</div>
	                <div class="listRight" id="addQuestion">
	                    <span class="listRightPadding5"></span>
	                    <b><i class="fa fa-plus-square-o"></i></b>
	                </div>
	            </div>
	            <c:if test="${expenseFeeList ne null and expenseFeeList.size() gt 0 }">
				  <c:forEach items="${expenseFeeList}" var="item">
		            <div class="stepwiseList" data-id="${item.id}" data-type="${item.category}">
		                <div>${item.categoryDisplay}<span>${item.money }</span>元</div>
		                <b><i class="fa fa-minus-square-o listDelBtn"></i>
		                </b>
		            </div>
		              </c:forEach>
				</c:if>
        	</div>
			
			<div class="formList clear repExpReasons">
				<div class="listRight">
					<textarea name="reasons" id="reasons" class="listRightPadding5"
						placeholder="请填写报销的事由" data-con="*" data-empty="请填写报销的事由">${expenseInfo.reason}</textarea>
				</div>
				<div class="listLeft listNameWidth5">报销事由：</div>
			</div>

			<div class="formList clear">
				<div class="listRight">
					<input type="text" name="money" id="money"
						class="listRightPadding5" placeholder="请填写报销总额" data-con="m"
						data-empty="请填写报销总额" data-error="报销总额格式错误"
						value="${expenseInfo.amount}" readonly> <b>元</b>
				</div>
				<div class="listLeft listNameWidth5">报销总额：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<input type="tel" name="accessoryNum" id="accessoryNum"
						class="listRightPadding5" placeholder="请填写附件数目" data-con="d"
						data-empty="请填写附件数目" data-error="附件数目格式错误"
						value="${expenseInfo.annexCount}"> <b>页</b>
				</div>
				<div class="listLeft listNameWidth5">附件数目：</div>
			</div>
			<div class="addCanningCopy" id="scanCopyList">
				<div class="canningTit">报销单及发票扫描件：</div>
				<div class="canningCopyList">
					<div class="canningCopy">
						<c:if test="${ accessoryInfor != null}">
							<c:forEach items="${ accessoryInfor}" var="item">
								<div accessoryId="${item.id}">
									<img src="${resPath}/01/imageZoom/${item.fileName}"
										data-original="${resPath}/01/resource/${item.fileName}">
									<i></i>
								</div>
							</c:forEach>
						</c:if>
						<div class="addCanningCopyBtn" id="addScanCopy">
							<img src="images/addImg.png">
						</div>
					</div>
				</div>
			</div>
			<!-- <div class="selectPeople" id="selectAuditorList">
                <div class="selectPeopleTit">审核人(无审核人可不选)：</div>
                <div class="selectPeopleList">
                    <div class="addedPeopleList" type='0'>
                    <div class="addPeople" id="addAuditor">
                        <img src="images/addPeople.gif">
                    </div>
                    </div>
                </div>
            </div> -->
			<div class="selectPeople borderTop borderBottom" id="selectAuditorList">
				<div class="selectPeopleTit">
					审核人(请依次选择各级审核人)：<i class="fa fa-plus-square-o" id="addAuditorBtn"></i>
				</div>
				<div class="selectStaffList">
					<div class="addedStaffList" type="0">
						<c:if test="${ auditorList != null}">
							<c:forEach items="${auditorList}" var="item" varStatus="status">
								<section class="staffList">
									<span>第<em name="level" index="${ item.remark}"></em>级审核人：
									</span>
									<div id="${item.id}" status="${item.dealResult}"
										uAvatar="${item.avatar}" uName="${item.accountName}"
										uId="${item.accountId}" type="1">
										<img src="${item.avatar}"> <span>${item.accountName}</span>
									</div>
									<b class="delStaffList"> <i class="fa fa-minus-square-o"></i>
									</b>
								</section>
							</c:forEach>
						</c:if>
					</div>
				</div>
			</div>
			<div class="selectPeople" id="principalList">
				<div class="selectPeopleTit">财务报销负责人：</div>
				<div class="selectPeopleList">
					<div class="addedPeopleList" type='1'>
						<div class="addPeople" id="addPrincipal">
							<img src="images/addPeople.gif">
						</div>
					</div>
				</div>
			</div>
			<input type="hidden" name="selectAuditor" id="selectAuditor"  data-con="*" data-empty="请选择审核人"> 
			<input type="hidden" name="principal" id="principal" data-con="*" data-empty="请选择财务报销负责人">
			<div class="btnGroup">
				<button type="button" class="warpBtn doubleBtn blurBg" id="audit"
					operationType="0" data-url="mobile/expense/expenseApply.action">申请审核</button>
				<button type="button" class="warpBtn doubleBtn greenBg" id="repExp"
					operationType="1" data-url="mobile/expense/expenseApply.action">直接申请报销</button>
			</div>
		</form>
		<c:if test="${expenseInfo.id != null}">
			<input type="hidden" name="expenseId" id="expenseId"
				value="${expenseInfo.id}">
		</c:if>
		<input type="hidden" value="mobile/expense/expenseDetailView.action"
			id="go-url">
	</div>
	<div class="addModal myHide" id="reimbursement">
			<div class="addReimbursement modalShell">
				<form id="reimbursementForm">
					<header>添加报销费用</header>
					<div class="modalCon">
						<div class="formList clear">
							<div class="listRight">
								<select class="listRightPadding5" id="feeType" name="feeType" data-con="*" data-empty="请选择费用类别">
									<option value="-1">请选择</option>
									<c:if test="${costCategoryList ne null and costCategoryList.size() gt 0 }">
									   <c:forEach items="${costCategoryList }" var="item">
									      <option value="${item.value }">${item.name }</option>
									   </c:forEach>
									</c:if>
								</select>
							</div>
							<div class="listLeft listNameWidth5">
								<span class="title">费用类别：</span>
							</div>
						</div>


						<div class="formList clear">
							<div class="listRight">
								<input type="tel" name="feeAmount" id="feeAmount"
									class="listRightPadding5" data-con="m" data-empty="请填写金额"
									data-error="金额格式错误">
							</div>
							<div class="listLeft listNameWidth5">
								<span class="title">金&emsp;&emsp;额：</span>
							</div>
						</div>
					</div>

					<footer>
						<button type="reset" id="cancelBtn">取消</button>
						<button type="button" id="saveBtn">确认</button>
					</footer>
				</form>
			</div>
		</div>
	<!-- 人员列表End -->
	<div class="membersListWarp myHide"></div>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/zoom_app.js"></script>
	<script src="js/eSelectPeople-1.1.js"></script>
	<script src="js/mobile/expense/rep-index.js"></script>
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