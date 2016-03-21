<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp"%>
    <title>审批</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/leave.css">
</head>
<body>
<div class="warp">
    <form action="" method="post">
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${approvalInfo.flowName}</span>
            </div>
            <div class="listLeft listNameWidth5">流程名称：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${approvalInfo.department}</span>
            </div>
            <div class="listLeft listNameWidth5">归属部门：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${approvalInfo.content}</span>
            </div>
            <div class="listLeft listNameWidth5">审批内容：</div>
        </div>
       
        <c:if test="${approvalInfo.flowType =='1' }">
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${approvalInfo.contractNumber}</span>
            </div>
            <div class="listLeft listNameWidth5">合同编号：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${approvalInfo.partner}</span>
            </div>
            <div class="listLeft listNameWidth5">合&nbsp;作&nbsp;方&nbsp;：</div>
        </div>
        </c:if>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${approvalInfo.remark}</span>
            </div>
            <div class="listLeft listNameWidth5">备&emsp;&emsp;注：</div>
        </div>
        <div class="addCanningCopy" id="scanCopyList">
            <div class="canningTit  whiteBg">相关材料扫描件：</div>
            <div class="canningCopyList">
                <div class="canningCopy">
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
        <div class="selectPeople borderTop" id="selectAuditorList">
            <div class="selectPeopleTit whiteBg">审核人：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList">
                     <c:if test="${auditorList != null}" >
	                    <c:forEach items="${auditorList}" var="item">
                         <div id="${item.id}" status="${item.dealResult}" entityType="${item.entityType}" uName="${item.accountName}" uId="${item.accountId}" >
	                         <img src="${item.avatar}">
	                         <span>${item.accountName}</span>
	                         <b class=${item.dealResult == "0"?"unApprIcon":"apprIcon" }></b>
                        </div>
                       </c:forEach>
                      </c:if>
                </div>
            </div>

        </div>
        <div class="formList clear borderTop">
            <div class="listRight">
                <span class="listRightPadding5">${ccUser}</span>
            </div>
            <div class="listLeft listNameWidth5">抄&nbsp;送&nbsp;人&nbsp;：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${stautsDisplay}</span>
            </div>
            <div class="listLeft listNameWidth5">当前状态：</div>
        </div>
        
        <!-- 如果当前已结束 或者 拒绝 退回时显示 -->
       <%--  <c:if test="${approvalStatus != null}">
	         <div class="formList clear">
	            <div class="listRight">
	                <span class="listRightPadding5">
	                 <c:if test="${approvalStatus == '3'}">通过</c:if>
	                 <c:if test="${approvalStatus == '-1'}">未通过，原因:${remark}</c:if>
	                </span>
	            </div>
	            <div class="listLeft listNameWidth5">审批意见：</div>
	        </div>
        </c:if> --%>

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

			<!-- 判断是否为当前审批提交者 -->
        <c:if test="${userType != null && userType == '0'}">
	        <c:choose>
	           <c:when test="${!empty approvalInfo.status and approvalInfo.status == '-1' }">
	           <!-- 如果被退回 或 拒绝这里需要重新审核 -->
		            <div class="btnGroup">
			            <button type="button" class="warpBtn singleBtn mainBg" id="detailAudit" data-url="mobile/approval/relaunchApprovalView.action?id=${approvalInfo.id}" approvalType="${approvalInfo.flowType}">重新发起</button>
		            </div>
	           </c:when>
	           <c:otherwise>
	              <c:if test="${!empty approvalInfo.status and approvalInfo.status != '0' }">
		               <div class="btnGroup">
		                 <button type="button" class="warpBtn doubleBtn blurBg" id="urgeBtn" data-url="mobile/approval/approvalAudit.action" approvalId="${approvalInfo.id}" entityAccountId="002">催办</button>
		               	 <!-- 审批撤回功能按钮 -->
		               	 <c:if test="${approvalInfo.status == '1'}">
		               	 	<button type="button" class="warpBtn doubleBtn greenBg" id="withdrawBtn" data-url="mobile/approval/approvalRecordView.action" approvalId="${approvalInfo.id}" entityAccountId="002">撤回</button>
		               	 </c:if>
		               </div>
	              </c:if>
	           </c:otherwise>
	        </c:choose>
        </c:if>
        
        <!-- 已办 -->
        <c:if test="${uType != null and uType =='1' and approvalInfo.status == '3' and  approvalInfo.cstatus == '0'}">
	        <div class="btnGroup">
	            <button type="button" class="warpBtn singleBtn mainBg" id="hasHandledBtn" approvalId="${approvalInfo.id}" entityAccountId="002">已办</button>
	        </div>
        </c:if>
    </form>
</div>
<input data-flag="${flag}" type="hidden" id="flag">
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/approval/approval-myDatail.js"></script>
<script src='js/jweixin-1.0.0.js'></script>
<script src='js/eJweixin-1.0.0.js'></script>
<script src="js/zoom_app.js"></script>
   <script type="text/javascript">
   var signature = ${signature};
   var jsApiList = ['chooseImage','uploadImage','previewImage'];
   var flag = '${flag}';
   $(function(){
   	$.eWeixinJSUtil.init(signature,jsApiList);
   	/* if(!!flag && flag == '0'){
   		setTimeout(function () {
   		 	window.addEventListener("popstate", function(e) {
   		 		//如果当前是从审核页面过来的 页面将会关闭
   		         //try{
   		        	 // WeixinJSBridge.call('closeWindow'); 
   		            //}catch (e) {
   		        		window.location.href="mobile/approval/approvalRecordView.action";
					//}
   		 	  }, true);
   		      window.history.pushState({title: "title",url: "mobile/approval/approvalRecordView.action"}, "title", "approval/approvalRecordView.action");
   		  	}, 100); 
   	} */
   });
</script>
<!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>
