/**问题反馈列表**/
var feedbackList = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/feedback/queryFeedbackList.action",
		columns : [ 
			{"mData" : "problem"}, 
			{"mData" : "statusDisplay"},
			{"mData" : "userName"},
			{"mData" : "suggest"},
			{"mData" : "createTime"},
			{"mData" : "id"}
		]
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		}
	});
	
	function _bindEvent(){
		_getTableData();
		plugin.handleDatePickers();
		_searchEvent();
	}
	
	//搜索
	function _searchEvent(){
		$("#queryBtn").bind("click",function(){
			var _start = $("#starttime").val(),
			_end = $("#endtime").val();
			if(!plugin.checkTime(_start,_end)){
				$('#endtime').val("");
				return;
			}
			$table.destroy();
			_getTableData();
		});
	}
	
	
	function _getParam(){
		var _param = {};
		plugin.addParam(_param,"startTime",$("#starttime").val());
		plugin.addParam(_param,"endTime",$("#endtime").val());
		plugin.addParam(_param,"status",$("#status").val());
		return _param;
	}
	
	//取表数据
	function _getTableData(){
		 var _data = _getParam();
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
				$('td:first',row).html('<div style="width:300px" title="'+data.problem+'" class="w-text">'+data.problem+'</div>');
				$('td:eq(3)',row).html('<div style="width:300px" title="'+data.suggest+'" class="w-text">'+data.suggest+'</div>');
				$('td:eq(4)',row).html(new Date(parseInt(data.createTime)).Format("yyyy-MM-dd hh:mm:ss"));
				$('td:last',row).html('<a class="JlookDetial" href="manage/feedback/feedbackDetail.action?id='+data.id+'">详情</a>');
			},
			"ordering":  false,
			"processing": true,
			"scrollX": true,
			"serverSide": true,
			"ajax": {
				url : options.getDataUrl,
				data:{searchConditions : JSON.stringify(_data)}
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	return module;
	
})(window.jQuery);

/**问题详情**/
var feedbackDetail = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/feedback/getFeedbackDetailData.action",
		sendUrl : "manage/feedback/addSuggestion4Revision.action",
		remindUrl : "manage/feedback/feedbackReminder.action"
	};	
	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		}
	});
	
	function _bindEvent(){
		_getPageData();
		_sendEvent();
		_remindSendEvent();
	}
	
	//初始化页面数据
	function _getPageData(){
		var _id = $("#id").val();
		plugin.doGet(options.getDataUrl,{
			id : _id,
		},function(data){
			if(data.value){
				var _feedback = data.value.feedbackInfo;
				$("#problem").text(_feedback.problem);
				$("#suggest").text(_feedback.suggest);
				$("#userId").val(_feedback.userId);
				if(data.value.accessoryInfor){
					var imgArr = new Array(),_imgList = data.value.accessoryInfor;
					for(var i = 0;i<_imgList.length;i++){
						imgArr.push('<li><a data-original="'+options.resPath+'/01/resource/'+_imgList[i].fileName+'" href="javascript:void(0)"><img src="'+options.resPath+'/01/imageZoom/'+_imgList[i].fileName+'" alt="'+_imgList[i].fileName+'" /></li>');
					}
					$(".imgs").append(imgArr.join(""));
					$body.zoom({
						parentName : ".imgs",
						tagName : "a",
					});
				}
				if(data.value.entityProgressList){
					var arr = new Array(),_arrList = data.value.entityProgressList;
					for(var i = 0;i<_arrList.length;i++){
						arr.push('<li><p>'+new Date(_arrList[i].createTime).Format("yyyy-MM-dd hh:mm:ss")+'</p><p class="text"><label class="text-primary">系统留言：</label>'+_arrList[i].content+'</p></li>');
					}
					$(".fb-info-list").append(arr.join(""));
				}
			}
		});
	}
	
	//发送处理回复
	function _sendEvent(){
		$("#send").bind("click",function(){
			var _text = $("#content").val(),$this = $(this);
			if($.trim(_text)==""){
				$body.msgBox({status : 'error',msg : "请输入回复内容"});
				return;
			}
			plugin.doAdd({
				url:options.sendUrl,
				param:{
					id : $("#id").val(),
					suggestion4Revision   : _text
				},
				btn:$this,
				endfun:function(data){
					$("#content").val("");
					var $li = ('<li><p>'+new Date(data.value.createTime).Format("yyyy-MM-dd hh:mm:ss")+'</p><p class="text"><label class="text-primary">系统留言：</label>'+data.value.content+'</p></li>');
					$(".fb-info-list").append($li);
				}
			});
		});
	}
	//提醒发送
	function _remindSendEvent(){
		$("#remind").bind("click",function(){
			var $this = $(this);
			var _data = {
					feedbackId : $("#id").val(),
					userId : $("#userId").val(),
					problem : $.trim($("#problem").text())
			} 
			var _json = JSON.stringify(_data);
			plugin.doAdd({
				url:options.remindUrl,
				param: {reminderInfo : _json},
				btn:$this,
				endfun:function(data){
					$body.msgBox({status : 'success',msg : "已提醒"});
				}
			});
		});
	}
	
	return module;
})(window.jQuery);