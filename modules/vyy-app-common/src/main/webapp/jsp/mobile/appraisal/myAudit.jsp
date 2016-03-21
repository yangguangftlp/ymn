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
    <title>员工评价</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
</head>
<body class="whiteBg">
<div class="warp">
<!--     <ul class="clear nav twoColumns">
        <li class="navSelected" flag="0" data-url="data/rep-record0.json">未结束</li>
        <li flag="0" data-url="data/rep-record0.json">已结束</li>
    </ul> -->
    
    <nav class="newSearchNav">
			<header
				class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
				<b></b>&emsp;&emsp; <input type="checkbox"> <span><i
					class="fa fa-angle-down projectIcon"></i></span>
			</header>
			<div class="newSearchBottom whiteBg switchCon myHide">
				<ul>
					<li class="newSearchBottom">未结束<!--<i class="fa fa-check mainColor"></i>--></li>
					<li>已结束</li>
					<!-- <li>全部状态</li> -->
				</ul>
			</div>

		</nav>
    
    <section>
        <ul>
        </ul>
    </section>
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/recordList.js"></script>
<script src="js/mobile/appraisal/myAudit.js"></script>
</body>
</html>
