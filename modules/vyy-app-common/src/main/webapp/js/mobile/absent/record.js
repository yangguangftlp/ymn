window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Underway_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var listModule= 2,//0：只有状态选择，1：只有搜索，2：机油状态又有搜索
    isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    searchPageIndex= 1,//搜索的页码（新）
    loadOperationType = "load",//操作类型（新）
    reviewedUrl="mobile/absent/getQueryAbsentRecord.action?time="+new Date().getTime(),//tab切换，请求url
    detailUrl="mobile/absent/absentDetailView.action",//列表详情url
    dataUrl = [reviewedUrl,reviewedUrl,reviewedUrl];
$(function(){
	//基础参数
    basicTransferData("operationType",1);
    //获得跳转链接
    var chaining=[$("input#go-url").val(),$("input#go-ts-url").val()];
    //选择时间
    selectTime();
    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);
    //为查看详情注册事件
    lookDetail(chaining);
    //搜索
    mySearch(listModule);
    //请求数据
    requestData(listModule,dataUrl);
    //重新加载页面的时候，判断是否有选中项
    judgeDataStatus(listModule,dataUrl);
    //状态的伸缩变化
    retractableNav(listModule);
    //重置搜索
    resetSearch();
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
});
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
                auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
            }
        }else
        if(_operatingStatus == 1){//滚动
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                    // 增加页码
                    if(loadOperationType == "load"){
                    	if(window.Current_Meeting_Type==0)
                            window.Current_Meeting_Not_PageIndex++;
                        if(window.Current_Meeting_Type==1)
                            window.Current_Meeting_Underway_PageIndex++;
                        if(window.Current_Meeting_Type==2)
                            window.Current_Meeting_Yes_PageIndex++;
                    }else{
                    	searchPageIndex++;
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
            isSlideBottom = false;
            searchPageIndex = 2;
            loadOperationType = "search";
            auditList.empty();
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList)
                } else {
                    auditList.append("<div class='noRecord whiteBg'>没有搜索到相关记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord whiteBg'>没有搜索到相关记录......</div>");
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
//判断是否滚动到底部，加载更多数据
function judgeScroll($dataUrl){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document) && !isSlideBottom){
            var pageIndex;
            if(window.Current_Meeting_Type==0){
                pageIndex=window.Current_Meeting_Not_PageIndex;
            }else if(window.Current_Meeting_Type==1){
                pageIndex=window.Current_Meeting_Underway_PageIndex;
            }else{
                pageIndex=window.Current_Meeting_Yes_PageIndex;
            }
            var $data = {};
            if(loadOperationType == "load"){
                $data={
                    //pageIndex: pageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    operationType : (window.Current_Meeting_Type+1)
                }
            }
            else{
                $data={
                    //pageIndex: pageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    operationType : (window.Current_Meeting_Type+1),
                    userName : $.trim($("#userName").val()),//人员姓名
                    startDate : $("#startDate").val(),//开始日期
                    endDate : $("#endDate").val()//结束日期
                };
            }
            if(loadOperationType == "search"){//（新）
                $data.pageIndex = searchPageIndex;//（新）
            }else{//（新）
                $data.pageIndex = pageIndex;//（新）
            }
            requestDataAjax($dataUrl[window.Current_Meeting_Type],$data,1)
        }
    });
}
//收集搜索的条件
function getSearchData(){
    var $searchData={
        pageIndex: 1,
        pageSize: window.Meeting_Info.pageSize,
        operationType : (window.Current_Meeting_Type+1),
        userName : $.trim($("#userName").val()),//人员姓名
        startDate : $("#startDate").val(),//开始日期
        endDate : $("#endDate").val()//结束日期
    };
    return $searchData;
}
//得到筛选的数据
function getFilterData(_searchData){
    var _searchUrl = reviewedUrl;
    requestDataAjax(_searchUrl,_searchData,2);
}
//赋值搜索值
function initSearchData(_filterConditions){
        $("#userName").val(_filterConditions.userName);//人员姓名
        $("#startDate").val(_filterConditions.startDate);//开始日期
        $("#endDate").val(_filterConditions.endDate);//结束日期
}
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex=2;
    window.Current_Meeting_Underway_PageIndex=2;
    window.Current_Meeting_Yes_PageIndex=2;
}
//查看详情
function lookDetail(){
    $(document).on("click","section li",function(){
        window.location.href=detailUrl+"?id="+$(this).attr("dataid");
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
            '<span class="mainColor">'+data[i].userName+' 申请 '+data[i].absentTypeName+'</span>'+//员工姓名  请假类型
            $str+
            '<i class="fa fa-angle-right"></i>'+
            '</header>'+
            '<div class="subColor clear"><span>'+data[i].screateDate+'提交</span></div>'+
            '</li>'
        domNode.append(html);
    }
}
//选择时间
function selectTime(){
	var leaveWarp = $(".leaveWarp"), // 请假页面
	promptModal = $(".promptModal");// 申请成功模态框
	/* 选择时间Start */
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
    var optDateTime = $.extend(opt['datetime'], opt['default']);
    $("#startDate,#endDate").mobiscroll(optDateTime).datetime(optDateTime); 
}
