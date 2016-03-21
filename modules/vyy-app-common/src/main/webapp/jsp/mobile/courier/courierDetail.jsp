<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
<%@ include file="../../include/head.jsp"%>
<title>我的快递详情</title>
<link rel="stylesheet" href="css/expressage.css">
</head>
<body>
<div class="warp">
    <header class="blurBg">
        快递详情
    </header>
    <div class="formList clear">
        <div class="listRight addRecipients">
            <span class="listRightPadding5">${courier.consigneeName}</span>
        </div>
        <div class="listLeft listNameWidth5">
            <span>收&nbsp;件&nbsp;人&nbsp;：</span>
        </div>
    </div>
    <div class="formList clear">
        <div class="listRight addTrackingNum">
            <span class="listRightPadding5">${courier.courierNum}</span>
        </div>
        <div class="listLeft listNameWidth5">
            <span>快递单号：</span>
        </div>
    </div>
    <div class="formList clear">
        <div class="listRight addTrackingNum">
            <span class="listRightPadding5">${courier.belong}</span>
        </div>
        <div class="listLeft listNameWidth5">
            <span>所属快递：</span>
        </div>
    </div>
    <div class="formList clear">
        <div class="listRight">
            <span class="listRightPadding5">
            <fmt:formatNumber type='number' value='${courier.money}' groupingUsed='false' /></span>
            <b class="addIcon wordIcon">元</b>
        </div>
        <div class="listLeft listNameWidth5">
            <span>到付金额：</span>
        </div>
    </div>
    <!-- <div class="getCode">领取二维码：</div> -->
    <c:choose>
      <c:when test="${courier.status eq '1'}">
         <!-- <div class="status-on">当前状态：<span>已领取</span></div> -->
         <div class="status-on rcv"></div>
      </c:when>
      <c:otherwise>
      <!--  <div class="status-on">当前状态：<span>未领取</span></div> -->
       <div class="status-on unrcv"></div>
      </c:otherwise>
     </c:choose>
    <c:if test="${courier.status eq '0' and (null != isShow and true == isShow)}">
	    <div class="btnGroup">
	            <button type="button" class="warpBtn singleBtn blurBg" id="receive" data-val="${courier.id}">领取</button>
	    </div>
	    <in
    </c:if> 
</div>
<footer class="copyright">© 微移云技术支持</footer>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/mobile/expressage/receive.js"></script>
</body>
</html>