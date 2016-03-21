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
<title>借款管理</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/loan.css">
<link rel="stylesheet" href="css/contact.css">
</head>
<body>
	<div class="warp">
		<!-- 隐藏字段  id、loanType -->
		<input type="hidden" id="loanId" value="${loan.id }"> <input
			type="hidden" id="loanType" value="${loan.loanType }">
		<!-- 公共部门（借款单号） -->
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5">${loan.loanNum}</span>
			</div>
			<div class="listLeft listNameWidth5">借款单号：</div>
		</div>
		<!-- 对私借款（所在部门） -->
		<c:if test="${ '0' eq loan.loanType }">
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loan.department}</span>
				</div>
				<div class="listLeft listNameWidth5">所在部门：</div>
			</div>
		</c:if>
		<!-- 对公借款（公司名称） -->
		<c:if test="${ '1' eq loan.loanType }">
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loan.company }</span>
				</div>
				<div class="listLeft listNameWidth5">公司名称：</div>
			</div>
		</c:if>
		<!-- 公共部分（借款人） -->
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5">${loan.userName }</span>
			</div>
			<div class="listLeft listNameWidth5">借&nbsp;款&nbsp;人：</div>
		</div>
		<!-- 对公借款（时间） -->
		<c:if test="${ '1' eq loan.loanType }">
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatDate
							value="${loan.applyDate}" pattern="yyyy-MM-dd" /></span>
				</div>
				<div class="listLeft listNameWidth5">时&emsp;&emsp;间：</div>
			</div>
			<!-- 对公借款（申请金额） -->
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatNumber
							type="number" value=" ${loan.amount}" pattern="0.00"
							maxFractionDigits="2" /></span><b class="iconLabel">元</b>
				</div>
				<div class="listLeft listNameWidth5">申请金额：</div>
			</div>
		</c:if>
		<!-- 对私借款（日期） -->
		<c:if test="${ '0' eq loan.loanType }">
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatDate
							value="${loan.applyDate}" pattern="yyyy-MM-dd" /></span>
				</div>
				<div class="listLeft listNameWidth5">日&emsp;&emsp;期：</div>
			</div>
			<!-- 对私借款（金额） -->
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatNumber
							type="number" value="${loan.amount}" pattern="0.00"
							maxFractionDigits="2" /></span><b class="iconLabel">元</b>
				</div>
				<div class="listLeft listNameWidth5">金&emsp;&emsp;额：</div>
			</div>
		</c:if>
		<!--  公共部门（大写金额）-->
		<div class="formList clear">
			<div class="listRight">
				<span class="listRightPadding5">${loan.capitalAmount}</span>
			</div>
			<div class="listLeft listNameWidth5">大写金额：</div>
		</div>
		<!-- 对私借款（出差地区及事由、预计使用明细）Start -->
		<c:if test="${ '0' eq loan.loanType }">
			<div class="formList clear">
				<div class="listRight">
					<pre class="listRightPadding8">${loan.subject}</pre>
				</div>
				<div class="listLeft listNameWidth8">出差地区及事由：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<pre class="listRightPadding8">${loan.details}</pre>
				</div>
				<div class="listLeft listNameWidth8">预计使用明细：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5" id="bankAccount">${loan.receiveAccount}</span>
				</div>
				<div class="listLeft listNameWidth5">收款账号：</div>
			</div>
			<!-- 对私借款（出差地区及事由、预计使用明细）End -->
		</c:if>
		<!-- 对公部分（合同金额、剩余金额、客户名称、开户行、账号、项目名称、借款用途、借款说明） Start-->
		<c:if test="${ '1' eq loan.loanType }">
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatNumber
							type="number" value="${loan.contractAmount}" pattern="0.00"
							maxFractionDigits="2" /></span><b class="iconLabel">元</b>
				</div>
				<div class="listLeft listNameWidth5">合同金额：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5"><fmt:formatNumber
							type="number" value="${loan.remainingAmount}" pattern="0.00"
							maxFractionDigits="2" /></span><b class="iconLabel">元</b>
				</div>
				<div class="listLeft listNameWidth5">剩余金额：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loan.clientName}</span>
				</div>
				<div class="listLeft listNameWidth5">客户名称：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loan.bank }</span>
				</div>
				<div class="listLeft listNameWidth5">开&nbsp;户&nbsp;行：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5" id="bankAccount">${loan.receiveAccount }</span>
				</div>
				<div class="listLeft listNameWidth5">账&emsp;&emsp;号：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loan.projectName }</span>
				</div>
				<div class="listLeft listNameWidth5">项目名称：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loanUseDisplay }</span>
				</div>
				<div class="listLeft listNameWidth5">借款用途：</div>
			</div>
			<div class="formList clear">
				<div class="listRight">
					<span class="listRightPadding5">${loan.remark }</span>
				</div>
				<div class="listLeft listNameWidth5">借款说明：</div>
			</div>
			<!-- 对公部分 End-->
		</c:if>
		<div class="addCanningCopy" id="scanCopyList">
			<div class="canningTit whiteBg">相关材料照片或扫描件：</div>
			<div class="canningCopyList">
				<div class="canningCopy">
					<c:if test="${accessoryInfor != null}">
						<c:forEach items="${accessoryInfor}" var="item">
							<div class="canningCopyImg" accessoryId="${item.id}">
								<a href="javascript:void(0)" data-original="${resPath}/01/resource/${item.fileName}"><img src="${resPath}/01/imageZoom/${item.fileName}"
									data-original="${resPath}/01/resource/${item.fileName}"></a>
							</div>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
		<!-- 对私借款（审核情况） -->
		<c:if test="${ '0' eq loan.loanType }">
			<div class="selectPeople" id="selectAuditorList">
				<div class="selectPeopleTit whiteBg">审核情况,在此加签：</div>
				<div class="selectPeopleList">
					<div class="addedPeopleList" type="0">
						<c:if test="${shList != null}">
							<c:forEach items="${shList}" var="item">
								<div id="${item.id}" status="${item.dealResult}"
									entityType="${item.entityType}" uName="${item.accountName}"
									uId="${item.accountId}">
									<img src="${item.avatar}"> <span>${item.accountName}</span>
									<!-- 未处理 以及确认中的 -->
									<b class=${(item.dealResult eq "0")?"unApprIcon":"apprIcon" }></b>
								</div>
							</c:forEach>
						</c:if>
						<c:if
							test="${!(isSh == true) and (currentEA ne null and !('0' eq currentEA.dealResult))}">
							<div class="addPeople" id="plusSignBtn">
								<img src="images/addPeople.gif">
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</c:if>
		<!-- 对公借款（审核情况） -->
		<c:if test="${ '1' eq loan.loanType  }">
			<div class="selectPeople borderTop">
				<div class="selectPeopleTit whiteBg">审核情况：</div>
				<div class="selectPeopleList">
					<div class="addedPeopleList" type="0">
						<c:if test="${shList != null}">
							<c:forEach items="${shList}" var="item">
								<div id="${item.id}" status="${item.dealResult}"
									entityType="${item.entityType}" uName="${item.accountName}"
									uId="${item.accountId}">
									<img src="${item.avatar}"> <span>${item.accountName}</span>
									<!-- 未处理 以及确认中的 -->
									<b class=${(item.dealResult eq "0" )?"unApprIcon":"apprIcon" }></b>
								</div>
							</c:forEach>
						</c:if>
					</div>
				</div>
			</div>
		</c:if>
		<!-- 对私借款（添加后续审核） -->
		<c:if
			test="${(currentEA ne null and !('0' eq currentEA.dealResult)) and !(isSh == true) and  '0' eq loan.loanType and awaitEAList ne null and awaitEAList.size() > 0}">
			<div class="selectPeople" id="followAudit">
				<div class="selectPeopleTit">后续审核(请选择)：</div>
				<div class="selectPeopleList">
					<div class="addedPeopleList" type="0">
						<c:forEach items="${awaitEAList}" var="item">
							<div uName="${item.accountName}" uId="${item.accountId}"
								uAvatar="${item.avatar}">
								<img src="${item.avatar}"> <span>${item.accountName}</span>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</c:if>
		<!-- 批复结果 -->
		<div class="replyResults">
			<header>批复结果：</header>
			<c:if test="${shList != null}">
				<ul>
					<c:forEach items="${shList}" var="item">
						<c:if test="${item.dealResult == '1' or item.dealResult == '4'}">
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
		<!-- 按钮 -->
		<c:if test="${currentEA ne null }">
			<c:choose>
				<c:when test="${'0' eq currentEA.dealResult }">
					<div class="btnGroup">
						<button type="button" class="warpBtn doubleBtn blurBg"
							id="sureBtn" data-type="3" entityAccountId="${currentEA.id}">确认借款</button>
						<button type="button" class="warpBtn doubleBtn grayBg"
							id="returnBtn" data-type="4" entityAccountId="${currentEA.id}">拒绝借款</button>
					</div>
				</c:when>
				<c:otherwise>
					<div class="btnGroup">
						<button type="button" class="warpBtn singleBtn blurBg"
							id="executionBtn" data-type="5" entityAccountId="${currentEA.id}">确认执行</button>
						<%-- <button type="button" class="warpBtn doubleBtn grayBg" id="unExecutionBtn" data-type="6" entityAccountId="${currentEA.id}">拒绝执行</button> --%>
					</div>
				</c:otherwise>
			</c:choose>

		</c:if>
	</div>
	<footer class="copyright">© 微移云技术支持</footer>
	<!-- 人员列表End -->
	<div class="membersListWarp myHide"></div>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/zoom_app.js"></script>
	<script src="js/eSelectPeople-1.1.js"></script>
	<script src="js/eJweixin-1.0.0.js"></script>
	<script src="js/mobile/loan/cwAudit_detail.js"></script>
	<script src="js/jweixin-1.0.0.js"></script>
	<script type="text/javascript">
    var signature = ${signature};
    var jsApiList = ['chooseImage','uploadImage','previewImage'];
    $(function(){
    	$.eWeixinJSUtil.init(signature,jsApiList);
    });
</script>
</body>
</html>
