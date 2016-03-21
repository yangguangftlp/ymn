/**请假类型**/
var absentType = (function($){
	var module = {};
	var $box = $('#addAbsentType');
	var options = {
		addUrl : "manage/absent/modifyAbsentType.action",
		delUrl : "manage/absent/deleteAbsentType.action"
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		}
	});
	
	function _bindEvent(){
		_addEvent();
		_delEvent();
		$box.on('hidden.bs.modal', function () {
			$box.find("input").val("");
		});
		_settingEvent();
	}
	
	//验证
	function _getData(){
		var _name = $("#addAbsentType #typeName").val(),
			_px = $("#addAbsentType #typePx").val();	
		var number = /^0$|^[1-9]\d*$/,_bool = true;
		if($.trim(_name)==""){
			$body.msgBox({status : 'error',msg : "名称不能为空", time :1000});
			_bool = false;
			return false;
		}
		if($.trim(_px)==""){
			$body.msgBox({status : 'error',msg : "请输入排序位置", time :1000});
			_bool = false;
			return false;
		}
		if(_px!=""&&!number.test(_px)){
			$body.msgBox({status : 'error',msg : "您输入的排序有误", time :1000});
			_bool = false;
			return false;
		}
		//校验唯一性
		var $tr = $("#ck_table tr");
		for(var i = 0;i < $tr.length;i++){
			var _tr = $($tr[i]);
			if(_tr.find(".type-name").text()==_name){
				$body.msgBox({status : 'error',msg : "该类型已经存在，请重新输入", time :1000});
				_bool = false;
				return false;
				break;
			}
		}
		if(_bool){
			options.data = {
				name : _name,
				px : _px,
				status : "1"
			};
			return true;
		}else{
			return false;
		}
	}
	
	//添加
	function _addEvent(){
		$box.on("click",'#save', function(event) {
			var $this = $(this);
			if(_getData()){
				plugin.doAdd({
					url:options.addUrl,
					param:{
						absentType:JSON.stringify(options.data)
					},
					box:$box,
					btn:$this,
					endfun:function(data){
						var newObj = options.data;
						$(".dataTables_empty").remove();
						var $tr = ("<tr id="+data.value+"><td>"+newObj.name+"</td><td>"+newObj.px+"</td><td><label data-id="+data.value+"  class='enabled'><small>已开启</small></label></td><td><a class='icon-trash active-trash' title='删除' href='javascript:void(0)' data-id='"+data.value+"'></a></td></tr>");
						if($("#ck_table tr").length==0){
							$("#ck_table tbody").append($tr);
						}else{
							$("#ck_table tr:eq(1)").before($tr);
						}
					}
				});
			}
		});
	}
	
	//设置
	function _settingEvent(){
		$("#ck_table").on("click","label",function(event){
			var $this = $(this),
				_id = $(this).data("id"),
				_status = 0,_className = "",_text = "";
			if($this.hasClass("enabled")){
				_status = 0;
				_className = "disabled";
				_text = "未开启";
			}else{
				_status = 1;
				_className = "enabled";
				_text = "已开启";
			}
			plugin.doSetting({
				url:options.addUrl,
				param:{
					absentType:JSON.stringify({
						id : _id,
						status : _status
					})
				},
				endfun:function(data){
					$this.prop("class",_className).find("small").text(_text);
				}
			});
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
			},options.delUrl,"删除请假类型会对应删除与之相关的请假，是否确定删除？",function(){
				$("tr#"+_id).remove();
				if($("#ck_table tbody tr").length==0){
					$("#ck_table tbody").append('<tr class="odd"><td valign="top" colspan="4" class="dataTables_empty">对不起，没有匹配的数据</td></tr>');
				}
			});
			
		});
	}
	
	return module;

})(window.jQuery);

/**请假列表**/
var absentList = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/absent/queryAbsentList.action",
		columns : [ 
			{"mData" : "userId"}, 
			{"mData" : "userName"},
			{"mData" : "position"},
			{"mData" : "department"},
			{"mData" : "reason"},
			{"mData" : "beginTime"},
			{"mData" : "endTime"},
			{"mData" : "createTime"},
			{"mData" : "statusDisplay"}
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
			window.open("manage/absent/exportAbsentListToExcel.action?searchConditions="
									+ JSON.stringify(data));
		});
	}
	
	function _getParam(){
		var _param = {};
		plugin.addParam(_param,"startTime",$("#starttime").val());
		plugin.addParam(_param,"endTime",$("#endtime").val());
		plugin.addParam(_param,"userName",$("#userName").val());
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
				$('td:eq(5)',row).html(new Date(parseInt(data.beginTime)).Format("yyyy-MM-dd hh:mm"));
				$('td:eq(6)',row).html(new Date(parseInt(data.endTime)).Format("yyyy-MM-dd hh:mm"));
				$('td:eq(7)',row).html(new Date(parseInt(data.createTime)).Format("yyyy-MM-dd hh:mm"));
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