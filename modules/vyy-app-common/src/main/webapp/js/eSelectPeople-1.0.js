/*
@auther taofang
2015-08-07 14:33
 */
(function($) {
	var eSelectPeople = {
		load1ChildDept : function(elParent, childDept) {
			var _self = this;
			var department = null;
			var firstTit = null;
			var inputEle = null;
			var iEle = null;
			for (var i = 0, size = childDept.length; i < size; i++) {
				if('81'== childDept[i].id){
					continue;
				}
				department = $("<section class='department'>");
				firstTit = $("<div class='firstTit'>");
				inputEle = $("<input type='checkbox' name='secondSelect'>");
				iEle = $("<i class='fa fa-angle-down'></i>");
				firstTit.append(childDept[i].name);
				firstTit.append(inputEle);
				firstTit.append(iEle);
				department.append(firstTit);
				elParent.append(department);
				inputEle.attr("btype", "getUser");
				inputEle.attr("flag", false);
				inputEle.attr("deptId", childDept[i].id);
				inputEle.attr("level", "1");
				department.attr("deptId", childDept[i].id);
				if (null != childDept[i].child && 'null' != childDept[i].child) {
					_self.load2ChildDept(department, childDept[i].child);
				} else {
					// 说明该部门下无子部门
				}
			}
		},
		load2ChildDept : function(elParent, childDept) {
			var _self = this;
			var department = $("<div class='firstCon oneDepartment members'>");
			var secondDirectory = null;
			var secondTit = null;
			var inputEle = null;
			var iEle = null;
			for (var i = 0, size = childDept.length; i < size; i++) {
				secondDirectory = $("<div class='secondDirectory'>");
				secondTit = $("<div class='secondTit'>");
				inputEle = $("<input type='checkbox' name='secondSelect'>");
				inputEle.attr("deptId", childDept[i].id);
				iEle = $("<i class='fa fa-angle-down'></i>");
				secondTit.append(childDept[i].name);
				secondTit.append(inputEle);
				secondTit.append(iEle);
				secondDirectory.append(secondTit);
				department.append(secondDirectory);
				inputEle.attr("btype", "getUser");
				inputEle.attr("flag", false);
				inputEle.attr("deptId", childDept[i].id);
				inputEle.attr("level", "2");
				secondDirectory.attr("deptId", childDept[i].id);
				if (null != childDept[i].child && 'null' != childDept[i].child) {
					_self.load3ChildDept(secondDirectory, childDept[i].child);
				} else {
					// 说明该部门下无子部门
				}
			}
			elParent.append(department);
		},
		load3ChildDept : function(elParent, childDept) {
			var department = $("<div class='secondCon'>");
			var thirdDirectory = null;
			var thirdTit = null;
			var inputEle = null;
			var iEle = null;
			for (var i = 0, size = childDept.length; i < size; i++) {
				thirdDirectory = $("<div class='thirdDirectory'>");
				thirdTit = $("<div class='thirdTit'>");
				inputEle = $("<input type='checkbox' name='thirdSelect'>");
				inputEle.attr("deptId", childDept[i].id);
				iEle = $("<i class='fa fa-angle-down'></i>");
				thirdTit.append(childDept[i].name);
				thirdTit.append(inputEle);
				thirdTit.append(iEle);
				thirdDirectory.append(thirdTit);
				department.append(thirdDirectory);
				inputEle.attr("btype", "getUser");
				inputEle.attr("flag", false);
				inputEle.attr("deptId", childDept[i].id);
				inputEle.attr("level", "3");
				thirdDirectory.attr("deptId", childDept[i].id);
				if (null != childDept[i].child && 'null' != childDept[i].child) {
					 load3ChildDept(secondDirectory,childDept[i].child);
				} else {
					// 说明该部门下无子部门
				}
			}
			elParent.append(department);
		},
		loadDeptUser : function(elem, userList) {
			var level = $(elem).attr("level");
			var deptId = $(elem).attr("deptId");
			var parentElem = null;
			var department = null;
			if ("1" == level) {
				if($("section[deptId='" + deptId + "']>.firstCon").length <= 0 ){
					parentElem = $("section[deptId='" + deptId + "']");
					department = $("<div class='firstCon oneDepartment members' style='display: block;'>");
					parentElem.append(department);
				}else {
					parentElem = $("section[deptId='" + deptId + "']>.firstCon");
					department = $("<div>");
					parentElem.prepend(department);
				}
				
			} else if ("2" == level) {
				if($("div[deptId='" + deptId + "']>.secondCon").length <= 0 ){
					parentElem = $("div[deptId='" + deptId + "']");
					department = $("<div class='secondCon members' style='display: block;'>");
					parentElem.append(department);
				}else {
					parentElem = $("div[deptId='" + deptId + "']>.secondCon");
					department = $("<div>");
					parentElem.prepend(department);
				}
			} else if ("3" == level) {
				if($("div[deptId='" + deptId + "']>.thirdCon").length <= 0 ){
					parentElem = $("div[deptId='" + deptId + "']");
					department = $("<div class='thirdCon members' style='display: block;'>");
					parentElem.append(department);
				}else {
					parentElem = $("div[deptId='" + deptId + "']>.thirdCon");
					department = $("<div>");
					parentElem.prepend(department);
				}
			} else {
				return;
			}
			var size = userList.length;
			if (size > 0) {
				for (var i = 0; i < size; i++) {
					var departPeople = $("<div class='departPeople'>");
					var toggle = $("<section class='toggle'>");
					var peopleList = $("<div class='peopleList'>");
					var inputElem = $("<input type='checkbox' name='view' value='list'>");
					
					inputElem.attr("id", userList[i].userid);
					inputElem.attr("eid",deptId+userList[i].userid);
					var label = $("<label>");
					label.attr("for", userList[i].userid);
					var peopleIcon = $("<div class='peopleIcon'>");
					var imag = $("<img>");
					imag.attr("src", userList[i].avatar);
					var div = $("<div>");
					var span1 = $("<span class='peopleName'>");
					span1.append(userList[i].name);
					var span2 = $("<span class='subhead peopleZC'>");
					span2.append(userList[i].position);
					peopleIcon.append(imag);
					div.append(span1);
					div.append(span2);
					label.append(peopleIcon);
					label.append(div);
					peopleList.append(inputElem);
					peopleList.append(label);
					toggle.append(peopleList);
					departPeople.append(toggle);
					department.append(departPeople);
				}
				if (!!Window.DEBUG_MODE && Window.DEBUG_MODE) {
					console.debug(department.html());
				}
			}
		},
		loadUserInfo : function(elem, deptId, callback) {
			var _self = this;
			$.ajax({
				url : 'mobile/common/getUser.action',
				type : 'post',
				data : {
					deptId : deptId
				},
				beforeSend:function(){
					$("#onLoading").show();
				},
				complete:function(){
					$("#onLoading").hide();
				},
				success : function(data, textStatus) {
					if (data.status == 0) {
						var userList = data.value.userlist;
						if (!!userList) {
							_self.loadDeptUser(elem, userList);
							if (typeof (callback) == "function") {
								callback();
							}
						}
						elem.attr("flag", true);
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					//alert(JSON.stringify(XMLHttpRequest));
				}
			});
		},
		loadDeptInfo :
		// 初始化加载人员列表
		function loadDeptInfo(elParent, callback) {
			var _self = this;
			$.ajax({
				url : 'mobile/common/getDept.action',
				type : 'post',
				data : {
					deptId : null
				},
				success : function(data, textStatus) {
					if (data.status == 0) {
						var dept = data.value;
						if (!!dept) {
							if (null != dept.child && 'null' != dept) {
								_self.load1ChildDept(elParent, dept.child);
								if (typeof (callback) == "function") {
									callback();
								}
							}
						}
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					//alert(JSON.stringify(XMLHttpRequest));
				}
			});
		}
	};

	$.fn.eSelectPeople = function(config) {
		
		var _config = config||{};
		var _self = $(this);
		_self.attr("_isMultipleChoice", _config.isMultipleChoice||false);
		var membersList = $("<div class='membersList'>");
		var membersListBtn = $("<div class='membersListBtn'>");
		var canceBtn = $("<button type='reset'>取消</button>");
		var okBtn = $("<button type='button'>确认</button>");
		
		membersListBtn.append(canceBtn);
		membersListBtn.append(okBtn);
		_self.append(membersList);
		_self.append(membersListBtn);
		
		//事件注册 
		canceBtn.on("click",function(){
			$(".peopleList > input").each(function() {
				var input = $(this);
				if(input.is(':checked')){
					var label =  $('label[for=' + input.attr('id') + ']');
					input.prop("checked",false);
					label.removeClass("checked");
				}
			});
			$(".firstTit>input,.secondTit>input,.thirdTit>input").each(function(){
				if ($(this).prop("checked")) {
					$(this).prop("checked",false);
					$(this).parent().next().stop().animate({
						height : 'hide'
					}, 200);
				} 
			});
			if (typeof (_config.btCance) == "function") {
				_config.btCance(_self);
			}
		});
		okBtn.on("click",function(){
			if (typeof (_config.btOk) == "function") {
				//获取选中的人员信息
				var data = [];
				$(".peopleList > input").each(function() {
					var input = $(this);
					if(input.is(':checked')){
						var id = input.attr("id");
						for(var i =0,size = data.length;i<size;i++){
							if(id == data[i].uId){
								return;
							}
						}
						var label =  $('label[for=' + input.attr('id') + ']');
						input.prop("checked",false);
						label.removeClass("checked");
						var url =label.find("img").attr("src");
						var name = label.find("span:eq(0)").html();
						data.push({"uId":id,"uAvatar":url,"uName":name});
					}
				});
				if (!!Window.DEBUG_MODE && Window.DEBUG_MODE) {
					console.debug(JSON.stringify(data));
				}
				$(".firstTit>input,.secondTit>input,.thirdTit>input").each(function(){
					if ($(this).prop("checked")) {
						$(this).prop("checked",false);
						console.log($(this).prop("checked"));
						$(this).parent().next().stop().animate({
							height : 'hide'
						}, 200);
					} 
				});
				_config.btOk(_self,data);
			}
		});
		eSelectPeople.loadDeptInfo(membersList, function() {
			
			$("input[btype='getUser']").on('click', function() {
				var flag = $(this).attr("flag");
				if ('false' == flag) {
					eSelectPeople.loadUserInfo($(this), $(this).attr("deptId"), function() {
						$(".peopleList > input").each(function() {
							if ($(this).is('[type=checkbox],[type=radio]')) {
								var input = $(this);
								// 使用输入的ID得到相关的标签
								var label = $('label[for=' + input.attr('id') + ']');
								//绑定自定义事件，触发它，绑定点击，焦点，模糊事件
								input.on('click',
												function() {
													input.is(':checked') ? label.addClass('checked'): label.removeClass('checked checkedHover checkedFocus');
												    if("false" == _self.attr("_isMultipleChoice")){
												    	var __self = $(this);
												    	$(".peopleList > input").each(function() {
															var input = $(this);
															
															if(__self.attr("eid") != input.attr("eid") && input.is(':checked')){
																var label =  $('label[for=' + input.attr('id') + ']');
																input.prop("checked",false);
																label.removeClass("checked");
															}
														});
												    }
								                }).trigger('updateState').click(
												function() {
													$('input[name='+ $(this).attr('name')+ ']').trigger('updateState');
												}).focus(function() {
											label.addClass('focus');
											if (input.is(':checked')) {
												$(this).addClass('checkedFocus');
											}
										}).blur(function() {
											label.removeClass('focus checkedFocus');
										});
							}
						});
					});
				}
			});
			//注册部门级展开事件
			$(".firstTit>input,.secondTit>input,.thirdTit>input").on("click",function(){
				if ($(this).prop("checked")) {
					$(this).parent().next().stop().animate({
						height : 'show'
					}, 200);
				} else {
					$(this).parent().next().stop().animate({
						height : 'hide'
					}, 200);
				}
			});
		});
		_self.eshow = function(config){
			var _self = $(this);
			var _config = config||{};
			_self.attr("_isMultipleChoice", _config.isMultipleChoice||false);
			_self.attr("_flag",_config.flag||false);
			_self.show();
		};
		return _self;
	};
})(jQuery);
