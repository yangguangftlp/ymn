var isSupportTouch = "ontouchend" in document ? true : false, touchEv = isSupportTouch ? 'touchend'
		: 'click';
+function($) {
	// Toast类定义
	var Toast = function(el, options) {
		this.options = options;
		this.$el = el;
		this.init();
	};
	Toast.prototype.Defaults = {
		msg : "成功...",// 显示信息
		url : "",
		transferType : "post",
		time : 500,// 模态框消失延迟的时间
		type : 1,// 1代表成功后提示，2代表表单验证提示,3代表温馨提示,4退回原因,5删除，6筛选条件,7增加会议室信息，8修改会议室信息
		autoFill : false,
		missingText : "通过",// 没有内容的时候，默认回填内容
		start : function() {
		},
		end : function() {
		},
		dataChange : function($val) {
			return $val;
		},
		callMode : "plugin"// plugin代表点击事件，func代表直接执行动画
	};
	Toast.prototype.init = function() {
		var ops = $.extend({}, this.Defaults, this.options);
		if (ops.callMode == "plugin") {
			this.$el.trigger("click");
		}
		if (ops.callMode == "func") {
			Toast.prototype.lifeStart(ops);
			return;
		}
		this.$el.click(function() {
			Toast.prototype.lifeStart(ops);
		});
	};
	Toast.prototype.lifeStart = function(ops) {
		Toast.prototype.judgeType(ops);
	};
	Toast.prototype.judgeType = function(ops) {
		if (ops.type == 1) {
			$("body").append(
					'<div class="screenModal">' + '<div class="promptModal">'
							+ ops.msg + '</div>' + '</div>');
			var $screenModal = $('.screenModal');
			setTimeout(function() {
				$screenModal.stop().animate({
					opacity : 0
				}, 500, function() {
					$screenModal.css("opacity", 1).remove();
					ops.end();
				});
			}, ops.time);
		} else if (ops.type == 2) {
			$("body")
					.append(
							'<div class="screenModal">'
									+ '<div class="onePrompt"><i class="fa fa-exclamation-circle"></i>'
									+ ops.msg + '</div>' + '</div>');
			var $screenModal = $('.screenModal');
			$(".onePrompt").stop().animate({
				top : "0px"
			}, 500, function() {
				setTimeout(function() {
					$(".onePrompt").stop().animate({
						top : "-40px"
					}, 500, function() {
						$screenModal.remove();
						ops.end();
					});
				}, ops.time);
			});
		} else if (ops.type == 3) {
			$("body").append(
					'<div class="screenModal">' + '<div class="twoPromptCon">'
							+ '<header>温馨提示</header>' + '<div>' + ops.msg
							+ '</div>' + '<footer>'
							+ '<button type="button">取消</button>'
							+ '<button type="button" id="sureBtn">确定</button>'
							+ '</footer>' + '</div>' + '</div>');
			var $screenModal = $('.screenModal');
			$(".twoPromptCon footer button").click(function() {
				var $thisId = $(this).attr("id");
				$screenModal.stop().animate({
					opacity : 0
				}, 500, function() {
					$screenModal.css("opacity", 1).remove();
					if ($thisId == "sureBtn") {
						ops.end();
					}
				});
			});
		} else if (ops.type == 4) {
			/*
			 * $scrollTop=$("body").scrollTop();
			 * $("body").addClass("windowHeight");
			 */
			$("body")
					.append(
							'<div class="reasonMyModal">'
									+ '<div class="returnReasons">'
									+ '<form id="returnForm" action='
									+ ops.url
									+ ' method='
									+ ops.transferType
									+ '>'
									+ '<header>'
									+ ops.msg
									+ '</header>'
									+ '<textarea placeholder="在此填写'
									+ ops.msg
									+ '" name="reasons"  required="required"></textarea>'
									+ '<footer>'
									+ '<button type="reset">取消</button>'
									+ '<button type="button">确定</button>'
									+ '</footer>' + '</form>' + '</div>'
									+ '</div>');
			// 取消退回模态框
			$(".returnReasons footer button:eq(0)").click(function() {
				$(".reasonMyModal").animate({
					opacity : 0
				}, 300, function() {
					$(".reasonMyModal").remove();
				});
			});
			// 确认退回原因
			$(".returnReasons footer button:eq(1)").click(function(e) {
				var $textareaVal = $(".returnReasons textarea").val();
				if (!ops.autoFill && $textareaVal == "") {
					$(".returnReasons textarea").addClass("placeholderStyle");
				} else {
					$(".reasonMyModal").animate({
						opacity : 0
					}, 300, function() {
						ops.dataChange($(".returnReasons textarea").val());
						$(".reasonMyModal").remove();
					});
				}
			});

		} else if (ops.type == 5) {
			var $html = '<div class="screenModal delImgModal">'
					+ '<div class="delImg">' + '<header>' + ops.msg
					+ '</header>' + '<footer>'
					+ '<button id="quXiao">取消</button>' + '<button>确定</button>'
					+ '</footer>' + '</div>' + '</div>';
			$("body").append($html);

			if ($(".delImg header").height() < 50) {
				$(".delImg header").css("lineHeight", "60px");
			}
			;
			// 取消删除
			$("#quXiao").click(function() {
				$(".delImgModal").remove();
				ops.start();
			});
			// 确认删除
			$(".delImg footer button:eq(1)").click(function() {
				$(".delImgModal").remove();
				// 回调
				ops.end();
			});
		} else if (ops.type == 6) {// 筛选会议室
			var _val = [];
			var arr = ops.data;
			$("body").append(arr.join(""));
			if (ops.apt) {
				ops.apt();
			}
			// 取消模态框
			$(".returnReasons footer button:eq(0)").click(function() {
				$(".reasonMyModal").animate({
					opacity : 0
				}, 300, function() {
					$(".reasonMyModal").remove();
				});
			});
			// 确认筛选条件
			$(".returnReasons footer button:eq(1)").click(function(e) {
				var $screenItems = $(".returnReasons #screenItems").val();
				var $peopleSorts = $(".returnReasons #peopleSorts").val();
				if (!$peopleSorts == '') {
					if (!/^[0-9]*[1-9][0-9]*$/.test($peopleSorts)) {
						$("body").toast({
							msg : "容纳人数请填写为正整数",
							type : 2,
							callMode : "func"
						});
						return

					}
				}
				_val.push($screenItems);
				_val.push($peopleSorts);
				$(".reasonMyModal").animate({
					opacity : 0
				}, 300, function() {
					ops.dataChange(_val);
					$(".reasonMyModal").remove();
				});
			});
		} else if (ops.type == 7) {// 添加会议室
			$("body")
					.append(
							'<div class="reasonMyModal">'
									+ '<div class="returnReasons">'
									+ '<form id="returnForm" action='
									+ ops.url
									+ ' method='
									+ ops.transferType
									+ '>'
									+ '<header>'
									+ ops.msg
									+ '</header>'
									+
									/*
									 * '<textarea placeholder="在此填写'+ops.msg+'"
									 * name="reasons" required="required"></textarea>'+
									 */
									'<div class="formList clear">'
									+ '<div class="listRight input-append controls">'
									+ '<input type="text" name="meetingName" id="meetingName" class="listRightPadding6" placeholder="请填写名称" data-con="*" data-empty="请填写名称" value="">'
									+ '</div>'
									+ '<div class="listLeft listNameWidth6">'
									+ '<span class="title">会议室名称:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear">'
									+ '<div class="listRight ">'
									+ '<input type="text" name="meetingRoomDress" id="meetingRoomDress" class="listRightPadding5" placeholder="请填写地点" data-con="*" data-empty="请填写地点" value="">'
									+ '</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">会议室地点:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear">'
									+ '<div class="listRight ">'
									+ '<input type="text" name="leaveDepartment" id="peopleSorts" class="listRightPadding5" placeholder="请填写人数" data-con="*" data-empty="请填写人数" value="">'
									+ '</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">容纳人数:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear equipmentArea">'
									+ '<div class="listRight ">'
									+
									/*
									 * '<input type="text"
									 * name="meetingEquipment"
									 * id="meetingEquipment"
									 * class="listRightPadding5"
									 * placeholder="请填写设备名称" data-con="*"
									 * data-empty="请填写设备名称" value="">'+
									 */
									'</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">设备情况:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear padtop"><textarea id="meetingEquipment" placeholder ="请填写设备名称" class="textarea-equipment"></textarea></div>'
									+ '<footer class="screen1">'
									+ '<button type="reset">取消</button>'
									+ '<button type="button">确定</button>'
									+ '</footer>'
									+ '</form>'
									+ '</div>'
									+ '</div>');
			// 取消退回模态框
			$(".returnReasons footer button:eq(0)").click(function() {
				$(".reasonMyModal").animate({
					opacity : 0
				}, 300, function() {
					$(".reasonMyModal").remove();
				});
			});
			// 确认添加会议室
			$(".returnReasons footer button:eq(1)")
					.click(
							function(e) {
								if (!ops.autoFill) {
									var roomName = $.trim($("#meetingName")
											.val()), capacity = $.trim($(
											"#peopleSorts").val()), equipment = $
											.trim($("#meetingEquipment").val()), adress = $
											.trim($("#meetingRoomDress").val());
									if (roomName == "") {
										$("body").toast({
											msg : "请填写会议室名称",
											type : 2,
											callMode : "func"
										});
										return false;
									} else if (adress == "") {
										$("body").toast({
											msg : "请填写地点",
											type : 2,
											callMode : "func"
										});
										return false;
									} else if (capacity == "") {
										$("body").toast({
											msg : "请填写容纳人数",
											type : 2,
											callMode : "func"
										});
										return false;
									} else if (!/^[0-9]*[1-9][0-9]*$/
											.test(capacity)) {
										$("body").toast({
											msg : "请填写容纳人数为正整数",
											type : 2,
											callMode : "func"
										});
									} else if (equipment == "") {
										$("body").toast({
											msg : "请填写设备情况",
											type : 2,
											callMode : "func"
										});
										return false;
									} else {
										$(".reasonMyModal")
												.animate(
														{
															opacity : 0
														},
														300,
														function() {
															var data = {
																"roomName" : $
																		.trim($(
																				"#meetingName")
																				.val()),
																"capacity" : $
																		.trim($(
																				"#peopleSorts")
																				.val()),
																"equipment" : $
																		.trim($(
																				"#meetingEquipment")
																				.val()),
																"adress" : $
																		.trim($(
																				"#meetingRoomDress")
																				.val())
															};
															ops
																	.dataChange(data);
															$(".reasonMyModal")
																	.remove();
														});
									}
								}

							});
		} else if (ops.type == 8) {// 编辑单个会议室信息
			$("body")
					.append(
							'<div class="reasonMyModal">'
									+ '<div class="returnReasons">'
									+ '<form id="returnForm" action='
									+ ops.url
									+ ' method='
									+ ops.transferType
									+ '>'
									+ '<header>'
									+ ops.msg
									+ '</header>'
									+
									/*
									 * '<textarea placeholder="在此填写'+ops.msg+'"
									 * name="reasons" required="required"></textarea>'+
									 */
									'<input type="hidden" id="roomId" value="'
									+ ops.roomId
									+ '">'
									+ '<div class="formList clear">'
									+ '<div class="listRight ">'
									+ '<input type="text" name="meetingRoomName" id="meetingRoomName" class="listRightPadding5" value="'
									+ ops.roomName
									+ '" data-con="*" data-empty="请填写会议室名称" value="">'
									+ '</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">会议室名称:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear">'
									+ '<div class="listRight ">'
									+ '<input type="text" name="meetingRoomDress" id="meetingRoomDress" class="listRightPadding5" value="'
									+ ops.roomAddress
									+ '" data-con="*" data-empty="请填写地点" value="">'
									+ '</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">会议室地点:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear">'
									+ '<div class="listRight ">'
									+ '<input type="text" name="leaveDepartment" id="peopleSorts" class="listRightPadding5" value="'
									+ ops.roomCapacity
									+ '" data-con="*" data-empty="请填写人数" value="">'
									+ '</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">容纳人数:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear equipmentArea">'
									+ '<div class="listRight ">'
									+
									/*
									 * '<input type="text"
									 * name="meetingEquipment"
									 * id="meetingEquipment"
									 * class="listRightPadding5"
									 * placeholder="请填写设备名称" data-con="*"
									 * data-empty="请填写设备名称" value="">'+
									 */
									'</div>'
									+ '<div class="listLeft listNameWidth5">'
									+ '<span class="title">设备情况:</span>'
									+ '</div>'
									+ '</div>'
									+ '<div class="formList clear padtop"><textarea id="meetingEquipment" placeholder ="" class="textarea-equipment">'
									+ ops.roomEquipment
									+ '</textarea></div>'
									+ '<footer class="screen1">'
									+ '<button type="reset">取消</button>'
									+ '<button type="button">确定</button>'
									+ '</footer>'
									+ '</form>'
									+ '</div>'
									+ '</div>');
			// 取消退回模态框
			$(".returnReasons footer button:eq(0)").click(function() {
				$(".reasonMyModal").animate({
					opacity : 0
				}, 300, function() {
					$(".reasonMyModal").remove();
				});
			});
			// 确认添加会议室
			$(".returnReasons footer button:eq(1)")
					.click(
							function(e) {
								if (!ops.autoFill) {
									var roomName = $.trim($("#meetingRoomName")
											.val()), capacity = $.trim($(
											"#peopleSorts").val()), equipment = $
											.trim($("#meetingEquipment").val()), adress = $
											.trim($("#meetingRoomDress").val());
									if (roomName == "") {
										$("body").toast({
											msg : "请填写会议室名称",
											type : 2,
											callMode : "func"
										});
										return false;
									} else if (adress == "") {
										$("body").toast({
											msg : "请填写地点",
											type : 2,
											callMode : "func"
										});
										return false;
									} else if (capacity == "") {
										$("body").toast({
											msg : "请填写容纳人数",
											type : 2,
											callMode : "func"
										});
										return false;
									} else if (!/^[0-9]*[1-9][0-9]*$/
											.test(capacity)) {
										$("body").toast({
											msg : "请填写容纳人数为正整数",
											type : 2,
											callMode : "func"
										});
									} else if (equipment == "") {
										$("body").toast({
											msg : "请填写设备情况",
											type : 2,
											callMode : "func"
										});
										return false;
									} else {
										$(".reasonMyModal")
												.animate(
														{
															opacity : 0
														},
														300,
														function() {
															var data = {
																"roomId" : $
																		.trim($(
																				"#roomId")
																				.val()),
																"roomName" : $
																		.trim($(
																				"#meetingRoomName")
																				.val()),
																"capacity" : $
																		.trim($(
																				"#peopleSorts")
																				.val()),
																"equipment" : $
																		.trim($(
																				"#meetingEquipment")
																				.val()),
																"adress" : $
																		.trim($(
																				"#meetingRoomDress")
																				.val())
															};
															ops
																	.dataChange(data);
															$(".reasonMyModal")
																	.remove();
														});
									}
								}
								/*
								 * var $screenItems=$(".returnReasons
								 * #screenItems").val(); var
								 * $peopleSorts=$(".returnReasons
								 * #peopleSorts").val();
								 * $(".reasonMyModal").animate({ opacity:0
								 * },300,function(){ if(ops.autoFill &&
								 * $textareaVal == ""){ }
								 * ops.dataChange($screenItems);
								 * $(".reasonMyModal").remove(); });
								 */
							});

		}
		// 获得浏览器的高度
		var windowH = window.innerHeight;
		var windowW = window.innerWidth;
		// 设置催办成功模态框的top、left
		var $promptModalW = $(".promptModal").outerWidth();
		var $promptModalH = $(".promptModal").outerHeight();
		$(".loading,.promptModal").css({
			"top" : (windowH - $promptModalH) / 2,
			"left" : (windowW - $promptModalW) / 2
		});

		$(".twoPromptCon").css({
			"top" : (windowH / 2 - 50),
			"left" : (windowW / 2 - 120)
		});
		$(".delImg").css({
			top : (windowH / 2 - 48)
		});
	};
	// toast插件定义
	$.fn.toast = toastFun = function(_ops) {
		new Toast(this, _ops);
		return this;
	};
}(jQuery);

/* 导航 */
+function($) {
	var Nav = function(el, options) {
		this.options = options;
		this.$el = el;
		this.init();
	};
	Nav.prototype.Defaults = {
		end : function($index) {
			return $index;
		}
	};
	Nav.prototype.init = function() {
		var ops = $.extend({}, this.Defaults, this.options);
		var pointX = new Array();
		var num = this.$el.children("li").length;
		var proportion = parseFloat(100 / num).toFixed(2);
		for (var i = 0; i < num; i++) {
			pointX.push(i * proportion + "%");
		}
		for (var j = 0; j < num - 1; j++) {
			this.$el.children("li").eq(j).addClass("borderRight");
		}
		this.$el.append('<div class="line"></div>');
		this.$el.children(".line,li").css("width", proportion + "%");
		Nav.prototype.clickFunc(this.$el, ops, pointX);
	};
	Nav.prototype.clickFunc = function(ele, ops, pointX) {
		$.each(ele.children("li"), function(index, item) {
			$(item).click(function() {
				// 导航标题切换
				$("li.navSelected").removeClass("navSelected");
				$(this).addClass("navSelected");
				$(".line").stop().animate({
					left : pointX[index]
				}, 100);
				ops.end(index);
			});
		});
	};
	// nav插件定义
	$.fn.navigation = function(_ops) {
		new Nav(this, _ops);
		return this;
	}
}(jQuery);
// loading加载动画
+function() {
	var Loading = function(el, options) {
		this.options = options;
		this.$el = el;
		this.init();
	};
	Loading.prototype.Defaults = {
		msg : "加载中...",
		move : "show",
		end : function() {

		}
	};
	Loading.prototype.init = function() {
		var ops = $.extend({}, this.Defaults, this.options);
		if ("show" == ops.move) {
			$("body").append(
					'<div class="loadingModal" id="loadModal">'
							+ '<div class="oneLoading loading">'
							+ '<div><img src="images/smallLoading.gif"></div>'
							+ '<div>' + ops.msg + '</div>' + '</div>'
							+ '</div>');
			// 获得浏览器的高度
			var windowH = window.innerHeight;
			var windowW = window.innerWidth;
			// 设置loading模态框的top、left
			$(".loading").css({
				"top" : (windowH / 2 - 50),
				"left" : (windowW / 2 - 67)
			});
		} else {
			$(".loadingModal").remove();
			ops.end();
		}
	};
	$.fn.loading = function(_ops) {
		new Loading(this, _ops);
		return this;
	};

}(jQuery);
/* 表单验证 */
+function($) {
	var Verification = function(el, options) {
		this.options = options;
		this.$el = el;
		this.init();
	};
	Verification.prototype.Defaults = {
		dataUrl : "",
		maxNum : 100000000,
		callback : function(resp) {
			// return resp;
		}
	};
	Verification.prototype.init = function() {
		var exp = {
			m : /^[0-9]\d{0,11}(\.[0-9]{1,2})?$/,
			d : /^0$|^[1-9]\d*$/,
			month : /^([1-9]|1[0-2])$/,
			e : /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
			// p:/*/^((\d|[1-9]\d)(\.\d+)?|100)$/*//^[1-9]\d?\.\d+$|^0\.\d+$|^[1-9]\d?$|^0$|^100$/
			p : /^((\d|[1-9]\d)(\.\d+)?|100)$/,
			bank : /^\d{15,19}$/
		};
		var ops = $.extend({}, this.Defaults, this.options);
		var arr = this.$el.serializeArray();
		for (var i = 0; i < arr.length; i++) {
			var $thisInput = $("#" + arr[i].name);
			var $thisVal = $.trim($thisInput.val());
			var $thisSeectVal = $thisInput.find("option:selected").val();
			if (!!$thisInput.attr("data-con")
					&& ($thisVal == "" || $thisSeectVal == "-1")) {
				toastFun({
					msg : $thisInput.attr("data-empty"),
					type : 2,
					callMode : "func",
					end : function() {
						if ($thisInput.attr("type") == "hidden") {
							var _ele = $("[data-link-field="
									+ $thisInput.attr("id") + "]");
							if (_ele.length == 0) {
								_ele = $thisInput;
								$thisInput[0].focus();
							} else {
								_ele.children("input")[0].focus();
							}
							$("html,body").animate({
								scrollTop : _ele.offset().top
							}, 500);
						} else {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput[0].focus();
						}
					}
				});
				return;
			} else if ("date" == $thisInput.attr("data-con") && $thisVal != "") {
				var name = $thisInput.prop("name");
				if (name == "etime") {
					var year = $("#date").val(), smonth = $("#stime").val(), start = year
							+ " " + smonth, end = year + " " + $thisVal, date1 = new Date(
							Date.parse(start.replace(/-/g, "/"))), date2 = new Date(
							Date.parse(end.replace(/-/g, "/")));
					if (date1 > date2) {
						toastFun({
							msg : "开始时间要早于结束时间",
							type : 2,
							callMode : "func",
							end : function() {
								$("html,body").animate({
									scrollTop : $thisInput.offset().top
								}, 500);
								$thisInput.focus();
							}
						});
						return;
					}
				}
			} else if ("m" == $thisInput.attr("data-con") && $thisVal != "") {
				if (!exp.m.test($thisVal)) {
					toastFun({
						msg : $thisInput.attr("data-error"),
						type : 2,
						callMode : "func",
						end : function() {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput[0].focus();
						}
					});
					return;
				} else {
					if (parseInt($thisVal) > parseInt(ops.maxNum)) {
						toastFun({
							msg : "所填的金额必须小于一亿",
							type : 2,
							callMode : "func",
							end : function() {
								$("html,body").animate({
									scrollTop : $thisInput.offset().top
								}, 500);
								$thisInput[0].focus();
							}
						});
						return;
					}
				}
				if (i == arr.length - 1) {
					ops.callback();
				}
			} else if ("d" == $thisInput.attr("data-con") && $thisVal != "") {
				if (!exp.d.test($thisVal)) {
					toastFun({
						msg : $thisInput.attr("data-error"),
						type : 2,
						callMode : "func",
						end : function() {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput[0].focus();
						}
					});
					return;
				}
				if (i == arr.length - 1) {
					ops.callback();
				}
			} else if ("month" == $thisInput.attr("data-con") && $thisVal != "") {
				if (!exp.month.test($thisVal)) {
					toastFun({
						msg : $thisInput.attr("data-error"),
						type : 2,
						callMode : "func",
						end : function() {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput.focus();
						}
					});
					return;
				} else {
					var name = $thisInput.prop("name");
					if (name == "endMonth") {
						var smonth = $("#startMonth").val();
						if (parseInt($thisVal) < parseInt(smonth)) {
							toastFun({
								msg : "第二个月份要大于第一个月份",
								type : 2,
								callMode : "func",
								end : function() {
									$("html,body").animate({
										scrollTop : $thisInput.offset().top
									}, 500);
									$thisInput.focus();
								}
							});
							return;
						}
					}
				}
				if (i == arr.length - 1) {
					ops.callback();
				}
			} else if ("e" == $thisInput.attr("data-con") && $thisVal != "") {
				if (!exp.e.test($thisVal)) {
					toastFun({
						msg : $thisInput.attr("data-error"),
						type : 2,
						callMode : "func",
						end : function() {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput[0].focus();
						}
					});
					return;
				}
				if (i == arr.length - 1) {
					ops.callback();
				}
			} else if ("p" == $thisInput.attr("data-con") && $thisVal != "") {
				if (!exp.p.test($thisVal)) {
					toastFun({
						msg : $thisInput.attr("data-error"),
						type : 2,
						callMode : "func",
						end : function() {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput[0].focus();
						}
					});
					return;
				}
				if (i == arr.length - 1) {
					ops.callback();
				}
			} else if ("bank" == $thisInput.attr("data-con") && $thisVal != "") {
				if (!exp.bank.test($thisVal.replace(/\s/g, ''))) {
					toastFun({
						msg : $thisInput.attr("data-error"),
						type : 2,
						callMode : "func",
						end : function() {
							$("html,body").animate({
								scrollTop : $thisInput.offset().top
							}, 500);
							$thisInput[0].focus();
						}
					});
					return;
				}
				if (i == arr.length - 1) {
					ops.callback();
				}
			} else if (i == arr.length - 1) {
				ops.callback();
			}
		}
	};
	$.fn.verification = function(_ops) {
		new Verification(this, _ops);
		return this;
	}
}(jQuery);

+function($) {
	$.fn.endlessScroll = function(_dom) {
		return ($(window).scrollTop() + $(window).height() + 40 >= $(_dom)
				.height());
	}
}(jQuery);
// 银行卡号4位为一组
+function($) {
	var BankCardNumr = function(el, options) {
		this.options = options;
		this.$el = el;
		this.init();
	};
	BankCardNumr.prototype = {
		init : function() {
			var num = 0;
			this.$el.on("keyup", function() {
				var str = $(this).val();
				var elem = $(this);
				var reg = /^(\d{1,4} ?){0,5}$/;
				if (!reg.test(str)) {
					for (var i = str.length; i > 0; i--) {
						var newStr = str.substr(0, i - 1);
						if (reg.test(newStr) || newStr == "") {
							$(this).val(newStr);
							return;
						}
					}
				}
				if (str.length > num) {
					var c = str.replace(/\s/g, "");
					if (str != "" && c.length > 4 && c.length % 4 == 1) {
						$(this).val(
								str.replace(/\s/g, '').replace(
										/(\d{4})(?=\d)/g, "$1 "));
					}
				}
				if (elem.setSelectionRange) {// W3C
					setTimeout(function() {
						elem.setSelectionRange(str.length, elem.value.length);
						elem.focus();
					}, 0);
				}
				num = str.length;
			});
		}
	};
	$.fn.bankCardNumr = function(_ops) {
		new BankCardNumr(this, _ops);
		return this;
	}
	// $(this).val(str.replace(/\s/g,'').replace(/(\d{4})(?=\d)/g,"$1 "))
}(jQuery);
// 将金额转化成大写金额
+function($) {
	var ConvertCurrency = function(el, options) {
		this.options = options;
		this.$el = el;
		this.init();
	};
	ConvertCurrency.prototype = {
		Defaults : {
			"targetSource" : "",// 目标源
			"MAXIMUM_NUMBER" : 99999999999.99
		// 最大数
		},
		init : function() {
			var ops = $.extend({}, this.Defaults, this.options);
			this.$el.on("input", function() {
				var currencyDigits = $(this).val();
				var newcurrencyDigits = ConvertCurrency.prototype
						.verificationChara($(this), ops, currencyDigits);
				ConvertCurrency.prototype.segmentation($(this), ops,
						newcurrencyDigits);
			});
		},
		verificationChara : function(_ele, _ops, _currencyDigits) {
			console.log(_currencyDigits);
			// 验证输入字符串是否合法
			if (_currencyDigits.toString() == "") {
				_ops.targetSource.text("");
				return _currencyDigits;
			}
			// 判断是否输入有效的数字格式
			var reg = /^[0-9]\d{0,11}(\.[0-9]*)?$/;
			if (!reg.test(_currencyDigits)) {
				for (var i = _currencyDigits.length; i > 0; i--) {
					var str = _currencyDigits.substr(0, i - 1);
					if (reg.test(str) || str == "") {
						_ele.val(str);
						return str;
					}
				}
			}
			_currencyDigits = _currencyDigits.replace(/,/g, "");
			_currencyDigits = _currencyDigits.replace(/^0+/, "");
			// 判断输入的数字是否大于定义的数值
			if (Number(_currencyDigits) > _ops.MAXIMUM_NUMBER) {
				$("body").toast({
					msg : "您输入的数字太大了",
					type : 2,
					callMode : "func"
				});
				_ele.focus();
				return _currencyDigits;
			}
			return _currencyDigits;
		},
		segmentation : function(_ele, ops, _currencyDigits) {
			var CN_ZERO = "零";
			var CN_ONE = "壹";
			var CN_TWO = "贰";
			var CN_THREE = "叁";
			var CN_FOUR = "肆";
			var CN_FIVE = "伍";
			var CN_SIX = "陆";
			var CN_SEVEN = "柒";
			var CN_EIGHT = "捌";
			var CN_NINE = "玖";
			var CN_TEN = "拾";
			var CN_HUNDRED = "佰";
			var CN_THOUSAND = "仟";
			var CN_TEN_THOUSAND = "万";
			var CN_HUNDRED_MILLION = "亿";
			var CN_DOLLAR = "元";
			var CN_TEN_CENT = "角";
			var CN_CENT = "分";
			var CN_INTEGER = "整";
			var integral, decimal, outputCharacters, parts;
			var digits, radices, bigRadices, decimals;
			var zeroCount;
			var i, p, d;
			var quotient, modulus;
			parts = _currencyDigits.split(".");
			if (parts.length > 1) {
				integral = parts[0];
				decimal = parts[1];
				if (decimal.length > 2) {
					decimal = decimal.substr(0, 2);
					_ele.val(integral + "." + decimal);
				}
			} else {
				integral = parts[0];
				decimal = "";
			}
			// 实例化字符大写人民币汉字对应的数字
			digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR,
					CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
			radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
			bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);
			decimals = new Array(CN_TEN_CENT, CN_CENT);

			outputCharacters = "";
			// 大于零处理逻辑
			if (Number(integral) > 0) {
				zeroCount = 0;
				for (i = 0; i < integral.length; i++) {
					p = integral.length - i - 1;
					d = integral.substr(i, 1);
					quotient = p / 4;
					modulus = p % 4;
					if (d == "0") {
						zeroCount++;
					} else {
						if (zeroCount > 0) {
							outputCharacters += digits[0];
						}
						zeroCount = 0;
						outputCharacters += digits[Number(d)]
								+ radices[modulus];
					}
					if (modulus == 0 && zeroCount < 4) {
						outputCharacters += bigRadices[quotient];
					}
				}
				outputCharacters += CN_DOLLAR;
			}
			// 包含小数部分处理逻辑
			if (decimal != "") {
				for (i = 0; i < decimal.length; i++) {
					d = decimal.substr(i, 1);
					if (d != "0") {
						outputCharacters += digits[Number(d)] + decimals[i];
					}
				}
			}
			// 确认并返回最终的输出字符串
			/*
			 * if (outputCharacters == "") { outputCharacters = CN_ZERO +
			 * CN_DOLLAR; }
			 */
			if (decimal == "" && outputCharacters != "") {
				outputCharacters += CN_INTEGER;
			}
			// 获取人民币大写
			ops.targetSource.text(outputCharacters);
		}
	};
	$.fn.convertCurrency = function(_ops) {
		new ConvertCurrency(this, _ops);
		return this;
	}
}(jQuery)

// 阿拉伯数字转汉字
function digitalSwitchChart(_currencyDigits) {
	var CN_ZERO = "零";
	var CN_ONE = "一";
	var CN_TWO = "二";
	var CN_THREE = "三";
	var CN_FOUR = "四";
	var CN_FIVE = "五";
	var CN_SIX = "六";
	var CN_SEVEN = "七";
	var CN_EIGHT = "八";
	var CN_NINE = "九";
	var CN_TEN = "十";
	var CN_HUNDRED = "百";
	var CN_THOUSAND = "千";
	var CN_TEN_THOUSAND = "万";
	var CN_HUNDRED_MILLION = "亿";
	var integral, decimal, outputCharacters;
	var digits, radices, bigRadices;
	var zeroCount;
	var i, p, d;
	var quotient, modulus;
	// 实例化字符大写人民币汉字对应的数字
	digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE,
			CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
	radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
	bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);

	outputCharacters = "";
	// 大于零处理逻辑
	if (Number(_currencyDigits) > 0) {
		zeroCount = 0;
		for (i = 0; i < _currencyDigits.length; i++) {
			p = _currencyDigits.length - i - 1;
			d = _currencyDigits.substr(i, 1);
			quotient = p / 4;
			modulus = p % 4;
			if (d == "0") {
				zeroCount++;
			} else {
				if (zeroCount > 0) {
					outputCharacters += digits[0];
				}
				zeroCount = 0;
				outputCharacters += digits[Number(d)] + radices[modulus];
			}
			if (modulus == 0 && zeroCount < 4) {
				outputCharacters += bigRadices[quotient];
			}
		}
	}
	// 获取人民币大写
	return outputCharacters;
}

String.prototype.replaceAll = function(s1, s2) {
	return this.replace(new RegExp(s1, "gm"), s2);
};