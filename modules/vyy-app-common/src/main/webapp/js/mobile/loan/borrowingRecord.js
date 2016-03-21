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
    unDealWithUrl="mobile/loan/getLoanRecord.action?operationType=0&time="+new Date().getTime(),//tab切换，未处理的请求url
    DealWithUrl="mobile/loan/getLoanRecord.action?operationType=1&time="+new Date().getTime(),//tab切换，已处理的请求url
    detailUrl="mobile/loan/loanDetailView.action.action",//列表详情url
    dataUrl=[unDealWithUrl,DealWithUrl];
$(function(){
	//基础参数
    basicTransferData();
    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);
    //为查看详情注册事件
    lookDetail();
    //状态的伸缩变化
    retractableNav(listModule);
    //请求数据
    requestData(listModule,dataUrl);
    //重新加载页面的时候，判断是否有选中项
    judgeDataStatus(listModule,dataUrl);
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
});
//判断是否滚动到底部，加载更多数据
function judgeScroll($dataUrl){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document)){
            if(!isSlideBottom){
                var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                    window.Current_Meeting_Yes_PageIndex;
                var _data={
                    pageIndex: pageIndex,
                        pageSize: window.Meeting_Info.pageSize
                };
                requestDataAjax($dataUrl[window.Current_Meeting_Type],_data,1);
            }
        }
    });
}
//数据渲染
function renderingData(_resData,_operatingStatus){
    var data = _resData.value;
    var auditList = $("section ul");
    if(0 == _resData.status){
        if(_operatingStatus == 0){//加载
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                } else {
                    auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord  whiteBg'>没有记录......</div>");
            }
        }else
        if(_operatingStatus == 1){//滚动
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
    $(document).on("click","section li",function(){
        //$(".nav li:eq(0)").trigger("click");
        window.location.href=detailUrl+"?id="+$(this).attr("dataid")+"&loanType="+$(this).attr("data-loanType")+"#flag=0";
    });
}
//增加记录
function additionRecord(len,data,domNode){
    var html="";
    for (var i = 0; i < len; i++) {
        var _str='',
            _li;
        if(data[i].status == "10"){
            _li = '<li class="twoLine lightGrayBg" dataid='+data[i].id+' data-loanType='+data[i].loanType+'>'
        }else{
            _li = '<li class="twoLine" dataid='+data[i].id+' data-loanType='+data[i].loanType+'>'
        }
        data[i].loanType == "0" ? _str = data[i].subject : _str = data[i].company;
        html=_li+//id
            '<header>' +
            '<span class="mainColor">'+_str+'</span>' +//主题
            '<span class="auditStatus greenColor">'+data[i].loanTypeDisplay+'</span>' +//类型
            '<i class="fa fa-angle-right"></i>' +
            '</header>' +
            '<div class="subColor clear">' +
            '<span>'+data[i].sapplyDate+'提交</span>' +//提交日期
            '<span>单号:'+data[i].loanNum+'</span>' +//单号
            '</div></li>';
        domNode.append(html);
    }
}
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex=2;
    window.Current_Meeting_Yes_PageIndex=2;
}


