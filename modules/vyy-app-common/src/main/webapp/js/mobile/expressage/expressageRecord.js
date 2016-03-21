window.Meeting_Info = {
    // 配置
    "pageSize": 10
};
window.Current_Meeting_Type=0;
window.Current_Meeting_Not_PageIndex=2;
window.Current_Meeting_Yes_PageIndex=2;
var listModule= 2,//0：只有状态选择，1：只有搜索，2：既有状态又有搜索
    isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    searchPageIndex= 1,//搜索的页码（新）
    loadOperationType = "load",//操作类型（新）
    unReceiveUrl = $('#go-url').val(),//未领取的链接地址
    collectedUrl = $('#go-url').val(),//已领取的链接地址
    detailUrl = $('#go-Detail-url').val(),
    dataUrl=[unReceiveUrl,collectedUrl];
$(function(){
	//基础参数
	basicTransferData("status",0);
    //判断是否滚动到底部，加载更多数据
    judgeScroll(dataUrl);
    //为查看详情注册事件
    lookDetail();
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
//判断是否滚动到底部，加载更多数据
function judgeScroll($dataUrl){
    $(document).scroll(function(){
        if($.fn.endlessScroll(document) && !isSlideBottom){
            if(!isSlideBottom && !isLoadIng){
                var pageIndex;
                if(window.Current_Meeting_Type==0){
                    pageIndex=window.Current_Meeting_Not_PageIndex;
                }else if(window.Current_Meeting_Type==1){
                    pageIndex=window.Current_Meeting_Yes_PageIndex;
                }else{
                    pageIndex=window.Current_Meeting_All_PageIndex;
                }
                var _data = {
                    pageIndex: pageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    status : window.Current_Meeting_Type
                };
                requestDataAjax($dataUrl[window.Current_Meeting_Type],_data,1);
            }
            var pageIndex = window.Current_Meeting_Type==0?window.Current_Meeting_Not_PageIndex:
                window.Current_Meeting_Yes_PageIndex;
            var $data = {};
            if(loadOperationType == "load"){
                $data={
                    pageIndex: pageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    status : window.Current_Meeting_Type - 1
                }
            }
            else{
                $data={
                    pageIndex: pageIndex,
                    pageSize: window.Meeting_Info.pageSize,
                    status : window.Current_Meeting_Type - 1,
                    consigneeName : $.trim($("userName").val())//领取快递人员姓名
                };
            }
            //判断是搜索的分页还是加载的分页
            if(loadOperationType == "search"){//（新）
                $data.pageIndex = searchPageIndex;//（新）
            }else{//（新）
                $data.pageIndex = pageIndex;//（新）
            }
            requestDataAjax($dataUrl[window.Current_Meeting_Type],$data,1)
        }
    });
}
//数据渲染
function renderingData(_resData,_operatingStatus){
    var _data = _resData.value.data;
    var auditList = $("section ul");
    if(_resData.status == 0){
        if(_operatingStatus == 0){//加载
            if (!!_data) {
                var html = "";
                var size = _data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        if(_data[i].status=="3"){
                            var $str='<span class="auditStatus subColor singleStatus">'+_data[i].courierNum+'</span>';
                        }else{
                            var $str='<span class="auditStatus greenColor singleStatus">'+_data[i].courierNum+'</span>';
                        }
                        var _Time = new Date(_data[i].createTime);
                        _TimeM = _Time.getMonth()+1;
                        _TimeD = _Time.getDate();
                        _thisDate = _TimeM+"月"+_TimeD+"日";
                        html='<li class="twoLine" dataid='+_data[i].id+' flowType='+_data[i].corpId+'>'+
                            '<header>'+
                            '<span class="mainColor">'+_data[i].consigneeName+'</span>'+
                            $str+
                            '<i class="fa fa-angle-right"></i>'+
                            '</header>'+
                            '<div class="subColor clear"><span>'+'由'+_data[i].creatorName+' '+_thisDate+' '+'提交</span></div>'+
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
            if (!!_data) {
                var html = "";
                var size = _data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        if(_data[i].status=="3"){
                            var $str='<span class="auditStatus subColor singleStatus">'+_data[i].courierNum+'</span>';
                        }else{
                            var $str='<span class="auditStatus greenColor singleStatus">'+_data[i].courierNum+'</span>';
                        }
                        var _Time = new Date(_data[i].createTime);
                        _TimeM = _Time.getMonth()+1;
                        _TimeD = _Time.getDate();
                        _thisDate = _TimeM+"月"+_TimeD+"日";
                        html='<li class="twoLine" dataid='+_data[i].id+' flowType='+_data[i].corpId+'>'+
                            '<header>'+
                            '<span class="mainColor">'+_data[i].consigneeName+'</span>'+
                            $str+
                            '<i class="fa fa-angle-right"></i>'+
                            '</header>'+
                            '<div class="subColor clear"><span>'+'由'+_data[i].creatorName+' '+_thisDate+' '+'提交</span></div>'+
                            '</li>'
                        auditList.append(html);
                    }
                    // 增加页码
                    if(window.Current_Meeting_Type==0)
                        window.Current_Meeting_Not_PageIndex++;
                    if(window.Current_Meeting_Type==1)
                        window.Current_Meeting_Yes_PageIndex++;
                    if(window.Current_Meeting_Type==2)
                        window.Current_Meeting_All_PageIndex++;
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
            }
        }else
        if(_operatingStatus == 2){//搜索
            isSlideBottom = false;
            searchPageIndex = 2;
            loadOperationType = "search";
            auditList.empty();
            if (!!_data) {
                var html = "";
                var size = _data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        if(_data[i].status=="3"){
                            var $str='<span class="auditStatus subColor singleStatus">'+_data[i].courierNum+'</span>';
                        }else{
                            var $str='<span class="auditStatus greenColor singleStatus">'+_data[i].courierNum+'</span>';
                        }
                        var _Time = new Date(_data[i].createTime);
                        _TimeM = _Time.getMonth()+1;
                        _TimeD = _Time.getDate();
                        _thisDate = _TimeM+"月"+_TimeD+"日";
                        html='<li class="twoLine" dataid='+_data[i].id+' flowType='+_data[i].corpId+'>'+
                            '<header>'+
                            '<span class="mainColor">'+_data[i].consigneeName+'</span>'+
                            $str+
                            '<i class="fa fa-angle-right"></i>'+
                            '</header>'+
                            '<div class="subColor clear"><span>'+'由'+_data[i].creatorName+' '+_thisDate+' '+'提交</span></div>'+
                            '</li>'
                        auditList.append(html);
                    }
                } else {
                    auditList.append("<div class='noRecord'>没有搜索到相关记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord'>没有搜索到相关记录......</div>");
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
//收集搜索的条件
function getSearchData(){
    var $searchData={
    	status : window.Current_Meeting_Type,
        pageIndex: 1,
        pageSize: window.Meeting_Info.pageSize,
        consigneeName : $.trim($("#userName").val()),//姓名
    };
    return $searchData;
}
//得到筛选的数据
function getFilterData(_searchData){
    var _searchUrl;
    if(sessionStorage.status){
        var _status = parseInt(sessionStorage.status);
        if(_status == 0){
            _searchUrl=unReceiveUrl;
        }else{
            _searchUrl=collectedUrl;
        }
    }else{
        if(window.Current_Meeting_Type == 0){
            _searchUrl=unReceiveUrl;
        }else{
            _searchUrl=collectedUrl;
        }
    }
    requestDataAjax(_searchUrl,_searchData,2);
}
//赋值搜索值
function initSearchData(_filterConditions){
    $("#userName").val(_filterConditions.userName);//项目名称,
}
//初始化页码
function initPage(){
    window.Current_Meeting_Not_PageIndex=2;
    window.Current_Meeting_Yes_PageIndex=2;
}
//查看详情
function lookDetail(){
    $(document).on("click","section li",function(){
        window.location.href = detailUrl + "?id="+$(this).attr("dataid");
    });
}
