//请求地址 查看地址

window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Week_PageIndex=2;
window.Current_Meeting_Month_PageIndex=2;
window.Current_Meeting_Totality_PageIndex=2;
var isSlideBottom=false,
	scrollPage= -1;
$(function(){
    var dataUrl=[$(".nav li").eq(0).attr("data-url"),$(".nav li").eq(1).attr("data-url"),$(".nav li").eq(2).attr("data-url")];
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
            $(".warp>section:eq("+index+") ul").html("");
            loadData($(".nav li").eq(index), index,dataUrl[index]);
            window.Current_Meeting_Week_PageIndex=2;
            window.Current_Meeting_Month_PageIndex=2;
            window.Current_Meeting_Totality_PageIndex=2;
            isSlideBottom=false;
            scrollPage= -1;
        }
    });

    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);
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
            //$this.attr("flag", "1");
            var data = resData.value;
            var auditList = $("section").eq(index).children("ul");
            if (!!data) {
                var html = "";
                var size = data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        html = '<li class="rankingList">'+
                            '<span class="feedbackNumIcon"><img src="images/app_icon_'+data[i].AppId+'.png" height="50"></span>'+
                            '<span class="biofeedback">'+data[i].AppName+'</span>'+
                            '<span class="feedbackNum">使用次数：'+data[i].Total+'</span>'+
                            '</li>';
                        auditList.append(html);
                    }
                } else {
                    auditList.append("<div class='noRecord'>没有记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord'>没有记录......</div>");
            }
        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            // alert(JSON.stringify(errorThrown));
        }
    });
}

//判断是否滚动到底部，加载更多数据
function judgeScroll($dataUrl){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document)){
            if(!isSlideBottom){
                var pageIndex;
                if(window.Current_Meeting_Type == 0){
                    pageIndex=window.Current_Meeting_Week_PageIndex;
                }else if(window.Current_Meeting_Type == 1){
                    pageIndex=window.Current_Meeting_Month_PageIndex;
                }else{
                    pageIndex=window.Current_Meeting_Totality_PageIndex;
                }
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
                            addRecord(resData);
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
function addRecord(resData){
    var data = resData.value;
    var auditList = $("section").eq(window.Current_Meeting_Type).children("ul");
    if (!!data) {
        var html = "";
        var size = data.length;
        if (size > 0) {
            for (var i = 0; i < size; i++) {
                html = '<li class="rankingList">'+
                    '<span class="feedbackNumIcon"><img src="images/app_icon_'+data[i].AppId+'.png" height="50"></span>'+
                    '<span class="biofeedback">'+data[i].AppName+'</span>'+
                    '<span class="feedbackNum">使用次数：'+data[i].Total+'</span>'+
                    '</li>';
                auditList.append(html);
            }
            // 增加页码
            if(window.Current_Meeting_Type==0)
                window.Current_Meeting_Week_PageIndex++;
            if(window.Current_Meeting_Type==1)
                window.Current_Meeting_Month_PageIndex++;
            if(window.Current_Meeting_Type==2)
                window.Current_Meeting_Totality_PageIndex++;
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
        isSlideBottom=true;
    }

}