window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var isSlideBottom=false,
    scrollPage= -1,
    unExpenseUrl="mobile/expense/getAuditRecord.action?operationType=3",//tab切换，未报销的请求url
    expenseUrl="mobile/expense/getAuditRecord.action?operationType=4",//tab切换，已报销的请求url
    searchUrl="mobile/expense/getAuditRecord.action",//搜索的url
    scrollBottomUrl="mobile/expense/getAuditRecord.action",//滚动到底部的url
    detailUrl="mobile/expense/auditDetailView.action";//列表详情url
$(function(){
    var windowW=window.innerWidth;
    $(".searchToggle").css("marginLeft",(windowW-33)/2);
    var dataUrl=[unExpenseUrl,expenseUrl];
    $(".nav li").removeAttr("data-url");
    //加载数据
    loadData(0,dataUrl[0]);
    //导航切换
    navSwitch(dataUrl);
    //搜索切换
    searchToggle();
    //判断是否滚动到底部，加载更多数据
    judgeScroll();
    //为查看详情注册事件
    lookDetail();
    //搜索
    mySearch();
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
});
//加载
function loadData(index,$dataUrl) {
    $.ajax({
        url : $dataUrl,
        type : 'post',
        data : {
            pageIndex: 1,
            pageSize: window.Meeting_Info.pageSize,
            operationType : (index+1)
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
        success : function(resData, textStatus) {
            $("body").loading({
                move:"hide"
            });
            var data = resData.value;
            var auditList = $("section.recordList").eq(index).children("ul");
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                    $(".recordList ").css("borderTop","1px solid #aeaeae");
                } else {
                    auditList.append("<div class='noSearchRecord'>没有记录......</div>");
                    $(".recordList ").css("borderTopWidth","0")
                }
            } else {
                auditList.append("<div class='noSearchRecord'>没有记录......</div>");
                $(".recordList ").css("borderTopWidth","0")
            }
        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            $("body").toast({
                msg:"获取记录出错，请稍后再试",
                type:2,
                callMode:"func"
            });
        }
    });
}
//导航切换
function navSwitch(dataUrl){
    $(".warp>.nav").navigation({
        //切换时需要做的事
        end:function(index){//index是导航的序号，从0开始计数
            /* 操作搜索框Start */
            var $toggleBtn=$(".searchToggle>i");
            $(".searchWarp form input").val("");
            $toggleBtn.attr("data-flag","0");
            if($toggleBtn.hasClass("fa-chevron-circle-up")){
                $toggleBtn.removeClass("fa-chevron-circle-up").addClass("fa-chevron-circle-down");
            }
            $(".searchWarp form").hide();
            $(".searchWarp").css("borderBottomWidth","0");
            /* 操作搜索框End */
            window.Current_Meeting_Type=index;
            //对应得内容进行切换
            $(".warp>section.recordList:eq("+index+")").removeClass("myHide").siblings("section.recordList").addClass("myHide");
            // 根据导航切换内容
            $(".warp>section.recordList:eq("+index+") ul").html("");
            loadData(index,dataUrl[index]);
            window.Current_Meeting_Not_PageIndex=2;
            window.Current_Meeting_Yes_PageIndex=2;
            isSlideBottom=false;
            scrollPage= -1;
        }
    });
}
//判断是否滚动到底部，加载更多数据
function judgeScroll(){
    $(document).scroll(function(){
        if($(window).scrollTop() + $(window).height()  +40 >= $(document).height()){
            if(!isSlideBottom){
                var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                    window.Current_Meeting_Yes_PageIndex;
                if(scrollPage != pageIndex){
                    scrollPage = pageIndex;
                    if($(".recordList ul").attr("data-isSearch") == "false"){
                        var $data={
                            pageIndex: pageIndex,
                            pageSize: window.Meeting_Info.pageSize,
                            operationType : (window.Current_Meeting_Type+1)
                        }
                    }else{
                        var $data={
                            pageIndex: pageIndex,
                            pageSize: window.Meeting_Info.pageSize,
                            operationType : (window.Current_Meeting_Type+1),
                            flowName : $("#flowName").val(),
                            userName : $("#userName").val(),
                            createDate : $("#createDate").val()
                        };
                        if($("#statusFlag").val() != "-1"){
                            $data.status=$("#statusFlag").val()
                        }
                    }
                    var _type=["?operationType=3","?operationType=4"],
                        _scrollBottomUrl;
                    if(window.Current_Meeting_Type==0)
                        _scrollBottomUrl=scrollBottomUrl+_type[0];
                    if(window.Current_Meeting_Type==1)
                        _scrollBottomUrl=scrollBottomUrl+_type[1];
                    $.ajax({
                        url: _scrollBottomUrl,
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
                            $("body").loading({
                                move:"hide"
                            });
                            if(0 == resData.status){
                                var data = resData.value;
                                var auditList = $(".recordList ul");
                                auditList.attr("data-isSearch","false");
                                if (!!data) {
                                    var size = data.length;
                                    if (size > 0) {
                                        additionRecord(size,data,auditList);
                                        // 增加页码
                                        if(window.Current_Meeting_Type==0)
                                            window.Current_Meeting_Not_PageIndex++;
                                        if(window.Current_Meeting_Type==1)
                                            window.Current_Meeting_Yes_PageIndex++;
                                    } else {
                                        $("body").toast({
                                            msg:"没有更多记录...",
                                            type:1,
                                            callMode:"func"
                                        });
                                        isSlideBottom=true;
                                    }
                                }else {
                                    $("body").toast({
                                        msg:"没有更多记录...",
                                        type:1,
                                        callMode:"func"
                                    });
                                    isSlideBottom=true;
                                }
                            }else{
                                $("body").toast({
                                    msg:resData.errorMsg,
                                    type:1,
                                    callMode:"func"
                                });
                                isSlideBottom=true;
                            }

                        },
                        error:function(){
                            $("body").toast({
                                msg:"获取记录出错，请稍后再试",
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
//查看详情
function lookDetail(){
    $(document).on("click","section li",function(){
        var _status = $(this).find(".auditStatus").text();
        if(_status == "退回"){
            window.location.href=detailUrl+"?id="+$(this).attr("dataid");
        }else{
            window.location.href=detailUrl+"?id="+$(this).attr("dataid");
        }
    });
}
//搜索
function mySearch(){
    $(".searchBtn").click(function(){
        var _searchUrl;
        //校验添加的时间
        if(!timeCheck()){
            return;
        }
        if(window.Current_Meeting_Type == 0){
            _searchUrl=searchUrl+"?operationType=3";
        }else{
            _searchUrl=searchUrl+"?operationType=4";
        }
        var $searchData={
            startDate : $("#startDate").val(),//开始日期
            endDate : $("#endDate").val(),//结束日期
            userName : $.trim($("#userName").val()),//人员姓名
            expenseNum : $.trim($("#number").val())//报销单号
        };
        var $toggleBtn=$(".searchToggle>i");
        $toggleBtn.removeClass("fa-chevron-circle-up").addClass("fa-chevron-circle-down");
        $(".searchWarp form input").val("");
        $(".searchWarp form").hide();
        $toggleBtn.attr("data-flag","0");
        $(".searchWarp").css("borderBottomWidth","0");
        $.ajax({
            url : _searchUrl,
            type : 'post',
            data : $searchData,
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
                if(0 == resData.status){
                    $(".recordList ul").empty();
                    var data = resData.value;
                    var auditList = $(".recordList").eq(window.Current_Meeting_Type).children("ul");
                    auditList.empty();
                    auditList.attr("data-isSearch","true");
                    if (!!data) {
                        var size = data.length;
                        if (size > 0) {
                            additionRecord(size,data,auditList)
                        } else {
                            auditList.append("<div class='noSearchRecord'>没有搜索到相关记录......</div>");
                        }
                    } else {
                        auditList.append("<div class='noSearchRecord'>没有搜索到相关记录......</div>");
                    }
                }else{
                    $("body").toast({
                        msg:resData.errorMsg,
                        type:2,
                        callMode:"func"
                    });
                }

            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                $("body").toast({
                    msg:textStatus,
                    type:2,
                    callMode:"func"
                });
            }
        });
    });

}


function timeCheck(){
    // 对时间校验
    var startDate = $("#startDate").val();
    var endDate = $("#endDate").val();
    if(startDate != ""&& endDate != ""){
        if (startDate > endDate) {
            promptSubmit("开始时间不能晚于结束时间！");
            return false;
        }
    }
    return true;
}
//表单验证提示
function promptSubmit(str,callback) {
    $(".onePrompt").children("span").text(str);
    $(".onePrompt").stop().animate({
        top : "0px"
    }, 500, function() {
        setTimeout(function() {
            $(".onePrompt").animate({
                top : "-40px"
            });
        }, 500,function(){
            if (typeof (callback) == "function") {
                callback(data);
            }
        });
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
                $toggleBtn.attr("data-flag","1");
                $(".searchWarp").css("borderBottom","1px solid #aeaeae");
            }else{
                $toggleBtn.removeClass("fa-chevron-circle-up").addClass("fa-chevron-circle-down");
                $(".searchWarp form input").val("");
                $toggleBtn.attr("data-flag","0");
                $(".searchWarp").css("borderBottomWidth","0");
            }
        });
    });
}
//增加记录
function additionRecord(len,data,domNode){
    var html="";
    for (var i = 0; i < len; i++) {
        if(data[i].status=="1"||data[i].status=="2"||data[i].status=="4"){
            var $str='<span class="auditStatus greenColor singleStatus">'+data[i].statusDisplay+'</span>';
        }else{
            var $str='<span class="auditStatus subColor singleStatus">'+data[i].statusDisplay+'</span>';
        }
        html='<li class="twoLine" dataid='+data[i].id+'>'+
            '<header>'+
            '<span class="mainColor">'+data[i].theme+'</span>'+
            $str+
            '<i class="fa fa-angle-right"></i>'+
            '</header>'+
            '<div class="subColor clear"><span>'+data[i].userName+' '+data[i].screateTime+'提交</span></div>'+
            '</li>'
        domNode.append(html);
    }
}

