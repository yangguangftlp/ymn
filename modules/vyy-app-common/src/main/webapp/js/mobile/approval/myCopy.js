window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_PageIndex=1;
var listModule= 1,//0：只有状态选择，1：只有搜索，2：机油状态又有搜索
    isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    searchPageIndex= 1,//搜索的页码（新）
    loadOperationType = "load",//操作类型（新）
    dataUrl = ["mobile/approval/getccApprovalRecord.action?time="+new Date().getTime()];
$(function(){
	//基础参数
    basicTransferData();
    //判断是否滚动到底部，加载更多数据
    judgeScroll();
    //状态的伸缩变化
    retractableNav(listModule);
    //选择时间
    selectTime();
    //为查看详情注册事件
    lookDetail();
    //搜索
    mySearch(listModule);
    //重置搜索
    resetSearch();
    //重新加载页面的时候，判断有搜索条件
    judgeDataStatus(listModule,dataUrl);

});
//数据渲染
function renderingData(_resData,_operatingStatus){
    var data = _resData.value;
    var auditList = $("section ul");
    if(0 == _resData.status){
        if(_operatingStatus == 0){//加载
            if (!!data) {
                var html = "";
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                    window.Current_PageIndex++;//（新）
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
                    additionRecord(size,data,auditList)
                    // 增加页码
                    window.Current_PageIndex++;//（新）
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
        }else
        if(_operatingStatus == 2){//搜索
            window.Current_PageIndex = 2;
            isSlideBottom = false;
            $("section ul").empty();
            if (!!data) {
                var size = data.length;
                if (size > 0) {
                    additionRecord(size,data,auditList);
                    //searchPageIndex++;//（新）
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
function judgeScroll(){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document) && !isSlideBottom){
            var $data = {};
            if(loadOperationType == "load"){
                $data={
                    pageIndex: window.Current_PageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    operationType : (window.Current_Meeting_Type+1)
                }
            }else{
                $data={
                    pageIndex: window.Current_PageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    operationType : (window.Current_Meeting_Type+1),
                    flowName : $("#flowName").val(),
                    userName : $("#userName").val(),
                    createDate : $("#createDate").val()
                };
                if($("#statusFlag").val() != "-1"){
                    $data.status=$("#statusFlag").val()
                }
            }
            requestDataAjax(dataUrl[0],$data,1);
        }
    });
}
//查看详情
function lookDetail(){
    $(document).on("click","section li",function(){
        window.location.href="mobile/approval/approvalDetailView.action?uType=1&id="+$(this).attr("dataid");
    });
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
        dateFormat: 'yyyy-mm-dd ',
        lang: 'zh',
        showNow: false,
        nowText: "今天",
        startYear: currYear - 10, //开始年份
        endYear: currYear + 10 //结束年份
    };
    $("#formDate").mobiscroll($.extend(opt['date'], opt['default']));
}
//收集搜索的条件
function getSearchData(){
    var $searchData={
        pageIndex: 1,
        pageSize: window.Meeting_Info.pageSize,
        operationType : (window.Current_Meeting_Type+1),
        flowName : $("#flowName").val(),
        userName : $("#userName").val(),
        createDate : $("#formDate").val()
    };
    if($("#statusFlag").val() != "-1"){
        $searchData.status=$("#statusFlag").val()
    }
    return $searchData;
}
//得到筛选的数据
function getFilterData(_searchData){
    requestDataAjax(dataUrl[0],_searchData,2);
}
//增加记录
function additionRecord(len,data,domNode){
    var html="";
    for (var i = 0; i < len; i++) {
        var _li,$str;
        if(data[i].cstatus == 1){
            _li='<li class="twoLine lightGrayBg" dataid='+data[i].id+' flowType='+data[i].flowType+'>';
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
        domNode.append(html);
    }
}
//重新加载页面的时候，判断有搜索条件
function judgeParameter(_filterConditions){
    var isLoadingData = false;
    if(!_filterConditions || (!_filterConditions.flowName && !_filterConditions.userName && !_filterConditions.createDate && (undefined == _filterConditions.status))){
        isLoadingData = true;
    }
    return isLoadingData;
}
//赋值搜索值
function initSearchData(_filterConditions){
    $("#flowName").val(_filterConditions.flowName);
    $("#userName").val(_filterConditions.userName);
    $("#formDate").val(_filterConditions.createDate);
    if(undefined != _filterConditions.status){
        $("#statusFlag").val(_filterConditions.status);
    }
}