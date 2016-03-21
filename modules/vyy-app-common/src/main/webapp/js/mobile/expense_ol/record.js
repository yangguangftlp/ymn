window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var isSlideBottom=false,
	scrollPage= -1;
$(function(){
    var dataUrl=[$(".nav li").eq(0).attr("data-url"),$(".nav li").eq(1).attr("data-url")];
    var chaining=$("input#go-Detail-url").val();
    loadData($(".navSelected"), 0,$(".navSelected").attr("data-url"));
    $(".nav li").removeAttr("data-url");
    //导航切换
    $(".warp>.nav").navigation({
        //切换时需要做的事
        end:function(index){//index是导航的序号，从0开始计数
            window.Current_Meeting_Type=index;
            //对应得内容进行切换
            $(".warp>section:eq("+index+")").removeClass("myHide").siblings("section").addClass("myHide");
            // 根据导航切换内容
            $(".warp>section:eq("+index+") ul").empty();
            loadData($(".nav li").eq(index), index,dataUrl[index]);
            window.Current_Meeting_Not_PageIndex=2;
            window.Current_Meeting_Yes_PageIndex=2;
            isSlideBottom=false;
            scrollPage= -1;
        }
    });

    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);

    //为查看详情注册事件
    lookDetail(chaining);
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
});
function loadData($this, index,$dataUrl) {
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
            var auditList = $("section").eq(index).children("ul");
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                } else {
                    auditList.append("<div class='noRecord'>没有记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord'>没有记录......</div>");
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

//判断是否滚动到底部，加载更多数据
function judgeScroll($dataUrl){
    $(document).scroll(function(){
        if($(window).scrollTop() + $(window).height() +40 >= $(document).height()){
            if(!isSlideBottom){
                var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                    window.Current_Meeting_Yes_PageIndex;
                if(scrollPage != pageIndex){
                    scrollPage = pageIndex;
                    $.ajax({
                        url:$dataUrl[window.Current_Meeting_Type],
                        data: {
                            pageIndex: pageIndex,
                            pageSize: window.Meeting_Info.pageSize,
                            operationType : (window.Current_Meeting_Type+1)
                        },//要发送的数据
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
                            var data = resData.value;
                            var auditList = $("section").eq(window.Current_Meeting_Type).children("ul");
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
                            } else {
                                $("body").toast({
                                    msg:"没有更多记录...",
                                    type:1,
                                    callMode:"func"
                                });
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
            '<div class="subColor clear"><span>'+data[i].screateTime+'提交</span></div>'+
            '</li>'
        domNode.append(html);
    }
}
//查看详情
function lookDetail($chaining){
    $(document).on("click","section li",function(){
        window.location.href=$chaining+"?id="+$(this).attr("dataid")+"#flag=0";
    });
}
