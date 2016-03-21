window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_PageIndex=2;
var isSlideBottom=false;
var loadUrl="mobile/loan/getPublicLoanRecord.action?time="+new Date().getTime(),//加载数据URL
    detailUrl="mobile/loan/loanDetailView.action?time="+new Date().getTime(),//详情的URL
    scrollPage= -1,
    isLoadIng = false;//是否正在加载
$(function(){
    //加载数据
    loadData("load");
    //搜索切换
    searchToggle();
    //判断是否滚动到底部，加载更多数据
    judgeScroll("scroll");
    //为查看详情注册事件
    lookDetail();
    //搜索
    mySearch("search");
    //选择时间
    selectTime();
});
//选择时间
function selectTime(){
    var currYear = (new Date()).getFullYear();
    var opt={};
    opt.date = {preset : 'date'};
    opt.datetime = {preset : 'datetime'};
    opt.time = {preset : 'time'};
    opt.default = {
        theme: 'android-ics light', //皮肤样式
        display: 'modal', //显示方式
        mode: 'scroller', //日期选择模式
        dateFormat: 'yyyy-mm-dd',
        lang: 'zh',
        showNow: false,
        nowText: "今天",
        startYear: currYear - 10, //开始年份
        endYear: currYear + 10 //结束年份
    };
    $("#ApplyDate").mobiscroll($.extend(opt['date'], opt['default']));
}
//加载
function loadData(_flag) {
    if(!isLoadIng){
        $.ajax({
            url : loadUrl,
            type : 'post',
            data : {
                pageIndex: 1,
                pageSize: window.Meeting_Info.pageSize,
                operationType : 1
            },
            beforeSend : function() {
                $("body").loading({
                    move:"show"
                });
                isLoadIng = true;
            },
            complete : function() {
                $("body").loading({
                    move:"hide"
                });
                isLoadIng = false;
            },
            success : function(resData) {
				isLoadIng = false;
                additionRecord(resData,_flag);
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
				isLoadIng = false;
                $("body").toast({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                });
            }
        });
    }
    
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
			if(!isLoadIng){
				if(!isSlideBottom){
                if(scrollPage != window.Current_PageIndex){
                    scrollPage = window.Current_PageIndex;
                    if($(".recordList ul").attr("data-isSearch") == "false"){
                        var $data={
                            pageIndex: window.Current_PageIndex,
                            pageSize: window.Meeting_Info.pageSize,
                            operationType : (window.Current_Meeting_Type+1)
                        }
                    }else{
                        var $data={
                            pageIndex: window.Current_PageIndex,
                            pageSize: window.Meeting_Info.pageSize,
                            operationType : (window.Current_Meeting_Type+1),
                            userName : $.trim($("#borrower").val()),//借款人
                            applyDate : $("#ApplyDate").val(),//日期
                            loanNum : $.trim($("#LoanNum").val())//借款单号
                        };
                    }
                    $.ajax({
                        url:loadUrl,
                        data: $data,//要发送的数据
                        type:"post",
                        dataType:"json",
                        beforeSend:function(){
                            $("body").loading({
                                move:"show"
                            });
                            isLoadIng = true;
                        },
                        complete:function(){
                            $("body").loading({
                                move:"hide"
                            });
                            isLoadIng = false;
                        },
                        success:function(resData){
							isLoadIng = false;
                            additionRecord(resData,_flag);
                        },
                        error:function(XMLHttpRequest, textStatus, errorThrown){
							isLoadIng = false;
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
            
        }
    });
}
//搜索
function mySearch(_flag){
    $(".searchBtn").click(function(){
        var $searchData={
            userName : $.trim($("#borrower").val()),//借款人
            applyDate : $("#ApplyDate").val(),//日期
            loanNum : $.trim($("#LoanNum").val())//借款单号
        };
        var $toggleBtn=$(".searchToggle>i");
        $toggleBtn.removeClass("fa-chevron-circle-up").addClass("fa-chevron-circle-down");
        $(".searchWarp form input").val("");
        $(".searchWarp form").hide();
        $toggleBtn.attr("data-flag","0");
		if(!isLoadIng){
			$.ajax({
            url : loadUrl,
            type : 'post',
            data : $searchData,
            beforeSend : function() {
                $("body").loading({
                    move:"show"
                });
				isLoadIng = true;
            },
            complete : function() {
                $("body").loading({
                    move:"hide"
                });
				isLoadIng =false;
            },
            success : function(resData) {
				isLoadIng = false;
                additionRecord(resData,_flag);
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
				isLoadIng = false;
                $("body").toast({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                });
            }
        });
		}
        
    });
}
//增加记录
function additionRecord(_resData,_judgeFlag){
    if(_resData.status == 0){
        /*搜索时，清空列表 Start*/
        if(_judgeFlag == "search"){
            $(".recordList ul").empty();
        }
        /*搜索时，清空列表 End*/
        var data = _resData.value;
        var auditList = $(".recordList ul");
        if(_judgeFlag != "search" ){
            auditList.attr("data-isSearch","false");
        }else{
            auditList.attr("data-isSearch","true");
        }
        if (!!data) {
            var size = data.length;
            if (size > 0) {
                for (var i = 0; i < size; i++) {
                    var html="";
                    var _str='',
                        _li='';
                    if(data[i].status == "10"){
                        _li = '<li class="twoLine lightGrayBg" dataid='+data[i].id+' data-loanType='+data[i].loanType+'>';
                    }else{
                        _li = '<li class="twoLine" dataid='+data[i].id+' data-loanType='+data[i].loanType+'>';
                    }
                    data[i].loanType == "0" ? _str = data[i].subject : _str = data[i].company;
                    html=_li +//id
                        '<header>' +
                        '<span class="mainColor">'+_str+'</span>' +//主题
                        '<span class="auditStatus greenColor">'+data[i].loanTypeDisplay+'</span>' +//类型
                        '<i class="fa fa-angle-right"></i>' +
                        '</header>' +
                        '<div class="subColor clear">' +
                        '<span class="subDate">'+data[i].userName+' '+data[i].sapplyDate+'提交</span>' +//提交日期
                        '<span  class="oddNumber">单号:'+data[i].loanNum+'</span>' +//单号
                        '</div></li>';
                    auditList.append(html);

                    // 增加页码
                }
                if(_judgeFlag == "scroll"){
                    window.Current_PageIndex++;
                }
            } else {
                if(_judgeFlag == "load"){
                    auditList.append("<div class='noRecord'>没有记录......</div>");
                }else if(_judgeFlag == "scroll"){
                    $("body").toast({
                        msg:"没有更多记录...",
                        type:1,
                        callMode:"func"
                    });
                    isSlideBottom=true;
                }else{
                    auditList.append("<div class='noRecord'>没有搜索到相关记录......</div>");
                }
            }
        } else {
            if(_judgeFlag == "load"){
                auditList.append("<div class='noRecord'>没有记录......</div>");
            }else if(_judgeFlag == "scroll"){
                $("body").toast({
                    msg:"没有更多记录...",
                    type:1,
                    callMode:"func"
                });
                isSlideBottom=true;
            }else{
                auditList.append("<div class='noRecord'>没有搜索到相关记录......</div>");
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
    $(document).on("click","section li",function(){
        window.location.href=detailUrl+"?id="+$(this).attr("dataid");
    });
}

