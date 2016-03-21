window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var listModule= 0,//0：只有状态选择，1：只有搜索，2：机油状态又有搜索
    isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    unsolvedUrl = "mobile/feedback/getFeedbackRecord.action?operationType=0&time="+new Date().getTime(),
    solvedUrl = "mobile/feedback/getFeedbackRecord.action?operationType=1&time="+new Date().getTime(),
    dataUrl = [unsolvedUrl,solvedUrl];
$(function(){
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
                    var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                        window.Current_Meeting_Yes_PageIndex;
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
    var data = _resData.value.data;
    var auditList = $("section ul");
    if(0 == _resData.status){
        if(_operatingStatus == 0){//加载
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    addRecord(data,size,auditList)
                } else {
                    auditList.append("<div class='noRecord'>没有记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord'>没有记录......</div>");
            }
        }else
        if(_operatingStatus == 1){//滚动
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    addRecord(data,size,auditList);
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
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex=2;
    window.Current_Meeting_Underway_PageIndex=2;
    window.Current_Meeting_Yes_PageIndex=2;
}
//增加记录
function addRecord(data,size,auditList){
    for (var i = 0; i < size; i++) {
        var $status="";
        if(!!data[i].sendTime){
            $status='<div class="subColor clear"><span>'+data[i].sendTime+'解决</span></div>'
        }else{
            $status='<div class="subColor clear"><span>'+data[i].screateTime+'创建</span></div>'
        }
        var html='<li class="twoLine" dataid='+data[i].id+' status='+data[i].status+'>'+
            '<header>'+
            '<span class="mainColor">'+data[i].problem+'</span>'+
            '<i class="fa fa-angle-right"></i>'+
            '</header>'+
            $status+
            '</li>';
        auditList.append(html);
    }
}
//查看详情
function lookDetail($chaining){
    $(document).on("click","section li",function(){
        window.location.href=$chaining[window.Current_Meeting_Type]+"?id="+$(this).attr("dataid");
    });
}
