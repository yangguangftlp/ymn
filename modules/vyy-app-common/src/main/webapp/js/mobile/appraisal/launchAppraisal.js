var submitUrl="mobile/appraisal/launchAppraisal.action",//发起评价请求url
    dataGlag="",//评价人、被评价人的flag
    jumpUrl="mobile/appraisal/launchResultView.action",//发起评价成功之后的跳转链接
    isEdit=false;
$(function(){
    //发起评价
    initiateEvaluation();
    //添加标准
    addReimbursement();
    //保存标准
    saveStandard();
    //取消标准
    cancelStandard();
    //删除标准
    delStandard();
    //编辑标准
    editStandard();
    //选择审核人
    selectReviewer();
});
//选择审核人
function selectReviewer(){
    var membersListWarp = $(".membersListWarp").eSelectPeople({
        btClose : function(_self) {
            $(".warp").show();
            _self.hide();
        },
        btCance : function(_self) {
            $(".warp").show();
            _self.hide();
        },
        btOk : function(_self,data) {
            if(!!data && data.length > 0 ){
                var div = null;
                var img = null;
                var span = null;
                var elemI = null;
                var uId = null;
                //判断是否已存在选择的用户 这里需要去重
                var flag = false;
                var type = ("false" == _self.attr("_flag"))?'0':'1';
                for(var i = 0,size = data.length;i<size;i++){
                    uId = data[i].uId;
                    if("0" == type){
                        //审核人只能选择一个人
                        $(".addedPeopleList[data-flag='"+dataGlag+"']").find("div[uId]").remove();
                    }
                    else {
                        $(".addedPeopleList[data-flag='"+dataGlag+"'] > div").each(function(){
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
                    $(".addedPeopleList[data-flag='"+dataGlag+"']").find(".addPeople").before(div);
                };
            }
            $(".warp").show();
            _self.hide();
        }
    });
    $("#addPeopleByEvaluated").on("click",function(){
        dataGlag=$(this).parent().attr("data-flag");
        $(".warp").hide();
        membersListWarp.eshow({flag:0,_isMultipleChoice:true});
    });
    //评价人
    $("#addAppraiser").on("click",function(){
        dataGlag=$(this).parent().attr("data-flag");
        $(".warp").hide();
        membersListWarp.eshow({flag:1,_isMultipleChoice:true});
    });
}
//发起评价
function initiateEvaluation(){
    $("#audit").click(function(){
        judgeIsNull();
        $(".warp>form").verification({
            callback:function(){
                mySubmit();
            }
        });
    });
}

function getData(){
    //评分标准
    var problemList = [];
    $(".stepwiseListModal .stepwiseList").each(function(index){
        var $this=$(this);
        problemList.push({
            quota : $this.find("em").eq(0).text(),//指标
            //standard : $this.attr("data-text").replaceAll("\r","<br/>"),//标准
            standard : $this.find("pre").text().toString().replaceAll("\r","<br/>"),//标准
            score : $this.find("span").text()//满分
        });
    });
    //被评价人
    var entityUserInfo = [];
    $(".addedPeopleList[data-flag='0'] > div[uId]").each(function() {
        entityUserInfo.push({
            "uId" : $(this).attr("uId"),
            "uAvatar" : $(this).attr("uAvatar"),
            "uType" : 2,
            "personType" : '4',
            "uName" : $(this).attr("uName"),
            "entityType" : "7"
        });
    });
    //评价人
    $(".addedPeopleList[data-flag='1'] > div[uId]").each(function() {
        entityUserInfo.push({
            //uType:'2',
            "uId" : $(this).attr("uId"),
            "uAvatar" : $(this).attr("uAvatar"),
            "uType" : 2,
            "personType" : '5',
            "uName" : $(this).attr("uName"),
            "entityType" : "7"
        });
    });
    var data = {
        theme: $.trim($("#subject").val()),//主题
        overallMerit : $("#checkBoxBtn").prop("checked"),//是否增加综合评价
        entityUserInfo:entityUserInfo,//评价人和被评价人
        problemList:problemList,//问题列表
        commandInfo:{
            commandType:'general'
        }
    };
    return data;
}
function mySubmit(){
    $.ajax({
        url: submitUrl,
        data:{
            jsonAppraisalInfo:JSON.stringify(getData())
        },
        type:"post",
        dataType:"json",
        beforeSend:function(){
            $("body").loading({
                move:"show"
            });
        },
        success:function(resData, textStatus){
            if (0 == resData.status) {
                window.location.replace(jumpUrl+"?id="+resData.value);
            } else {
                $("body").loading({
                    move:"hide"
                });
                $("body").toast({
                    msg:resData.errorMsg,
                    type:2,
                    callMode:"func"
                });
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown) {
            $("body").loading({
                move:"hide"
            });
            $("body").toast({
                msg:textStatus,
                type:2,
                callMode:"func"
            });
        }
    });
}
//添加标准
function addReimbursement(){
    $("#addQuestion>span,#addQuestion>b").click(function(){
        $("#standardModal").addClass("boxModel").show();
    });
}
//保存标准
function saveStandard(){
    $("#saveBtn").click(function(){
        $("#standardForm").verification({
            callback: function(){
                var $evaluationVal= $.trim($("#evaluation").val());//评价指标
                var $evaluationStandard=$.trim($("#evaluationStandard").val().replaceAll("\r","<br/>"));//评价标准
                var $fraction=$.trim($("#fraction").val());//满分
                if(isEdit){
                    $(".stepwiseListModal .stepwiseList").each(function(index){
                        var $this=$(this);
                        if(!!$this.attr("isEdit")){
                            $this.find("em").eq(0).text($evaluationVal);
                            //$this.find("data-text",$evaluationStandard);
                            $this.find("pre").text($evaluationStandard);
                            $this.find("span").text($fraction);
                            $this.removeAttr("isEdit");
                            isEdit=false;
                            return;
                        }
                    });
                }else{
                    //插入数据
                    var $html='<div class="stepwiseList" data-text='+$evaluationStandard+'>' +
                        '<pre class="myHide">'+$evaluationStandard+'</pre>'+
                        '<div>' +
                        '<em>'+$evaluationVal+'</em>' +
                        '<em>(满分<span>'+$fraction+'</span>分)</em>' +
                        '</div>' +
                        '<b><i class="fa fa-edit listEditBtn"></i>' +
                        '<i class="fa fa-minus-square-o listDelBtn"></i>' +
                        '</b>' +
                        '</div>';
                    $(".stepwiseListModal").append($html);
                }
                //淡出模态框
                fadeOutMOdal();
            }
        })
    });
}
//取消标准
function cancelStandard(){
    $("#cancelBtn").click(function(){
        $("#standardModal").hide();
    });
}
//删除标准
function delStandard(){
    $(document).on(touchEv,".stepwiseList b .listDelBtn",function(){
        var $this=$(this);
        $("body").toast({
            msg:"是否确认删除该条记录",
            type:5,
            callMode:"func",
            end:function(){
                $this.parents(".stepwiseList").remove();
            }
        });
    });
}
//编辑标准
function editStandard(){
    $(document).on(touchEv,".stepwiseList b .listEditBtn",function(){
        var $this=$(this);
        $this.parents(".stepwiseList").attr("isEdit","true");
        var $parent=$this.parents(".stepwiseList");
        var quota=$parent.find("em").eq(0).text(),//指标
        //standard= $parent.attr("data-text").toString().replaceAll("\r","<br/>"),//标准
            standard=$parent.find("pre").text().replaceAll("\r","<br/>"),
            score=$parent.find("span").text()//满分
        $("#standardModal").addClass("boxModel").show();
        $("#evaluation").val(quota);//指标
        $("#evaluationStandard").val(standard);//标准
        $("#fraction").val(score);
        isEdit=true;
    });
}
//淡出模态框
function fadeOutMOdal(){
    $("#standardModal").hide();
    //模态框数据恢复初始化
    $("#evaluation,#evaluationStandard").val("");
    $("#fraction").val("5");
}
//判断评价人与被评价人是否已选
function judgeIsNull(){
    var _hiddenEle='<input type="hidden" name="stepwiseL" id="stepwiseL" data-con="*" data-empty="请添加评价问题" value="">'+
            '<input type="hidden" name="peopleByEvaluatedL" id="peopleByEvaluatedL" data-con="*" data-empty="请选择被评价人" val="">' +
            '<input type="hidden" name="appraiserL" id="appraiserL"  data-con="*" data-empty="请选择评价人" val="">'
        ;
    $(".warp>form").append(_hiddenEle);
    //问题列表
    var _stepwiseL = $(".stepwiseList").length;
    if(_stepwiseL > 0){
        $("#stepwiseL").val(_stepwiseL);
    }else{
        $("#stepwiseL").val("");
    }
    //被评价人
    var _peopleByEvaluatedL = $("#selectPeopleByEvaluated .addedPeopleList>div[uId]").length;
    if(_peopleByEvaluatedL>0){
        $("#peopleByEvaluatedL").val(_peopleByEvaluatedL);
    }else{
        $("#peopleByEvaluatedL").val("");
    }
    //评价人
    var _appraiserL = $("#selectAppraiser .addedPeopleList>div[uId]").length;
    if(_appraiserL > 0){
        $("#appraiserL").val(_appraiserL);
    }else{
        $("#appraiserL").val("");
    }
}
String.prototype.replaceAll  = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
};
