<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
    <title>问题反馈</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/feedback.css">
</head>
<body>
<div class="warp">
    <div class="rankingHeader">
        TOP 20
    </div>
    <c:if test="${feedbackTopList != null and feedbackTopList.size() gt 0 }">
      <ul>
		<c:forEach items="${feedbackTopList }" var="item" varStatus="status">
			<li class="rankingList">
			<c:choose>
			  <c:when test="${item.Rank eq '1' }">
			  	<span class="feedbackNumIcon"><img src="images/goldMedal.png" height="50"></span>
			  </c:when>
			  <c:when test="${item.Rank eq '2' }">
			  	<span class="feedbackNumIcon"><img src="images/silverMedal.png" height="50"></span>
			  </c:when>
			  <c:when test="${item.Rank eq '3' }">
			  	<span class="feedbackNumIcon"><img src="images/copperMedal.png" height="50"></span>
			  </c:when>
			  <c:otherwise>
			        <span class="feedbackNumIcon"><i class="topIcon">${item.Rank}</i></span>
			  </c:otherwise>
			</c:choose>
			<span class="biofeedback">${item.UserName}</span>
			<span class="feedbackNum">反馈问题总数：${item.Total}</span>
			</li>
		</c:forEach>
	   </ul>
     </c:if>

</div>
<script src="js/jquery-1.8.3.min.js"></script>
<!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>