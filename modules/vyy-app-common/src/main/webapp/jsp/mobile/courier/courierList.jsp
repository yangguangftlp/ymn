<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
    <title>快递列表</title>
    <link rel="stylesheet" href="css/expressage.css">
</head>
<body>
<div class="warp">
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
                <li class="newSearchBottom">待领快递</li>
                <li>已领快递</li>
                <!-- <li>全部状态</li> -->
            </ul>
        </div>
        <div class="filterForm newSearchBottom whiteBg switchCon myHide">
            <ul class="whiteBg">
                <li class="newSearchBottom">
                    <label class="listNameWidth3">收件人：</label>
                    <input name="userName" id="userName" value="">
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
            <!-- <li class="twoLine" dataid="1961ef2c63244fd9869034223e9afbac" status="1" flowtype="0">
                <header>
                    <span class="mainColor">王士楠</span>
                    <span class="auditStatus greenColor">46892222222</span>
                    <i class="fa fa-angle-right"></i>
                </header>
                <div class="subColor">10月09日录入</div>
            </li> -->
        </ul>
    </section>
    <input type="hidden" name="go-url" id="go-url" value="mobile/courier/getCourierList.action">
    <input type="hidden" name="go-Detail-url" id="go-Detail-url" value="mobile/courier/courierDetailView.action">
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/recordList.js"></script>
<script src="js/mobile/expressage/expressageRecord.js"></script>
</body>
</html>
