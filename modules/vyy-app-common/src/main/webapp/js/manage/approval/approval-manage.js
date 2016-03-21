/**审批列表**/
var approvalList = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/approval/queryApprovalList.action",
		columns : [ 
			{"mData" : "flowName"}, 
			{"mData" : "userId"},
			{"mData" : "userName"},
			{"mData" : "department"},
			{"mData" : "content"},
			{"mData" : "contractNumber"},
			{"mData" : "partner"},
			{"mData" : "remark"},
			{"mData" : "createTime"},
			{"mData" : "statusDisplay"},
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
		_exportEvent();
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
	
	//导出
	function _exportEvent(){
		$('#exportBtn').on('click',function(event) {
			var data = _getParam();
			window.open("manage/approval/exportApprovalListToExcel.action?searchConditions="
									+ JSON.stringify(data));
		});
	}
	
	function _getParam(){
		var _param = {};
		plugin.addParam(_param,"startTime",$("#starttime").val());
		plugin.addParam(_param,"endTime",$("#endtime").val());
		plugin.addParam(_param,"userName",$("#userName").val());
		plugin.addParam(_param,"status",$("#status").val());
		plugin.addParam(_param,"flowName",$("#flowName").val());
		return _param;
	}
	
	//取表数据
	function _getTableData(){
		 var _data = _getParam();
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
				$('td:eq(8)',row).html(new Date(parseInt(data.createTime)).Format("yyyy-MM-dd hh:mm"));
				$('td:last',row).html('<a class="JlookDetial" href="manage/approval/approvalDetail.action?id='+data.id+'">详情</a>');
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