// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
// 例子： 
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};
var $body = $("body");
//操作提示框
(function ($) {	
	$.fn.msgBox = function(options){
		var settings = {
	        status: 'success',//success,info,error
	        msg: '成功',
	        istime:true,
	        time:3000,
	        end:function(){}
	    };
		 return this.each(function () { 
	        var opts =  $.extend(settings, options);
	        var sClass,status = opts.status,msg = opts.msg,t= opts.time;
            switch(status){
            	case "loading":
            		sClass = "alert-info";
            		msg = '<i class="icon-spinner icon-spin"></i>' + msg;
            		opts.istime = false;
            		break;
	            case "success":
	              opts.istime = true;
	              sClass = "alert-success";
	              break;
	            case "danger":
		              opts.istime = true;
		              sClass = "alert-danger";
		              break;
	            case "info":
	              opts.istime = true;
	              sClass = "alert-info";
	              break;
	            case "error":
	              opts.istime = true;
	              sClass = "alert-error";
	              break;
	            }
	            var msgHtml = '<div class="alert alert-modal '+sClass+'">'+msg+'</div>';
	            var backdrop = '<div class="modal-backdrop alert-modal-backdrop"></div>';
	            $body.append(msgHtml);
	            if($(".modal-backdrop").length==0){
	            	$body.append(backdrop);
	            }
	            if(opts.istime){
	            	setTimeout(function(){
	                    $(".alert-modal,.alert-modal-backdrop").remove();
	                    opts.end();
	                },t); 
	            }
		 });
		
	};
	$.msgBox = {
		close : function(){
			$(".alert-modal,.alert-modal-backdrop").remove();
		}
	};
})(window.jQuery);
(function ($) {
    $.fn.modalBox = function(options){
        var settings = {
            title: '确认',//success,info,error
            msg: '确认删除？',
            okFun:function(){}
        };
        return this.each(function () { 
        	var opts =  $.extend(settings, options);
            var modal = '<div id="sureBox" class="modal hide fade">'+
            '<div class="modal-header">'+
              '<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>'+
              '<h3>'+opts.title+'</h3>'+
           ' </div>'+
            '<div class="modal-body">'+
              '<p>'+opts.msg+'</p>'+
            '</div>'+
            '<div class="modal-footer">'+
              '<a href="javascript:void(0)" class="cancel btn">取消</a>'+
              '<a href="javascript:void(0)" class="sure btn orange">确认</a>'+
            '</div></div>';
            $("#sureBox").remove();
    		$(modal).appendTo("body").modal("show");
    		$("#sureBox").on("click",".cancel",function(event){
    			$("#sureBox").modal("hide");
    		});
    		$("#sureBox").on("click",".sure",function(event){
    			opts.okFun();
    		});
    		
        });
        
    };
    $.modalBox = {
		close : function(){
	    	$("#sureBox").modal("hide");
	    }	
    };
})(window.jQuery);
/**公共组件**/
var plugin = (function($){
	var module = {};
	var isSending = false;
	$.extend(module,{	
		tLanguage : {
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
		},
		refreshEvent:function(){
			var that = this;
			$("#refreshBtn").bind("click",function(){
				$(".form-horizontal input").val("");
				$(".form-horizontal select").val("");
				$table.destroy();
				that.getTableData();
			});
		},
		handleDatePickers:function(){
			if (jQuery().datepicker) {
	            $('.date-picker').datepicker({
	                format: 'yyyy-mm-dd',
	                autoclose: true,
	                todayBtn: 'linked',
	            });
	        }
		},
		//服务端加载数据
		doGet : function(url,data,callback,option){
			var defaultOption = {
				type : 'get',
				url : url,
				data : data || {},
				dataType : "json",
				beforeSend:function(){
					isSending = true;
					$body.msgBox({
						 status : 'loading',//success,info,error
				         msg : "正在加载，请稍候"
					});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					isSending = false;
					$.msgBox.close();
					$body.msgBox({
						 status : 'error',//success,info,error
				         msg : textStatus
					});
				},
				complete:function(){
					isSending = false;
					//$.msgBox.close();
				},
				success : function(data) {
					isSending = false;
					$.msgBox.close();
					if (callback) {
						callback(data);
					}
				}
			};

			if (option) {
				$.extend(defaultOption, option);
			}
			if(!isSending){
				$.ajax(defaultOption);
			}
			

		},
		//操作数据
		doPost : function(url,data,callback,btn){
			var defaultOption = {
				type : 'post',
				url : url,
				data : data || {},
				dataType : "json",
				beforeSend:function(){
					isSending = true;
					if(btn){
						btn.prop("disabled","disabled");
					}
					$body.msgBox({
						 status : 'loading',//success,info,error
				         msg : "操作正在进行，请稍候"
					});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					isSending = false;
					if(btn){
						btn.removeProp("disabled");
					}
					$.msgBox.close();
					$body.msgBox({
						 status : 'error',//success,info,error
				         msg : textStatus
					});
				},
				complete:function(){
					isSending = false;
					if(btn){
						btn.removeProp("disabled");
					}
				},
				success : function(data) {
					isSending = false;
					if(btn){
						btn.removeProp("disabled");
					}
					$.msgBox.close();
					if (callback) {
						callback(data);
					}
				}
			};

//			if (option) {
//				$.extend(defaultOption, option);
//			}
			if(!isSending){
				$.ajax(defaultOption);
			}

		},
		//删除
		doDel:function(param,url,text,endfun){
			$body.modalBox({
				msg:text,
				okFun:function(){
					plugin.doPost(url,param,function(data){
						$.modalBox.close();
						if (data.status === 0) {
							$body.msgBox({
								 status : 'success',
						         msg : "已删除",
						         time:1000,
						         end:endfun
							});
						} else {
							$body.msgBox({
								 status : 'error',
						         msg : data.errorMsg
							});
						}
					});
				}
			});
		},
		//添加
		doAdd:function(options){
			var opts = options;
			plugin.doPost(opts.url,opts.param,function(data){				
				if (data.status === 0) {
					if(data.value==null){
						$body.msgBox({
							 status : 'error',
					         msg : data.errorMsg
						});
					}else{
						if(opts.box){opts.box.modal('hide');}
						
						$body.msgBox({
							 status : 'success',
					         msg : "操作成功",
					         time:1000,
					         end:opts.endfun(data)
						});
					}
					
				} else {
					$body.msgBox({
						 status : 'error',
				         msg : data.errorMsg == null ? "程序有误，请稍候再试" : data.errorMsg
					});
				}
			},opts.btn);
		},
		doSetting:function(options){
			var opts = options;
			plugin.doPost(opts.url,opts.param,function(data){				
				if (data.status === 0) {
					if(data.value==null){
						$body.msgBox({
							 status : 'error',
					         msg : data.errorMsg
						});
					}else{
						$body.msgBox({
							 status : 'success',
					         msg : "操作成功",
					         time:1000,
					         end:opts.endfun(data)
						});
					}
					
				} else {
					$body.msgBox({
						 status : 'error',
				         msg : data.errorMsg == null ? "程序有误，请稍候再试" : data.errorMsg
					});
				}
			});	
		},
		//封装对象
		addParam : function(params,paramName,paramVal){
			params[paramName] = paramVal;
		},
		//开始不能大于结束时间
		checkTime:function(stime,etime){
			var _bool = true;
			var _startNum = new Date(stime).getTime();
			var _endNum = new Date(etime).getTime();
			if (_endNum < _startNum) {
				$body.msgBox({
					 status : 'error',//success,info,error
			         msg : "“结束时间 ”不能早于“开始时间 ” ",
			         time : 2000
				});
				_bool = false;
			}
			return _bool;
		}
		
	});
	return module;
})(window.jQuery);
