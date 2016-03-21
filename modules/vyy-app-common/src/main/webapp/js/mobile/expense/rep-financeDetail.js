/**
 * Created by liyuxia on 2015/8/22.
 */
$(function(){
    //点击确认报销
    $("#confirmRepExp").click(function(){
        //若成功通过
        $("body").toast({
            msg:"确认成功",
            type:1,
            callMode:"func"
        })
    });
    //点击拒绝报销
    $("#refuseRepExp").toast({
        msg:"拒绝原因",
        url:"www.baidu.com",
        type:4,
        dataChange:function($data){//$data退回原因
            //提交表单(数据的传输)


            //把原因回填到页面
            $(".returnInit").removeClass("myHide");
            $(".returnInit span").text($data);
        }
    });


});
