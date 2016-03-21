/*
	@auther dd.ze@vyiyun.com
	2015-11-10
*/
(function($) {
	var eSelectPeople = {
        loadDeptInfo : function loadDeptInfo(elParent, callback) {
        	var _self = this;
        	_self.loadDate('mobile/common/getDepts.action?time='+new Date().getTime(),function(data){
        		var wrap = $(".membersList");
                var data = data.value;
                var deplistWrap = $("<div class='deplistWrap'></div>");
                var deplist = $("<ul class='depList'></ul>");
                if(data.length == 1){
                	if(data[0].id == '1'){
                		data = data[0].child;
                	}
                }
                _self.printDep(data,deplist);
                deplistWrap.append(deplist);
                wrap.append(deplistWrap);
                //_self.depListHeight();
                if (typeof (callback) == "function") {
                    callback();
                }
        	});
        },
        depListHeight:function(){
            var winHeight = $(window).height();
            var seacrhHeight = $(".contactSearch").outerHeight(true);
            var searchResultHeight = winHeight-seacrhHeight;
            //按钮的高度+20像素的margin边距
            var btnHeight = $(".membersListBtn button").outerHeight(true);
            var depListHeight = searchResultHeight-btnHeight-40;
            $(".deplistWrap").height(depListHeight-20);
        },
        loadCustomInfo : function() {
        	var _self = this;
        	_self.loadDate('mobile/common/getWTag.action?time='+new Date().getTime(),function(data){
        		var wrap = $(".membersList");
                var data = data.value;
                var deplistWrap = $(".deplistWrap");
                if(deplistWrap.length>0){
                	deplistWrap = deplistWrap;
                }else{
                	deplistWrap = $("<div class='deplistWrap'></div>");
                }
                var deplist = $("<ul class='depList tagList'></ul>");
                _self.printDep(data,deplist);
                deplistWrap.append(deplist);
                deplist.prepend($('<h2 class="customHead">自定义标签</h2>'));
                deplistWrap.append(deplist);
        	});
        },
        loadDate:function(url,callback){
        	$.ajax({
                url : url,
                type : 'post',
                data : {},
                beforeSend:function(){
                   // $("#loading").show();
                },
                success : function(data, textStatus) {
                    if (data.status == 0) {
                        var dept = data.value;
                        if (!!dept) {                           
                        	if (typeof (callback) == "function") {
                                callback(data);
                            }
                        }
                    }
                },
                complete:function(){
                  //$("#loading").hide();
                },
                error : function(XMLHttpRequest, textStatus, errorThrown) {
                    //alert(JSON.stringify(XMLHttpRequest));
                }
                
            });
        },
        printDep:function(data,parent){
        	var _self = this;
        	var depLen = data.length;   
            for(var i=0;i < depLen;i++){
                if(data[i].child){//有子级
                     var li = $("<li></li>");
                    //将li的文本设置好，并马上添加一个空白的ul子节点，并且将这个li添加到父亲节点中,空白的ul作为下一个递归遍历的父亲节点传入
                    $(li).append("<div class='dep'><a href='javascript:;' class='depName'><i class='dropright-caret'></i>"+ data[i].name +"</a><a href='javascript:;' class='getIn' data-id='"+ data[i].id+"'><i class='icon-chevron-right'></i></a></div>").append("<ul></ul>").appendTo(parent);
                    _self.printDep(data[i].child, $(li).children("ul"));
                }else{//无子级
                    $("<li class='noChild'></li>").append("<div class='dep'><a href='javascript:;' class='depName'>"+ data[i].name +"</a><a href='javascript:;' class='getIn' data-id='"+ data[i].id+"'><i class='icon-chevron-right'></i></a></div>").appendTo(parent);
                }   
            } 
        },
        printPeople:function(data,parent){
        	var depLen = data.length;
            var info,pic;
            if(!depLen){
            	$("<div class='noResult'>此部门无成员</div>").appendTo(parent);
            }
            for(var i=0;i < depLen;i++){    
                    if(data[i].position == null){
                        info = "<div class='name' style='line-height:38px;'>"+ data[i].name +"</div>"
;
                    }else{
                        info = "<div class='name'>"+ data[i].name +"</div><div class='pos'>"+ data[i
].position +"</div>"; 
                    }
                    if(data[i].avatar == null){
                        pic = "<div class='head'><i class='fa fa-user'></i></div>";
                    }else{
                        pic = "<div class='head'><img src='"+ data[i].avatar +"64'></div>";
                    }
                    $("<li></li>").append("<div class='people' id='"+ data[i].userid +"'><div class='ckBox'><input type='checkbox' name='"+ data[i].userid +"'><i class='icon-unchecked'></i></div>"+ pic + info +"</div>").appendTo(parent);   
            }
        },
        handleTree:function(){
        	var wrap = $(".membersListWarp");
            wrap.on("click",".depName",function(){
                var thisChild = $(this).parents(".dep").siblings("ul").children("li"),
                    thisClass = $(this).find("i");
                var isNoChild = $(this).parents("li").hasClass("noChild");
                //如果没有子节点，点击进入成员
                if(isNoChild){
                    var thisGetIn = $(this).siblings(".getIn");
                    thisGetIn.trigger("click");
                }
                if(thisChild.is(":visible")){
                    thisChild.css("display","none");
                    thisClass.removeClass("caret").addClass("dropright-caret");
                }else{
                    thisChild.css("display","block");
                    thisClass.removeClass("dropright-caret").addClass("caret");
                }
            });
        },
        handlePopSearchResult:function(){
        	var _self = this;
        	var isMultiselect = $(".membersListWarp").attr("_ismultiplechoice"),searchResult;
            var wrap = $(".membersListWarp");

            if(isMultiselect == "true"){
                searchResult = '<div class="searchResult"><div class="depPeopleList"></div><div class="btnTool mult clearfix"><div id="JselAll" class="checkAll pull-left"><input type="checkbox"/><i class="icon-unchecked"></i>全选</div><div class="pull-right"><a href="javascript:;" class="btn orange btn-ok">确定</a><a href="javascript:;" class="btn  btn-cancel">取消</a></div></div></div>';
            }else{
                searchResult = '<div class="searchResult"><div class="depPeopleList"></div><div class="btnTool"><a href="javascript:;" class="btn orange btn-ok">确定</a><a href="javascript:;" class="btn btn-cancel">取消</a></div></div>';
            }
            _self.handleHideSearchResult();//保证一次只有一个搜索结果层存在
            wrap.append(searchResult);  
           // _self.handleHeight();
            //隐藏列表
            $(".deplistWrap,.closeBar,.membersListBtn").hide();
            wrap.on("click",".btn-cancel",function(){
            	_self.handleHideSearchResult();
            	_self.config.btCance($(".membersListWarp"));
            });
        },
        handleHideSearchResult:function(){
        	if($(".searchResult").length != 0){
                $(".searchResult").remove();
            }
            //显示列表
            $(".closeBar").show();
            //清空搜索
            $("#JsearchInput").val("").css({"padding-left":"30px","width":"80%"});
            $("#JsearchInput").siblings("i").show();
            $("#JsearchInput").siblings("input[type='submit']").hide();
        },
        handleSearch:function(){
        	var _self = this;
        	var wrap = $(".membersListWarp"),mumberData=[];
        	var $input = $("#JsearchInput");
            var $btn = $input.siblings("input[type='submit']");
            $input.on("click",function(){
                $btn.show();
                $btn.siblings("i").hide();
                $input.css({"padding-left":"16px","width":"80%","margin-right":"0px"});
                
            });
            $btn.on("click",function(){
                if($.trim($input.val()) ==""){
                    return false;
                }
                if(mumberData.length == 0){
                    //公司id=1
                	_self.loadDate('mobile/mobile/getDeptUser.action?deptId=1&time='+new Date().getTime(),_self
.printSearchResult);
                }else{
                	_self.printSearchResult(mumberData);
                }
            });
        },
        printSearchResult:function(data){
        	var _self = this;
        	var mumberData = data;
        	//全部成员数据 做一次保存
            if(mumberData.length == 0){
                mumberData = data;
            }

            var data = data.value,
                txt = $.trim($("#JsearchInput").val()),
                dataLen = data.length,dataRes = [];
            //遍历查找
            for(var i=0;i < dataLen;i++){
                var dName = data[i].name;
                if(dName.indexOf(txt)!=-1){
                    var dId = data[i].userid,
                        dAvatar = data[i].avatar,
                        dPos = data[i].position;
                    dataRes.push({"userid":dId,"avatar":dAvatar,"name":dName,"position":dPos}); 
                }
            }
            //有无搜索结果
            if(!!dataRes.length){
                var peoplelist = $("<ul class='memberList'></ul>");
                eSelectPeople.printPeople(dataRes,peoplelist);
                eSelectPeople.handlePopSearchResult();
                $(".depPeopleList").append(peoplelist);
            }else{
                var noResult = $("<div class='noResult'>无搜索结果</div>");
                eSelectPeople.handlePopSearchResult();
                $(".depPeopleList").append(noResult);
            }
        },
        handleHeight:function(){
            var winHeight = $(window).height();
            var seacrhHeight = $(".contactSearch").outerHeight(true);
            var searchResultHeight = winHeight-seacrhHeight;
            var depPeopleListHeight = searchResultHeight-$(".searchResult .btnTool").outerHeight(true
);
            
            $(".searchResult").height(searchResultHeight-40);
            $(".depPeopleList").height(depPeopleListHeight-40);
        },
        handleSelMumber:function(){
        	var _self = this;
            var wrap = $(".membersListWarp");
            //全选按钮
            wrap.on("click","#JselAll",function(){
                var allCheckBox = $(this).find("input[type='checkbox']"),
                    isChecked = allCheckBox.prop("checked"),
                    thisClass = $(this).find("i");
                var ckBox = $(".ckBox").find("input[type='checkbox']"),
                    ckClass = $(".ckBox").find("i");
                if(!isChecked){
                    allCheckBox.prop("checked",true);
                    thisClass.attr("class","icon-check");
                    ckBox.prop("checked",true);
                    ckClass.attr("class","icon-check");
                }else{
                    allCheckBox.prop("checked",false);
                    thisClass.attr("class","icon-unchecked");
                    ckBox.prop("checked",false);
                    ckClass.attr("class","icon-unchecked");         
                }
            });
            //选择按钮
            wrap.on("click",".people",function(e){
            	//多选与否
                var isMultiselect = $(".membersListWarp").attr("_ismultiplechoice");
                if(isMultiselect == "true"){
                	var thisCheckBox = $(this).find("input[type='checkbox']"),
	                    isChecked = thisCheckBox.prop("checked"),
	                    thisClass = thisCheckBox.siblings("i");
	                thisCheckBox.prop("checked",!isChecked);
	                if(thisCheckBox.prop("checked")){
	                    thisClass.attr("class","icon-check");
	                }else{
	                    thisClass.attr("class","icon-unchecked");
	                }
	                //是不是全部
	                var ckBox = $(".ckBox").find("input[type='checkbox']"),
	                    ckClass = $(".ckBox").find("i"),ckNum = 0,ckSize;
	                    ckSize = ckBox.length;
	                var allCkBox = $("#JselAll input[type='checkbox']");
	                ckBox.each(function(){
	                    if($(this).prop("checked")){
	                        ckNum++;
	                    }
	                });
	                if(ckNum == ckSize){
	                    allCkBox.prop("checked",true);
	                    allCkBox.siblings("i").attr("class","icon-check");
	                }else{
	                    allCkBox.prop("checked",false);
	                    allCkBox.siblings("i").attr("class","icon-unchecked");
	                }
                }else{
                	var ckBox = $(".ckBox").find("input[type='checkbox']"),
                    	ckClass = $(".ckBox").find("i");
                	var thisIndex = $(this).parents("li").index();
	                ckBox.each(function(i){
	                    var thisCheckBox = ckBox.eq(i),
	                        isChecked = thisCheckBox.prop("checked"),
	                        thisClass = thisCheckBox.siblings("i");
	                    if(thisIndex == i){ 
	                        thisCheckBox.prop("checked",!isChecked);
	                        if(thisCheckBox.prop("checked")){
	                            thisClass.attr("class","icon-check");
	                        }else{
	                            thisClass.attr("class","icon-unchecked");
	                        }
	                    }else{
	                        thisCheckBox.prop("checked",false);
	                        thisClass.attr("class","icon-unchecked");
	                    }
	                });
                }
            });
            
            //确定提交
            wrap.on("click",".btn-ok",function(){
            	var data = _self.saveSelectorMember();
            	 _self.handleHideSearchResult();
            	 _self.config.btOk(wrap,data);
            	 
            });
        },
        saveSelectorMember:function(){
        	var isMultiselect = $(".membersListWarp").attr("_ismultiplechoice");
            var wrap = $(".membersListWarp");  
            var cked = $(".ckBox").find("input[type='checkbox']:checked");
            if(isMultiselect){
                selDataList=[];//数组先清空
                //var len = cked.size();//选择的人数
                cked.each(function(){
                    var $this = $(this),
                        people = $this.parents(".people"),
                        userId = people.attr("id"),
                        userAvatar = people.find("img").attr("src"),
                        userName = people.find(".name").text();
                    selDataList.push({"uId":userId,"uAvatar":userAvatar,"uName":userName});
                });     
            }else{      
                var people = cked.parents(".people"),
                    userId = people.attr("id"),
                    userAvatar = people.find("img").attr("src"),
                    userName = people.find(".name").text();
                selDataList=[];//数组先清空
                selDataList.push({"uId":userId,"uAvatar":userAvatar,"uName":userName});
            }
            return selDataList;
        }
	};
	$.fn.eSelectPeople = function(config) {
		var _config = config||{};
        var _self = $(this);
        eSelectPeople.config=_config;

        _self.attr("_isMultipleChoice", _config._isMultipleChoice||false);
        _self.attr("_isShowTag", _config._isShowTag||false);
        
        var membersList = $("<div class='membersList'>");
        var membersListBtn = $("<div class='membersListBtn'>");
       // var closeBtn = $("<button class='btn btn-default' type='button'>关闭</button>");
        var loading = $("<div class='load' id='loading' style='display:none'><i class='fa fa-spinner'></i></div>");
        var search = $('<div class="contactSearch"><input type="text" placeholder="姓名" id="JsearchInput"><i class="icon-search"></i><input type="submit" value="搜索" id="JsearchBtn"/></div>');
        //membersListBtn.append(closeBtn);
        _self.append(membersList);
        _self.append(membersListBtn);
        membersList.append(loading);
        membersList.append(search);
        
      //事件注册
//      closeBtn.on("click",function(){
//      	  if (typeof (_config.btClose) == "function") {
//                _config.btClose(_self);
//          }
//      	 $(".deplistWrap,.closeBar,.membersListBtn").hide();
//      });
      
      eSelectPeople.handleSearch();
      eSelectPeople.loadDeptInfo(membersList, function() {
    	  var wrap = $(".membersListWarp");
    	  var isShowTag = wrap.attr("_isShowTag");
    	  if(isShowTag == "true"){
        	  eSelectPeople.loadCustomInfo();//自定义标签
    	  }
    	  eSelectPeople.handleTree();
    	  //查看成员
          wrap.on("click",".getIn",function(event){ 
        	  event.preventDefault();
  			  event.stopPropagation();
              var depId = $(this).attr("data-id");
              var isTag = $(this).parents("ul").hasClass("tagList");
              if(isTag){
            	  actionUrl = 'mobile/common/getWTagChild.action?tagId='+depId+"&time"+new Date().getTime();
              }else{
            	  actionUrl = 'mobile/common/getDeptUser.action?deptId='+depId+"&time"+new Date().getTime();
              }
              eSelectPeople.loadDate(actionUrl,function(data){
            	  var data = data.value;//.userlist;
                  var peoplelist = $("<ul class='memberList'></ul>");
                 eSelectPeople.printPeople(data,peoplelist);
                  eSelectPeople.handlePopSearchResult();
                  $(".depPeopleList").append(peoplelist);
              });
          });
    	  //选择成员
          eSelectPeople.handleSelMumber();          
      }); 
      _self.eshow = function(config){
          var _self = $(this);
          var _config = config||{};
          for(var obj in config){
        	  if (typeof ( config [obj]) != "function" ){
        		  _self.attr(obj,_config[obj]);
        	  }
          }
    	  if(_config._isShowTag){
    		  if($(".tagList").length == 0){
    			  eSelectPeople.loadCustomInfo();
    		  }
    	  }else{
    		  $(".tagList").remove();
    	  }
          _self.show().find(".deplistWrap").show();
          _self.find(".membersListBtn").show();
      };
      return _self;  
	};
})(jQuery);