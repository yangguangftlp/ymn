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
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/mobiscroll.css">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/empEvaluation.css">
</head>
<body class="whiteBg">
<div class="warp">
    <!-- 搜索 -->
    <nav class="newSearchNav">
        <header class="newSearchHead whiteColor centrallyLocated mainBg newSearchBottom">
            <span><i class="fa fa-filter"></i></span>&emsp;筛选
            <input type="checkbox">
        </header>
        <div class="filterForm newSearchBottom whiteBg switchCon myHide">
            <ul class="whiteBg">
                <li class="newSearchBottom">
                    <label class="listNameWidth5">发起主题：</label>
                    <input  name="theme" id="theme" value="">
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">发起日期：</label>
                    <input type="text" name="createDate"
                           id="createDate" readonly="readonly">
                    <label class="add-on" for="createDate">
                        <i class="icon-th fa fa-calendar"></i>
                    </label>
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">结束日期：</label>
                    <input type="text" name="endDate" id="endDate"
                           vreadonly="readonly">
                    <label class="add-on" for="endDate">
                        <i class="icon-th fa fa-calendar"></i>
                    </label>
                </li>
            </ul>
            <div style="text-align: center">
                <button class="mainBg whiteColor" id="searchBtn">搜 索</button>&emsp;&emsp;
                <button class="whiteColor yellowBg" id="resetSearch">重置</button>
            </div>
        </div>
    </nav>
    <!-- 列表 -->
    <section>
        <ul>
        </ul>
    </section>
</div>
	<footer class="copyright">© 微移云技术支持</footer>
	<script src="js/jquery-1.8.3.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/mobiscroll.js"></script>
	<script src="js/mobiscroll_zh.js"></script>
	<script src="js/public-plug-in.js"></script>
	<script src="js/recordList.js"></script>
	<script src="js/mobile/appraisal/queryAppraisal.js"></script>
</body>
</html>
