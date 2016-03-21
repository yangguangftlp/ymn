window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_PageIndex=2;
var isSlideBottom=false,
	scrollPage= -1;
var loadUrl="mobile/appraisal/getAppraisalDetailRecord.action?time="+new Date().getTime(),//加载数据URL
    detailUrl="mobile/appraisal/appraisalView.action";//详情的URL
$(function(){
    //加载数据
    loadData("load");
    //判断是否滚动到底部，加载更多数据
    judgeScroll("scroll");
    //为查看详情注册事件
    lookDetail();
});
//加载
function loadData(_flag) {
    $.ajax({
        url : loadUrl,
        type : 'post',
        data : {
            pageIndex: 1,
            pageSize: 40,
            operationType : 1,
            id : $("#appraisalId").val()
        },
        beforeSend : function() {
            $("body").loading({
                move:"show"
            });
        },
        complete : function() {
            $("body").loading({
                move:"hide"
            });
        },
        success : function(resData) {
            additionRecord(resData,_flag);
        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            $("body").toast({
                msg:"系统异常，请稍后再试",
                type:2,
                callMode:"func"
            });
        }
    });
}
//搜索切换
function searchToggle(){
    var $toggleBtn=$(".searchToggle>i");
    $toggleBtn.click(function(){
        $(".searchWarp form").animate({
            height:'toggle'
        },function(){
            if("0" == $toggleBtn.attr("data-flag")){
                $toggleBtn.removeClass("fa-chevron-circle-down").addClass("fa-chevron-circle-up");
                $(".searchWarp form select").val("-1");
                $toggleBtn.attr("data-flag","1")
            }else{
                $toggleBtn.removeClass("fa-chevron-circle-up").addClass("fa-chevron-circle-down");
                $toggleBtn.attr("data-flag","0")
                $(".searchWarp form input").val("");
            }
        });
    });
}
//判断是否滚动到底部，加载更多数据
function judgeScroll(_flag){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document)){
            if(!isSlideBottom){
                if($(".recordList ul").attr("data-isSearch") == "false"){
                    var $data={
                        pageIndex: window.Current_PageIndex,
                        pageSize: 40,
                        operationType : (window.Current_Meeting_Type+1),
                        id : $("#appraisalId").val()
                    }
                }else{
                    var $data={
                        pageIndex: window.Current_PageIndex,
                        pageSize: 40,
                        operationType : (window.Current_Meeting_Type+1),
                        theme : $.trim($("#theme").val()),//主题
                        startDate : $("#createDate").val(),//发起时间
                        endDate : $("#endDate").val(),//结束时间
                        id : $("#appraisalId").val()
                    };
                }
                if(scrollPage != window.Current_PageIndex){
                    scrollPage = window.Current_PageIndex;
                    $.ajax({
                        url:loadUrl,
                        data: $data,//要发送的数据
                        type:"post",
                        dataType:"json",
                        beforeSend:function(){
                            $("body").loading({
                                move:"show"
                            });
                        },
                        complete:function(){
                            $("body").loading({
                                move:"hide"
                            });
                        },
                        success:function(resData){
                            additionRecord(resData,_flag);
                        },
                        error:function(XMLHttpRequest, textStatus, errorThrown){
                            $("body").toast({
                                msg:"系统异常，请稍后再试",
                                type:2,
                                callMode:"func"
                            });
                        }
                    });
                }
                
            }
        }
    });
}
//增加记录
function additionRecord(_resData,_judgeFlag){
    if(_resData.status == 0){
        var data = _resData.value;
        var auditList = $(".evaStaffList");
        if (!!data) {
            var html = "";
            var size = data.length;
            if (size > 0) {
                for (var i = 0; i < size; i++) {
                    var _countStr='';
                    if( data[i].Count > 0){
                        _countStr='<b class=apprIcon></b>';
                    }else{
                        _countStr='<b class=unApprIcon></b>';
                    }
                    var html='<div id='+data[i].ID+' EntityID='+$("#appraisalId").val()+'>' +
                        '<div AccountID='+data[i].AccountID+' class="everyone">' +
                        '<img src='+data[i].Avatar+'>' +
                        _countStr +
                        '<span>'+data[i].AccountName+'</span>' +
                        '</div>' +
                        '</div>';
                    auditList.append(html);
                }
                // 增加页码
                if(_judgeFlag == "scroll"){
                    window.Current_PageIndex++;
                }
            } else {
                if(_judgeFlag == "load"){
                    $("body").append("<div class='noRecord'>评价已被取消......</div>");
                }else if(_judgeFlag == "scroll"){
                    $("body").toast({
                        msg:"没有更多记录...",
                        type:1,
                        callMode:"func"
                    });
                    isSlideBottom=true;
                }
            }
        } else {
            if(_judgeFlag == "load"){
                $("body").append("<div class='noRecord'>评价已被取消......</div>");
            }else if(_judgeFlag == "scroll"){
                $("body").toast({
                    msg:"没有更多记录...",
                    type:1,
                    callMode:"func"
                });
                isSlideBottom=true;
            }
        }
    }else{
        $("body").toast({
            msg:_resData.errorMsg,
            type:2,
            callMode:"func"
        });
    }

}
//查看详情
function lookDetail(){
    $(document).on(touchEv,".evaStaffList .everyone",function(){
        window.location.href=detailUrl+"?eaId="+$(this).parent().attr("id")+"&appraisalId="+$(this).parent().attr("EntityID")+"#flag=0";
    });
}

