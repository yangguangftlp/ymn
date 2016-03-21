window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var listModule= 2,//0：只有状态选择，1：只有搜索，2：机油状态又有搜索
    isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    searchPageIndex= 1,//搜索的页码（新）
    loadOperationType = "load",//操作类型（新）
    unDealWithUrl="mobile/loan/getShAuditRecord.action.action?time="+new Date().getTime(),//tab切换，未处理的请求url
    DealWithUrl="mobile/loan/getShBeAuditRecord.action.action?time="+new Date().getTime(),//tab切换，已处理的请求url
//searchUrl="",//搜索的url
    detailUrl="mobile/loan/shAuditDetailView.action",//未处理详情url
    unDetailUrl="mobile/loan/loanDetailView.action",//已处理详情
    dataUrl=[unDealWithUrl,DealWithUrl];
$(function(){
    var myDetailUrl=[detailUrl,unDetailUrl];
    //基础参数
	basicTransferData("operationType",1);
    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);
    //选择时间
    selectTime();
    //请求数据
    requestData(listModule,dataUrl);
    //为查看详情注册事件
    lookDetail(myDetailUrl);
    //搜索
    mySearch(listModule);
    //重新加载页面的时候，判断是否有选中项
    judgeDataStatus(listModule,dataUrl);
    //状态的伸缩变化
    retractableNav(listModule);
    //重置搜索
    resetSearch();
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
});
//判断是否滚动到底部，加载更多数据
function judgeScroll(_dataUrl){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document)){
            if(!isSlideBottom){
                var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                    window.Current_Meeting_Yes_PageIndex;
                var $data = {};
                if(loadOperationType == "load"){
                    $data={
                        //pageIndex: pageIndex,
                        pageSize: window.Meeting_Info.pageSize,
                        operationType : (window.Current_Meeting_Type+1)
                    }
                }else{
                    $data={
                        //pageIndex: pageIndex,
                        pageSize: window.Meeting_Info.pageSize,
                        operationType : (window.Current_Meeting_Type+1),
                        userName : $.trim($("#borrower").val()),//借款人
                        applyDate : $("#ApplyDate").val(),//日期
                        loanNum : $.trim($("#LoanNum").val())//借款单号
                    };
                }
                if(loadOperationType == "search"){//（新）
                    $data.pageIndex = searchPageIndex;//（新）
                }else{//（新）
                    $data.pageIndex = pageIndex;//（新）
                }
                requestDataAjax(_dataUrl[window.Current_Meeting_Type],$data,1)
            }
        }
    });
}
//查看详情
function lookDetail(_myDetailUrl){
    $(document).on("click","section li",function(){
        //$(".nav li:eq(0)").trigger("click");
        window.location.href=_myDetailUrl[window.Current_Meeting_Type]+"?id="+$(this).attr("dataid")+"&loanType="+$(this).attr("data-loanType");
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
                    $(".recordList ").css("borderTop","1px solid #aeaeae");
                } else {
                    auditList.append("<div class='noSearchRecord whiteBg'>没有记录......</div>");
                    $(".recordList ").css("borderTopWidth","0")
                }
            } else {
                auditList.append("<div class='noSearchRecord whiteBg'>没有记录......</div>");
                $(".recordList ").css("borderTopWidth","0")
            }
        }else
        if(_operatingStatus == 1){//滚动
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                    // 增加页码
                    /*if(window.Current_Meeting_Type==0)
                     window.Current_Meeting_Not_PageIndex++;
                     if(window.Current_Meeting_Type==1)
                     window.Current_Meeting_Yes_PageIndex++;*/
                    if(loadOperationType == "load"){
                        if(window.Current_Meeting_Type==0){
                            window.Current_Meeting_Not_PageIndex++;
                        }
                        if(window.Current_Meeting_Type==1){
                            window.Current_Meeting_Yes_PageIndex++;
                        }
                    }else if(loadOperationType == "search"){
                        searchPageIndex++;//（新）
                    }

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
        }else
        if(_operatingStatus == 2){//搜索
            searchPageIndex = 2;
            isSlideBottom = false;
            auditList.empty();
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList)
                } else {
                    auditList.append("<div class='noSearchRecord whiteBg'>没有搜索到相关记录......</div>");
                    $(".recordList ").css("borderTopWidth","0")
                }
            } else {
                auditList.append("<div class='noSearchRecord whiteBg'>没有搜索到相关记录......</div>");
                $(".recordList ").css("borderTopWidth","0")
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
//收集搜索的条件
function getSearchData(){
    var _searchData={
        pageIndex: 1,
        pageSize: window.Meeting_Info.pageSize,
        operationType : (window.Current_Meeting_Type+1),
        userName : $.trim($("#borrower").val()),//借款人
        applyDate : $("#ApplyDate").val(),//日期
        loanNum : $.trim($("#LoanNum").val())//借款单号
    };
    return _searchData;
}
//得到筛选的数据
function getFilterData(_searchData){
    var _searchUrl;
    if(sessionStorage.status){
        var _status = parseInt(sessionStorage.status);
        if(_status == 0){
            _searchUrl=unDealWithUrl;
        }else{
            _searchUrl=DealWithUrl;
        }
    }else{
        if(window.Current_Meeting_Type == 0){
            _searchUrl=unDealWithUrl;
        }else{
            _searchUrl=DealWithUrl;
        }
    }
    requestDataAjax(_searchUrl,_searchData,2);
}
//赋值搜索值
function initSearchData(_filterConditions){
    $("#borrower").val(_filterConditions.userName);
    $("#ApplyDate").val(_filterConditions.applyDate);
    $("#LoanNum").val(_filterConditions.loanNum);
}
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex=2;
    window.Current_Meeting_Yes_PageIndex=2;
}
//增加记录
function additionRecord(len,data,domNode){
    var html="";
    for (var i = 0; i < len; i++) {
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
            '<span>'+data[i].userName+' '+data[i].sapplyDate+'提交</span>' +//提交日期
            '<span>单号:'+data[i].loanNum+'</span>' +//单号
            '</div></li>';
        domNode.append(html);
    }
}
