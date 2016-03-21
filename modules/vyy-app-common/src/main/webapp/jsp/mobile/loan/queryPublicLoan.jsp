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
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
    <meta name="format-detection" content="telephone=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta http-equiv="pragma" content="no-cache">
    <title>借款查询</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/mobiscroll.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/empEvaluation.css">
</head>
<body>
<!--  评价查询  -->
<div class="warp">
    <!-- 搜索 -->
    <section class="searchWarp">
        <form class="myHide">
            <div class="searchList">
                <label>借款人：</label><input name="borrower" id="borrower" value="">
            </div>
            <div class="searchList">
                <label>日期：</label>
                <div class="controls input-append "><input type="text" name="ApplyDate" id="ApplyDate" readonly="">
                    <label class="add-on" for="ApplyDate"><i class="icon-th fa fa-calendar"></i></label>
                </div>
            </div>
            <div class="searchList">
                <label>借款单号：</label><input name="LoanNum" id="LoanNum" value="">
            </div>
            <div class="searchBtn blurBg">搜索</div>
        </form>
        <div class="searchToggle">
            <i class="fa fa-chevron-circle-down" data-flag="0"></i>
        </div>
    </section>
    <!-- 列表 -->
    <section class="recordList">
        <ul>
            <!-- <li class="twoLine" dataid="1c43b8e5370148b6932005606c9722ba" data-loantype="1">
                <header>
                    <span class="mainColor">嘎嘎嘎</span>
                    <span class="auditStatus greenColor">对公</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor clear">
                    <span>李玉霞 12月30日提交</span>
                    <span class="oddNumber">单号:JK151230110728</span>
                </div>
            </li> -->
        </ul>
    </section>
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobiscroll.js"></script>
<script src="js/mobiscroll_zh.js"></script>
<script src="js/mobile/loan/query.js"></script>
</body>
</html>