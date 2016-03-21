<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 
<!DOCTYPE html>
<html>
<head lang="en">
    <%@ include file="../../include/head.jsp" %>
    <title>问题反馈</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/feedback.css">
</head>
<body>
    <div class="warp">
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${feedbackInfo.problem}</span>
            </div>
            <div class="listLeft listNameWidth5">问题内容：</div>
        </div>
        <div class="formList clear">
            <div class="listRight">
                <span class="listRightPadding5">${feedbackInfo.suggest}</span>
            </div>
            <div class="listLeft listNameWidth5">修改建议：</div>
        </div>
        <div class="addCanningCopy" id="scanCopyList">
            <div class="canningTit">页面截图：</div>
            <div class="canningCopyList">
                <div class="canningCopy">
                   <c:if test="${ accessoryInfor != null}">
	                    <c:forEach items="${ accessoryInfor}" var="item">
	                       <div class="canningCopyImg" accessoryId="${item.id}">
	                         <a href="javascript:void(0)" data-original="${resPath}/01/resource/${item.fileName}"><img src="${resPath}/01/imageZoom/${item.fileName}" data-original="${resPath}/01/resource/${item.fileName}"></a>
	                       </div>
	                    </c:forEach>
		            </c:if>
                </div>
            </div>
        </div>
        <!-- 反馈信息Start -->
        <section>
            <div id="infoHeader" class="feedback" data-type="0" data-url="">
                修改进展
            </div>
	        <article id="InfoDisplay" class="feedbackCon">
            <c:if test="${entityProgressList != null and entityProgressList.size() > 0 }">
	                <c:forEach items="${ entityProgressList}" var="item">
	                     <div><b><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm"/> </b><span>${item.content}</span></div>
	                </c:forEach>
	         </c:if>
	         </article>
            <c:if test="${flag eq '0'}">
              <footer id="commentFooter">
                <input name="comment" id="comment" placeholder="在此输入评论">
                <img src="images/addComment.png" width="28" height="28">
           	 </footer>
            </c:if>
        </section>
        <!-- 反馈信息End -->
        <c:if test="${flag eq '0'}">
       	 <div class="btnGroup">
            <button type="button" class="warpBtn singleBtn blurBg" id="resolved" data-url="">已解决</button>
         </div>
        </c:if>
        <!-- 反馈id -->
        <input type="hidden" id="feedbackId" value="${feedbackInfo.id}">
        <!-- 操作url 发送消息 已解决操作 -->
        <input type="hidden" id="go-url" value="mobile/feedback/doFeedbackOperate.action">
    </div>
<script src="js/jquery-1.8.3.min.js"></script>
<script src='js/jweixin-1.0.0.js'></script>
<script src='js/eJweixin-1.0.0.js'></script>
<script src="js/mobile/feedback/problemDetail.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/zoom_app.js"></script>
<script type="text/javascript">
var signature = ${signature};
var jsApiList = ['chooseImage','uploadImage','previewImage'];
var flag = '${flag}';
    $(function(){  
    	$.eWeixinJSUtil.init(signature,jsApiList);
    });
    </script>
    <!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>