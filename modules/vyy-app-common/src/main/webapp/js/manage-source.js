/**选择控件**/
(function ($) {
//	$.fn.extend({     
//		selectbox:function(options){     
//		          // Our plugin implementation code goes here.       
//		}     
//	});
	$.fn.selectbox = function (options) {
		var settings = $.extend({}, $.fn.selectbox.defaults, options);
		return this.each(function () {
			var _obj = $(this);
			new SelectUI(settings, _obj).init();
		});
	}
	$.fn.selectbox.defaults = {
		"optionclickcallback":null,//选择某一项执行的除默认方法外的一些自定义方法
		"width":200,//宽度，有的文字比较长
		"height":150,
		"value":"",
		"text":"请选择"
	}  

	function SelectUI(opts, obj) {
		this.conf = opts;
		this.selectobj = obj;
		this.data = {};
		this.dom = {};
		this.func = this;
	}
	SelectUI.prototype = {
		init: function () {
			this.define().addEvent();
		},
		define: function () {
			var that = this;
			that.dom.selectobj = that.selectobj;
			that.data.width = that.conf.width;
			that.data.height = that.conf.height;
			that.dom.actionbtn = null;
			that.dom.acttext = null;
			that.dom.showwrap = null;
			that.data.selectedval = that.conf.value;
			that.data.selectedtext = that.conf.text;
			that.dom.chooseactbtn = null;
			that.dom.tmpl = new Array();
			that.dom.tmpl.push('<div id={1} class="select_wrap">');
			that.dom.tmpl.push('<div class="action_wrap clearfix"><div class="action_left"><div class="action_right">');
			that.dom.tmpl.push('<span class="action_text" value="{3}">{2}</span><i class="action_btn"></i>');
			that.dom.tmpl.push('</div></div></div><ul class="select_options">{0}</ul></div>');
			that.func.optionclickcallback = that.conf.optionclickcallback;
			return that;
		},
		bulidSelectDom: function(){
			var that = this;
			that.dom.selectobj.hide();
			var _suiid = that.dom.selectobj.prop("id")+"_selectUI";
			var _options = that.dom.selectobj.find("option");
			var _lis = new Array();
			for(var i=0;i<_options.length;i++){
				var _obj = $(_options[i]);
				var _cur = "";
				_lis.push("<li><a href='javascript:void(0)' value='"+_obj.val()+"'>"+_obj.text()+"</a></li>");
			}
			var strtmpl = that.dom.tmpl.join("");
			strtmpl = strtmpl.replace('{0}',_lis.join("")).
			replace('{1}',_suiid).replace('{2}',that.data.selectedtext).replace('{3}',that.data.selectedval);
			var _sdiv = $(strtmpl);
			that.dom.selectobj.after(_sdiv);			
			_sdiv.css({
				'width':that.data.width
			});
			that.dom.actionbtn = $("#"+_suiid).find(".action_wrap");
			that.dom.acttext = $("#"+_suiid).find(".action_text");
			that.dom.showwrap = $("#"+_suiid).find(".select_options");
			//$("#"+_suiid).find(".action_right").width(that.data.width-20);
			that.dom.showwrap.css({'width':that.data.width,'height':that.data.height});
			that.dom.chooseactbtn = $("#"+_suiid).find("li a");
			that.actionClickEvent();
			that.sOptionClickEvent();
			return that;
		},
		actionClickEvent: function () {
			var that = this,_ismenter = false, _seltimer = null;
			that.dom.actionbtn.bind("click", function (event) {
				_ismenter = true;
				//选中已选择过的值
				var _value = that.dom.acttext.attr("value");
				that.dom.showwrap.find(".current").removeClass("current");
				that.dom.showwrap.find("a[value="+_value+"]").addClass("current");
				var _ul = that.dom.showwrap;
				var _cname = _ul.prop("class");
				if(_cname.indexOf("show")>0){
					_ul.hide().removeClass("show");
				}else{
					_ul.show().addClass("show");
				}
			}).bind("mouseleave", function () {
				_ismenter = false;
				if (_seltimer) clearTimeout(_seltimer);
				_seltimer = setTimeout(function () {
					if (!_ismenter) {
						that.dom.showwrap.hide().removeClass("show");
					}
				}, 100);
			});
			
			that.dom.showwrap.bind("mouseleave", function () {
				_ismenter = false;
				if (_seltimer) clearTimeout(_seltimer);
				_seltimer = setTimeout(function () {
					if (!_ismenter) {
						that.dom.showwrap.hide().removeClass("show");
					}
				},100);
				
			}).bind("mouseenter", function () {
				_ismenter = true;
			});
			return that;
		},
		sOptionClickEvent:function(){
			var that = this;
			that.dom.chooseactbtn.bind("click", function (event) {
				event.preventDefault();//阻止a标签的默认调转动作，但不能阻止点击事件的冒泡，点击事件会向父标签中传播
				event.stopPropagation();//阻止a标签点击事件的冒泡
				var _obj = $(this),_text = _obj.text();
				that.dom.acttext.text(_text);
				var _value = _obj.attr("value");
				that.dom.acttext.attr("value",_value);
				//console.log(that.dom.selectobj.find("option[value='"+_value+"']").length);
				that.dom.selectobj.find("option").removeAttr("selected");
				that.dom.selectobj.find("option[value='"+_value+"']").attr("selected",true);
				if(that.optionclickcallback!=null){
					that.optionclickcallback(_value,_text);
				}
				that.dom.showwrap.hide().removeClass("show");
			});
			return that;
		},
		addEvent: function () {
			var that = this;
			that.bulidSelectDom();
			return that;
		}
	}
})(jQuery);

//表格控件中文
var tableLanguage = {
	"sLengthMenu" : "每页显示 _MENU_ 条",
	"sZeroRecords" : "对不起，没有匹配的数据",
	"sInfo" : "第 _START_ - _END_ 条 / 共 _TOTAL_ 条数据",
	"sInfoEmpty" : "没有匹配的数据",
	"sInfoFiltered" : "(数据表中共 _MAX_ 条记录)",
	"sProcessing" : "正在加载中...",
	"sSearch" : "全文搜索：",
	"oPaginate" : {
		"sFirst" : "第一页",
		"sPrevious" : " 上一页 ",
		"sNext" : " 下一页 ",
		"sLast" : " 最后一页 "
	}
}

/**公共组件**/
var Component = (function($){
	var module = {};
	var options = {};	
	var $body = $("body");
	$.extend(module,{	
		okImg:"<img src='/images/ok.png' />",
		errorImg:"<img src='/images/error.png' />",
		warning:"<img src='/images/warning.png' />",
		loading:"<img src='/images/smallLoading.gif' />",
		//用户未输入时提示
		focusTips:function(obj,text,option){
			var defaultOption = {
				title:text,
				placement:"right",
				container:"body"
			}
			if (option) {
				$.extend(defaultOption, option);
			}
			
			$(obj).tooltip(defaultOption);
		},
		//服务端加载数据
		getDataFromServer:function(url,data,callback,option){
			var defaultOption = {
				type : 'get',
				url : url,
				data : data || {},
				dataType : "json",
				error : function(err) {
				},
				success : function(data) {
					if (callback) {
						callback(data);
					}
				}
			};

			if (option) {
				$.extend(defaultOption, option);
			}
			$.ajax(defaultOption);

		},
		//封装对象
		addParam : function(params,paramName,paramVal){
			params[paramName] = paramVal;
		},
		clearCache:function(url){//清除缓存
			var that = this;
			$("#clearCache").unbind("click");
			$("#clearCache").bind("click",function(){
				module.getDataFromServer(url,{},function(data){
					if(data.status=="0"){
						Component.pageBigTips("w5-mauto pos-top",Component.okImg+"已清除",$body,"alert-success");
						
					}else{
						Component.pageBigTips("w5-mauto pos-top",Component.warning+data.errorMsg,$body,"alert-warning");
					}
				},{
					type:"post"
				});
			});
			return that;
		},
		pageBigTips:function(style,text,parent,type){
			var loadHtml = new Array();
			loadHtml.push('<div id="pageBigTips" class="'+style+'"><div class="alert '+type+' alert-dismissable">');
			loadHtml.push('&nbsp;'+text+'<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button></div>');
			$(parent).append(loadHtml.join(""));
			setTimeout(function(){
				$("#pageBigTips").remove();
			},2000);
		},
		
		//左侧菜单点击事件
		leftMenuEvent:function(){
			var that = this;
			var $nav = $(".nav-list");
			var ajax = {
					type:"get",
					dataType:"html",
					error:function(XMLHttpRequest){
						var req = XMLHttpRequest;
						var status = req.status;
						var msg = "系统出现错误，请联系管理员";
						if(status=="404"){
							msg = "地址有误。"
						}
						$("#pageBigTips").remove();
						Component.pageBigTips("w5-mauto",Component.warning+msg,$(".page-content"),"alert-warning");
					},
					beforeSend:function(){
						$(".page-content").empty();
						Component.pageBigTips("w5-mauto",Component.loading+"正在加载，请稍后。。。",$(".page-content"),"alert-info");
					}
			}
			$nav.off("click","a");
			$nav.on("click","a",function(event){
				event.stopPropagation();
				event.preventDefault();
				var $obj = $(this),
					$parent = $obj.parent(),
					_cname = $parent.prop("class"),
					_url = $obj.attr("url"),
					_haschild = $obj.attr("hasChild");
				
				
				$(".nav-list .open .submenu").hide();
				if(_cname!=undefined&&_cname.indexOf("open")>=0){
					$(".nav-list .open").removeClass("open");
					$parent.find(".submenu").hide();
				}
				
				if(_cname==""||_cname==undefined){
					$(".nav-list .open").removeClass("open");
					$parent.addClass("open").find(".submenu").show();
				}
				
				if(_haschild=="false"){
					Component.getDataFromServer(_url,{},function(data){
						$("#pageBigTips").remove();
						$(".page-content").html(data);
					},ajax);
					
				}
			});
			
			$(".submenu").off("click","li a");
			$(".submenu").on("click","li a",function(event){
				event.stopPropagation();
				event.preventDefault();
				var $obj = $(this),_url = $obj.attr("url");
				$(".submenu a.active").removeClass("active");
				$obj.addClass("active");
				Component.getDataFromServer(_url,{},function(data){
					$("#pageBigTips").remove();
					$(".page-content").html(data);
				},ajax);
				
			});
			
			//菜单左右缩进
			$(".retract-btn").off("click","i");
			$(".retract-btn").on("click","i",function(){
				var $that = $(this),_cname = $that.prop("class");
				var $sidebar = $("#sidebar"),$main = $(".main-content");
					if(_cname.indexOf("chevron-right")<0){
						$sidebar.addClass("sidebar-s");
						$main.addClass("main-con-s");
						$that.addClass("glyphicon-chevron-right");
					}else{
						$sidebar.removeClass("sidebar-s");
						$main.removeClass("main-con-s");
						$that.removeClass("glyphicon-chevron-right");
					}
					
			});
			
			return that;
		}
		
	});
	
	return module;
})(window.jQuery);

/**会议室维护**/
var MeetingRoom = (function($){
	var module = {};
	var options = {};	
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_getTableData();
			
		}
	});
	
	
	function _bindEvent(){
		Component.clearCache(clearUrl).leftMenuEvent();
		$(".multi-sel-list input").prop("checked",false);
		_checkAllCK();
		_selectCustom();
		_delMPos();
		_sureDelBuild();
		_mPosChange();
		_delMRoom();
		var $bbox = $("#addBuildingBox"),$ambox = $("#addMRoomBox");
		Component.focusTips($bbox.find("#buildingName"),"请输入楼栋或楼层",{
			trigger:"focus"
		});
		
		Component.focusTips($ambox.find("#building"),"请选择楼栋或楼层",{
			trigger:"focus"
		});

		Component.focusTips($ambox.find("#roomName"),"请输入会议室名称",{
			trigger:"focus"
		});
		
		Component.focusTips($ambox.find("#capacity"),"请输入容纳人数",{
			trigger:"focus"
		});
		
		Component.focusTips($ambox.find("#equipment"),"请输入设备情况",{
			trigger:"focus"
		});
		
		$bbox.on('hidden.bs.modal', function () {
			$bbox.find("#buildingName").val("");
		});
		
		$ambox.on('hidden.bs.modal', function () {
			$ambox.find("input").val("");
			$ambox.find("select").val("");
		})
		
		//添加楼栋或楼层
		$("#sureAddBulid").bind("click",function(){
			var $btn = $(this);
			var $mroom = $bbox.find("#buildingName"),
				_trimmr = $.trim($mroom.val());
				if(_trimmr ==""||_trimmr==null){
					$mroom.tooltip('show');
				}else{
					Component.getDataFromServer(options.addFloorUrl,{
						"buildingName":_trimmr
					},function(data){
						$btn.removeAttr("disabled");
						$("#pageBigTips").remove();
						$("#addBuildingBox").modal("hide");
						if(data.status=="0"){
							var obj = data.value;
							$(".multi-sel-list").append("<li><input id="+obj.id+" type=checkbox value="+obj.id+"><label for="+obj.id+">"+obj.buildingName+"</label></li>");
							_updateBoxFloor("add",obj.id,obj.buildingName);
						}else{
							Component.pageBigTips("w5-mauto pos-top",Component.warning+data.errorMsg,$body,"alert-warning");
						}
				    },{
				    	type:"post",
				    	beforeSend:function(){
				    		$btn.prop("disabled","disabled");
							Component.pageBigTips("pos-top w5-mauto",Component.loading+"正在添加，请稍候。。。",$body,"alert-info");
				    	},
				    	error:function(){
				    		$btn.removeAttr("disabled");
				    		$("#pageBigTips").remove();
				    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
				    	}
				    });
				}
		});

		//添加会议室
		$("#addMRoomBox #sureAddMRoom").bind("click",function(){
			var $btn = $(this),$box = $("#addMRoomBox");
			var obj = _validateIpt();
			if(obj.bool){
				_mRoperateAjax(options.addMRoomUrl,{
					"buildingId":obj.building,
					"roomName":obj.roomName,
					"capacity":obj.capacity,
					"equipment":obj.equipment
				},$btn,$box);
			}
		});
	}
	
	//删除会议室
	function _delMRoom(){
		var _ids = new Array();	
		$("#delMRoom").bind("click",function(){
			var $selDel = $("#example tbody input:checked");
			var $btn = $(this),$box = $("#addMRoomBox");
			if($selDel.length==0){
				Component.pageBigTips("w5-mauto pos-top",Component.warning+"请选择要删除的数据",$body,"alert-warning");
			}else{
				for(var i = 0;i < $selDel.length; i++){
					_ids.push($selDel[i].value);
				}
				var _strids = _ids.join(",");
				_mRoperateAjax(options.delMRoomUrl,{
					"ids":_strids
				},$btn,null);
			}
		});
	}
	
	//添加和删除请求
	function _mRoperateAjax(url,data,btn,box){
		Component.getDataFromServer(url,data,function(data){
			btn.removeAttr("disabled");
			$("#pageBigTips").remove();
			if(box){
				box.modal("hide");
			}
			if(data.status=="0"){
				$table.destroy();
				_getTableData();
			}else{
				Component.pageBigTips("w5-mauto pos-top",Component.warning+data.errorMsg,$body,"alert-warning");
			}
	    },{
	    	type:"post",
	    	beforeSend:function(){
	    		btn.prop("disabled","disabled");
				Component.pageBigTips("pos-top w5-mauto",Component.loading+"操作正在进行，请稍候。。。",$body,"alert-info");
	    	},
	    	error:function(){
	    		btn.removeAttr("disabled");
	    		$("#pageBigTips").remove();
	    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
	    	}
	      }
	   );
	}

	//及时更新弹出框下拉楼栋或楼层的值
	function _updateBoxFloor(type,val,text){
		var $build = $("#addMRoomBox #building");
		if(type=="add"){
			$build.append("<option value='"+val+"'>"+text+"</option>");
		}else{
			var _ids = val.split(",");
			for(var i = 0;i<_ids.length;i++){
				$build.find("option[value="+_ids[i]+"]").remove();
			}
			
		}
	}
	
	//添加会议室验证
	function _validateIpt(){
		var $mrpos = $("#addMRoomBox #building"),
			$mrname = $("#addMRoomBox #roomName"),
			$number = $("#addMRoomBox #capacity"),
			$remark = $("#addMRoomBox #equipment"),
			_trimmrp = $.trim($mrpos.val()),
			_trimmrn = $.trim($mrname.val()),
			_trimnum = $.trim($number.val()),
			_trimrmrk = $.trim($remark.val()),
			bool = true,
			objectData = new Array();
		
		if(_trimmrp ==""||_trimmrp==null){
			$mrpos.tooltip('show');
			bool = false;
		}
		
		if(_trimmrn ==""||_trimmrn==null){
			$mrname.tooltip('show');
			bool = false;
		}
		
		if(_trimnum ==""||_trimnum==null){
			$number.tooltip('show');
			bool = false;
		}else{
			 var re =  /^[1-9]+[0-9]*]*$/ ;  //判断正整数  
		     if (!re.test(_trimnum)){
		    	$number.tooltip('destroy');
				Component.focusTips($number,"请输入有效的人数",{
					trigger:"manual"
				});
				$number.tooltip('show');
		        return false;
		     }
			
		}
		
		if(_trimrmrk ==""||_trimrmrk==null){
			$remark.tooltip('show');
			bool = false;
		}
		Component.addParam(objectData,"building",_trimmrp);
		Component.addParam(objectData,"roomName",_trimmrn);
		Component.addParam(objectData,"capacity",_trimnum);
		Component.addParam(objectData,"equipment",_trimrmrk);
		Component.addParam(objectData,"bool",bool);
		return objectData;
	}
	
	//全选
	function _mPosChange(){
		
		$(".multi-sel-list").on("change"," input",function(){
			var $obj = $(this),$selectPos = $("#addMRoomBox #building");
			$obj.parent().prop("lang",$obj.prop("checked"));
			
		});
		
	}
	
	
	//删除楼栋或楼层
	function _delMPos(){
		$("#delMPos").bind("click",function(){
			var $selDel = $(".multi-sel-list input:checked");
			if($selDel.length==0){
				Component.pageBigTips("w5-mauto pos-top",Component.warning+"请选择要删除的数据",$body,"alert-warning");
			}else{
				$("#sureDelBox").modal("show");

			}
		});
	}
	
	//确定删除
	function _sureDelBuild(){
		var _ids = new Array();	
		$("#sureDelBox").on("click","#sureDelBuild",function(){
			var $obj = $(this);
			var $selDel = $(".multi-sel-list input:checked");
			for(var i = 0;i < $selDel.length; i++){
				var dom = $selDel[i];
				_ids.push(dom.value);
			}
			var _strids = _ids.join(",");
			Component.getDataFromServer(options.delFloorUrl,{
				"ids":_strids
			},function(data){
				//删除数据后回调函数
				$obj.removeAttr("disabled");
				$("#sureDelBox").modal("hide");
				$(".multi-sel-list li[lang=true]").remove();
				_updateBoxFloor("del",_strids,"");
				$table.destroy();
				_getTableData();
			},{
				type:"post",
				beforeSend:function(){
					$obj.prop("disabled","disabled");
				},
				error:function(){
					$obj.removeAttr("disabled");
		    		$("#pageBigTips").remove();
		    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
		    	}
			});
		});
	}
	
	
	//下拉列表绑定
	function _selectCustom(){
		$('#pos').selectbox({
			"width":285,
			"text":$("#pos option:selected").text(),
			"value":$("#pos").val()
		});
	}
	//表格当前页复选框全选
	function _checkAllCK(){
		$("#example").on("change",".check-all",function(){
			var $obj = $(this),_seledval = $obj.prop("checked");
			$("#example .checkbox").prop("checked",_seledval);
		});
	}
	
	//取表数据
	function _getTableData(){
		 $table = $('#example').DataTable({
			"oLanguage" : tableLanguage,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){
				
				$('td:first', row).html("<input type='checkbox' class='checkbox' value='"+data.id+"' />");
			},
			"searching": false,
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			"ajax": options.getTDataUrl,
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	return module;
	
})(window.jQuery);

/**项目查询**/
var project = (function($){
	var module = {};
	var options = {
	//	"getTHUrl":"",//表头请求地址
		"tableThData":{},
		"tableobj":null
	};	
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_bulidTable();
		}//初始化方法	
		
	});

	function _bindEvent(){
		Component.clearCache(clearUrl).leftMenuEvent();
		//下拉列表绑定
		var $column = $('#columnNum'),$prog = $('#programDept');
		$column.selectbox({
			"width":175,
			"text":$column.find("option:selected").text(),
			"value":$column.val()
		});
		$prog.selectbox({
			"width":175,
			'height':300,
			"text":$prog.find("option:selected").text(),
			"value":$prog.val()
		});
		//搜索按钮
		$("#search").bind("click",function(){
			 options.tableobj.destroy();
			_bulidTable();
		});
		
	}	
	
	function _getParam(){
		var _pname = options.ajaxData;
		var _param = {};
		Component.addParam(_param,_pname[0],$("#columnNum").val());
		Component.addParam(_param,_pname[1],$("#programDept").val());
		Component.addParam(_param,_pname[2],$("#startTime").val());
		Component.addParam(_param,_pname[3],$("#endTime").val());
		Component.addParam(_param,_pname[4],encodeURI($("#programName").val(),"UTF-8"));
		Component.addParam(_param,_pname[5],encodeURI($("#partner").val(),"UTF-8"));
		return _param;
	}
	//创建表格
	function _bulidTable(){
		var _data = _getParam();
		
		options.tableobj = $('#example').DataTable({
			"oLanguage" :tableLanguage,
			"processing": true,
			"serverSide": true,
			"ajax":{
				"url":options.getTDataUrl,
				"data":_data
			},
			"searching": false,
			"ordering":  false,     
			"aoColumns":options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});
	}
	//取表头数据Component.focusTips
	function _getTableHeadData(){
		Component.getDataFromServer(options.getTHUrl,null,function(data){
			options.tableThData = data;
			_bulidTable();
		},{});
	}
	
	
	return module;
})(window.jQuery);

/**统计分析**/
var StaAnalysis = (function($){
	var module = {};
	var options = {};
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			
		}//初始化方法	
		
	});
	function _bindEvent(){
		Component.clearCache(clearUrl).leftMenuEvent();
		//搜索按钮
		$("#btns button").bind("click",function(){
			var $btn = $(this),_url = $btn.data("url");
			$("#btns button.btn-success").removeClass("btn-success");
			$btn.addClass("btn-success");
			$("#reportFrame").prop("src",_url);
		});
	}	
	
	return module;
})(window.jQuery);	

/**系统反馈**/
var FeedBack = (function($){
	var module = {};
	var options = {
		"tableobj":null
	};	
	
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_bulidTable();
		},//初始化方法	
		detailInit:function(opt){
			$.extend(options,opt);	
			_detailPageEvent();
		}
	});
	
	function _detailPageEvent(){
		Component.clearCache(options.clearUrl).leftMenuEvent();
		//图片放大
		_zoomEvent();
		
		//系统消息
		_sysMsgSubmit();
		
		//提醒
		_remindSendEvent();
	}
	
	function _bindEvent(){
		Component.clearCache(clearUrl).leftMenuEvent();
		
		var $status = $('#status');
		//下拉列表绑定
		$status.selectbox({
			"width":175,
			"text":$status.find("option:selected").text(),
			"value":$status.val()
		});
		//搜索按钮
		$("#search").bind("click",function(){
			 options.tableobj.destroy();
			_bulidTable();
		});
		
		
	}	
	function _getParam(){
		var _pname = options.ajaxData;
		var _param = {};
		Component.addParam(_param,_pname[0],$("#status").val());
		Component.addParam(_param,_pname[1],$("#startTime").val());
		Component.addParam(_param,_pname[2],$("#endTime").val());
		return _param;
	}
	//创建表格
	function _bulidTable(){
		
		options.tableobj = $('#example').DataTable({
			"oLanguage" :tableLanguage,
			"processing": true,
			"serverSide": true,
			
			"ajax":{
				"url":options.getTDataUrl,
				"data":_getParam()
			}, 
			"searching": false,
			"ordering":  false,
			"fnRowCallback":function( row, data, displayIndex, displayIndexFull){
				var _durl = options.detailUrl.replace("{0}",data.id); 
				$('td:first', row).html("<div class=text-ellipsis title="+data.problem+">"+data.problem+"</div>");
				$('td:eq(3)', row).html("<div class=text-ellipsis title="+data.suggest+">"+data.suggest+"</div>");
				$('td:last', row).html('<a target="view_window" href="'+_durl+'" class="btn btn-success btn-xs" >处理</a>');
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});
	}
	
	//图片放大
	function _zoomEvent(){
		$.zoom.init({
			boxName : 'li',
			parentName : ".fb-pic-list",
			tagName : 'a'
		});
	}
	
	//系统消息提交
	function _sysMsgSubmit(){
		$("#send").bind("click",function(){
			var _msg = $("#content").val();
			var _param = options.feedbackInfo;
			var _data = {
				"commandInfo":{
					"commandType":0  //发送消息，2 提示
				},
				"content":'<label class="text-primary">系统留言：</label>'+_msg  ///消息内容 当commandType=0 有效	
			} 
			$.extend(true,_param,_data);
			var _json = JSON.stringify(_param);
			Component.getDataFromServer(options.sysSubmitUrl,{
				"feedbackInfo":_json
			},function(data){
				if(data.status=="0"){
					var li = new Array();
					li.push('<li><p>'+data.value.scurrentTime+'</p>');
					li.push('<p class="text">'+data.value.content+'</p></li>');
					$(".fb-info-list").append(li.join(""));
					$("#content").val("");
					$(document).scrollTop($(document).height());
				}else{
					Component.pageBigTips("w5-mauto pos-top",Component.warning+data.errorMsg,$body,"alert-warning");
				}
				
			},{type:"post"});
		});
	}
	
	//提醒发送
	function _remindSendEvent(){
		$("#remind").bind("click",function(){
			var _param = options.feedbackInfo;
			var _data = {
				"commandInfo":{
					"commandType":3  //发送消息，2 提示
				}
			} 
			$.extend(true,_param,_data);
			var _json = JSON.stringify(_param);
			Component.getDataFromServer(options.remindUrl,{
				"feedbackInfo":_json
			},
			function(data){
				if(data.status=="0"){
					Component.pageBigTips("w5-mauto pos-top",Component.okImg+"已提醒",$body,"alert-success");
				}else{
					Component.pageBigTips("w5-mauto pos-top",Component.warning+data.errorMsg,$body,"alert-warning");
				}
			});
		});
	}
	
	return module;
})(window.jQuery);	

/**登陆**/
var Login = (function($){
	var module = {};
	var options = {
			submitFormUrl:"",//表单提交地址
			mainUrl:""//登录成功之后跳转地址
	};	
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			
		}//初始化方法	
	});
	
	function _bindEvent(){
		//Component.clearCache(options.clearUrl);
		Component.focusTips($("#name"),"请输入用户名",{
			trigger:"focus",
			placement:"left"
		});
		
		Component.focusTips($("#pwd"),"请输入密码",{
			trigger:"focus",
			placement:"left"
		});
		
		Component.focusTips($("#code"),"请输入验证码",{
			trigger:"focus",
			placement:"left"
		});
		
		_loginEvent();
		
		_resetEvent();
		
		$(document).keydown(function(e){
			var curKey = e.which;
			if(curKey == 13){
				$("#login").click();
				return false;
			}
		}); 
		
		$(".code-pic a,.code-pic-change a").bind("click",function(){
			var date = new Date();
			$(".code-pic").find("img").attr('src','validateCode?date='+date.getTime());
			
		});
		
	}	
	
	//重置按钮
	function _resetEvent(){
		
		$("#reset").bind("click",function(){
			$("#name").val("");
			$("#pwd").val("");
			$("#code").val("");
		});
	}
	
	//登录按钮
	function _loginEvent(){
		$("#login").bind("click",function(){
			var _obj = _validateIpt(),
				$btn = $("#login"),
				_name = $("#name"),
				_pwd = $("#pwd"),
				_code = $("#code");
			if(_obj.bool){
				Component.getDataFromServer(options.submitFormUrl,{
					"name":_obj.name,
					"pwd":_obj.pwd,
					"validateCode":_obj.code
				},function(data){
					_name.removeAttr("disabled");
					_pwd.removeAttr("disabled");
					_code.removeAttr("disabled");
					$btn.removeAttr("disabled");
					$("#pageBigTips").remove();
					if(data.status==0){
						window.location = options.mainUrl;
					}else{
						Component.pageBigTips("pos-top w5-mauto",Component.warning+data.errorMsg,$body,"alert-warning");
					}
				},{
					type:"post",
					beforeSend:function(){
						_name.prop("disabled","disabled");
						_pwd.prop("disabled","disabled");
						_code.prop("disabled","disabled");
						$btn.prop("disabled","disabled");
						Component.pageBigTips("pos-top w5-mauto",Component.loading+"正在登录，请稍候。。。",$body,"alert-info");
					},
					error:function(){
						_name.removeAttr("disabled");
						_pwd.removeAttr("disabled");
						_code.removeAttr("disabled");
						$btn.removeAttr("disabled");
						$("#pageBigTips").remove();
						Component.pageBigTips("pos-top w5-mauto",Component.warning+"系统出错，请联系管理员。。。",$body,"alert-warning");
					}
				});
			}
		});
	}
	
	
	//验证是否输入用户名和密码
	function _validateIpt(){
		var $name = $("#name"),
			$pwd = $("#pwd"),
			$code = $("#code"),
			_trimname = $.trim($name.val()),
			_trimpwd = $.trim($pwd.val()),
			_trimcode = $.trim($code.val()),
			bool = true,objdata = new Array();
		if(_trimname ==""||_trimname==null){
			$name.tooltip('show');
//			_name.tooltip({
//				trigger:"manual"
//			});
			bool = false;
		}
		if(_trimpwd ==""||_trimpwd==null){
			$pwd.tooltip('show');
//			_pwd.tooltip({
//				trigger:"manual"
//			});
			bool = false;
		}
		if(_trimcode ==""||_trimcode==null){
			$code.tooltip('show');
			bool = false;
		}
		Component.addParam(objdata,"name",_trimname);
		Component.addParam(objdata,"pwd",_trimpwd);
		Component.addParam(objdata,"code",_trimcode);
		Component.addParam(objdata,"bool",bool);
		
		return objdata;
	}
	
	
	return module;
})(window.jQuery);

/**找回密码**/
var FindPwd = (function($){
	var module = {};
	var options = {};
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();		
		}//初始化方法	
	});
	
	function _bindEvent(){
		Component.clearCache(clearUrl).leftMenuEvent();
		Component.focusTips($("#name"),"请输入用户名",{
			trigger:"focus"
		});
		
		Component.focusTips($("#email"),"请输入邮箱",{
			trigger:"focus"
		});
		
		Component.focusTips($("#code"),"请输入验证码",{
			trigger:"focus"
		});
		
		_sureEvent();
	}	
	
	function _sureEvent(){
		$("#sure").bind("click",function(){
			var _bool = _validateIpt();
		});
	}
	//验证是否输入
	function _validateIpt(){
		var $name = $("#name"),
			$email = $("#email"),
			$code = $("#code"),
			_trimname = $.trim($name.val()),
			_trimemail = $.trim($email.val()),
			_trimcode = $.trim($code.val()),
			bool = true;
		var myreg = /^([a-zA-Z0-9]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
		if(_trimname ==""||_trimname==null){
			$name.tooltip('show');
			bool = false;
		}
		if(_trimemail ==""||_trimemail==null){
			$email.tooltip('destroy');
			Component.focusTips($email,"请输入邮箱",{
				trigger:"manual"
			});
			$email.tooltip('show');
			bool = false;
		}else{
			if(!myreg.test(_trimemail)){
				$email.tooltip('destroy');
				Component.focusTips($email,"请输入有效的邮箱地址",{
					trigger:"manual"
				});
				$email.tooltip('show');
				bool = false;
			 }
		}
		
		if(_trimcode ==""||_trimcode==null){
			$code.tooltip('show');
			bool = false;
		}
		return bool;
	}
	return module;
})(window.jQuery);

/**修改密码**/
var UpdatePwd = (function($){
	var module = {};
	
	var options = {};
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			
		}//初始化方法	
	});
	
	
	function _bindEvent(){
		Component.clearCache(options.clearUrl).leftMenuEvent();
		Component.focusTips($("#password"),"请输入密码",{
			trigger:"focus"
		});
		
		Component.focusTips($("#newpassword"),"请输入新密码",{
			trigger:"focus"
		});
		
		Component.focusTips($("#surepassword"),"请确认新密码",{
			trigger:"focus"
		});
		
		_sureEvent();
		
		$("#reset").bind("click",function(){
			$("input[class*='form-control']").val("");
		});
	}	
	
	function _sureEvent(){
		$("#sure").bind("click",function(){
			var _data = _validateIpt(),$btn = $(this);
			if(_data.bool){
				Component.getDataFromServer(options.editPwdUrl,{
					"oldPassword":_data.oldPassword,
					"newPassword":_data.newPassword
				},function(msg){
					$btn.removeAttr("disabled");
					$("#pageBigTips").remove();
					if(msg.status=="0"){
						Component.pageBigTips("w5-mauto pos-top",Component.okImg+"操作成功！！！",$body,"alert-success");
						window.location="login/doQuit.action";
					}else{
						Component.pageBigTips("w5-mauto pos-top",Component.warning+msg.errorMsg,$body,"alert-warning");
					}
			    },{
			    	type:"post",
			    	beforeSend:function(){
			    		$btn.prop("disabled","disabled");
						Component.pageBigTips("pos-top w5-mauto",Component.loading+"操作正在进行，请稍候。。。",$body,"alert-info");
			    	},
			    	error:function(){
			    		$btn.removeAttr("disabled");
			    		$("#pageBigTips").remove();
			    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
			    	}
			      }
			   );
			}
		});
	}
	
	//验证是否输入
	function _validateIpt(){
		var $pwd = $("#password"),
			$npwd = $("#newpassword"),
			$spwd = $("#surepassword"),
			_trimpwd = $.trim($pwd.val()),
			_trimnpwd = $.trim($npwd.val()),
			_trimspwd = $.trim($spwd.val()),
			_userId = $("#loginUserId").val(),
			objectData = new Array();
			bool = true;
		
		if(_trimpwd ==""||_trimpwd==null){
			$pwd.tooltip('show');
			bool = false;
		}
		
		if(_trimnpwd ==""||_trimnpwd==null){
			$npwd.tooltip('show');
			bool = false;
		}
		if(_trimspwd ==""||_trimspwd==null){
			$spwd.tooltip('show');
			bool = false;
		}
		if(_trimspwd!=_trimnpwd){
			$spwd.tooltip('show');
			bool = false;
		}
		Component.addParam(objectData,"oldPassword",_trimpwd);
		Component.addParam(objectData,"newPassword",_trimnpwd);
		Component.addParam(objectData,"userId",_userId);
		Component.addParam(objectData,"bool",bool);
		return objectData;
	}
	
	return module;
	
})(window.jQuery);

/**用户管理**/
var User = (function($){
	var module = {};
	var options = {};
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_getTableData();
		}
	});

	function _bindEvent(){
		var that = this;
		Component.clearCache(clearUrl).leftMenuEvent();
		var $abox = $("#addUserBox"),
			$ebox = $("#editUserBox"),
			$rbox = $("#roleBox");
		Component.focusTips($abox.find("#aname"),"用户名必须为4—16位的英文字母或数字",{
			trigger:"focus"
		});
		
		Component.focusTips($abox.find("#apwd"),"请填写登录密码",{
			trigger:"focus"
		});
		
		Component.focusTips($ebox.find("#epwd"),"请填写登录密码",{
			trigger:"focus"
		});
		
		$abox.on('hidden.bs.modal', function () {
			$abox.find("input").val("");
		});
		
		$ebox.on('hidden.bs.modal', function () {
			$ebox.find("input").val("");
		});
		
		$rbox.on('hidden.bs.modal', function () {
			$rbox.find("input").prop("checked",false);
		});
		
		_btnOperate();
		_addUserSubmit();
		_editUserSubmit();
		_checkAllCK();
		_sureDelUser();
		_roleEvent();
		//搜索按钮
		$("#search").bind("click",function(){
			$table.destroy();
			_getTableData();
		});
	}
	
	//表格当前页复选框全选
	function _checkAllCK(){
		$("#example").on("change",".check-all",function(){
			var $obj = $(this),_seledval = $obj.prop("checked");
			$("#example .checkbox").prop("checked",_seledval);
		});
	}
	
	//添加提交
	function _addUserSubmit(){
		var $box = $("#addUserBox");
		$box.on("click","#sureAdd",function(event){
			var $btn = $(this),
			    obj = _validateIpt($box);
			if(obj.bool){
				_operateAjax(options.addUrl,$btn,{
					"userName":obj.name,
					"password":obj.pwd
				},$box);
			}
		});
	}
	
	//编辑提交
	function _editUserSubmit(){
		var $box = $("#editUserBox");
		$box.on("click","#sureEdit",function(){
		    var $btn = $(this),
		    	obj = _validateIpt($box);
			if(obj.bool){
				_operateAjax(options.editUrl,$btn,{
					"userId":obj.id,
					"password":obj.pwd
				},$box);
			}
		});
	}

	//确定删除
	function _sureDelUser(){
		var $box = $("#sureDelBox");
		$box.on("click","#sureDelUser",function(){
			var $btn = $(this);
			var _userId = $box.find("#userId").val();
			_operateAjax(options.delUrl,$btn,{
				"userId":_userId
			},$box);
		});
	}
	
	//添加、编辑、删除、角色分配ajax请求方法
	function _operateAjax(url,btn,data,box){
		Component.getDataFromServer(url,data,function(msg){
			btn.removeAttr("disabled");
			$("#pageBigTips").remove();
			box.modal("hide");
			if(msg.status=="0"){
				Component.pageBigTips("w5-mauto pos-top",Component.okImg+"操作成功！！！",$body,"alert-success");
				$table.destroy();
				_getTableData();
			}else{
				Component.pageBigTips("w5-mauto pos-top",Component.warning+msg.errorMsg,$body,"alert-warning");
			}
	    },{
	    	type:"post",
	    	beforeSend:function(){
	    		btn.prop("disabled","disabled");
				Component.pageBigTips("pos-top w5-mauto",Component.loading+"操作正在进行，请稍候。。。",$body,"alert-info");
	    	},
	    	error:function(){
	    		btn.removeAttr("disabled");
	    		$("#pageBigTips").remove();
	    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
	    	}
	      }
	   );
	}

	//添加验证
	function _validateIpt(box){
		var $box = box,
			$name = $box.find("input[name=name]"),
			$pwd = $box.find("input[name=pwd]"),
			_trimn = $.trim($name.val()),
			_trimp = $.trim($pwd.val()),
			_id = $box.find("input[name=id]").val(),
			bool = true,
			objectData = new Array();
		 var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/; 
		if(_trimn ==""||_trimn==null){
			$name.tooltip('show');
			bool = false;
		}else{
			if(reg.test(_trimn)){
				$name.tooltip('show');
				bool = false;
			}
		}
		
		if(_trimp ==""||_trimp==null){
			$pwd.tooltip('show');
			bool = false;
		}
		Component.addParam(objectData,"name",_trimn);
		Component.addParam(objectData,"pwd",_trimp);
		Component.addParam(objectData,"id",_id);
		Component.addParam(objectData,"bool",bool);
		return objectData;
	}
	
	//操作
	function _btnOperate(){
		$("#example").on("click","tbody .td-btn",function(event){
			var $td = $(this),
				$target = $(event.target),
				_name = $target.data("btn"),
				_user = $td.parent().data("user");
			if(_name=="edit"){
				_showEditModal("#editUserBox",_user);
			}
			
			if(_name=="role"){
				_showRoleModal("#roleBox",_user);
			}
			
			if(_name=="del"){
				var $delbox = $("#sureDelBox");
					$delbox.find("#userId").val(_user.id);
					$delbox.modal("show");
			}
		});
	}
	
	//编辑框
	function _showEditModal(id,data){
		var $modal = $(id),_user = data;
		$modal.find("#eid").val(_user.id);
		$modal.find("#ename").val(_user.name);
		//$modal.find("#epwd").val(_user.pwd);
		$modal.modal("show");
	}
	
	//角色框
	function _showRoleModal(id,data){
		var $modal = $(id),_user = data,_rids = _user.roleIds;
		$modal.find("#userId").val(_user.id);
		if(_rids!=""&&_rids!=null){
			var _ids = _rids.split(",");
			for(var i = 0;i<_ids.length;i++){
				$modal.find("[value='"+_ids[i]+"']").prop("checked",true);
			}
		}
		$modal.modal("show");
	}
	
	//角色设置
	function _roleEvent(){
		var $box = $("#roleBox");
		$box.on("click","#sureRole",function(){
			var $btn = $(this),
		        $ckboxs = $box.find("input:checked"),
		        userId = $box.find("#userId").val(),
		        roleIds = new Array();
			for(var i = 0;i<$ckboxs.length;i++){
				roleIds.push($($ckboxs[i]).val());
			}
			_operateAjax(options.roleUrl,$btn,{
				"userId":userId,
				"roleIds":roleIds.join(",")
			},$box);
		});
	}
	
	//取表数据
	function _getTableData(){
		var _pname = options.ajaxData,_param = {};
		var _suname = $("#suname").val();
		Component.addParam(_param,_pname[0],encodeURI(_suname,"UTF-8"));
		 $table = $('#example').DataTable({
			"oLanguage" : tableLanguage,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){
				$(row).data("user",{"id":data.userId,"name":data.userName,"pwd":data.password,"roleIds":data.roleIds});
				$('td:first', row).width(300).css("padding-left","80px");
				var last = ['<button class="btn btn-xs btn-success" data-btn="edit" title="编辑"><span data-btn="edit" class="glyphicon glyphicon-pencil"></span></button>',
				            '<button class="btn btn-xs btn-default" data-btn="role" title="角色分配"><span data-btn="role" class="glyphicon glyphicon-cog"></span></button>',
				            '<button class="btn btn-xs btn-danger" data-btn="del" title="删除"><span data-btn="del" class="glyphicon glyphicon-trash"></span></button>'];
				$('td:last', row).html(last.join("")).addClass("td-btn");
				
			},
			"searching": false,
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			ajax: {
				url:options.getTDataUrl,
				data:_param
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	return module;
	
})(window.jQuery);

/**角色管理**/
var Role = (function($){
	var module = {};
	var options = {};
	var $body = $("body");
	var treeobj = {};
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_getTableData();
		}
	});
	
	function _bindEvent(){
		var that = this;
		Component.clearCache(clearUrl).leftMenuEvent();
		var $abox = $("#addBox"),
			$mbox = $("#menuBox"),
			$ebox = $("#editBox");
		Component.focusTips($abox.find("#name"),"请填写角色名",{
			trigger:"focus"
		});
		
		Component.focusTips($ebox.find("#name"),"请填写角色名",{
			trigger:"focus"
		});
		
		$abox.on('hidden.bs.modal', function () {
			$abox.find("input").val("");
			$abox.find("textarea").val("");
		});
		
		$ebox.on('hidden.bs.modal', function () {
			$ebox.find("input").val("");
			$ebox.find("textarea").val("");
		});
		
		_btnOperate();
		_addSubmit();
		_editSubmit();
		_sureDelUser();
		_sureSetMenu();
		//搜索按钮
		$("#search").bind("click",function(){
			$table.destroy();
			_getTableData();
		});
		
		$mbox.on('hidden.bs.modal', function () {
			treeobj.checkAllNodes(false);
		});
		
		_initTree();
		
		$mbox.on('shown.bs.modal', function () {
			$("#menuBox .boxmenu-wrap").perfectScrollbar({
				suppressScrollX: true
			});
		});
	}
	
	function _initTree(){
		$.fn.zTree.init($("#treeDemo"), options.treeSet, options.treeData[0].children);
		treeobj = $.fn.zTree.getZTreeObj("treeDemo");
		treeobj.expandAll(true);
		$("#menuBox #rootId").val(options.treeData[0].id);
	}
	//操作
	function _btnOperate(){
		$("#example").on("click","tbody .td-btn",function(event){
			var $td = $(this),
				$target = $(event.target),
				_name = $target.data("btn"),
				_role = $td.parent().data("role");
			if(_name=="edit"){
				_showEditModal("#editBox",_role);
			}
			
			if(_name=="menu"){
				_showMenuModal("#menuBox",_role);
			}
			
			if(_name=="del"){
				var $delbox = $("#sureDelBox");
					$delbox.find("#roleId").val(_role.id);
					$delbox.modal("show");
			}
		});
	}
	
	//菜单框
	function _showMenuModal(id,data){
		var $modal = $(id),_role = data;
		$modal.find("#roleId").val(_role.id);
		var _menuIds = _role.menuIds;
		if(_menuIds!=""&&_menuIds!=null){
			var arr = _menuIds.split(",");
			
			for(var i = 1;i<arr.length;i++){
				var node = treeobj.getNodeByParam("id", arr[i], null);
				treeobj.checkNode(node, true, false);
			}
		}
		$modal.modal("show");
	}
	
	//编辑框
	function _showEditModal(id,data){
		var $modal = $(id),_role = data;
		$modal.find("#roleId").val(_role.id);
		$modal.find("#name").val(_role.name);
		$modal.find("#description").val(_role.desc);
		$modal.modal("show");
	}
	
	//添加提交
	function _addSubmit(){
		var $box = $("#addBox");
		$box.on("click","#sureAdd",function(event){
			var $btn = $(this),
			    obj = _validateIpt($box);
			if(obj.bool){
				_operateAjax(options.addUrl,$btn,{
					"roleName":obj.name,
					"description":obj.desc
				},$box);
			}
		});
	}
	
	//编辑提交
	function _editSubmit(){
		var $box = $("#editBox");
		$box.on("click","#sureEdit",function(){
		    var $btn = $(this),
		    	obj = _validateIpt($box);
			if(obj.bool){
				_operateAjax(options.editUrl,$btn,{
					"roleId":obj.id,
					"roleName":obj.name,
					"description":obj.desc
				},$box);
			}
		});
	}

	//确定删除
	function _sureDelUser(){
		var $box = $("#sureDelBox");
		$box.on("click","#sureDel",function(){
			var $btn = $(this);
			var _roleId = $box.find("#roleId").val();
			_operateAjax(options.delUrl,$btn,{
				"roleId":_roleId
			},$box);
		});
	}
	
	//确定菜单分配
	function _sureSetMenu(){
		var $box = $("#menuBox");
		$box.on("click","#sureMe",function(){
			var $btn = $(this);
			var _roleId = $box.find("#roleId").val();
			var _nodes = treeobj.getCheckedNodes(true);
			var _rootId = $box.find("#rootId").val();
			var _menuIds = new Array();
			for(var i = 0;i<_nodes.length;i++){
				_menuIds.push(_nodes[i].id);
			}
			if(_menuIds.length>0){
				_menuIds.unshift(_rootId);
			}
			
			_operateAjax(options.setMenuUrl,$btn,{
				"roleId":_roleId,
				"menuIds":_menuIds.join(",")
			},$box);
		});
	}
	
	//添加、编辑、删除、分配菜单ajax请求方法
	function _operateAjax(url,btn,data,box){
		Component.getDataFromServer(url,data,function(msg){
			btn.removeAttr("disabled");
			$("#pageBigTips").remove();
			box.modal("hide");
			if(msg.status=="0"){
				Component.pageBigTips("w5-mauto pos-top",Component.okImg+"操作成功！！！",$body,"alert-success");
				$table.destroy();
				_getTableData();
			}else{
				Component.pageBigTips("w5-mauto pos-top",Component.warning+msg.errorMsg,$body,"alert-warning");
			}
	    },{
	    	type:"post",
	    	beforeSend:function(){
	    		btn.prop("disabled","disabled");
				Component.pageBigTips("pos-top w5-mauto",Component.loading+"操作正在进行，请稍候。。。",$body,"alert-info");
	    	},
	    	error:function(){
	    		btn.removeAttr("disabled");
	    		$("#pageBigTips").remove();
	    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
	    	}
	      }
	   );
	}

	//添加验证
	function _validateIpt(box){
		var $box = box,
			$name = $box.find("input[name=name]"),
			_desc = $box.find("textarea[name=description]").val(),
			_trimn = $.trim($name.val()),
			_id = $box.find("input[name=roleId]").val(),
			bool = true,
			objectData = new Array();
		if(_trimn ==""||_trimn==null){
			$name.tooltip('show');
			bool = false;
		}
		Component.addParam(objectData,"name",_trimn);
		Component.addParam(objectData,"desc",_desc);
		Component.addParam(objectData,"id",_id);
		Component.addParam(objectData,"bool",bool);
		return objectData;
	}
	
	//取表数据
	function _getTableData(){
		var _pname = options.ajaxData,_param = {};
		var _srname = $("#srname").val();
		Component.addParam(_param,_pname[0],encodeURI(_srname,"UTF-8"));
		 $table = $('#example').DataTable({
			"oLanguage" : tableLanguage,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){
				$(row).data("role",{"id":data.roleId,"name":data.roleName,"desc":data.description,"menuIds":data.menuIds});
				$('td:first', row).width(300).css("padding-left","80px");
				var last = ['<button class="btn btn-xs btn-success" data-btn="edit" title="编辑"><span data-btn="edit" class="glyphicon glyphicon-pencil"></span></button>',
				            '<button class="btn btn-xs btn-default" data-btn="menu" title="分配菜单"><span data-btn="menu" class="glyphicon glyphicon-cog"></span></button>',
				            '<button class="btn btn-xs btn-danger" data-btn="del" title="删除"><span data-btn="del" class="glyphicon glyphicon-trash"></span></button>'];
				$('td:last', row).html(last.join("")).addClass("td-btn");
				if(data.roleId=="011"){
					$(row).find(".btn-danger,.btn-default").prop("disabled","disabled");
					//$(row).find("").prop("disabled","disabled");
				}
			},
			"searching": false,
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			ajax: {
				url:options.getTDataUrl,
				data:_param
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	return module;
	
})(window.jQuery);

/**签到日期维护**/
var WeekDay = (function($){
	var module = {};
	var options = {};
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_getTableData();
		}
	});
	
	function _bindEvent(){
		var that = this,_year = new Date().getFullYear();
		
		Component.clearCache(clearUrl).leftMenuEvent();
		var $abox = $("#addBox");
		$abox.find("#weekday").focus(function(){
			WdatePicker({minDate:_year});
		});
		Component.focusTips($abox.find("#weekday"),"请选择日期",{
			trigger:"focus"
		});
		
		$abox.on('hidden.bs.modal', function () {
			$abox.find("input").val("");
		});

		_btnOperate();
		_addSubmit();
		_sureDel();
		//搜索按钮
		$("#search").bind("click",function(){
			$table.destroy();
			_getTableData();
		});
		
	}
	
	//操作
	function _btnOperate(){
		$("#example").on("click","tbody .td-btn",function(event){
			var $td = $(this),
				$target = $(event.target),
				_name = $target.data("btn"),
				_date = $td.parent().data("date");
			if(_name=="del"){
				var $delbox = $("#sureDelBox");
					$delbox.find("#dateId").val(_date.id);
					$delbox.modal("show");
			}
		});
	}
	
	//添加提交
	function _addSubmit(){
		var $box = $("#addBox");
		$box.on("click","#sureAdd",function(event){
			var $btn = $(this),
			    obj = _validateIpt($box);
			if(obj.bool){
				_operateAjax(options.addUrl,$btn,{
					"weekday":obj.weekday,
					"weekdayType":obj.weekdayType
				},$box);
			}
		});
	}
	

	//确定删除 
	function _sureDel(){
		var $box = $("#sureDelBox");
		$box.on("click","#sureDel",function(){
			var $btn = $(this);
			var _dateId = $box.find("#dateId").val();
			_operateAjax(options.delUrl,$btn,{
				"id":_dateId
			},$box);
		});
	}
	
	
	//添加、删除ajax请求方法
	function _operateAjax(url,btn,data,box){
		Component.getDataFromServer(url,data,function(msg){
			btn.removeAttr("disabled");
			$("#pageBigTips").remove();
			box.modal("hide");
			if(msg.status=="0"){
				Component.pageBigTips("w5-mauto pos-top",Component.okImg+"操作成功！！！",$body,"alert-success");
				$table.destroy();
				_getTableData();
			}else{
				Component.pageBigTips("w5-mauto pos-top",Component.warning+msg.errorMsg,$body,"alert-warning");
			}
	    },{
	    	type:"post",
	    	beforeSend:function(){
	    		btn.prop("disabled","disabled");
				Component.pageBigTips("pos-top w5-mauto",Component.loading+"操作正在进行，请稍候。。。",$body,"alert-info");
	    	},
	    	error:function(){
	    		btn.removeAttr("disabled");
	    		$("#pageBigTips").remove();
	    		Component.pageBigTips("w5-mauto pos-top",Component.warning+"系统出错，请联系管理员。",$body,"alert-warning");
	    	}
	      }
	   );
	}

	//添加验证
	function _validateIpt(box){
		var $box = box,
			$date = $box.find("input[name=weekday]"),
			_type = $box.find("#wkType").val(),
			_trimd = $.trim($date.val()),
			bool = true,
			objectData = new Array();
		if(_trimd ==""||_trimd==null){
			$date.tooltip('show');
			bool = false;
		}
		Component.addParam(objectData,"weekday",_trimd);
		Component.addParam(objectData,"weekdayType",_type);
		Component.addParam(objectData,"bool",bool);
		return objectData;
	}
	
	//取表数据
	function _getTableData(){
		var _pname = options.ajaxData,_param = {};
		var _date = $("#weekday").val();
		Component.addParam(_param,_pname[0],_date);
		 $table = $('#example').DataTable({
			"oLanguage" : tableLanguage,
			fnRowCallback:function( row, data, displayIndex, displayIndexFull){
				$(row).data("date",{"id":data.id});
				$('td:first', row).css("padding-left","80px");
				var last = ['<button class="btn btn-xs btn-danger" data-btn="del" title="删除"><span data-btn="del" class="glyphicon glyphicon-trash"></span></button>'];
				$('td:last', row).html(last.join("")).addClass("td-btn");
			},
			"searching": false,
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			ajax: {
				url:options.getTDataUrl,
				data:_param
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	return module;
	
})(window.jQuery);

/**考勤记录**/
var AttendanceRecord = (function($){
	var module = {};
	var options = {};
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			_getTableData();
		}
	});
	
	function _bindEvent(){
		Component.clearCache(clearUrl).leftMenuEvent();
		//搜索按钮
		$("#search").bind("click",function(){
			$table.destroy();
			_getTableData();
		});
		
	}
	
	function _getParam(){
		var _pname = options.ajaxData,
			_param = {},
		    _stime = $("#startTime").val(),
			_etime = $("#endTime").val();
		Component.addParam(_param,_pname[0],_stime);
		Component.addParam(_param,_pname[1],_etime);
		return _param;
	}

	//取表数据
	function _getTableData(){
		 var _data = _getParam();
		 $table = $('#example').DataTable({
			"oLanguage" : tableLanguage,
			
			"searching": false,
			"ordering":  false,
			"processing": true,
			"serverSide": true,
			ajax: {
				url:options.getTDataUrl,
				data:_data
			},
			"aoColumns" : options.columns,
			"dom": 'rt<"page-bottom clearfix"lip>'
		});	
	}
	
	return module;
	
})(window.jQuery);

/**首页**/
var Index = (function($){
	var module = {};
	var options = {};
	var $body = $("body");
	$.extend(module,{	
		init:function(opt){
			$.extend(options,opt);	
			_bindEvent();	
			
		}
	});
	
	function _bindEvent(){
		var that = this;
		Component.clearCache(clearUrl).leftMenuEvent();
		
	}
	
	return module;
	
})(window.jQuery);
