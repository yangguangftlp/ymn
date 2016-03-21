/**借款设置**/
var loanSetting = (function($){
	var module = {};
	var $box = $('#addExpenseType');
	var options = {
		addUrl : "manage/loan/modifyLoanRule.action"
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
		}
	});
	
	function _bindEvent(){
		_addEvent();
		_initSelectPeo();
	}
	
	//选择人员
	function _initSelectPeo(){
		var membersListWarp = $(".membersListWarp").eSelectPeople({
		    btCance : function(_self) {
		    	$("#selectBox").modal('hide');    
		    },
		    btClose : function(_self) {
		    	$("#selectBox").modal('hide');
		    },
		    btOk : function(_self,data) {
		    	 if(!!data && data.length > 0 ){
		                //判断是否已存在选择的用户 这里需要去重
		                for(var i = 0,size = data.length;i<size;i++){
		                	var obj = data[i],
		                		uId = obj.uId,
		                		name = obj.uName,
		                		uavatar = obj.uAvatar;
		                	var $parent = $("#addFlag"+options.flag);
		                	var $exist = $parent.find("[data-uid="+uId+"]");
		                    if($exist.length>0){
		                    	continue;
		                    } 
		                    var $div = $("<div class='selpeo'>").attr("data-uid",uId).attr("data-name",name).attr("data-uavatar",uavatar);
		                    var $img = $("<img>").attr("src",uavatar).on("click",function(){
		                        $(this).parent().remove();
		                    });
		                    var $span = $("<span>").text(name);
		                    var $elemI = $("<i>");
		                    $parent.find(".selpeo").remove();
		                    $div.append($img);
		                    $div.append($span);
		                    $div.append($elemI);
		                    $parent.append($div);
		                };
		            }
		    	 $("#selectBox").modal('hide');
		    }                
		});
		$(".addedPeopleList").on("click","i",function(){
            $(this).parent().remove();
        });
		$("#selectBox").on('hidden.bs.modal',function(){
			$(".searchResult").hide();
		});
		$(".addPeople").bind("click",function(){
			var $that = $(this),_id = $that.prop("id");
			$("#selectBox").modal('show');
			if(_id=="addPriPeo"){
				options.flag = 0;
				membersListWarp.eshow({flag:0,_isMultipleChoice:false});
			}else{
				options.flag = 1;
				membersListWarp.eshow({flag:1,_isMultipleChoice:false});
			}
		});
	}
	
	//验证
	function _getData(){
		var _priPeo = $("#addFlag0 .selpeo"),
			_pubPeo = $("#addFlag1 .selpeo"),
			_priId = $("#privateId").val(),_priPeoId = _priPeo.data("uid"),_priPeoName = _priPeo.data("name"),
			_pubId = $("#publicId").val(),_pubPeoId = _pubPeo.data("uid"),_pubPeoName = _pubPeo.data("name");
			
		var _bool = true;
		if(_priPeo.length==0){
			$body.msgBox({status : 'error',msg : "请选择对私借款财务负责人", time :1000});
			_bool = false;
			return false;
		}
		if(_pubPeo.length==0){
			$body.msgBox({status : 'error',msg : "请选择对公借款财务负责人", time :1000});
			_bool = false;
			return false;
		}
		if(_bool){
			options.data = {
				Loan1Financial:JSON.stringify({
	                id : _priId,
	                status : 1,
	                name : _priPeoName,
	                value: _priPeoId,
	                px : 1
	            }),
	            Loan2Financial:JSON.stringify({
	                id : _pubId,
	                status : 1,
	                name : _pubPeoName,
	                value : _pubPeoId,
	                px : 1
	            })
			};
			return true;
		}else{
			return false;
		}
	}
	
	//添加
	function _addEvent(){
		$('#save').bind("click", function(event) {
			var $this = $(this);
			if(_getData()){
				plugin.doAdd({
					url:options.addUrl,
					param:options.data,
					btn:$this,
					endfun:function(data){
					}
				});
			}
		});
	}
	
	return module;

})(window.jQuery);

/**借款列表**/
var loanList = (function($){
	var module = {};
	var options = {
		getDataUrl : "manage/loan/queryLoanList.action",
		exportUrl : "manage/loan/exportLoanListToExcel.action",
		type :0,
		columns : [ 
			{"mData" : "loanNum"}, 
			{"mData" : "userId"},
			{"mData" : "userName"},
			{"mData" : "department"},
			{"mData" : "applyDate"},
			{"mData" : "amount"},
			{"mData" : "capitalAmount"},
			{"mData" : "subject"},
			{"mData" : "receiveAccount"},
			{"mData" : "statusDisplay"},
			{"mData" : "id"}
		],
		detailUrl:"manage/loan/privateLoanDetail.action",
		rowFun:function(row, data, displayIndex, displayIndexFull){
			
		}
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
			window.open(options.exportUrl+"?searchConditions="
									+ JSON.stringify(data));
		});
	}
	
	function _getParam(){
		var _param = {};
		plugin.addParam(_param,"startTime",$("#starttime").val());
		plugin.addParam(_param,"endTime",$("#endtime").val());
		plugin.addParam(_param,"userName",$("#userName").val());
		plugin.addParam(_param,"status",$("#status").val());
		plugin.addParam(_param,"loanNum",$("#loanNum").val());
		plugin.addParam(_param,"loanType",options.type);
		return _param;
	}
	
	//取表数据
	function _getTableData(){
		 var _data = _getParam();
		 $table = $('#ck_table').DataTable({
			"oLanguage" : plugin.tLanguage,
			"searching": false,
			fnRowCallback:function(row, data, displayIndex, displayIndexFull){
				$('td:eq(4)',row).html(new Date(data.applyDate).Format("yyyy-MM-dd"));
				$('td:last',row).html('<a class="JlookDetial" href="'+options.detailUrl+'?id='+data.id+'">详情</a>');
			},
			"ordering":  false,
			"scrollX": true,
			"processing": true,
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

/**借款用途**/
var loanUse = (function($){
	var module = {};
	var $box = $('#addLoanUse');
	var options = {
		addUrl : "manage/loan/modifyLoanUse.action",
		delUrl : "manage/loan/deleteLoanUse.action"
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
		var _name = $("#addLoanUse #useName").val(),
			_px = $("#addLoanUse #usePx").val();	
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
						loanUse:JSON.stringify(options.data)
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
					loanUse:JSON.stringify({
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
			},options.delUrl,"删除借款用途会对应删除与之相关的借款，是否确定删除？",function(){
				$("tr#"+_id).remove();
				if($("#ck_table tbody tr").length==0){
					$("#ck_table tbody").append('<tr class="odd"><td valign="top" colspan="4" class="dataTables_empty">对不起，没有匹配的数据</td></tr>');
				}
			});
			
		});
	}
	
	return module;

})(window.jQuery);
