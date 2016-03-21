window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Underway_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
window.Current_Meeting_All_PageIndex=2;
var listModule= 0,//0：只有状态选择，1：只有搜索，2：机油状态又有搜索
    isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    dataUrl=["mobile/approval/getApprovalRecord.action?operationType=0&time="+new Date().getTime(), "mobile/approval/getApprovalRecord.action?operationType=1&time="+new Date().getTime(), "mobile/approval/getApprovalRecord.action?operationType=2&time="+new Date().getTime()];
$(function(){
    //获得跳转链接
    var chaining=[$("input#go-url").val(),$("input#go-ts-url").val()];
    //基础参数
    basicTransferData("operationType",1);
    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);
    //为查看详情注册事件
    lookDetail(chaining);
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
    //状态的伸缩变化
    retractableNav(listModule);
    //请求数据
    requestData(listModule,dataUrl);
    //重新加载页面的时候，判断是否有选中项
    judgeDataStatus(listModule,dataUrl);
    
});
//判断是否滚动到底部，加载更多数据
function judgeScroll($dataUrl){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document)){
            if(!isLoadIng){
                if(!isSlideBottom){
                    var pageIndex;
                    if(window.Current_Meeting_Type==0){
                        pageIndex=window.Current_Meeting_Not_PageIndex;
                    }else if(window.Current_Meeting_Type==1){
                        pageIndex=window.Current_Meeting_Underway_PageIndex;
                    }else if(window.Current_Meeting_Type==2){
                        pageIndex=window.Current_Meeting_Yes_PageIndex;
                    }else{
                        pageIndex=window.Current_Meeting_All_PageIndex;
                    }
                    var _data = {
                        pageIndex: pageIndex,
                        pageSize: window.Meeting_Info.pageSize,
                        operationType : (window.Current_Meeting_Type+1)
                    };
                    requestDataAjax($dataUrl[window.Current_Meeting_Type],_data,1);
                }
            }

        }
    });
}
//数据渲染
function renderingData(_resData,_operatingStatus){
    var data = _resData.value;
    var auditList = $("section ul");
    if(_operatingStatus == 0){//加载
        if(_resData.status == 0){
            if (!!data) {
                var html = "";
                var size = data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        var _li,$str;
                        if(data[i].cstatus == 1){
                            _li='<li class="twoLine lightGrayBg" dataid='+data[i].id+' status='+data[i].status+' flowType='+data[i].flowType+'>';
                        }else{
                            _li='<li class="twoLine" dataid='+data[i].id+' status='+data[i].status+' flowType='+data[i].flowType+'>';
                        }
                        if(data[i].status=="3"){
                            $str='<span class="auditStatus subColor singleStatus">'+data[i].statusDisplay+'</span>'
                        }else{
                            $str='<span class="auditStatus greenColor singleStatus">'+data[i].statusDisplay+'</span>'
                        }
                        html=_li+
                            '<header>'+
                            '<span class="mainColor">'+data[i].flowName+'</span>'+
                            $str+
                            '<i class="fa fa-angle-right"></i>'+
                            '</header>'+
                            '<div class="subColor">'+data[i].userName+'&nbsp;'+data[i].screateTime+'提交</div>'+
                            '</li>';
                        auditList.append(html);
                    }
                } else {
                    auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
            }
        }else{
            $("body").toast({
                msg:_resData.errorMsg,
                type:2,
                callMode:"func"
            });
        }

    }else
    if(_operatingStatus == 1){//滚动
        if(_resData.status == 0){
            if (!!data) {
                var html = "";
                var size = data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        var _li,$str;
                        if(data[i].cstatus == 1){
                            _li='<li class="twoLine lightGrayBg" dataid='+data[i].id+' status='+data[i].status+' flowType='+data[i].flowType+'>';
                        }else{
                            _li='<li class="twoLine" dataid='+data[i].id+' status='+data[i].status+' flowType='+data[i].flowType+'>';
                        }
                        if(data[i].status=="3"){
                            $str='<span class="auditStatus subColor singleStatus">'+data[i].statusDisplay+'</span>'
                        }else{
                            $str='<span class="auditStatus greenColor singleStatus">'+data[i].statusDisplay+'</span>'
                        }
                        html=_li+
                            '<header>'+
                            '<span class="mainColor">'+data[i].flowName+'</span>'+
                            $str+
                            '<i class="fa fa-angle-right"></i>'+
                            '</header>'+
                            '<div class="subColor">'+data[i].userName+'&nbsp;'+data[i].screateTime+'提交</div>'+
                            '</li>';
                        auditList.append(html);
                    }
                    // 增加页码
                    if(window.Current_Meeting_Type == 0)
                        window.Current_Meeting_Not_PageIndex++;
                    if(window.Current_Meeting_Type == 1)
                        window.Current_Meeting_Underway_PageIndex++;
                    if(window.Current_Meeting_Type == 2)
                        window.Current_Meeting_Yes_PageIndex++;
                    if(window.Current_Meeting_Type == 3)
                        window.Current_Meeting_All_PageIndex++
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
        }else{
            $("body").toast({
                msg:_resData.errorMsg,
                type:2,
                callMode:"func"
            });
        }

    }
}
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex=2;
    window.Current_Meeting_Underway_PageIndex=2;
    window.Current_Meeting_Yes_PageIndex=2;
}
//查看详情
function lookDetail($chaining){
    $(document).on("click","section li",function(){
        if("0" == $(this).attr("status")){
            window.location.href=$chaining[1]+"?id="+$(this).attr("dataid")+"&approvalType="+$(this).attr("flowType")+"&flag=0";
        }else{
            window.location.href=$chaining[0]+"?id="+$(this).attr("dataid")+"&flag=0";
        }

    });
}