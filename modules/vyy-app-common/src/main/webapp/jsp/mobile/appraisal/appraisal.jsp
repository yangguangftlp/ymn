<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<title>员工评价</title>
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/empEvaluation.css">
</head>
<body class="whiteBg">
	<!-- 隐藏字段  id、loanType -->
	<input type="hidden" id="appraisalId" value="${appraisalId }">
	<input type="hidden" id="eaId" value="${eaId }">
	<div class="warp">
		<header class="warpHeader">${appraisalInfo.theme }</header>
		<c:set value="0" var="flag1" />
		<c:if test="${scoreMapList ne null and scoreMapList.size() gt 0}">
			<!-- 这里定义变量 主要用来区分问题是否已提交评分 -->
			<form>
				<section class="problemList">
					<c:forEach items="${scoreMapList }" var="item" varStatus="index">
						<c:if test="${flag1 eq '0' and  item.score ne null}">
							<c:set value="1" var="flag1" />
						</c:if>
						<article id="${item.id}"
							overallMerit="${item.standard ne null?false:true}">
							<header>${item.px}.${item.quota}<c:if
									test="${item.standard ne null}">(${item.scores}分)</c:if>
							</header>
							<c:choose>
								<c:when test="${item.standard ne null}">
									<c:choose>
										<c:when test="${item.score eq null}">
											<div class="subtitle">
												<label>评</label><input name="${index.index}" id="${index.index}" type="tel" scores="${item.scores}"
													placeholder="请填写" data-con="d" data-empty="请输入你的评分"
													data-error="评分必须为数字"><label>分</label>
											</div>
											<pre>${item.standard }</pre>
										</c:when>
										<c:otherwise>
											<div class="subtitle">评分：${item.score}</div>
											<pre>${item.standard }</pre>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${item.opinion eq null}">
											<textarea class="multiLine" name="comAppraisal"
												id="comAppraisal" data-con="*" data-empty="请填写综合评价"></textarea>
										</c:when>
										<c:otherwise>
											<pre>${item.opinion}</pre>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</article>
					</c:forEach>
				</section>
			</form>
		</c:if>
		<!-- <form>
        <section class="problemList">
            <article id="0" overallMerit="">
                <header>1. 该员工对工作认真态度(5分)</header>
                进行评价
                <div class="subtitle"><label>评</label><input name="score1" id="score1" placeholder="请填写" data-con="d" data-empty="请输入你的评分" data-error="评分格式错误"><label>分</label></div>
                评价之后
                <div class="subtitle">评分：2</div>
                公共
                <pre>职称可评可考。一般而言，具备职称评定条件的人不用参加全国通考，可以直接评定职称，与考试获得的职称具有同等效力，政府机关、国有企事业单位等均与认可，并且可以将职称评定档案调入所在单位主管人事部门。如果有的人不具备职称评定条件，又想获得职称，那就需要参加全国统一考试了。所以说，职称可评可考，考评效力同等，全国各地通用。</pre>
            </article>
            <article id="1" overallMerit="">
                <header>1. 该员工对工作认真态度(5分)</header>
                进行评价
                <div class="subtitle"><label>评</label><input name="score1" id="score1" placeholder="请填写" data-con="d" data-empty="请输入你的评分" data-error="评分格式错误"><label>分</label></div>
                评价之后
                <div class="subtitle">评分：2</div>
                公共
                <pre>职称可评可考。一般而言，具备职称评定条件的人不用参加全国通考，可以直接评定职称，与考试获得的职称具有同等效力，政府机关、国有企事业单位等均与认可，并且可以将职称评定档案调入所在单位主管人事部门。如果有的人不具备职称评定条件，又想获得职称，那就需要参加全国统一考试了。所以说，职称可评可考，考评效力同等，全国各地通用。</pre>
            </article>
            综合评价意见
            <article id="2" overallMerit="">
                <header>6. 综合评价意见</header>
                进行评价
                <textarea class="multiLine" name="comAppraisal" id="comAppraisal" data-con="*" data-empty="请填写综合评价"></textarea>
                评价之后
                <pre class="subtitle">人员很好，总体很优秀，建议公司给予相应奖金激励</pre>
            </article>
        </section>
    </form> -->
		<!-- 进行评价 -->
		<c:if test="${flag1 eq '0' }">
			<div class="btnGroup">
				<button type="button" class="warpBtn singleBtn mainBg"
					id="subEvaluation" operationType="0">提交评价</button>
			</div>
		</c:if>
	</div>
	<footer class="copyright">© 微移云技术支持</footer>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/mobile/appraisal/appraisal.js"></script>
	<script src="js/jweixin-1.0.0.js"></script>
</body>
</html>
