var loadUrl="mobile/appraisal/getEmployeeRankingRecord.action?id="+$("#appraisalId").val()+"&time="+new Date().getTime(),
    detailUrl="mobile/appraisal/appraisalResultView.action";
window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_PageIndex=2;
var isSlideBottom=false,
	scrollPage= -1;
$(function(){
    //加载数据
    loadData(true);
    //判断是否滚动到底部，加载更多数据
    judgeScroll(false);
    //详情
    lookDetail();
});
function loadData(_flag) {
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
        },
        complete : function() {
            $("body").loading({
                move:"hide"
            });
        },
        success : function(resData, textStatus) {
            addRecord(resData,_flag);
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

//判断是否滚动到底部，加载更多数据
function judgeScroll(_flag){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document)){
            if(!isSlideBottom){
                var pageIndex=window.Current_PageIndex;
                if(scrollPage != pageIndex){
                    scrollPage = pageIndex;
                    $.ajax({
                        url:loadUrl,
                        data: {
                            pageIndex: pageIndex,
                            pageSize: window.Meeting_Info.pageSize,
                            operationType : 1
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
                            addRecord(resData,_flag);
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
function addRecord(resData,loadFlag){
    var data = resData.value;

    var auditList = $("section ul");
    if (!!data) {
        var html = "";
        var size = data.length;
        if (size > 0) {
            for (var i = 0; i < size; i++) {
                var ranking=data[i].rank,//排名次序
                    _str,
                    _li;
                if(data[i].accountId == $("#uId").val()){
                    _li='<li class="rankingList" style="background: #d0d0d0;" id='+$("#appraisalId").val()+' userId='+data[i].accountId+'>'
                }else{
                    _li='<li class="rankingList" id='+$("#appraisalId").val()+' userId='+data[i].accountId+'>';
                }
                if(ranking == 1){
                    _str='<img src="images/goldMedal.png" height="50">'
                }else if(ranking == 2){
                    _str='<img src="images/silverMedal.png" height="50">'
                }else if(ranking == 3){
                    _str='<img src="images/copperMedal.png" height="50">'
                }else{
                    _str='<i>'+ranking+'</i>'
                }
                html =_li+
                    '<span>' +
                    _str+
                    '</span>' +
                    '<span>'+data[i].accountName+'</span>' +
                    '<span>平均得分：'+data[i].avgScore+'</span>' +
                    '</li>';
                auditList.append(html);
            }
            if(!loadFlag){
                // 增加页码
                window.Current_PageIndex++;
            }

        } else {
            if(loadFlag){
                auditList.append("<div class='noRecord'>没有记录......</div>");
            }else{
                $("body").loading({
                    move:"hide"
                });
                $("body").toast({
                    msg:"没有更多记录...",
                    type:1,
                    callMode:"func"
                });
                isSlideBottom=true;
            }
        }
    } else {
        if(loadFlag){
            auditList.append("<div class='noRecord'>没有记录......</div>");
        }else{
            $("body").loading({
                move:"hide"
            });
            $("body").toast({
                msg:"没有更多记录...",
                type:1,
                callMode:"func"
            });
            isSlideBottom=true;
        }
    }
}
//详情
function lookDetail(){
    $(document).on(touchEv,"ul>li",function(){
        if(!!$("#uId").val()){
            if($(this).attr("userId") == $("#uId").val()){
                window.location.href=detailUrl+"?id="+$(this).attr("id")+"&userId="+$(this).attr("userId");
            }else{
                $("body").toast({
                    msg:"只可以查看个人排行详情哦",
                    type:1,
                    callMode:"func"
                });
            }
        }else{
            window.location.href=detailUrl+"?id="+$(this).attr("id")+"&userId="+$(this).attr("userId");
        }
    });
}
