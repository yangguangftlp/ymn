/**会议室管理**/
var meeting = (function($){
	var module = {};
	var $box = $('#setMeetingRoomTime');
	var $mbox = $('#addMeetingRoom');
	var options = {
		getDataUrl : "manage/meeting/queryMeetingRooms.action",
		addUrl : "manage/meeting/modifyMeetingRoom.action",
		delUrl : "manage/meeting/deleteMeetingRoom.action",
		editUrl : "manage/meeting/modifyMeetingRoomOpenTime.action",
		columns : [ 
		           	{"mData" : "id"}, 
					{"mData" : "roomName"}, 
					{"mData" : "address"},
					{"mData" : "capacity"},
					{"mData" : "equipment"}
					
				],
		deData : ["08:30:00","21:00:00"]
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		}
	});
	
	
	function _bindEvent(){
		$("#setMeetingRoomTime #start").val(options.seData[0]);
		$("#setMeetingRoomTime #end").val(options.seData[1]);
		$mbox.on('hidden.bs.modal', function () {
			$mbox.find("input").val("");
		});
		_getTableData();
		_saveSetting();
		_defaultEvent();
		_addEvent();
		_delEvent();
		_checkedAll();
	}
	
	//保存起止时间的设置
	function _saveSetting(){	
		$box.on("click",'#setting', function(event) {
			var $this = $(this);
			_settingEvent($this);
		});
	}
	
	//起止时间设置
	function _settingEvent(btn){
		var _start = $box.find("#start option:selected"),
			_end = $box.find("#end option:selected"),
			_sindex = _start.index(),
			_eindex = _end.index(),
			_svalue = _start.val(),
			_evalue = _end.val();
		if(_sindex==_eindex||_sindex>_eindex){
			$body.msgBox({status : 'error',msg : "开始时间要早于结束时间", time :1000});
			return;
		}
		plugin.doAdd({
			url:options.editUrl,
			param:{ 
				totalStart :_svalue,
				totalEnd : _evalue
			},
			box:$box,
			btn:btn,
			endfun:function(data){
				$("#setMeetingRoomTime #start").val(data.value[0]);
				$("#setMeetingRoomTime #end").val(data.value[1]);
			}
		});
	}
	
	//恢复默认
	function _defaultEvent(){
		$box.on("click",'#default', function(event) {
			var $this = $(this);
			$("#setMeetingRoomTime #start").val(options.deData[0]);
			$("#setMeetingRoomTime #end").val(options.deData[1]);
			_settingEvent($this);
		});
	}
	
	//验证
	function _getData(){
		var _name = $("#addMeetingRoom #roomName").val(),
			_address = $("#addMeetingRoom #address").val(),
			_capacity = $("#addMeetingRoom #capacity").val(),
			_equipment = $("#addMeetingRoom #equipment").val();
		var number = /^[1-9]\d*$/,_bool = true;
		if($.trim(_name)==""){
			$body.msgBox({status : 'error',msg : "会议室名称不能为空", time :1000});
			_bool = false;
			return false;
		}
		if($.trim(_address)==""){
			$body.msgBox({status : 'error',msg : "会议室地点不能为空", time :1000});
			_bool = false;
			return false;
		}
		if($.trim(_capacity)==""){
			$body.msgBox({status : 'error',msg : "容纳人数不能为空", time :1000});
			_bool = false;
			return false;
		}
		if(_capacity!=""&&!number.test(_capacity)){
			$body.msgBox({status : 'error',msg : "您输入的人数有误", time :1000});
			_bool = false;
			return false;
		}
		if($.trim(_equipment)==""){
			$body.msgBox({status : 'error',msg : "设备情况不能为空", time :1000});
			_bool = false;
			return false;
		}
		if(_bool){
			options.data = {
				roomName : _name,
				capacity : _capacity,
				equipment : _equipment,
				address : _address
			};
			return true;
		}else{
			return false;
		}
	}
	
	//添加会议室
	function _addEvent(){
		$mbox.on("click",'#save', function(event) {
			var $this = $(this);
			if(_getData()){
				plugin.doAdd({
					url:options.addUrl,
					param:{
						meetingRoom :JSON.stringify(options.data)
					},
					box:$mbox,
					btn:$this,
					endfun:function(){
						 $table.destroy();
			        	 _getTableData();
					}
				});
			}
		});
	}
	
	//删除会议室
	function _delEvent(){
		$("#delMRoom").bind("click",function(){
			var $cked = $("#ck_table .checkbox:checked");
			if($cked.length==0){
				$body.msgBox({status : 'error',msg : "请选择要删除的数据", time:1000});
			}else{
				var _ids = new Array();
				for(var i = 0;i<$cked.length;i++){
					_ids.push($($cked[i]).val());
				}
				plugin.doDel({
					ids:JSON.stringify(_ids)
				},options.delUrl,"删除会议室会对应删除与之相关的会议，是否确定删除？",function(){
					$("#ck_table .group-checkable").prop("checked",false);
					 $table.destroy();
		        	 _getTableData();
				});
			}
		});
	}
	
	//取表数据
	function _getTableData(){
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			"ordering":  false,
			"processing": true,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){	
				$('td:first',row).html("<input type=checkbox value="+data.id+" class=checkbox />");
			},
			"serverSide": true,
			"ajax": {
				url : options.getDataUrl
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	//全选
	function _checkedAll(){
		$("#ck_table").on("change",".group-checkable",function(){
			var $obj = $(this),_seledval = $obj.prop("checked");
			$("#ck_table .checkbox").prop("checked",_seledval);
		});
	}
	
	return module;

})(window.jQuery);

