//url , 是否需要标记已下载
$(function(){
    /*var dataUrl=[$(".nav li").eq(0).attr("data-url"),$(".nav li").eq(1).attr("data-url")];*/
    /*loadData($(".navSelected"), 0,$(".navSelected").attr("data-url"));*/
    /*$(".nav li").removeAttr("data-url");*/

    //导航切换
    $(".warp>.nav").navigation({
        //切换时需要做的事
        end:function(index){//index是导航的序号，从0开始计数
            //对应得内容进行切换
            $(".warp>section:eq("+index+")").removeClass("myHide").siblings("section").addClass("myHide");
            // 根据导航切换内容
            /*$(".warp>section:eq("+index+") ul").html("");
            loadData($(".nav li").eq(index), index,dataUrl[index]);*/
        }
    });
});
function loadData($this, index,$dataUrl) {
    $.ajax({
        url : $dataUrl,
        type : 'post',
        data : {
            pageIndex: 1,
            operationType : (index+1)
        },
        beforeSend : function() {
            $("body").loading({
                move:"show"
            });
        },
        complete : function() {
            $("body").loading({
                move:"hide"
            });
        },
        success : function(resData, textStatus) {
            $this.attr("flag", "1");
            var data = resData.value;
            var auditList = $("section").eq(index).children("ul");
            if (!!data) {
                var html = "";
                var size = data.length;
                if (size > 0) {
                    for (var i = 0; i < size; i++) {
                        html='';
                        auditList.append(html);
                    }
                } else {
                    auditList.append("<div class='noRecord'>没有记录......</div>");
                }
            } else {
                auditList.append("<div class='noRecord'>没有记录......</div>");
            }
        },
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            // alert(JSON.stringify(errorThrown));
        }
    });
}
