window.Meeting_Info = {
	// 会议相关访问地址
    "Meeting_Rooms_View": "/energy-web/meeting/roomsView.action",
    // 通用功能访问地址
    "Common_Enums": "/common/enums.action"
};

window.Meeting_Type = {
	"Time": "MeetingTime",
	"TimeSpan": "MeetingTimeSpan"
};

$(function(){
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
    $("#formDate").mobiscroll($.extend(opt['date'], opt['default']));
    
    //赋值
    assignment();

    //快速预订
    $("#meeting_add_launch").click(function(){
        $("form").verification({
            callback:function(){
                if(compareTime()){
                    var $data=getData();
                    window.location.href=window.Meeting_Info.Meeting_Rooms_View+"?"+$data;
                }
            }
        });
    });
});
//赋值
function assignment(){
    var idArr=["#address","#formTime","#duration"];
    $("select").each(function(index){
        var $this=$(this);
        $this.change(function(){
            var $selectVal=$this.find("option:selected").val();
            if("01" == $selectVal){
                $(idArr[index]).val("");
            }else{
                $(idArr[index]).val($this.find("option:selected").val());
                $(idArr[index]).attr("text", $this.find("option:selected").text());
            }
        });
    });
}

function getData(){
    var data="buildingId="+$("#address").val()+
	"&buildingName="+$("#address").attr("text")+
	"&date="+$("#formDate").val()+
	"&time="+$("#formTime").val()+
	"&timeSpan="+$("#duration").val();
    return data;
}

//时间比较
function compareTime(){
    var now = new Date();
    var startStr = $("#formDate").val()+" "+$("#formTime").attr("text");
    var start=new Date(startStr.replace("-", "/"));
    if(now > start){
        $("body").toast({
            msg:"开始时间必须大于当前时间",
            type:2,
            callMode:"func"
        });
        return false;
    }else{
        return true;
    }
}

/**
 * 会议室条件设定页面初始化数据
 */
function meetingInitReady(){
	// 1. 初始化楼栋数据
	for ( var int = 0; int < meeting_init_buildings.length; int++) {
		var build = meeting_init_buildings[int];
		$("#buildingItems").append("<option value='"+build.id+"'>"+build.buildingName+"</option>");
	}
	// 2. 获取会议时间
	for ( var int = 0; int < meetingTime.length; int++) {
		var time = meetingTime[int];
		$("#timeItems").append("<option value='"+time.status+"'>"+time.name+"</option>");
	}
	// 3. 获取会议时长
	for ( var int = 0; int < meetingTimeSpan.length; int++) {
		var timeSpan = meetingTimeSpan[int];
		$("#timeSpanItems").append("<option value='"+timeSpan.status+"'>"+timeSpan.name+"</option>");
	}
}
