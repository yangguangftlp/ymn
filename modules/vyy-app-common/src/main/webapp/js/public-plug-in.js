var isSupportTouch = "ontouchend" in document ? true : false,
    touchEv = isSupportTouch ? 'touchend' : 'click';
/* toast效果 */
var $scrollTop;
+function($){
    //Toast类定义
    var Toast = function (el,options) {
        this.options = options;
        this.$el = el;
        this.init();
    };
    Toast.prototype.Defaults={
        msg:"成功...",//显示信息
        url:"",
        transferType:"post",
        time:500,//模态框消失延迟的时间
        type:1,//1代表成功后提示，2代表表单验证提示,3代表温馨提示,4退回原因,5删除，
        autoFill:false,
        missingText:"通过",//没有内容的时候，默认回填内容
        start:function(){
        },
        end:function(){
        },
        dataChange:function($val){
            return $val;
        },
        callMode:"plugin"//plugin代表点击事件，func代表直接执行动画
    };
    Toast.prototype.init=function(){
        var ops=$.extend({},this.Defaults,this.options);
        if(ops.callMode=="plugin"){
            this.$el.trigger("click");
        }
        if(ops.callMode=="func"){
            Toast.prototype.lifeStart(ops);
            return;
        }
        this.$el.click(function(){
            Toast.prototype.lifeStart(ops);
        })
    };
    Toast.prototype.lifeStart=function(ops){
        //if($(".screenModal").length==0)
        //{
        //console.log(ops.end);
        Toast.prototype.judgeType(ops);
        //}
    };
    Toast.prototype.judgeType=function(ops){
        if(ops.type==1)
        {
            $("body").append('<div class="screenModal">'+
                '<div class="promptModal">'+ops.msg+'</div>'+
                '</div>'
            );
            var $screenModal=$('.screenModal');
            setTimeout(function(){
                $screenModal.stop().animate({
                    opacity:0
                },500,function(){
                    $screenModal.css("opacity",1).remove();
                    ops.end();
                });
            },ops.time);
        }else if(ops.type==2)
        {
            $("body").append('<div class="screenModal">'+
                '<div class="onePrompt"><i class="fa fa-exclamation-circle"></i>'+ops.msg+'</div>'+
                '</div>'
            );
            var $screenModal=$('.screenModal');
            $(".onePrompt").stop().animate({
                top:"0px"
            },500,function(){
                setTimeout(function(){
                    $(".onePrompt").stop().animate({
                        top:"-40px"
                    },500,function(){
                        $screenModal.remove();
                        ops.end();
                    })
                },ops.time)
            });
        }else if(ops.type==3)
        {
            $("body").append('<div class="screenModal">'+
                '<div class="twoPromptCon">'+
                '<header>温馨提示</header>'+
                '<div>'+ops.msg+'</div>'+
                '<footer>'+
                '<button type="button">取消</button>'+
                '<button type="button" id="sureBtn">确定</button>'+
                '</footer>'+
                '</div>'+
                '</div>'
            );
            var $screenModal=$('.screenModal');
            $(".twoPromptCon footer button").click(function(){
                var $thisId=$(this).attr("id");
                $screenModal.stop().animate({
                    opacity:0
                },500,function(){
                    $screenModal.css("opacity",1).remove();
                    if($thisId=="sureBtn"){
                        ops.end();
                    }
                });
            });
        }else if(ops.type==4)
        {
            /*$scrollTop=$("body").scrollTop();
             $("body").addClass("windowHeight");*/
            $("body").append('<div class="reasonMyModal">'+
                '<div class="returnReasons">'+
                '<form id="returnForm" action='+ops.url+' method='+ops.transferType+'>'+
                '<header>'+ops.msg+'</header>'+
                '<textarea placeholder="在此填写'+ops.msg+'" name="reasons"  required="required"></textarea>'+
                '<footer>'+
                '<button type="reset">取消</button>'+
                '<button type="button">确定</button>'+
                '</footer>'+
                '</form>'+
                '</div>'+
                '</div>'
            );
            //取消退回模态框
            $(".returnReasons footer button:eq(0)").click(function(){
                $(".reasonMyModal").animate({
                    opacity:0
                },300,function(){
                    $(".reasonMyModal").remove();
                });
            });
            //确认退回原因
            $(".returnReasons footer button:eq(1)").click(function(e){
                var $textareaVal=$(".returnReasons textarea").val();
                if(!ops.autoFill && $textareaVal == ""){
                    $(".returnReasons textarea").addClass("placeholderStyle");
                }else{
                    $(".reasonMyModal").animate({
                        opacity:0
                    },300,function(){
                        if(ops.autoFill && $textareaVal == ""){
                            $(".returnReasons textarea").val(ops.missingText);
                        }
                        ops.dataChange($(".returnReasons textarea").val());
                        $(".reasonMyModal").remove();
                    });
                }
            });

        }else if(ops.type==5){
            var $html='<div class="screenModal delImgModal">'+
                '<div class="delImg">'+
                '<header>'+ops.msg+'</header>'+
                '<footer>'+
                '<button id="quXiao">取消</button>'+
                '<button>确定</button>'+
                '</footer>'+
                '</div>'+
                '</div>';
            $("body").append($html);

            if($(".delImg header").height() < 50){
                $(".delImg header").css("lineHeight","60px");
            };
            //取消删除
            $("#quXiao").click(function(){
                $(".delImgModal").remove();
                ops.start();
            });
            //确认删除
            $(".delImg footer button:eq(1)").click(function(){
                $(".delImgModal").remove();
                //回调
                ops.end();
            });
        }
        //获得浏览器的高度
        var windowH=window.innerHeight;
        var windowW=window.innerWidth;
        //设置催办成功模态框的top、left
        var $promptModalW=$(".promptModal").outerWidth();
        var $promptModalH=$(".promptModal").outerHeight();
        $(".loading,.promptModal").css({"top":(windowH-$promptModalH)/2,"left":(windowW-$promptModalW)/2});

        $(".twoPromptCon").css({"top":(windowH/2-50),"left":(windowW/2-120)});
        $(".delImg").css({top:(windowH/2-48)});
    };
    //toast插件定义
    $.fn.toast=toastFun=function(_ops){
        new Toast(this,_ops);
        return this;
    }
}(jQuery);
/* 导航 */
+function($){
    var Nav=function(el,options){
        this.options = options;
        this.$el = el;
        this.init();
    };
    Nav.prototype.Defaults={
        end:function($index){
            return $index;
        }
    };
    Nav.prototype.init=function(){
        var ops= $.extend({},this.Defaults,this.options);
        var pointX=new Array();
        var num=this.$el.children("li").length;
        var proportion=parseFloat(100/num).toFixed(2);
        for(var i=0;i<num;i++){
            pointX.push(i*proportion+"%");
        }
        for(var j= 0;j<num-1;j++){
            this.$el.children("li").eq(j).addClass("borderRight");
        }
        this.$el.append('<div class="line"></div>');
        this.$el.children(".line,li").css("width",proportion+"%");
        Nav.prototype.clickFunc(this.$el,ops,pointX);
    };
    Nav.prototype.clickFunc=function(ele,ops,pointX){
        $.each(ele.children("li"),function(index,item){
            $(item).click(function(){
                //导航标题切换
                $("li.navSelected").removeClass("navSelected");
                $(this).addClass("navSelected");
                $(".line").stop().animate({left:pointX[index]},100);
                ops.end(index);
            });
        });
    };
    //nav插件定义
    $.fn.navigation=function(_ops){
        new Nav(this,_ops);
        return this;
    }
}(jQuery);

+function(){
    var Loading=function(el,options){
        this.options = options;
        this.$el = el;
        this.init();
    };
    Loading.prototype.Defaults={
        msg:"加载中...",
        move:"show",
        end:function(){

        }
    };
    Loading.prototype.init=function(){
        var ops= $.extend({},this.Defaults,this.options);
        if("show"==ops.move){
            $("body").append('<div class="loadingModal" id="loadModal">'+
                '<div class="oneLoading loading">'+
                '<div><img src="images/smallLoading.gif"></div>'+
                '<div>'+ops.msg+'</div>'+
                '</div>'+
                '</div>'
            );
            //获得浏览器的高度
            var windowH=window.innerHeight;
            var windowW=window.innerWidth;
            //设置loading模态框的top、left
            $(".loading").css({"top":(windowH/2-50),"left":(windowW/2-67)});
        }else{
            $(".loadingModal").remove();
            ops.end();
        }
    };
    $.fn.loading=function(_ops){
        new Loading(this,_ops);
        return this;
    }

}(jQuery);

/* 表单验证 */
+function($){
    var Verification=function(el,options){
        this.options = options;
        this.$el = el;
        this.init();
    };
    Verification.prototype.Defaults={
        dataUrl:"",
        maxNum :100000000,
        callback:function(resp){
            //return resp;
        }
    };
    Verification.prototype.init=function(){
        var exp={
            m:/^[0-9]\d{0,11}(\.[0-9]{1,2})?$/,//钱
            d:/^0$|^[1-9]\d*$/,//整数
            month:/^([1-9]|1[0-2])$/,//月
            e:/[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,//邮箱
            //p:/*/^((\d|[1-9]\d)(\.\d+)?|100)$/*//^[1-9]\d?\.\d+$|^0\.\d+$|^[1-9]\d?$|^0$|^100$/
            p:/^((\d|[1-9]\d)(\.\d+)?|100)$/,//百分比
            bank:/^\d*$/,//银行卡号
            wholeNum : /^[1-9]\d*$/,//整数
            mobile : /^1\d{10}$/
        };
        var ops= $.extend({},this.Defaults,this.options);
        var arr=this.$el.serializeArray();
        for(var i=0;i<arr.length;i++)
        {
            var $thisInput=$("#"+arr[i].name);
            var $thisVal=$.trim($thisInput.val());
            var $thisSeectVal=$thisInput.find("option:selected").val();
            if(!!$thisInput.attr("data-con") && ($thisVal=="" || $thisSeectVal=="-1")){
                toastFun({
                    msg:$thisInput.attr("data-empty"),
                    type:2,
                    callMode:"func",
                    end:function(){
                        if($thisInput.attr("type") == "hidden"){
                            var _ele = $("[data-link-field="+$thisInput.attr("id")+"]");
                            if(_ele.length == 0){
                                _ele = $thisInput;
                                $thisInput[0].focus();
                            }else{
                                _ele.children("input")[0].focus();
                            }
                            $("html,body").animate({scrollTop: _ele.offset().top}, 500);
                        }else{
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    }
                });
                return;
            }else if("m"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.m.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }else{
                    if( parseInt($thisVal) > parseInt(ops.maxNum)){
                        toastFun({
                            msg:"所填的金额必须小于一亿",
                            type:2,
                            callMode:"func",
                            end:function(){
                                $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                                $thisInput[0].focus();
                            }
                        });
                        return;
                    }
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("d"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.d.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("month"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.month.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput.focus();
                        }
                    });
                    return;
                }else{
                    var name = $thisInput.prop("name");
                    if(name=="endMonth"){
                        var smonth = $("#startMonth").val();
                        if(parseInt($thisVal)<parseInt(smonth)){
                            toastFun({
                                msg:"第二个月份要大于第一个月份",
                                type:2,
                                callMode:"func",
                                end:function(){
                                    $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                                    $thisInput.focus();
                                }
                            });
                            return;
                        }
                    }
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("e"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.e.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("p"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.p.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("bank"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.bank.test($thisVal.replace(/\s/g,''))){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("wholeNum"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.wholeNum.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }else if("mobile"==$thisInput.attr("data-con") && $thisVal!=""){
                if(!exp.mobile.test($thisVal)){
                    toastFun({
                        msg:$thisInput.attr("data-error"),
                        type:2,
                        callMode:"func",
                        end:function(){
                            $("html,body").animate({scrollTop: $thisInput.offset().top}, 500);
                            $thisInput[0].focus();
                        }
                    });
                    return;
                }
                if(i==arr.length-1){
                    ops.callback();
                }
            }
            else if(i==arr.length-1){
                ops.callback();
            }
        }
    };
    $.fn.verification=function(_ops){
        new Verification(this,_ops);
        return this;
    }
}(jQuery);

+function($){
    $.fn.endlessScroll=function(_dom){
        return ($(window).scrollTop() + $(window).height() + 40 >= $(_dom).height());
    }
}(jQuery);
//银行卡号4位为一组
+function($){
    var BankCardNumr = function (el,options) {
        this.options = options;
        this.$el = el;
        this.init();
    };
    BankCardNumr.prototype={
        init:function(){
            var num = 0;
            this.$el.on("keyup",function(){
                var str = $(this).val();
                var elem = $(this);
                var reg=/^(\d{1,4} ?){0,}$/;
                if(!reg.test(str)){
                    for(var i=str.length;i>0;i--){
                        var newStr=str.substr(0,i-1);
                        if(reg.test(newStr) || newStr == ""){
                            $(this).val(newStr);
                            return;
                        }
                    }
                }
                if(str.length > num){
                    var c = str.replace(/\s/g,"");
                    if(str != "" && c.length > 4 && c.length % 4 == 1){
                        $(this).val(str.replace(/\s/g,'').replace(/(\d{4})(?=\d)/g,"$1 "));
                    }
                }
                if(elem.setSelectionRange){//W3C
                    setTimeout(function(){
                        elem.setSelectionRange(str.length,elem.value.length);
                        elem.focus();
                    },0);
                }
                num = str.length;
            });
            //如果val不为空
            if(this.$el.val() != ""){
                var strVal= this.$el.val();
                var newC = strVal.replace(/\s/g,"");
                if(newC.length > 4){
                    this.$el.val(strVal.replace(/\s/g,'').replace(/(\d{4})(?=\d)/g,"$1 "));
                }
            }

        }
    };
    $.fn.bankCardNumr=function(_ops){
        new BankCardNumr(this,_ops);
        return this;
    }
    //$(this).val(str.replace(/\s/g,'').replace(/(\d{4})(?=\d)/g,"$1 "))
}(jQuery);
//将金额转化成大写金额
+function($){
    var ConvertCurrency= function (el,options){
        this.options = options;
        this.$el = el;
        this.init();
    };
    ConvertCurrency.prototype={
        Defaults : {
            "targetSource" : "",//目标源
            "MAXIMUM_NUMBER" : 99999999999.99//最大数
        },
        init : function(){
            var that = this;
            var ops = $.extend({},this.Defaults,this.options);
            this.$el.on("input",function(){
                var currencyDigits=$(this).val();
                var newcurrencyDigits=that.verificationChara($(this),ops,currencyDigits);
                if(ops.targetSource!=""&&ops.targetSource){
                    that.segmentation($(this),ops,newcurrencyDigits);
                }

            });
        },
        verificationChara : function(_ele,_ops,_currencyDigits){
            console.log(_currencyDigits);
            // 验证输入字符串是否合法
            if (_currencyDigits.toString() == "") {
                if(_ops.targetSource!=""&&_ops.targetSource){
                    _ops.targetSource.text("");
                }

                return _currencyDigits;
            }
            //判断是否输入有效的数字格式
            var reg = /^[0-9]\d{0,11}(\.[0-9]*)?$/;
            if (!reg.test(_currencyDigits)) {
                for(var i=_currencyDigits.length;i>0;i--){
                    var str=_currencyDigits.substr(0,i-1);
                    if(reg.test(str) || str == ""){
                        _ele.val(str);
                        return str;
                    }
                }
            }
            _currencyDigits = _currencyDigits.replace(/,/g, "");
            _currencyDigits = _currencyDigits.replace(/^0+/, "");
            //判断输入的数字是否大于定义的数值
            if (Number(_currencyDigits) > _ops.MAXIMUM_NUMBER) {
                $("body").toast({
                    msg:"您输入的数字太大了",
                    type:2,
                    callMode:"func"
                });
                _ele.focus();
                return _currencyDigits;
            }
            return _currencyDigits;
        },
        segmentation : function(_ele,ops,_currencyDigits){
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
                if(decimal.length>2){
                    decimal = decimal.substr(0, 2);
                    _ele.val(integral+"."+decimal);
                }
            }
            else {
                integral = parts[0];
                decimal = "";
            }
            // 实例化字符大写人民币汉字对应的数字
            digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
            radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
            bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);
            decimals = new Array(CN_TEN_CENT, CN_CENT);

            outputCharacters = "";
            //大于零处理逻辑
            if (Number(integral) > 0) {
                zeroCount = 0;
                for (i = 0; i < integral.length; i++) {
                    p = integral.length - i - 1;
                    d = integral.substr(i, 1);
                    quotient = p / 4;
                    modulus = p % 4;
                    if (d == "0") {
                        zeroCount++;
                    }
                    else {
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
            //确认并返回最终的输出字符串
            /*if (outputCharacters == "") {
             outputCharacters = CN_ZERO + CN_DOLLAR;
             }*/
            if (decimal == "" && outputCharacters != "") {
                outputCharacters += CN_INTEGER;
            }
            //获取人民币大写
            ops.targetSource.text(outputCharacters);
        }
    };
    $.fn.convertCurrency=function(_ops){
        new ConvertCurrency(this,_ops);
        return this;
    }
}(jQuery)

//阿拉伯数字转汉字
function digitalSwitchChart(_currencyDigits){
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
    digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
    radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
    bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);

    outputCharacters = "";
    //大于零处理逻辑
    if (Number(_currencyDigits) > 0) {
        zeroCount = 0;
        for (i = 0; i < _currencyDigits.length; i++) {
            p = _currencyDigits.length - i - 1;
            d = _currencyDigits.substr(i, 1);
            quotient = p / 4;
            modulus = p % 4;
            if (d == "0") {
                zeroCount++;
            }
            else {
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
    //获取人民币大写
    return outputCharacters;
}
String.prototype.replaceAll  = function(s1,s2){
    return this.replace(new RegExp(s1,"gm"),s2);
};
/*@zedd 2016.2
 * 微信客户端上传图片
 * 使用方法
 * <div class="addCanningCopy" id="scanCopyList">
        <div class="canningTit">图片：</div>
        <div class="canningCopyList">
            <div class="canningCopy">
                <div class="addCanningCopyBtn" id="addScanCopy">
                    <img src="images/addImg.png">
                </div>
            </div>
        </div>
    </div>
        
 * $("body").clientUploadImg({
		uploadUrl:"common/uploadAccessory.action",//上传路径
        boxName :'#addScanCopy',//上传区域
        numBoxName:"#JphotoNum",//计数区域，非必须
        maxImgLen : 3,//最大上传数，非必须
        imgType:"jpg|png|JPG|PNG"//格式
	});
 * 
 * */
+(function($) {
    $.fn.clientUploadImg = function(options){
        var settings = {
                uploadUrl:"",
                boxName :'',
                numBoxName:"",
                maxImgLen :9,
                imgType:".jpg|.png|.JPG|.PNG"
            };
        return this.each(function () {
            if (options) {
                settings = $.extend(settings, options);  
            }
            var uploadUrl = settings.uploadUrl,
                $uploadWrap = $(settings.boxName),
                len = settings.maxImgLen,$num,
                imgType = settings.imgType;
            if(len == 9){
            	len = 10000;//不设张数限制
            }
            if(settings.numBoxName !=""){
                $num =  $(settings.numBoxName);
            }else{
            	var JphotoNumId = settings.boxName.replace("#","")+"_Num";
                $uploadWrap.after('<span id="'+JphotoNumId+'" class="JphotoNum" style="visibility:hidden">0<span>');
                $num = $("#"+JphotoNumId);
            }
            /*是否微信客户端*/  
            function is_wx_client(){  
                 var ua = navigator.userAgent.toLowerCase();  
                 var isWeixin = ua.indexOf('micromessenger') != -1;
                 var isAndroid = ua.indexOf('android') != -1;
                 var isIos = (ua.indexOf('iphone') != -1) || (ua.indexOf('ipad') != -1);
                 if(isWeixin && isAndroid || isIos){//手机端
                     return false;
                 }else{
                     return true;  
                 }
            };
            function uploadImg($uploadWrap,len){
              if(is_wx_client()){
                    //解绑事件
                   $uploadWrap.unbind("click");
                   //插入上传控件
                   var fileId = settings.boxName + "File",
                       fileName = fileId.split("#")[1];
                   $uploadWrap.append('<input id="'+fileName+'" type="file" accept="image/*" name="file" multiple="multiple"/>');
                   if(window.File && window.FileList && window.FileReader && window.Blob) {
                     $("body").on("change",fileId,function(e){
                            e = e || window.event;
                            var files = e.target.files,
                                fLen = files.length,
                                fd = new FormData();
                            for(var i=0;i<fLen;i++){
                                var file = $(fileId).get(0).files[i];
                                //图片格式
                                var reg = "/"+imgType+"/i";
                                    reg = eval(reg);
                                if (!reg.test(file.name)) {
                                    $("body").toast({
                                          msg:"只能上传jpg,png图片！",
                                          type:1,
                                          callMode:"func"
                                    });
                                    return false;
                                }  
                                fd.append("file",file);
                            }
                            var photoNum = parseInt($num.html());                   
                            var seledNum = fLen + photoNum;
                            if(fLen == 0){//未选择图片
                                return false;
                            }
                            if(fLen>9){
                            	var msg = "一次最多选择9张图片！";
                                $("body").toast({
                                     msg:msg,
                                     type:1,
                                     callMode:"func"
                                 });
                                return false;
                            }
                            if(fLen > len || seledNum > len){
                                var msg = "最多选择"+len+"张图片！";
                                $("body").toast({
                                     msg:msg,
                                     type:1,
                                     callMode:"func"
                                 });
                                return false;
                            }
                            
                            $.ajax({
                              url : uploadUrl,
                              type : 'post',
                              data : fd,
                              processData: false,
                              contentType: false,
                              beforeSend:function(){
                                $("body").loading({
                                      msg:"上传中...",
                                      move:"show"
                                  });
                              },
                              complete:function(){
                                $("body").loading({
                                      move:"hide"
                                  });
                              },
                              success : function(data, textStatus) {
                                uploadImgList(data);                                
                              },
                              error : function(XMLHttpRequest, textStatus, errorThrown) {
                                $("body").toast({
                                      msg:"操作失败，请稍后再试！",
                                      type:1,
                                      callMode:"func"
                                });
                              }
                              
                          });
                           
                        });
                        
                    } else {
                        $("body").toast({
                           msg:"您的浏览器不支持File API！",
                           type:1,
                           callMode:"func"
                       });
                    }
              }
            }
            //上传文件缩略图展示
            function uploadImgList(data){
                   var datalen = data.value.length;
                   for(var i=0;i<datalen;i++){
                      var $str='<div class="canningCopyImg" accessoryId='+data.value[i].accessoryId+'>'+
                          '<a href="javascript:;" data-original='+data.value[i].original+'><img data-original='+data.value[i].original+ '  src='+data.value[i].thumb+' ></a>'+
                          '<i></i>'+
                          '</div>';
                      $uploadWrap.before($str);
                      var photoNum = parseInt($num.html())+1;
                      $num.html(photoNum);
                      /*if(photoNum==len){
                          $uploadWrap.hide();
                      }*/
                  }
                 uploadImgPreview();
              }
              //上传文件大图预览
              function uploadImgPreview(){
            	  $("body").zoom({
                      parentName : ".canningCopy",
                      boxName:".canningCopyImg",
                      tagName : "a"
                  });
              }
              uploadImg($uploadWrap,len);
        });
    };
})(jQuery);
//客户端预览图片
+(function($) {
    $.fn.pcPreviewImg = function(options){
        var settings = {
        		parentName:"",
        		boxName :'',
        		tagName:""
            };
        return this.each(function () {
            if (options) {
                settings = $.extend(settings, options);  
            }
            var parentName = settings.parentName,
            	boxName = settings.boxName,
            	tagName = settings.tagName;
            
            /*是否微信客户端*/  
            function is_wx_pc(){  
                 var ua = navigator.userAgent.toLowerCase();  
                 var isWeixin = ua.indexOf('micromessenger') != -1;
                 var isAndroid = ua.indexOf('android') != -1;
                 var isIos = (ua.indexOf('iphone') != -1) || (ua.indexOf('ipad') != -1);
                 if(isWeixin && isAndroid || isIos){//手机端
                     return false;
                 }else{//pc端
                     return true;  
                 }
            };
            if(is_wx_pc()){
            	$("body").zoom({
                    parentName : parentName,
                    boxName:boxName,
                    tagName : tagName
              });
            }
        });
    };
})(jQuery);