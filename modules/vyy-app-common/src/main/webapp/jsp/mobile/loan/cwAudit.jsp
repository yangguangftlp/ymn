<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp" %>
    <title>借款管理</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/mobiscroll.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/loan.css">
</head>
<body class="whiteBg">
<div class="warp">
    <!-- 列表 -->
    <nav class="newSearchNav">
        <div class="clear newSearchBottom publicNav mainBg">
            <div class="newSearchHead whiteColor newSearchHeaderL" id="1">
                <b>未处理</b>
                <input type="checkbox">
                <i class="fa fa-angle-down projectIcon"></i>
            </div>
            <div class="newSearchHeaderC"></div>
            <div class="newSearchHead whiteColor centrallyLocated newSearchHeaderR" id="2">
                <span><i class="fa fa-filter"></i></span>&emsp;筛选
                <input type="checkbox">
            </div>
        </div>
        <div class="newSearchBottom whiteBg switchCon myHide">
            <ul>
                <li class="newSearchBottom">未处理</li>
                <li>已处理</li>
                <!-- <li>全部状态</li> -->
            </ul>
        </div>
        <div class="filterForm newSearchBottom whiteBg switchCon myHide">
            <ul class="whiteBg">
                <li class="newSearchBottom">
                    <label class="listNameWidth4">借款人：</label>
                    <input name="borrower" id="borrower" value="">
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">申请日期：</label>
                    <input type="text" name="ApplyDate" id="ApplyDate" readonly>
                    <label class="add-on" for="ApplyDate">请选择 <i class="fa fa-angle-right"></i></label>
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth5">借款单号：</label>
                    <input name="LoanNum" id="LoanNum" value="" type="tel">
                </li>

            </ul>
            <div style="text-align: center">
                <button class="mainBg whiteColor" id="searchBtn">搜 索</button>&emsp;&emsp;
                <button class="whiteColor yellowBg" id="resetSearch">重置</button>
            </div>
        </div>
    </nav>
    <!-- 搜索 -->
    <section>
        <ul class="whiteBg">
            <!--<li class="twoLine" dataid="" data-loanType="">
                <header>
                    <span class="mainColor">江苏优客互联科技股份有限公司</span>
                    <span class="auditStatus greenColor">对公借款</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor clear">
                    <span>李玉霞 10月14日 14:40提交</span>
                    <span>单号:JK151102001</span>
                </div>
            </li>-->
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
<script src="js/mobile/loan/cwAudit.js"></script>
</body>
</html>