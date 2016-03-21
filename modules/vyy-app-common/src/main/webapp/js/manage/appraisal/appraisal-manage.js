/**员工评价**/
var appraisalList = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/appraisal/queryAppraisalList.action",
		columns : [ 
			{"mData" : "theme"}, 
			{"mData" : "userId"},
			{"mData" : "userName"},
			{"mData" : "screateDate"},
			{"mData" : "statusDisplay"},
			{"mData" : "overallMerit"},
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
			window.open("manage/appraisal/exportAppraisalListToExcel.action?searchConditions="
									+ JSON.stringify(data));
		});
	}
	
	function _getParam(){
		var _param = {};
		plugin.addParam(_param,"startTime",$("#starttime").val());
		plugin.addParam(_param,"endTime",$("#endtime").val());
		plugin.addParam(_param,"theme",$("#theme").val());
		return _param;
	}
	
	//取表数据
	function _getTableData(){
		 var _data = _getParam();
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
				$('td:first',row).html('<div style="width:260px" title="'+data.theme+'" class="w-text">'+data.theme+'</div>');
				$('td:eq(3)',row).html(new Date(parseInt(data.createTime)).Format("yyyy-MM-dd hh:mm"));
				$('td:eq(5)',row).html(data.overallMerit==true?"有":"无");
				$('td:eq(6)',row).html('<a class="JlookDetial" href="manage/appraisal/appraisalDetail.action?id='+data.id+'">详情</a>');
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

/**员工评价详情**/
var appraisalDetail = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/appraisal/queryUserRankingList.action",
		columns : [ 
			{"mData" : "rank"}, 
			{"mData" : "accountName"},
			{"mData" : "avgScore"},
			{"mData" : "accountId"}
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
	}
	
	
	//取表数据
	function _getTableData(){
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
				$('td:eq(3)',row).html('<a class="JlookDetial" href="manage/appraisal/userDetail.action?id='+data.entityId+'&accountId='+data.accountId+'&total='+ data.avgScore +' ">详情</a>');
			},
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			"ajax": {
				url : options.getDataUrl,
				data:{id :$("#id").val()}
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	return module;
	
})(window.jQuery);