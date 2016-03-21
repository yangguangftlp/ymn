<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="pragma" content="no-cache">
<title>请假</title>
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/global.css">
<link rel="stylesheet" href="css/common.css">
<link rel="stylesheet" href="css/mobiscroll.css">
<link rel="stylesheet" href="css/newExpense.css">
</head>
<body class="whiteBg">
<div class="warp">
    <!-- 列表 -->
    <nav class="newSearchNav">
        <div class="clear newSearchBottom publicNav mainBg">
            <div class="newSearchHead whiteColor newSearchHeaderL" id="1">
                <b></b>
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
                <li class="newSearchBottom">待审核</li>
                <li class="newSearchBottom">审核中</li>
                <li>已审核</li>
                <!-- <li>全部状态</li> -->
            </ul>
        </div>
        <div class="filterForm newSearchBottom whiteBg switchCon myHide">
            <ul class="whiteBg">
                <li class="newSearchBottom">
                    <label class="listNameWidth3">请假人：</label>
                    <input name="userName" id="userName" value="">
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth4">开始日期：</label>
                    <input type="text" name="startDate" id="startDate">
                    <label class="add-on" for="startDate">请选择 <i
                            class="fa fa-angle-right"></i></label>
                </li>
                <li class="newSearchBottom">
                    <label class="listNameWidth4">结束日期：</label>
                    <input type="text" name="endDate" id="endDate">
                    <label class="add-on" for="endDate">请选择 <i
                            class="fa fa-angle-right"></i></label>
                </li>
            </ul>
            <div style="text-align: center">
                <button class="mainBg whiteColor" id="searchBtn">搜 索</button>&emsp;&emsp;
                <button class="whiteColor yellowBg" id="resetSearch">重置</button>
            </div>
        </div>
    </nav>
    <section>
        <ul>
            <!--<li class="twoLine" dataid="" flowtype="0">
                <header>
                    <span class="mainColor">刘闯闯 申请 事假</span>
                    <span class="auditStatus greenColor singleStatus">待审核</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor clear">
                    <span>10月14日 14:40提交</span>
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
<script src="js/recordList.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/absent/record.js"></script>
</body>
</html>