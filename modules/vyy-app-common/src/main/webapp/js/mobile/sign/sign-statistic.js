/**
 * Created by zedd on 2015/11/27.
 */
$(function() {
	$(function(){
		//信息提示层
		var infoWrap = $("<div id='infoWrap' class='infoWrap'><div class='intro'><span class='ok'></span> 正常<span class='error'></span> 异常</div><div class='kq' id='kq'></div></div>");
        infoWrap.appendTo($("body"));
	    //日程
	  $('#cal').jCal({
	    day:new Date(),
	    days:1,
	    showMonths:1,
	    drawBack:function () {
	      var yearMonth = $(".month").html();
	      var ym = getYearMonth(yearMonth);
	      var monthDate = ym.substr(0,4)+"-"+ym.substr(4,6);//获得数据日期格式2015-10
	      abnormalAttendance(monthDate);
	      $(".day").each(function(){
	    	var d = $(this).text();
	    	$(this).html("<span>"+d+"</span>");
	      }); 
	      $("#kq").html('');
	    },
	    dow:['日', '一', '二', '三', '四', '五', '六'],
	    ml:['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
	    monthSelect:false,
	    sDate:new Date(),
	    callback:function (day, days) {
	        var dCursor = new Date( day.getTime() );
	        var currDay = $(this._target).find('[id*=d_' + ( dCursor.getMonth() + 1 ) + '_' + dCursor.getDate() + '_' + dCursor.getFullYear() + ']');
	        var hasKq = currDay.hasClass("kq-00")||currDay.hasClass("kq-01")||currDay.hasClass("kq-10")||currDay.hasClass("kq-11") ;
	        var currId= currDay.attr("id");
	        var currArray = currId.split("_");
	        var currDate = currArray[3]+"-"+currArray[1]+"-"+currArray[2];

	        var thisStime= currDay.attr("data-stime");
	        var thisEtime= currDay.attr("data-etime");

	        if(hasKq){//是不是考勤日
	        	if("" == thisStime && "" == thisEtime ){//未签到 未签退
	        		$("#kq").html(currDate+" "+'<span class="time">未签到</span>，<span class="time">未签退</span>');
	        	}else if("" != thisStime && ""==thisEtime){//签到 未签退
	        		$("#kq").html(currDate+" "+'<span class="time">'+thisStime+'签到</span>，<span class="time">未签退</span>');
	        	}else if("" != thisStime && "" != thisEtime){//签退签退
	        		$("#kq").html(currDate+" "+'<span class="time">'+thisStime+'签到</span>，<span class="time">'+thisEtime+'签退</span>');
	        	}else {
	        		$("#kq").html(currDate+" "+'<span class="time">'+thisStime+'签到</span>，<span class="time">'+thisEtime+'签退</span>');
	        	}
	        }
	        return true;
	      }
	    });
	  });
	//获取月份
	var getYearMonth = function(str){
		var m = str.replace(/[^0-9]/ig,"");
		return m;
	}
	//设置天
	var setDay = function(myd,sTime,eTime){
		var kqDate = myd;
		var dArray = kqDate.split("-");
		var y = dArray[0],
			m = dArray[1],
			d = dArray[2];
		if(d.substr(0,1)=="0"){
			d=d.substr(1,1);
		}
		if(m.substr(0,1)=="0"){
			m=m.substr(1,1);
		}
		var cld = "c1d"+"_"+m+"_"+d+"_"+y;
 
		$("#"+cld).attr({"data-sTime":""+ sTime,"data-eTime":""+ eTime});

		return cld;
	}
	//考勤
	var abnormalAttendance = function(monthDate) {
        $.ajax({
            url : 'mobile/sign/getExceptionKQRecordByMonth.action',
            type : 'post',
            data : {
                monthDate : monthDate
            },
            beforeSend:function(){
                $("#onLoading").show();
            },
            complete:function(){
                $("#onLoading").hide();
            },
            success : function(resData, textStatus) {
            	if (!!resData && !!resData.data && resData.data.length > 0) {
            		var data = resData.data;
                    var kqDate,sTime,eTime,attendType0,attendType1,flag;
                    for (var i = 0, size = data.length; i < size; i++) {
                		kqDate = data[i].kqDate;
                		sTime = data[i].bSignTime;
                		eTime = data[i].eSignTime;
                		attendType0 = data[i].attendType0;
                		attendType1 = data[i].attendType1;
                		flag =  data[i].flag;
                		//console.log(kqDate+" : "+attendType+" : "+flag);
            	       /*
            	        *  attendType0=-1 上午未打卡
						* attendType1=-1 下午未打卡
						* attendType0=1 迟到
						* attendType1=1 早退
						* attendType0=0 正常
						* attendType1=0 正常
						* 
						* flag=1 一天只打卡一次
            	         */
                		if("1" == flag){
                			var cld = setDay(kqDate,sTime,eTime);            			
	            			if("1" == attendType0){
	            				$("#"+cld).addClass("kq-00");
	            			}else{
	            				$("#"+cld).addClass("kq-10");
	            			}
                		}else{
                			if(("-1" == attendType0 && "-1" == attendType1) || ("1" == attendType0 && "1" == attendType1)){
                    			var cld = setDay(kqDate,sTime,eTime);
    	            			$("#"+cld).addClass("kq-00");
                    		}else if(("-1" == attendType0 && "0" == attendType1) || ("1" == attendType0 && "0" == attendType1)){
                    			var cld = setDay(kqDate,sTime,eTime);
    	            			$("#"+cld).addClass("kq-01");
                    		}else if("0" == attendType0 && "0" == attendType1){
                    			var cld = setDay(kqDate,sTime,eTime);
    	            			$("#"+cld).addClass("kq-11");
                    		}else if(("0" == attendType0 && "-1" == attendType1) || ("0" == attendType0 && "1" == attendType1)){
                    			var cld = setDay(kqDate,sTime,eTime);
    	            			$("#"+cld).addClass("kq-10");
                    		}
                		}
                    }
            	}
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                alert(JSON.stringify(errorThrown));
            }
        });
    }
	
});
