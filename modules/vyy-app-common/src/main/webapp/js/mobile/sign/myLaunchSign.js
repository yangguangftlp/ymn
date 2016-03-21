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
	dataUrl = ["mobile/sign/getMyLaunchSignRecord.action?time="+new Date().getTime(),"mobile/sign/getMyLaunchSignRecord.action?time="+new Date().getTime()];
$(function(){
	//基础参数
	basicTransferData("type",0);
	//判断是否滚动到底部，加载更多数据
	judgeScroll(dataUrl);
	//为查看详情注册事件
	lookDetail();
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
						pageIndex=window.Current_Meeting_Yes_PageIndex;
					}
					var _data = {
						pageIndex: pageIndex,
						pageSize: window.Meeting_Info.pageSize,
						type : window.Current_Meeting_Type
					};
					requestDataAjax($dataUrl[window.Current_Meeting_Type],_data,1);
				}
			}
		}
	});
}
//数据渲染
function renderingData(_resData,_operatingStatus){
	var data = _resData.data;
	var auditList = $("section ul");
	if(_operatingStatus == 0){//加载
		if(!!data){
			var size = data.length;
			if(size > 0){
				addRecord(data,size,auditList);
			}else {
				auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
			}
		}else {
			auditList.append("<div class='noRecord whiteBg'>没有记录......</div>");
		}
	}else
	if(_operatingStatus == 1){//滚动
		if(!!data){
			var size = data.length;
			if(size > 0){
				addRecord(data,size,auditList);
				// 增加页码
				if(window.Current_Meeting_Type==0)
					window.Current_Meeting_Not_PageIndex++;
				if(window.Current_Meeting_Type==1)
					window.Current_Meeting_Yes_PageIndex++;
			}else {
				$("body").toast({
					msg:"没有更多记录...",
					type:1,
					callMode:"func"
				});
			}
		}else {
			$("body").toast({
				msg:"没有更多记录...",
				type:1,
				callMode:"func"
			});
		}
	}

}
//初始化页码
function initPage(){
	window.Current_Meeting_Not_PageIndex=2;
	window.Current_Meeting_Yes_PageIndex=2;
}
//查看详情
function lookDetail(){
	$(document).on("click","section>ul>div",function(){
		window.location.href="mobile/sign/myLaunchSignDetailView.action?id="+$(this).data("id");
	});
}
//增加记录
function addRecord(data,size,auditList){
	var html = "";
	for(var i =0;i<size;i++){
		html+="<div class='signInList' data-id="+data[i].id+"><header>"+data[i].theme+"</header><div class='signInTime'><em><i class='fa fa-sun-o'></i></em>"+data[i].sbeginTime+"<br/><em><i class='fa fa-moon-o'></i></em>"+data[i].sendTime+"</div>" +
			"<i class='fa fa-angle-right'></i></div>";
	}
	auditList.append(html);
}