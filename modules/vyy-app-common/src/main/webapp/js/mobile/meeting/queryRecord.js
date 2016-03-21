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
    tabLeftUrl="mobile/meeting/getMyMeetingWaitData.action?time="+new Date().getTime(),//
    tabRightUrl="mobile/meeting/getMyMeetingOverData.action?time="+new Date().getTime(),
    detailUrl="mobile/meeting/myMeetingDetail.action",//待处理url
    dataUrl = [tabLeftUrl,tabRightUrl];
$(function(){
    //基础参数
    basicTransferData();
    judgeScroll(dataUrl);
    //为查看详情注册事件
    lookDetail();
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
    //状态的伸缩变化
    retractableNav(listModule);
    //请求数据
    requestData(listModule,dataUrl,true);
    //重新加载页面的时候，判断是否有选中项
    judgeDataStatus(listModule,dataUrl,true);
    //页面加载的时候 ，滚动条滚动到顶部
    $("body,html").scrollTop(0);
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
                        pageSize: window.Meeting_Info.pageSize
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
    if(_resData.status == 0){
        if(_operatingStatus == 0){//加载
            if(!!data){
                if(data.length>0){
                    additionRecord(data,data.length,auditList);
                } else {
                    auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
                }
            }else{
                auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
            }
        }else
        if(_operatingStatus == 1){//滚动
            if(!!data){
                if(data.length==0){
                    $("body").toast({
                        msg:"没有更多会议记录...",
                        type:1,
                        callMode:"func"
                    });
                    isSlideBottom=true
                } else {
                    // 增加页码
                    if(window.Current_Meeting_Type==0)
                        window.Current_Meeting_Not_PageIndex +=1;
                    if(window.Current_Meeting_Type==1)
                        window.Current_Meeting_Yes_PageIndex +=1;
                    // 显示数据
                    additionRecord(data,data.length,auditList);
                }
            }else{
                $("body").toast({
                    msg:"没有更多会议记录...",
                    type:1,
                    callMode:"func"
                });
                isSlideBottom=true
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
    window.Current_Meeting_Yes_PageIndex=2;
}
//查看详情
function lookDetail(){
    $(document).on("click","section li",function(){
        window.location.href=detailUrl+"?meetingId="+$(this).attr("dataid")+"#flag=0";
    });
}
//增加记录
function additionRecord(data,size,auditList){
    var html=[];
    for (var i = 0; i < size; i++) {
        html=['<li class="twoLine" dataid='+data[i].id+' status="0">' +
        '<header>' +
        '<span class="mainColor">'+data[i].theme+'</span>' +
        '<i class="fa fa-angle-right"></i>' +
        '</header>' +
        '<div class="clear roomItem">' +
        getLocalTime(data[i].date,"MM月dd日")+'&emsp;<b class="timeColor">'+getLocalTime(data[i].start,"hh:mm")+'~'+getLocalTime(data[i].end,"hh:mm")+'</b>'+'&emsp;会议室：'+data[i].roomName +
        '</div></li>'];
        html = html.join("");
        auditList.append(html);
    }
}
//时间格式改变
function getLocalTime(_time,_format){
    var str = new Date(parseInt(_time)).Format(_format);
    return str;
}
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


