window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var listModule= 0,//0：只有状态选择，1：只有搜索，2：机油状态又有搜索
    isSlideBottom=false,
    scrollPage= -1,
    isLoadIng = false,//是否正在加载
    dataUrl=["mobile/approval/getAuditRecord.action?time="+new Date().getTime(),"mobile/approval/getBeAuditRecord.action?time="+new Date().getTime()];
$(function(){
    var chaining=[$("input#go-url").val(),$("input#go-Detail-url").val()];
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
            if(!isSlideBottom){
                var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                    window.Current_Meeting_Yes_PageIndex;
                var _data ={
                    pageIndex: pageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    operationType : (window.Current_Meeting_Type+1)
                };
                requestDataAjax($dataUrl[window.Current_Meeting_Type],_data,1);
            }
        }
    });
}
//查看详情
function lookDetail($chaining){
    $(document).on("click","section li",function(){
        window.location.href = $chaining[window.Current_Meeting_Type]+"?id="+$(this).attr("dataid")+"&flag=0";
    });
}
//数据渲染
function renderingData(_resData,_operatingStatus){
    var data = _resData.value;
    var auditList = $("section ul");
    if(_operatingStatus == 0){//加载
        if (!!data) {
            var html = "";
            var size = data.length;
            if (size > 0) {
                for (var i = 0; i < size; i++) {
                    var _li,$str;
                    if(data[i].cstatus == 1){
                        _li='<li class="twoLine lightGrayBg" dataid='+data[i].id+' flowType='+data[i].flowType+'>'
                    }else{
                        _li='<li class="twoLine" dataid='+data[i].id+' flowType='+data[i].flowType+'>';
                    }
                    if(data[i].status=="3"){
                        $str='<span class="auditStatus subColor singleStatus">'+data[i].statusDisplay+'</span>';
                    }else{
                        $str='<span class="auditStatus greenColor singleStatus">'+data[i].statusDisplay+'</span>';
                    }
                    html=_li+
                        '<header>'+
                        '<span class="mainColor">'+data[i].flowName+'</span>'+
                        $str+
                        '<i class="fa fa-angle-right"></i>'+
                        '</header>'+
                        '<div class="subColor clear"><span>'+data[i].userName+' '+data[i].screateTime+'提交</span></div>'+
                        '</li>'
                    auditList.append(html);
                }
            } else {
                auditList.append("<div class='noRecord'>没有记录......</div>");
            }
        } else {
            auditList.append("<div class='noRecord'>没有记录......</div>");
        }
    }else
    if(_operatingStatus == 1){//滚动
        if (!!data) {
            var html = "";
            var size = data.length;
            if (size > 0) {
                for (var i = 0; i < size; i++) {
                    var _li,$str;
                    if(data[i].cstatus == 1){
                        _li='<li class="twoLine lightGrayBg" dataid='+data[i].id+' flowType='+data[i].flowType+'>'
                    }else{
                        _li='<li class="twoLine" dataid='+data[i].id+' flowType='+data[i].flowType+'>';
                    }
                    if(data[i].status=="3"){
                        $str='<span class="auditStatus subColor singleStatus">'+data[i].statusDisplay+'</span>';
                    }else{
                        $str='<span class="auditStatus greenColor singleStatus">'+data[i].statusDisplay+'</span>';
                    }
                    html=_li+
                        '<header>'+
                        '<span class="mainColor">'+data[i].flowName+'</span>'+
                        $str+
                        '<i class="fa fa-angle-right"></i>'+
                        '</header>'+
                        '<div class="subColor clear"><span>'+data[i].userName+' '+data[i].screateTime+'提交</span></div>'+
                        '</li>'
                    auditList.append(html);
                }
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
}
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex = 2;
    window.Current_Meeting_Yes_PageIndex = 2;
}
