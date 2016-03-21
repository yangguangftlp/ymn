/*_operatingStatus 0：load ,1:scroll,2 : search */
/*window.Meeting_Info = {
 // 配置
 "pageSize": 10
 };*/
var isSlideBottom=false,
    isLoadIng = false,//是否正在加载
    loadOperationType = "load",//操作类型（新）
    changeData=originalData=[],
    typeField = "",
    initValue,
    dataKeyword = "";
function requestDataAjax(_url,_data,_operatingStatus) {
    if(!isLoadIng){
        $.ajax({
            url : _url,
            type : 'post',
            data : _data,
            beforeSend : function() {
                $("body").loading({
                    move:"show"
                });
                isLoadIng = true;
            },
            complete : function() {
                isLoadIng = false;
                $("body").loading({
                    move:"hide"
                });
            },
            success : function(resData, textStatus) {
                isLoadIng = false;
                $("body").loading({
                    move:"hide"
                });
                //数据渲染
                renderingData(resData,_operatingStatus);
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                isLoadIng = false;
                $("body").toast({
                    msg:"系统异常，请稍后再试",
                    type:2,
                    callMode:"func"
                });
            }
        });
    }
}
//重置搜索
function resetSearch(){
    $("#resetSearch").click(function(){
        $(".filterForm select").val("-1");
        $(".filterForm input").val("").data("uId","");
    });
}
//搜索 _listModule值和搜索的数据
function mySearch(_listModule,_hasTimeCompare){
    $("#searchBtn").click(function(){
        if(_hasTimeCompare){//是否有开始时间与结束时间的比较
            timeCheck();
        }
        loadOperationType = "search";
        var $searchData=getSearchData();
        //将搜索条件加到sessionStorage
        sessionStorage.filterConditions = JSON.stringify($searchData);
        $(".filterForm").stop().animate({
            height: 'hide'
        },500,function(){
            if(_listModule == 2){
                $(".newSearchHead:eq(1) input").prop("checked",false);
            }else if(_listModule == 1){
                $(".newSearchHead input").prop("checked",false);
            }
        });
        //加载数据
        getFilterData($searchData);
        //保证背景和字的颜色正确
        $(".newSearchHead:eq(1)").addClass("mainBg whiteColor").removeClass("mainColor whiteBg");
        $(".publicNav").addClass("mainBg").removeClass("whiteBg");
    });
}
//导航的伸缩变化
function retractableNav(_listModule){
    if(0 == _listModule || 1 == _listModule){
        $(".newSearchHead input").on("click",function(){
            var $this=$(this);
            if($this.prop("checked")){
                $(".switchCon").eq(0).stop().animate({
                    height: 'show'
                },500);
            }else{
                $(".switchCon").eq(0).stop().animate({
                    height: 'hide'
                },500);
            }
        });
    }else if(2 == _listModule){
        $(".newSearchHead").each(function(index){
            var $this =  $(this),
                $input = $this.children("input"),
                $brothers = $this.siblings(".newSearchHead"),
                $brothersInput = $brothers.children("input");
            $input.click(function(){
                if($input.prop("checked")){
                    $this.addClass("whiteBg mainColor").removeClass("whiteColor");
                    $(".switchCon").eq(index).stop().animate({
                        height: 'show'
                    },500);
                    $brothers.addClass("mainBg whiteColor").removeClass("whiteBg mainColor");
                    if($brothersInput.prop("checked")){
                        $brothersInput.prop("checked",false);
                        $(".switchCon").eq(index).siblings(".switchCon").stop().animate({
                            height: 'hide'
                        },500);
                    }else{
                        $(".publicNav").removeClass("mainBg").addClass("whiteBg");
                    }
                    if(index == 0){
                        $(".filterForm select").val("-1");
                        $(".filterForm input").val("");
                    }
                }else{
                    $this.removeClass("whiteBg mainColor").addClass("mainBg whiteColor");
                    $(".switchCon").eq(index).stop().animate({
                        height: 'hide'
                    },500);
                    if(!$brothersInput.prop("checked")){
                        $(".publicNav").addClass("mainBg").removeClass("whiteBg");
                    }
                }
            });
        });
    }
}

//根据状态请求数据
function requestData(_listModule,_dataUrl,_isMeeting){
    $(".switchCon:eq(0) ul li").each(function(index){
        var $this = $(this);
        $this.on("click",function(){
            if($this.children("i").length < 1){
                $this.append('<i class="fa fa-check mainColor"></i>');//选择状态增加选中的符号
                $this.siblings().children("i").remove();//选中行的兄弟去掉选中的符号
                $(".newSearchHead:eq(0) input").prop("checked",false);//全部状态后小三角的状态发生变化
                sessionStorage.status = index.toString();//将选中行的index储存在sessionStorage中
                var _text = $this.text();//得到对应选中列的字符
                //保证背景和字的颜色正确
                if(2 == _listModule){
                    $(".newSearchHead:eq(0)").addClass("mainBg whiteColor").removeClass("mainColor whiteBg");
                    $(".publicNav").addClass("mainBg").removeClass("whiteBg");
                }
                $(".switchCon:eq(0)").stop().animate({//收起下拉列表
                    height: 'hide'
                },500,function(){
                    $(".newSearchHead:eq(0) b").text(_text);//使头部的文字与选中的文字相同
                    $("section ul").empty();//清空列表
                    var transferData = getBasicData(parseInt(sessionStorage.status));
                    requestDataAjax(_dataUrl[parseInt(sessionStorage.status)],transferData,0);
                });
                window.Current_Meeting_Type = index;//访问的第几个tab切换页
                //重新加载后，滚动请求的第一个页码为2
                initPage();
                //没有提示过"没有更多数据"
                isSlideBottom=false;
            }else{
                $(".switchCon").eq(0).stop().animate({
                    height: 'hide'
                },500);
                $(".newSearchHead:eq(0) input").prop("checked",false);
                $("section ul").empty();
                var transferData = getBasicData(index);
                requestDataAjax(_dataUrl[index],transferData,0);
                //保证背景和字的颜色正确
                if(2 == _listModule){
                    $(".newSearchHead:eq(0)").addClass("mainBg whiteColor").removeClass("mainColor whiteBg");
                    $(".publicNav").addClass("mainBg").removeClass("whiteBg");
                }

            }
        });
    });
}
//加载页面的时候，判断是否有选中项
function judgeDataStatus(_listModule,_dataUrl,_isMeeting){
    var urlLength = _dataUrl.length;
    var _status = sessionStorage.status;
    var _filterConditions = $.parseJSON(sessionStorage.filterConditions);
    if(0 == _listModule){
        if(_status == undefined){
            //加载全部数据
            var _data = getBasicData(0);
            requestDataAjax(_dataUrl[0],_data,0);
            $(".newSearchHead b").text($(".switchCon:eq(0) ul li:first").text());
            $(".newSearchNav ul li:first").append('<i class="fa fa-check mainColor"></i>');
        }else{
            //加载对应的数据
            _status = parseInt(_status);
            window.Current_Meeting_Type = _status;
            var _data = getBasicData(_status);
            requestDataAjax(_dataUrl[_status],_data,0);
            var _text = $(".newSearchNav ul li").eq(_status).text();
            $(".newSearchHead b").text(_text);//使头部的文字与选中的文字相同
            $(".newSearchNav ul li").eq(_status).append('<i class="fa fa-check mainColor"></i>');
        }
    }else if(1 == _listModule){
        if(judgeParameter(_filterConditions)){
            //加载全部数据
            var _data = getBasicData(0);
            requestDataAjax(_dataUrl[0],_data,0);

        }else{
            //加载对应的数据
            loadOperationType = "search";
            getFilterData(_filterConditions);
            initSearchData(_filterConditions);
        }
    }else if(2 == _listModule){
        if(undefined == _status &&　undefined == sessionStorage.filterConditions){
            //加载全部数据
            var transferData = getBasicData(0);
            requestDataAjax(_dataUrl[0],transferData,0);
            $(".newSearchHead b").text($(".switchCon:eq(0) ul li:first").text());
            $(".switchCon:eq(0) ul li:first").append('<i class="fa fa-check mainColor"></i>');
        }else if(undefined != _status && undefined == sessionStorage.filterConditions){
            //加载对应的数据
            _status = parseInt(_status);
            window.Current_Meeting_Type = _status;
            var transferData = getBasicData(_status);
            requestDataAjax(_dataUrl[_status],transferData,0);
            var _text = $(".switchCon:eq(0) ul li").eq(_status).text();
            $(".newSearchHead:eq(0) b").text(_text);//使头部的文字与选中的文字相同
            $(".switchCon:eq(0) ul li").eq(_status).append('<i class="fa fa-check mainColor"></i>');
        }else {
            var _text,_statusFlag;
            if(undefined != _status){
                _status = _statusFlag = parseInt(_status);
                window.Current_Meeting_Type = _status;
                _text = $(".switchCon:eq(0) ul li").eq(_status).text();
            }else{
                _statusFlag = 0;
                _text = $(".switchCon:eq(0) ul li").eq(0).text();
            }
            $(".switchCon:eq(0) ul li").eq(_statusFlag).append('<i class="fa fa-check mainColor"></i>');
            $(".newSearchHead:eq(0) b").text(_text);//使头部的文字与选中的文字相同
            loadOperationType = "search";
            getFilterData(_filterConditions);
            //赋值搜索值
            initSearchData(_filterConditions);
        }
    }
}
//基础参数
function basicTransferData(_typeField,_initValue){//_typeField类型字段的名字，_initValue状态初始化值
    typeField = _typeField;
    initValue = _initValue;
}
//得到基础参数
function getBasicData(_basicValue){
    var _transferData = {
        pageIndex: 1,
        pageSize: window.Meeting_Info.pageSize
    };
    if($.isFunction(window.specialData)){
    	var _transferData = $.extend({}, _transferData,specialData());
    }
    if("" != typeField && initValue == 0){
        _transferData[typeField] = _basicValue;
    }else if("" != typeField && initValue == 1){
        _transferData[typeField] = _basicValue+1;
    }
    if(!!dataKeyword){
        var _data={};
        _data[dataKeyword] = JSON.stringify(_transferData);
        return _data;
    }else{
        return _transferData;
    }
}