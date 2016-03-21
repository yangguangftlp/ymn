<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<%


%>
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp"%>
    <title>审批</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/approval.css">
    <link rel="stylesheet" href="css/contact.css">
</head>
<body>
<div class="warp isTs">
    <form action="" method="post">
        <div class="formList clear">
            <div class="listRight">
                  <input type="text" name="flowName" id="flowName" class="listRightPadding5" placeholder='例如：${approvalType =='1'?"合同审批流程":"普通审批流程"}' data-con="*" data-empty="请填写流程名" value="${approvalInfo.flowName}">
            </div>
            <div class="listLeft listNameWidth5">流程名称：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="text" name="attrDepartment" id="attrDepartment" class="listRightPadding5" placeholder="请填写归属部门" data-con="*" data-empty="请填写归属部门"  value="${approvalInfo.department}" readonly>
            </div>
            <div class="listLeft listNameWidth5">归属部门：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <textarea name="content" id="content" class="listRightPadding5" placeholder='${approvalType =="1"?"请填写需要审批的事项":"采购物品请写明产品名称、参数、规格、 数 量、收货地址等 " }' data-con="*" data-empty="请填写需要审批内容" >${approvalInfo.content}</textarea>
            </div>
            <div class="listLeft listNameWidth5">审批内容：</div>
        </div>
        <!-- 合同审批 -->
        <c:if test="${approvalType =='1' }">
	        <div class="formList clear">
	            <div class="listRight">
	                <input name="contractNum" id="contractNum" class="listRightPadding5" placeholder="请填写合同编号" data-con="*" data-empty="请填写合同编号"  value="${approvalInfo.contractNumber}">
	            </div>
	            <div class="listLeft listNameWidth5">合同编号：</div>
	        </div>
	        <div class="formList clear">
	            <div class="listRight">
	                <input name="partner" id="partner" class="listRightPadding5" placeholder="请填写合作方" data-con="*" data-empty="请填写合作方"  value="${approvalInfo.partner}">
	            </div>
	            <div class="listLeft listNameWidth5">合&nbsp;作&nbsp;方&nbsp;：</div>
	        </div>
        </c:if>
        <div class="formList clear">
            <div class="listRight">
                <input type="text" name="remark" id="remark" class="listRightPadding5" placeholder="请填写备注" value="${approvalInfo.remark}">
            </div>
            <div class="listLeft listNameWidth5">备&emsp;&emsp;注：</div>
        </div>
        <div class="addCanningCopy" id="scanCopyList">
            <div class="canningTit">相关材料扫描件：</div>
            <div class="canningCopyList">
                <div class="canningCopy">
                  <c:if test="${ accessoryInfor != null}">
                    <c:forEach items="${ accessoryInfor}" var="item">
                       <div class="canningCopyImg" accessoryId="${item.id}">
                         <a href="javascript:;" data-original="${resPath}/01/resource/${item.fileName}"><img src="${resPath}/01/imageZoom/${item.fileName}" data-original="${resPath}/01/resource/${item.fileName}"></a><i></i>
                       </div>
                    </c:forEach>
                 </c:if>
                  <div class="addCanningCopyBtn" id="addScanCopy">
                        <img src="images/addImg.png">
                 </div>
                </div>
            </div>
        </div>
        <!-- 新模板审核人Start -->
        <div class="selectPeople borderTop" id="selectAuditorList">
            <div class="selectPeopleTit">审核人(请依次选择各级审核人)：<i class="fa fa-plus-square-o" id="addAuditorBtn"></i></div>
            <div class="selectStaffList">
                <div class="addedStaffList" type="0">
                     <c:if test="${ auditorList != null}" >
	                    <c:forEach items="${auditorList}" var="item" varStatus="status">
                        <section class="staffList">
                        <span>第<em name="level" index="${ item.remark}"></em>级审核人：</span>
                        <div id="${item.id}" status="${item.dealResult}" uAvatar="${item.avatar}" uName="${item.accountName}" uId="${item.accountId}" type="1">
                            <img src="${item.avatar}">
                            <span>${item.accountName}</span>
                        </div>
                        <b class="delStaffList">
                            <i class="fa fa-minus-square-o"></i>
                        </b>
                    	</section>
                       </c:forEach>
                      </c:if>
                </div>

            </div>

        </div>
        <!-- 新模板审核人End  -->
        <%-- <div class="selectPeople" id="selectAuditorList">
            <div class="selectPeopleTit">审核人：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList" type="0">
                      <c:if test="${ auditorList != null}" >
	                    <c:forEach items="${auditorList}" var="item">
                         <div id="${item.id}" status="${item.dealResult}" uAvatar="${item.avatar}" uName="${item.accountName}" uId="${item.accountId}" type="1">
	                         <img src="${item.avatar}">
	                         <span>${item.accountName}</span>
	                         <i></i>
                        </div>
                       </c:forEach>
                      </c:if>
                    <div class="addPeople" id="addAuditor">
                     <img src="images/addPeople.gif">
                	</div>
                </div>
            </div>
        </div> --%>
        <div class="selectPeople borderTop" id="principalList">
            <div class="selectPeopleTit">抄&nbsp;送&nbsp;人&nbsp;：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList" type="1">
                    <c:if test="${ principalList != null}">
	                    <c:forEach items="${principalList}" var="item">
	                      <div id="${item.id}" status="${item.dealResult}" uAvatar="${item.avatar}" uName="${item.accountName}" uId="${item.accountId}" type="1">
	                       <img src="${item.avatar}">
	                       <span>${item.accountName}</span>
	                        <i></i>
	                      </div>
	                   </c:forEach>
                   </c:if>
                   <div class="addPeople" id="addPrincipal">
                   <img src="images/addPeople.gif">
                   </div>
                </div>
            </div>
        </div>
        <input type="hidden" name="scanCopy" id="scanCopy"  data-empty="请上传相关材料扫描件">
        <input type="hidden" name="selectAuditor" id="selectAuditor" data-con="*" data-empty="请选择审核人">
        <input type="hidden" name="principal" id="principal"  data-empty="请选择抄送人">
        <div class="btnGroup">
            <button type="button" class="warpBtn doubleBtn blurBg" id="approval" data-url="mobile/approval/launchApproval.action">发起审批</button>
            <button type="button" class="warpBtn doubleBtn greenBg" id="tempStorage" data-url="mobile/approval/launchApproval.action">暂存</button>
        </div>
    </form>
    <input type="hidden" value="mobile/approval/approvalDetailView.action" id="go-url">
    <input type="hidden" value="${approvalType}" id=approvalType>
    <c:if test="${! empty approvalId }">
    	<input type="hidden" value="${approvalId}" id="approvalId">
    </c:if>
</div>
<!-- 人员列表End -->
<div class="membersListWarp myHide"></div>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/zoom_app.js"></script>
<script src="js/eSelectPeople-1.1.js"></script>
<script src="js/mobile/approval/approval-index.js"></script>
<script src='https://res.wx.qq.com/open/js/jweixin-1.0.0.js'></script>
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