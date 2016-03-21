/*url  data
 *报销费用怎么传数据
 * 添加费用成功后 返回的数据
 *  */
$(function(){
    //申请报销
    applyForReim();
    //添加费用
    addReimbursement();
    //保存费用
    saveReimbursement();
    //取消费用
    cancelReimbursement();
    //删除费用
    delReimbursement();
});
function applyForReim(){
    $("#audit").click(function(){
        $(".warp>form").verification({
            callback:function(){
                mySubmit();
            }
        });
    });
}

function getData(){
    var expensefee = [];
    $(".stepwiseListModal .stepwiseList").each(function(index){
        if(!!!$(this).attr("data-id")){
            expensefee.push({
                "category" : $(this).attr("data-type"),//类型
                "money" : $(this).children("div").find("span").text()//金额
            });
        }
    });
    var data = {
        theme:$("#repExpTheme").val(),//主题
        department:$("#department").val(),
        reason:$("#reasons").val(),//报销事由
        amount:$("#money").val(),//金额
        expensefeeInfo:expensefee
    };
    if(!!$("#expenseId").val()){
        data.expenseId = $("#expenseId").val();
    }
    return data;
}
function mySubmit(){
    $.ajax({
        url: "mobile/expense/expenseApply.action",
        data:{
            expenseApplyInfo:JSON.stringify(getData())
        },
        type:"post",
        dataType:"json",
        beforeSend:function(){
            $("body").loading({
                move:"show"
            });
        },
        success:function(resData, textStatus){
            $("body").loading({
                move:"hide"
            });
            if (0 == resData.status) {
                window.location.replace($("#go-url").val()+"?id="+resData.value);
            } else {
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
                msg:textStatus,
                type:2,
                callMode:"func"
            });
        }
    });
}
//添加费用
function addReimbursement(){
    $("#addQuestion>span,#addQuestion>b").click(function(){
        $("#reimbursement").addClass("boxModel").show();
        //判断是否已添加费用类别
        $("#reimbursementForm select option").removeClass("myHide");
        var feeListLen=$(".stepwiseListModal .stepwiseList").length;
        if(feeListLen>0){
            $(".stepwiseListModal .stepwiseList").each(function(index){
                var dataType=parseInt($(this).attr("data-type"));
                $("#reimbursementForm select option").each(function(){
                    if(parseInt($(this).val()) == dataType){
                        $(this).addClass("myHide");
                    }
                });
            });
        }
    });
}
//保存费用
function saveReimbursement(){
    $("#saveBtn").click(function(){
        $("#reimbursementForm").verification({
            callback: function(){
                var $selectVal=$("#reimbursement select").val();
                var $feeType=$("#reimbursement select option:selected").text();
                var $money=parseFloat($("#feeAmount").val()).toFixed(2);

                /*报销总额叠加*/
                var $nowMoney;
                if(!!!$("#money").val()){
                    $nowMoney=0;
                }else{
                    $nowMoney=parseFloat($("#money").val()).toFixed(2);
                }
                $("#money").val(parseFloat(Number($nowMoney)+Number($money)).toFixed(2))

                $("#reimbursement input").val("");
                $("#reimbursement select").val("-1");
                $("#reimbursement").hide();

                //插入数据
                /*var $html='<div class="feeList" data-type='+$selectVal+'>' +
                    '<div>'+$feeType+'  <span>'+$money+'</span>元</div>' +
                    '<i class="fa fa-minus-square-o"></i>' +
                    '</div>';*/
                var $html=['<div class="stepwiseList" data-type='+$selectVal+'>' +
                    //<div>交通费用<span>165.00</span></div>
                    '<div>'+$feeType+'  <span>'+$money+'</span>元</div>' +
                    '<b><i class="fa fa-minus-square-o listDelBtn"></i>'+
                    '</b>'+
                    '</div>'];
                $(".stepwiseListModal").append($html.join(""));
                //$(".reimModal").append($html.join(""));
            }
        })
    });
}
//取消费用
function cancelReimbursement(){
    $("#cancelBtn").click(function(){
        $("#reimbursement").hide();
    });
}
//删除费用
function delReimbursement(){
    $(document).on(touchEv,".stepwiseList b",function(){
        var $this=$(this);
        $prev=$this.prev();
        $("body").toast({
            msg:"是否确认删除该条记录",
            type:5,
            callMode:"func",
            end:function(){
                if(!!!$this.parent().attr("data-id")){
                    /*报销总额递减*/
                    var $money=parseFloat($("#money").val()).toFixed(2);
                    var $delMoney=parseFloat($prev.children("span").text()).toFixed(2);
                    $("#money").val(parseFloat(Number($money)-Number($delMoney)).toFixed(2));
                    if($("#money").val() == 0){
                        $("#money").val("")
                    }
                    //删除数据
                    $prev.parent().remove();
                }else{
                    $.ajax({
                        url:"mobile/expense/deleteExpenseFee.action",
                        data:{
                            id:$this.parent().attr("data-id")
                        },
                        type:"post",
                        dataType:"json",
                        beforeSend:function(){
                            $("body").loading({
                                move:"show"
                            });
                        },
                        success:function(resData, textStatus){
                            $("body").loading({
                                move:"hide"
                            });
                            if (0 == resData.status) {
                                /*报销总额递减*/
                                var $money=parseFloat($("#money").val()).toFixed(2);
                                var $delMoney=parseFloat($prev.children("span").text()).toFixed(2);
                                $("#money").val(parseFloat(Number($money)-Number($delMoney)).toFixed(2));
                                if($("#money").val() == 0){
                                    $("#money").val("")
                                }
                                //删除数据
                                $prev.parent().remove();
                            } else {
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
                                msg:textStatus,
                                type:2,
                                callMode:"func"
                            });
                        }
                    });
                }
            }
        });


    });
}
