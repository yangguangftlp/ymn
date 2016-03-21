var subUrl="mobile/appraisal/appraisalOperate.action",
    jumpUrl="";
var isError=false;
$(function(){
    //提交评价
    subEvaluation();
});
//提交评价
function subEvaluation(){
    $("#subEvaluation").on(touchEv,function(){
    	
        //判断分数是否合理
        if(judgeScore()){
        	
            $("form").verification({
                callback : function(){
                    $.ajax({
                        url: subUrl,
                        data:{
                            jsonAppraisalInfo : JSON.stringify(getData())
                        },
                        type:"post",
                        dataType:"json",
                        beforeSend:function(){
                            $("body").loading({
                                move:"show"
                            });
                        },
                        success:function(resData, textStatus){
                            if (0 == resData.status) {
                                window.location.replace("mobile/appraisal/appraisalView.action?flag=1&eaId="+$("#eaId").val());
                            } else {
                                $("body").loading({
                                    move:"hide"
                                });
                                $("body").toast({
                                    msg:resData.errorMsg,
                                    type:2,
                                    callMode:"func"
                                });
                            }
                        },
                        error:function(XMLHttpRequest, textStatus, errorThrown) {
                            $("body").loading({
                                move:"hide"
                            });
                            $("body").toast({
                                msg:"系统异常，请稍后再试",
                                type:2,
                                callMode:"func"
                            });
                        }
                    });
                }
            });
        }
    });
}
//收集数据
function getData(){
    var _score=[];
    $(".problemList>article").each(function(index){
        var $this=$(this),_everyData;
        _everyData={
            id : $this.attr("id"),
            overallMerit : $this.attr("overallMerit")
        };
        if(!!$this.find("input").val()){
            _everyData.score = $this.find("input").val();
        }
        if("true" == _everyData.overallMerit){
            _everyData.opinion = $.trim($this.find("textarea").val()).replaceAll("\r","<br/>");
        }
        _score.push(_everyData);
    });
    var data={
        id : $("#appraisalId").val(),
        eaId : $("#eaId").val(),
        problemScore:_score,
        commandInfo : {
            commandType : 1
        }
    };
    return data;

}
//判断分数是否合理
function judgeScore(){
    var flag=true;
    $(".problemList>article").each(function(index){
        var $this=$(this);
        var _d=/^0$|^[1-9]\d*$/;
        var score=parseInt($this.find("input").attr("scores"));
        if($this.find("input").val() == ""){
            toastFun({
                msg:"第"+(index+1)+"项评分不能为空",
                type:2,
                callMode:"func",
                end:function(){
                    $("html,body").animate({scrollTop: $this.find("input").offset().top}, 500);
                    $this.find("input")[0].focus();
                }
            });
            flag=false;
        }else if(!!$this.find("input").val() && $this.find("input").val().indexOf(".")>-1){
            toastFun({
                msg:"第"+(index+1)+"项评分不可以为小数",
                type:2,
                callMode:"func",
                end:function(){
                    $("html,body").animate({scrollTop: $this.find("input").offset().top}, 500);
                    $this.find("input")[0].focus();
                    $this.find("input").val("");
                }
            });
            flag=false;
        }else if(!!$this.find("input").val() && parseInt($this.find("input").val()) < 1){
            toastFun({
                msg:"第"+(index+1)+"项评分不可以为0",
                type:2,
                callMode:"func",
                end:function(){
                    $("html,body").animate({scrollTop: $this.find("input").offset().top}, 500);
                    $this.find("input")[0].focus();
                    $this.find("input").val("");
                }
            });
            flag=false;
        }else if(!!$this.find("input").val() && parseInt($this.find("input").val()) > score){
            toastFun({
                msg:"第"+(index+1)+"项评分已超出最大评分值",
                type:2,
                callMode:"func",
                end:function(){
                    $("html,body").animate({scrollTop: $this.find("input").offset().top}, 500);
                    $this.find("input")[0].focus();
                    $this.find("input").val("");
                }
            });
            flag=false;
        }
    });
    return flag;
};
