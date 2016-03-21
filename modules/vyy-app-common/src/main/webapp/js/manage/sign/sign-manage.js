/**签到列表**/

var signList = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/sign/queryAttendanceList.action",
		columns : [ 
					{"mData" : "userId"}, 
					{"mData" : "userName"},
					{"mData" : "location"},
					{"mData" : "signTime"},
					{"mData" : "attendType"},
					{"mData" : "remark"}
				]
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		},
		//取表数据
		getTableData:function(){
			 var _data = _getParam();
			 $table = $('#ck_table').DataTable({
				"oLanguage" : plugin.tLanguage,
				"searching": false,
				fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
					$('td:eq(4)', row).html(data.attendType=="0"?"签到":"签退");
					$('td:eq(3)', row).html(new Date(parseInt(data.signTime)).Format("yyyy-MM-dd hh:mm"));
					$('td:last',row).html('<div style="width:160px" title="'+data.remark+'" class="w-text">'+data.remark+'</div>');
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
	});
	
	
	function _bindEvent(){
		module.getTableData();
		plugin.handleDatePickers();
		_searchEvent();
		_exportEvent();
		plugin.refreshEvent.apply(module);
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
			module.getTableData();
		});
	}
	
	//导出
	function _exportEvent(){
		$('#exportBtn').on('click',function(event) {
			
			var data = _getParam();
			window.open("manage/sign/exportAttendanceListToExcel.action?searchConditions="
									+ JSON.stringify(data));
		});
	}
	
	function _getParam(){
		var _param = {};
		plugin.addParam(_param,"startTime",$("#starttime").val());
		plugin.addParam(_param,"endTime",$("#endtime").val());
		plugin.addParam(_param,"userName",$("#userName").val());
		return _param;
	}
	
	return module;

})(window.jQuery);

/**考勤设置**/
var setupAttendance = (function($){
	var module = {};
	var options = {
		submitUrl : "manage/sign/modifyAttendanceRule.action",
		defaultObj:{
			startHour:'9',
		    startMinute:'00',
		    endHour:'18',
	 	    endMinute:'00',
            delayMinutes:'0',
            attendDays:["mon","tues","wed","thur","fri"]
		}
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		}
	});
	
	
	function _bindEvent(){
		_setupEvent();
		_defaultEvent();
		_initSetting();
	}
	
	//初始化设置
	function _initSetting(){
		var _default = options.defaultObj,
			_setting,_object = _default;
		if(options.settingObj){
			_setting = $.parseJSON(options.settingObj);
			_object = _setting;
			$("#id").val(_object.id);
		}
		
		
		var _wd = _object.attendDays;
		for(var i = 0;i<_wd.length;i++){
			$("#"+_wd[i]).prop("checked",true);
		}
		$("#startHour").val(_object.startHour);
		$("#startMinute").val(_object.startMinute);
		$("#endHour").val(_object.endHour);
		$("#endMinute").val(_object.endMinute);
		$("#delayMinutes").val(_object.delayMinutes);
	}
	//考勤设置
	function _setupEvent(){
		$('#setupBtn').on("click", function(event) {
			_setting();

		});
	}
	
	//设置考勤
	function _setting(){
		var _param = {
				id : $("#id").val(),
				mon : $("#mon").prop("checked") ? "1" : "0",
				tues : $("#tues").prop("checked") ? "1" : "0",
				wed : $("#wed").prop("checked") ? "1" : "0",
				thur : $("#thur").prop("checked") ? "1" : "0",
				fri : $("#fri").prop("checked") ? "1" : "0",
				sat : $("#sat").prop("checked") ? "1" : "0",
				sun : $("#sun").prop("checked") ? "1" : "0",
				startHour : $("#startHour").val(),
				startMinute : $("#startMinute").val(),
				delayMinutes : $("#delayMinutes").val(),
				endHour : $("#endHour").val(),
				endMinute : $("#endMinute").val()
			};
			plugin.doSetting({
				url:options.submitUrl,
				param:{attendanceRule : JSON.stringify(_param)},
				endfun:function(data){}
			});
	}
	//恢复默认
	function _defaultEvent(){
		$("#defaultBtn").bind("click",function(){
			var _object = options.defaultObj;
			var _wd = _object.attendDays;
			$("input:checkbox").prop("checked",false)
			for(var i = 0;i<_wd.length;i++){
				$("#"+_wd[i]).prop("checked",true);
			}
			$("#startHour").val(_object.startHour);
			$("#startMinute").val(_object.startMinute);
			$("#endHour").val(_object.endHour);
			$("#endMinute").val(_object.endMinute);
			$("#delayMinutes").val(_object.delayMinutes);
			_setting();
		});
	}
	
	return module;
	
})(window.jQuery);

/**排除或增加日期增加**/
var workdayList = (function($){
	var module = {};
	var options = {
		addUrl : "manage/sign/addWorkDay.action",
		delUrl : "manage/sign/delWorkDay.action",
		getDataUrl: "manage/sign/queryWorkDayList.action",
		columns : [ 
					{"mData" : "startDay"}, 
					{"mData" : "endDay"},
					{"mData" : "attendOrNot"},
					{"mData" : "remark"},
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
		plugin.handleDatePickers();
		_addEvent();
		_delEvent();
		_getTableData();
		var $box = $('#addSignType');
		$box.on('hidden.bs.modal', function () {
			$box.find("input").val("");
		});
	}
	
	//取表数据
	function _getTableData(){
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
				$('td:first',row).html(new Date(parseInt(data.startDay)).Format("yyyy-MM-dd"));
				$('td:eq(1)',row).html(new Date(parseInt(data.endDay)).Format("yyyy-MM-dd"));
				$('td:eq(2)',row).html(data.attendOrNot=="0"?"排除考勤日期":"增加考勤日期");
				$('td:last', row).html('<a data-id='+data.id+' href="javascript:void(0)" class="icon-trash active-trash" title="删除"></a>');
			},
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			"ajax": {
				url : options.getDataUrl
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	//验证时间
	function _getData(){
		var _stime = $("#addSignType #startDay").val(),
			_etime = $("#addSignType #endDay").val(),
			_type = $("#addSignType #attendOrNot").val(),
			_remark = $("#addSignType #remark").val();
		var _bool = true;
		if($.trim(_stime)==""){
			$body.msgBox({status : 'error',msg : "开始时间不能为空", time :1000});
			return false;
		}
		if($.trim(_etime)==""){
			$body.msgBox({status : 'error',msg : "结束时间不能为空", time :1000});
			return false;
		}
		if(_stime!=""&&_etime!=""){
			_bool = plugin.checkTime(_stime,_etime);
		}
		if(_bool){
			options.data = [{
				startDay : _stime,
				endDay : _etime,
				attendOrNot : _type,
				remark : _remark
			}];
			return true;
		}else{
			return false;
		}
	}
	//添加
	function _addEvent(){
		var $box = $('#addSignType');
		$box.on("click",'#save', function(event) {
			var $this = $(this);
			if(_getData()){
				plugin.doAdd({
					url:options.addUrl,
					param:{
						workDay :JSON.stringify(options.data)
					},
					box:$box,
					btn:$this,
					endfun:function(){
						 $table.destroy();
			        	 _getTableData();
					}
				});
			}
		});
	}
	
	//删除
	function _delEvent(){
		$("#ck_table").on("click",".active-trash",function(event){
			event.preventDefault();
			event.stopPropagation();
			var _id = $(this).data("id");
			plugin.doDel({
				id:_id
			},options.delUrl,"确定删除？",function(){
				 $table.destroy();
	        	 _getTableData();
			});
		});
	}
	
	return module;
	
})(window.jQuery);