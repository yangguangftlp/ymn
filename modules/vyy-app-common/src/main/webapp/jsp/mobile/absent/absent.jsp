<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
    <link rel="stylesheet" href="css/communal.css">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/mobiscroll.css">
    <link rel="stylesheet" href="css/leave.css">
    <link rel="stylesheet" href="css/contact.css">
    <style type="text/css">
		.loading {
			width: 134px;
			height: 94px;
			text-align: center;
			background: rgba(0, 0, 0, 0.6);
			color: #fff;
			border-radius: 4px;
			margin: auto;
			font-size: 14px;
			z-index: 2500;
			position: absolute;
		}
		/* 没有签到事项End */
		.oneLoading>div:first-child {
			padding-top: 20px;
		}
    </style>
</head>
<body>
    <!-- 加载样式 -->
	<!-- <div class="screenModal myHide"  id="onLoading">
       <div class="oneLoading loading" id="loading">
		<div>
			<img src="images/smallLoading.gif">
		</div>
		<div>加载中...</div>
	  </div>
    </div> -->
    <div class="leaveWarp">
        <form id="leaveForm">
            <div class="formList clear">
                <div class="listRight">
                    <div class="listRightPadding5">
                        <select name="leaveReason" id="leaveReason" data-con="*" data-empty="请选择请假事由">
                            <option value="-1" style="border: 0px !important;">请选择</option>
                            <c:forEach items="${absentTypeList}" var="item" varStatus="status">
                                 <option value ="${item.value}" >${item.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                </div>
                <div class="listLeft listNameWidth5">请假事由：</div>
            </div>
            <div class="formList clear">
                <div class="listRight">
                    <input type="text" name="leaveExplain" id="leaveExplain" class="listRightPadding5" >
                </div>
                <div class="listLeft listNameWidth5">请假说明：</div>
            </div>
            <div class="formList clear">
                <div class="listRight">
                    <input type="text" name="leaveJob" id="leaveJob" class="listRightPadding5" data-con="*" data-empty="请填写职位" value="${weixinUser.position}">
                </div>
                <div class="listLeft listNameWidth5">职&emsp;&emsp;位：</div>
            </div>
            <div class="formList clear">
                <div class="listRight">
                    <input type="text" name="leaveDepartment" id="leaveDepartment" class="listRightPadding5" data-con="*"  data-empty="请填写部门" value="${department}"  readonly>
                </div>
                <div class="listLeft listNameWidth5">部&emsp;&emsp;门：</div>
            </div>
            <div class="formList clear">
                <div class="listRight">
                	<input type="text" class="listRightPadding5" name="leaveStartTime" id="leaveStartTime" data-con="*" data-empty="请选择开始时间">
                	<label class="icon-th fa fa-clock-o" for="leaveStartTime"></label>
                    <!-- <div class="controls input-append date form_date" data-date="" data-picker-position="bottom-left" data-date-format="yyyy-mm-dd hh:ii" data-link-field="leaveStartTime" data-link-format="yyyy-mm-dd hh:ii">
                        <input type="text" value="" readonly class="listRightPadding5" placeholder="请选择开始时间">
                        <span class="add-on"><i class="icon-th fa fa-clock-o"></i></span>
                    </div>
                     -->
                </div>
                <div class="listLeft listNameWidth5">开始时间：</div>
            </div>
            <div class="formList clear">
                <div class="listRight">
                	<input type="text" class="listRightPadding5" name="leaveStartEnd" id="leaveStartEnd" data-con="*" data-empty="请选择结束时间">
                	<label class="icon-th fa fa-clock-o" for="leaveStartEnd"></label>
                    <!-- <div class="controls input-append date form_datetime" data-date="" data-picker-position="bottom-left" data-date-format="yyyy-mm-dd hh:ii" data-link-field="leaveStartEnd" data-link-format="yyyy-mm-dd hh:ii">
                        <input type="text" value="" readonly  class="listRightPadding5"  placeholder="请选择结束时间">
                        <span class="add-on"><i class="icon-th fa fa-clock-o"></i></span>
                    </div>
                     -->
                </div>
                <div class="listLeft listNameWidth5">结束时间：</div>
            </div>
            <!-- <div class="selectPeople" id="selectAuditor">
                <div class="selectPeopleTit">审核人：</div>
                <div class="selectPeopleList">
                    <div class="addedPeopleList" type='0'>
                      <div class="addPeople" id="addAuditor">
                        <img src="images/addPeople.gif">
                      </div>
                    </div>
                </div>

            </div> -->
            <div class="selectPeople borderBottom" id="selectAuditorList">
            <div class="selectPeopleTit">审核人(请依次选择各级审核人)：<i class="fa fa-plus-square-o" id="addAuditorBtn"></i></div>
            <div class="selectStaffList">
                <div class="addedStaffList" type="0">
                     <c:if test="${ auditorList != null}" >
	                    <c:forEach items="${auditorList}" var="item" varStatus="status">
                        <section class="staffList">
                        <span>第<em name="level" index="${ item.remark}"></em>级审核人：</span>
                        <div id="${item.id}" status="${item.dealResult}" uAvatar="${item.avatar}" uName="${item.accountName}" uId="${item.accountId}" type="1">
                            <img src="${item.avatar}">
                            <span>${item.accountName}</span>
                        </div>
                        <b class="delStaffList">
                            <i class="fa fa-minus-square-o"></i>
                        </b>
                    	</section>
                       </c:forEach>
                      </c:if>
                </div>

            </div>

        </div>
            <input type="hidden" name="myAuditor" id="myAuditor" data-con="*" data-empty="请选择审核人" value="">
            <div class="selectPeople" id="copyTo">
                <div class="selectPeopleTit">抄送人：</div>
                <div class="selectPeopleList">
                    <div class="addedPeopleList" type='1' data-empty="请选择抄送人">
                       <div class="addPeople" id="addCc">
                        <img src="images/addPeople.gif">
                       </div>
                    </div>
                </div>

            </div>          
            <div class="btnGroup">
                <button type="button" class="singleBtn warpBtn" id="applyFor">申请</button>
            </div>
        </form>
    </div>
    <!-- 人员列表End -->
    <div class="membersListWarp myHide"></div>
    <script src="js/jquery-1.8.3.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/mobiscroll.js"></script>
	<script src="js/mobiscroll_zh.js"></script>
	<script src="js/public-plug-in.js"></script>
    <script src="js/mobile/absent/absent.js"></script>
    <script src="js/eSelectPeople-1.1.js"></script>
    <script type="text/javascript">
    $(function(){
    	var membersListWarp = $(".membersListWarp").eSelectPeople({
    		btClose : function(_self) {
    			$(".leaveWarp").show();
    			$("body").css("overflow-y","auto");
    			_self.hide();
    		},
    		btCance : function(_self) {
    			$(".leaveWarp").show();
    			$("body").css("overflow-y","auto");
    			_self.hide();
    		},
    		btOk : function(_self,data) {
    			$("body").css("overflow-y","auto");
    			 if(!!data && data.length > 0 ){
    				var div = null;
    				var img = null;
    				var span = null;
    				var elemI = null;
    				var uId = null;
    				//判断是否已存在选择的用户 这里需要去重
    				var flag = false;
    				var type = _self.attr("flag");
    				for(var i = 0,size = data.length;i<size;i++){
    					uId = data[i].uId;
    					if("0" == type){
    						//审核人只能选择一个人
    						 //$(".addedPeopleList[type='"+type+"']").find("div[uId]").remove();
    						var _num=parseInt($(".addedStaffList>section").length)+1;
                            if(judgeRepeat(uId,_num)){

                                var _html='<section class="staffList">' +
                                    '<span>第<em>'+digitalSwitchChart(_num.toString())+'</em>级审核人：</span>' +
                                    '<div uId='+uId+' uAvatar='+data[i].uAvatar+' uName='+data[i].uName+'>' +
                                    '<img src='+data[i].uAvatar+'>' +
                                    '<span>' +
                                    data[i].uName +
                                    '</span>' +
                                    '</div>' +
                                    '<b class="delStaffList"><i class="fa fa-minus-square-o"></i></b>' +
                                    '</section>';
                                $(".addedStaffList").append(_html);
                            }
    					}
    					else {
    						$(".addedPeopleList[type='"+type+"'] > div").each(function(){
        						if($(this).attr("uId") == uId){
        							flag = true;
        						}
        					});
        					if(flag){
        						flag = false;
        						continue;
        					}
    					}
    					
    					div = $("<div>");
    					div.attr("uId",uId);
    					div.attr("uAvatar",data[i].uAvatar);
    					div.attr("uName",data[i].uName);
    					img = $("<img>");
    					img.attr("src",data[i].uAvatar);
    					img.on("click",function(){
    						$(this).parent().remove();
    					});
    					span = $("<span>");
    					span.append(data[i].uName);
    					elemI = $("<i>");
    					elemI.css("cursor","pointer");
    					elemI.on("click",function(){
    						$(this).parent().remove();
    					});
    					div.append(img);
    					div.append(span);
    					div.append(elemI);
    					 $(".addedPeopleList[type='"+type+"']").find(".addPeople").before(div);
    				}
    			}
    			
    			$(".leaveWarp").show();
    			_self.hide();
    		}
    	});
        $("#addAuditorBtn").click(function(){
        	$("body").css("overflow","hidden");
            $(".leaveWarp").hide();
            membersListWarp.eshow({flag:"0",_isMultipleChoice:false});
        });

    	$("#addCc").on("click",function(){
        	$(".leaveWarp").hide();
        	membersListWarp.eshow({_isMultipleChoice:true,flag:1});
        }); 
    	function judgeRepeat(_uId,_len){
    		var $parent = $(".addedStaffList[type='0']");
    		var $obj = $parent.find("[uid="+_uId+"]");
    		if($obj.length>0){
    			return false;
    		}else{
    			 return true;
    		}
    	}
    });
    </script>
   <!--  <script type="text/javascript">
		$(document).ready(function(){
	           //获得浏览器的高度
	           var windowH=window.innerHeight;
	           var windowW=window.innerWidth;
	           //设置发起签到模态框的top、left
	           $("#loading").css({"top":(windowH/2-50),"left":(windowW/2-67),"z-index":"200000"});
	           $("#onLoading").css({"z-index":"100000"});
		});
  </script> -->
<!-- 版本信息 -->
<script type="text/javascript" src="js/foot.js"></script>
</body>
</html>