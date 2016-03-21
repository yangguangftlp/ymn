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
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/contact.css">
    <link rel="stylesheet" href="css/empEvaluation.css">
</head>
<body>
<div class="warp">
    <form action="" method="post">
        <div class="formList clear">
            <div class="listRight">
                <input type="text" name="subject" id="subject" class="listRightPadding5" data-con="*" data-empty="请填写评价主题">
            </div>
            <div class="listLeft listNameWidth5">评价主题：</div>
        </div>
        <!-- 评价标准模态框Start -->
        <div class="stepwiseListModal">
            <div class="stepwiseListHeader">
                <div class="">问题列表：</div>
                <div class="listRight" id="addQuestion">
                    <span class="listRightPadding5"></span>
                    <b><i class="fa fa-plus-square-o"></i></b>
                </div>
            </div>
        </div>
        <div class="formList clear">
            <label for="checkBoxBtn" class="customCheckBoxPar"><input type="checkbox" name="checkBoxBtn" id="checkBoxBtn" class="customCheckBox" checked>需要填写综合评价</label>
        </div>
        <!-- 被评价人 -->
        <div class="selectPeople" id="selectPeopleByEvaluated">
            <div class="selectPeopleTit">被评价人：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList" type="1" data-flag="0">
                    <div class="addPeople" id="addPeopleByEvaluated">
                        <img src="images/addPeople.gif">
                    </div>
                </div>
            </div>
        </div>
        <!-- 评价人 -->
        <div class="selectPeople" id="selectAppraiser">
            <div class="selectPeopleTit">评价人：</div>
            <div class="selectPeopleList">
                <div class="addedPeopleList" type="1" data-flag="1">
                    <div class="addPeople" id="addAppraiser">
                        <img src="images/addPeople.gif">
                    </div>
                </div>
            </div>
        </div>

        <div class="btnGroup">
            <button type="button" class="warpBtn singleBtn mainBg" id="audit" operationType="0">发起评价</button>
        </div>
    </form>
    <div class="addModal myHide" id="standardModal">
        <div class="addReimbursement modalShell">
            <form id="standardForm">
                <header>填写评报指标</header>
                <div class="modalCon">
                    <div class="formList clear">
                        <div class="listRight">
                            <input type="text" name="evaluation" id="evaluation"  class="listRightPadding5" data-con="*" data-empty="请填写评价指标">
                        </div>
                        <div class="listLeft listNameWidth5">
                            <span class="title">评价指标：</span>
                        </div>
                    </div>

                    <div class="formList clear">
                        <div class="listRight">
                            <textarea name="evaluationStandard" id="evaluationStandard" class="listRightPadding5 multiLine" placeholder="请填写" data-con="*" data-empty="请填写评价标准"></textarea>
                        </div>
                        <div class="listLeft listNameWidth5">
                            <span class="title">评价标准：</span>
                        </div>
                    </div>
                    <div class="formList clear">
                        <div class="listRight">
                            <input type="text" name="fraction" id="fraction" value="5" class="listRightPadding5" data-con="d" data-empty="请填写满分数字" data-error="满分数字格式错误">
                        </div>
                        <div class="listLeft listNameWidth5">
                            <span class="title">满&emsp;&emsp;分：</span>
                        </div>
                    </div>
                </div>
                <footer>
                    <button type="reset" id="cancelBtn">取消</button>
                    <button type="button" id="saveBtn">确认</button>
                </footer>
            </form>
        </div>
    </div>
</div>
<footer class="copyright">© 微移云技术支持</footer>
<!-- 人员列表End -->
<div class="membersListWarp myHide"></div>
<script src="js/jquery-1.8.3.min.js"></script>
<script src="js/public-plug-in.js"></script>
<script src="js/eSelectPeople-1.1.js"></script>
<script src="js/mobile/appraisal/launchAppraisal.js"></script>
<!-- <script src="js/eSelectPeople-1.0.js"></script> -->
</body>
</html>
