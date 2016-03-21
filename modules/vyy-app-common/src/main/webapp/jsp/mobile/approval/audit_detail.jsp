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
        <div class="formList clear borderTop">
            <div class="listRight">
                <span class="listRightPadding5">${ccUser}</span>
            </div>
            <div class="listLeft listNameWidth5">抄&nbsp;送&nbsp;人&nbsp;：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${alreadyUser}</span>
            </div>
            <div class="listLeft listNameWidth5">已审核人：</div>
        </div>
        <!-- <div class="selectPeople" id="selectAuditorList">
            <div class="selectPeopleTit whiteBg">下一级审核人(无审核人可不选)：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList">
	                <div class="addPeople" id="addAuditor">
	                    <img src="images/addPeople.gif">
	                </div>
                </div>
            </div>

        </div> -->
        
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
            <button type="button" class="warpBtn doubleBtn blurBg" id="passBtn" data-url="mobile/approval/approvalAudit.action" approvalId="${approvalInfo.id}" entityAccountId="${entityAccount.id}">通过</button>
            <button type="button" class="warpBtn doubleBtn yellowBg" id="returnBtn" data-url="mobile/approval/approvalAudit.action" approvalId="${approvalInfo.id}" entityAccountId="${entityAccount.id}">退回</button>
        </div>
    </form>
       <input type="hidden" value="mobile/approval/approvalDetailView.action?id=${approvalInfo.id}&flag=1" id="go-url">
</div>
<!-- 人员列表End -->
<div class="membersListWarp myHide"></div>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/eSelectPeople-1.0.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/approval/approval-auditDetail.js"></script>
<script src='js/jweixin-1.0.0.js'></script>
<script src='js/eJweixin-1.0.0.js'></script>
<script src="js/zoom_app.js"></script>
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
