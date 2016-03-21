<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/empEvaluation.css">
</head>
<body>
	<div class="warp">
		<header class="warpHeader">${appraisalInfo.theme }</header>
		<c:set var="problemTemplate" />
		<c:if test="${problemTemplateMapList ne null and problemTemplateMapList.size() gt 0 }">
			<section class="problemList">
				<c:forEach items="${problemTemplateMapList }" var="item">
					<c:if test="${item.standard ne null }">
						<article>
							<header>${item.px}.${item.quota}<c:if test="${item.standard ne null}">(${item.scores}分)</c:if>
							</header>
							<c:if test="${item.scoreData ne null }">
								<div class="subtitle">
									<c:forEach items="${item.scoreData}" var="childItem"> 
                					 ${childItem.score}分——${childItem.uCount}人&emsp;
             						 </c:forEach>
								</div>
							</c:if>
							<pre>${item.standard }</pre>
						</article>
					</c:if>
					<c:if test="${item.standard eq null }">
						<c:set value="${item}" var="problemTemplate" />
					</c:if>
				</c:forEach>
			</section>
			<!-- 综合评价意见 -->
			<c:if test="${ bpj eq '1' and  appraisalInfo.overallMerit and problemTemplate ne null}">
				<div class="replyResults">
					<header>${problemTemplate.px}. ${problemTemplate.quota}：</header>
					<c:if test="${opinionMapList ne null and opinionMapList.size() gt 0 }">
						<ul>
							<c:forEach items="${opinionMapList }" var="item">
								<li><b>>> ${item.raterName }：</b><span>${item.opinion }</span></li>
							</c:forEach>
						</ul>
					</c:if>
				</div>
			</c:if>
		</c:if>
	</div>
	<footer class="copyright">© 微移云技术支持</footer>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/mobile/appraisal/employeeRanking.js"></script>
</body>
</html>
