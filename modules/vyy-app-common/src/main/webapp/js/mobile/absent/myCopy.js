window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_PageIndex=2;
var isSlideBottom=false,
	scrollPage= -1,
    isLoadIng = false,//是否正在加载
    absentUrl="mobile/absent/getccAbsentRecord.action?time="+new Date().getTime();
$(function(){
    //加载数据
    loadData();
    //判断是否滚动到底部，加载更多数据
    judgeScroll();
    //为查看详情注册事件
    lookDetail();
});
//加载
function loadData() {
    if(!isLoadIng){
        $.ajax({
            url : absentUrl,
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
                var data = resData.value;
                var auditList = $("section ul");
                if (!!data) {
                    var html = "";
                    var size = data.length;
                    if (size > 0) {
                        additionRecord(size,data,auditList)
                    } else {
                        auditList.append("<div class='noRecord'>没有记录......</div>");
                    }
                } else {
                    auditList.append("<div class='noRecord'>没有记录......</div>");
                }
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
//判断是否滚动到底部，加载更多数据
function judgeScroll(){
    $(document).scroll(function(){
        if($(window).scrollTop() + $(window).height() +40 >= $(document).height()){
            if(!isLoadIng){
                if(!isSlideBottom){
                    if(scrollPage != window.Current_PageIndex){
                        scrollPage = window.Current_PageIndex;
                        $.ajax({
                            url:absentUrl,
                            data: {
                                pageIndex: window.Current_PageIndex,
                                pageSize: window.Meeting_Info.pageSize,
                                operationType : 1
                            },//要发送的数据
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
                                $("body").loading({
                                    move:"hide"
                                });
                                var data = resData.value;
                                var auditList = $(".recordList ul");
                                if (!!data) {
                                    var size = data.length;
                                    if (size > 0) {
                                        additionRecord(size,data,auditList)
                                        // 增加页码
                                        window.Current_PageIndex++;
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
                            },
                            error:function(){
                                isLoadIng = false;
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


        }
    });
}
//查看详情
function lookDetail(){
    $(document).on("click","section li",function(){
        window.location.href="mobile/absent/absentDetailView.action?id="+$(this).attr("dataid")+"&flag=1";
    	//location.replace("absent/absentDetailView.action?id="+$(this).attr("dataid")+"&flag=1"+"#cc");
    });
}
//增加记录
function additionRecord(len,data,domNode){
    var html="";
    for (var i = 0; i < len; i++) {
        if(data[i].status=="3"){
            var $str='<span class="emphasisFont subColor">'+data[i].statusDisplay+'</span>';
        }else{
            var $str='<span class="emphasisFont greenColor">'+data[i].statusDisplay+'</span>';
        }
        html='<li class="twoLine" dataid='+data[i].id+'>' +
            '<header>' +
            '<span>'+data[i].sbeginTime+"~"+data[i].sEndTime+'</span>' +
            '<span class="auditStatus greenColor">'+data[i].absentTypeName+'</span>' +
            '<i class="fa fa-angle-right"></i>' +
            '</header>' +
            '<div class="subColor clear">' +
            '<span>'+data[i].userName+'</span>' +
            $str +
            '</div></li>'
        domNode.append(html);
    }
}

