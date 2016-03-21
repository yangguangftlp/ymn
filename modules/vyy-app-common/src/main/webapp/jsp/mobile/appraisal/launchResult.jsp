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
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
</head>
<body>
<div class="warp">
	<input type="hidden" id="referer" value="${referer }">
	<input type="hidden" id="appraisalId" value="${appraisalInfo.id }">
    <header class="warpHeader">${appraisalInfo.theme}</header>
    <c:if test="${problemTemplateList ne null and problemTemplateList.size() gt 0 }">
      <section class="problemList">
      <c:forEach items="${problemTemplateList}" var="item" >
         <article>
            <header>${item.px}. ${item.quota}
            <c:if test="${item.standard ne null}">(${item.scores}分)</c:if>
            </header>
            <pre>${item.standard}</pre>
        </article>
      </c:forEach>
      </section>
    </c:if>
   
    <!-- 被评价人 -->
    <div class="selectPeople borderTop">
        <div class="selectPeopleTit">被评价人：</div>
        <div class="selectPeopleList">
            <div class="addedPeopleList">
                <c:if test="${beAppraisalUser != null}" >
                <c:forEach items="${beAppraisalUser}" var="item">
                    <div id="${item.id}" status="${item.dealResult}" entityType="${item.entityType}" uName="${item.accountName}" uId="${item.accountId}" >
                     <img src="${item.avatar}">
                     <span>${item.accountName}</span>
                   </div>
                  </c:forEach>
                </c:if>
            </div>
        </div>
    </div>
    <!-- 被评价人 -->
    <div class="selectPeople borderTop borderBottom">
        <div class="selectPeopleTit">评价人：</div>
        <div class="selectPeopleList">
            <div class="addedPeopleList">
               <c:if test="${appraisalUser != null}" >
                <c:forEach items="${appraisalUser}" var="item">
                    <div id="${item.id}" status="${item.dealResult}" entityType="${item.entityType}" uName="${item.accountName}" uId="${item.accountId}" >
                     <img src="${item.avatar}">
                     <span>${item.accountName}</span>
                   </div>
                  </c:forEach>
                </c:if>
            </div>
        </div>
    </div>
    <c:if test="${isSponsor != null and isSponsor eq '1' }">
    <div class="btnGroup">
        <button type="button" class="warpBtn singleBtn mainBg" id="cancelEvaluation" operationType="0">取消评价</button>
    </div>
    </c:if>
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/appraisal/launchResults.js"></script>
</body>
</html>
