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
    <%@ include file="../../include/head.jsp" %>
    <title>借款管理</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/loan.css">
    <link rel="stylesheet" href="css/contact.css">
</head>
<body>
<div class="warp">
    <form id="loanForm">
        <div class="formList clear">
            <div class="listRight">
                <input  class="listRightPadding5" type="text" name="companyName" id="companyName" data-con="*" data-empty="请填写公司名称" value="${loan.company }">
            </div>
            <div class="listLeft listNameWidth5">公司名称：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input  class="listRightPadding5" type="text" name="borrower" id="borrower" readonly value="${loan.userName }">
            </div>
            <div class="listLeft listNameWidth5">借&nbsp;款&nbsp;人：</div>
        </div>
       <%--  <div class="formList clear">
            <div class="listRight">
                <div class="controls input-append date form_start_date" data-date="" data-date-format="yyyy-mm-dd" data-link-field="loanDate" data-link-format="yyyy-mm-dd">
                    <input type="text" value='<fmt:formatDate value="${loan.applyDate}" pattern="yyyy-MM-dd"/>' readonly class="listRightPadding5">
                    <span class="add-on"><i class="icon-th fa fa-calendar"></i></span>
                </div>
                <input type="hidden" name="loanDate" value='<fmt:formatDate value="${loan.applyDate}" pattern="yyyy-MM-dd"/>' id="loanDate" data-con="*" data-empty="请选择日期">
            </div>
            <div class="listLeft listNameWidth5">日&emsp;&emsp;期：</div>
        </div> --%>
        <div class="formList clear">
            <div class="listRight">
                <input  class="listRightPadding5" type="tel" name="money" id="money" value="${loan.amount}" data-con="m" data-empty="请填写申请金额" data-error="申请金额格式错误">
            </div>
            <div class="listLeft listNameWidth5">金&emsp;&emsp;额：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5" id="getCapital">${loan.capitalAmount}</span>
            </div>
            <div class="listLeft listNameWidth5">大写金额：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="tel" class="listRightPadding5" value="${loan.contractAmount}" name="contractAmount" id="contractAmount" data-error="合同金额格式错误">
                <b class="iconLabel">元</b>
            </div>
            <div class="listLeft listNameWidth5">合同金额：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="tel" class="listRightPadding5" value="${loan.remainingAmount}" name="remainingAmount" id="remainingAmount" data-error="剩余金额格式错误">
                <b class="iconLabel">元</b>
            </div>
            <div class="listLeft listNameWidth5">剩余金额：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="text" name="clientName" id="clientName" value="${loan.clientName}" class="listRightPadding5" data-con="*" data-empty="请填写客户名称">
            </div>
            <div class="listLeft listNameWidth5">客户名称：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="text" name="bank" id="bank" value="${loan.bank }" class="listRightPadding5" data-con="*" data-empty="请填写开户行">
            </div>
            <div class="listLeft listNameWidth5">开&nbsp;户&nbsp;行：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="tel" name="accountNum" id="accountNum" value="${loan.receiveAccount }" class="listRightPadding5"  data-con="bank" data-empty="请填写账号" data-error="账号格式错误">
            </div>
            <div class="listLeft listNameWidth5">账&emsp;&emsp;号：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input type="text" name="projectName" id="projectName" value="${loan.projectName }" class="listRightPadding5" data-con="*" data-empty="请填写项目名称">
            </div>
            <div class="listLeft listNameWidth5">项目名称：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <select name="loanPurpose" id="loanPurpose" class="listRightPadding5" data-con="*" data-empty="请选择借款用途">
                    <option value="-1">请选择</option>
                    <c:if test="${systemStatusList ne null and systemStatusList.size() gt 0 }">
                     <c:forEach items="${systemStatusList }" var="item">
                       <option value="${item.value }"  ${item.value eq loan.loanUse?'selected':''} >${item.name }</option>
                     </c:forEach>
                    </c:if>
                </select>
            </div>
            <div class="listLeft listNameWidth5">借款用途：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <input name="remark" id="remark" class="listRightPadding5" value="${loan.remark }">
            </div>
            <div class="listLeft listNameWidth5">借款说明：</div>
        </div>
        <div class="addCanningCopy" id="scanCopyList">
            <div class="canningTit">相关材料照片或扫描件：</div>
            <div class="canningCopyList">
                <div class="canningCopy">
                     <c:if test="${accessoryInfor != null}">
                       <c:forEach items="${accessoryInfor}" var="item">
                          <div class="canningCopyImg" accessoryId="${item.id}">
                             <a href="javascript:void(0)" data-original="${resPath}/01/resource/${item.fileName}"><img src="${resPath}/01/imageZoom/${item.fileName}" data-original="${resPath}/01/resource/${item.fileName}"></a>
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
            <div class="selectPeopleTit">审核人(请选择部门主管或经理)：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList" type="0">
                    <div class="addPeople" id="addAuditor">
                        <img src="images/addPeople.gif">
                    </div>
                </div>
            </div>
        </div>  -->
                <%-- <div class="selectPeople borderTop" id="selectAuditorList">
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

        </div> --%>
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
        <input type="hidden" id="loanType" name="loanType" value="${loan.loanType}">
        <input type="hidden" id="loanId" name="loanId" value="${loan.id}">
        <div class="btnGroup">
            <button type="button" class="warpBtn singleBtn blurBg" id="subBtn">提交申请</button>
        </div>
    </form>
</div>
<input type="hidden" name="scanCopy" id="scanCopy" value="" ><!-- data-con="*" data-empty="请上传相关材料照片或扫描件" -->
<input type="hidden" name="selectAuditor" id="selectAuditor" value=""  data-con="*" data-empty="请选择审核人">
<!-- 人员列表End -->
<div class="membersListWarp myHide"></div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/eSelectPeople-1.1.js"></script>
<script src="js/mobile/loan/corporateBorrow.js"></script>
<script src='js/jweixin-1.0.0.js'></script>
<script src='js/eJweixin-1.0.0.js'></script>
<script src="js/zoom_app.js"></script>
<script type="text/javascript">
    var signature = ${signature};
    var jsApiList = ['chooseImage','uploadImage','previewImage','closeWindow'];
    var flag = '${flag}';
    $(function(){
    	$.eWeixinJSUtil.init(signature,jsApiList);
    });
    </script>
</body>
</html>