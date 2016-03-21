<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head lang="en">
	<%@ include file="../../include/head.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
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
</head>
<body>
<div class="warp">
    <form action="" method="post">
      <div class="formList clear">
            <div class="listRight">
                <span  class="listRightPadding5">${expenseInfo.expenseNum}</span>
            </div>
            <div class="listLeft listNameWidth5">报销单号：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span  class="listRightPadding5">${expenseInfo.theme}</span>
            </div>
            <div class="listLeft listNameWidth5">报销主题：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${expenseInfo.department}</span>
            </div>
            <div class="listLeft listNameWidth5">报销部门：</div>
        </div>
        <!-- 报销费用Start -->
        <div class="reimModal">
            <div class="reimType">
                <div class="">报销费用：</div>
            </div>
            <c:if test="${listExpenseFee ne null and listExpenseFee.size() gt 0 }">
              <c:forEach items="${listExpenseFee}" var="item">
                <div class="feeList" data-id="${item.id }">
                <%-- <div>${item.categoryDisplay } ${item.money }元</div> --%>
                <div>${item.categoryDisplay } <fmt:formatNumber  type='number' value='${item.money }' groupingUsed='false' />元</div>
            	</div>
              </c:forEach>
            </c:if>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span  class="listRightPadding5">${expenseInfo.reason}</span>
            </div>
            <div class="listLeft listNameWidth5">报销事由：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${expenseInfo.amount}</span>
                <b>元</b>
            </div>
            <div class="listLeft listNameWidth5">报销总额：</div>
        </div>
        <!-- 实际报销金额 -->
        <c:if test="${expenseInfo.status eq '5'}">
            <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${expenseInfo.actualCost}</span>
                <b>元</b>
            </div>
            <div class="listLeft listNameWidth5">实际报销：</div>
        </div>
        </c:if>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${expenseInfo.annexCount}</span>
                <b>页</b>
            </div>
            <div class="listLeft listNameWidth5">附件数目：</div>
        </div>
        <div class="addCanningCopy" id="scanCopyList">
            <div class="canningTit whiteBg">报销单及发票扫描件：</div>
            <div class="canningCopyList">
                <div class="canningCopy" >
                 <c:if test="${ accessoryInfor != null}">
                    <c:forEach items="${ accessoryInfor}" var="item">
                       <div class="canningCopyImg" accessoryId="${item.id}">
                         <a href="javascript:void(0)" data-original="${resPath}/01/resource/${item.fileName}"><img src="${resPath}/01/imageZoom/${item.fileName}" data-original="${resPath}/01/resource/${item.fileName}"></a>
                       </div>
                    </c:forEach>
                 </c:if>
               </div>
            </div>
        </div>
        <div class="selectPeople  borderTop" id="selectAuditorList">
            <div class="selectPeopleTit whiteBg">审核人：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList">
                    <c:forEach items="${auditorList}" var="item">
                       <div status="${item.dealResult}" uId="${item.accountId}">
                        <img src="${item.avatar}">
                        <span>${item.accountName}</span>
                        <b class=${item.dealResult == "0"?"unApprIcon":"apprIcon" }></b>
                       </div>
                    </c:forEach>
                </div>
            </div>

        </div>
        <div class="selectPeople borderTop" id="principalList">
            <div class="selectPeopleTit whiteBg">财务报销负责人：</div>
            <div class="selectPeopleList">
                  <div class="addedPeopleList">
                   <c:forEach items="${principalList}" var="item">
                      <div status="${item.dealResult}" uId="${item.accountId}">
                       <img src="${item.avatar}">
                       <span>${item.accountName}</span>
                       <b class=${item.dealResult == "0"?"unApprIcon":"apprIcon" }></b>
                      </div>
                   </c:forEach>
                  </div>
            </div>
        </div>
        <div class="formList clear borderTop">
            <div class="listRight">
                <span class="listRightPadding5">${stautsDisplay}</span>
            </div>
            <div class="listLeft listNameWidth5">当前状态：</div>
        </div>
        
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
        <!-- 如果被退回 或 拒绝这里需要重新审核 -->
        <c:if test="${userType != null && userType == '0'}">
	        <c:choose>
	           <c:when test="${expenseInfo.status == '-1' || expenseInfo.status == '-2'}">
		            <div class="btnGroup">
			            <button type="button" class="warpBtn singleBtn mainBg" id="detailAudit" data-url="mobile/expense/relaunchExpenseView.action?id=${expenseInfo.id}">重新发起</button>
		            </div>
	           </c:when>
	           <c:otherwise>
	                 <c:if test="${!empty expenseInfo.status and (expenseInfo.status != '-1' and expenseInfo.status != '-2' and expenseInfo.status != '5') }">
		               <div class="btnGroup">
		               	 <button type="button" class="warpBtn doubleBtn blurBg" id="urgeBtn" expenseId="${expenseInfo.id}" data-url="mobile/expense/expenseAudit.action">催办</button>
		               	 <!-- 审批撤回功能按钮 -->
		               	 <c:if test="${expenseInfo.status == '1' or ((auditorList.size() eq 0 )and expenseInfo.status == '4')}">
		               	 	<button type="button" class="warpBtn doubleBtn greenBg" id="withdrawBtn" data-url="mobile/expense/expenseAudit.action" expenseId="${expenseInfo.id}">撤回</button>
		               	 </c:if>
		               </div>
	              </c:if>
	               
	               
	           </c:otherwise>
	        </c:choose>
        </c:if>
     </form>
	</div>
    <script src="js/jquery-1.8.3.min.js"></script>
    <script src="js/public-plug-in.js"></script>
    <script src="js/zoom_app.js"></script>
    <script src="js/mobile/expense/rep-detail.js"></script>
    <script src='js/jweixin-1.0.0.js'></script>
	<script src='js/eJweixin-1.0.0.js'></script>
    <script type="text/javascript">
    var signature = ${signature};
    var jsApiList = ['chooseImage','uploadImage','previewImage','closeWindow'];
    var flag = '${flag}';
    $(function(){
    	$.eWeixinJSUtil.init(signature,jsApiList);
    	
    });
    </script>
    <!-- 版本信息 -->
	<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>