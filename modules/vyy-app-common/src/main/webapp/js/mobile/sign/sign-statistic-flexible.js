/**
 * Created by zedd on 2015/11/27.
 */
/*window.sign_Info = {
    // 打卡时间配置
    "signStart": "9:30",//上班打卡不得晚于
    "signEnd":"17:30",   //下班打卡不得早于
    "workHours":9        //工作时长不得少于
};*/
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
	//时间区间
	var checkTime = function (bTime,eTime) {
		//系统设置时间
		var beginTime = window.sign_Info.signStart,
			endTime = window.sign_Info.signEnd;
		var workHours = window.sign_Info.workHours;
		var result;
		if(bTime != "" && eTime != ""){
			//时间
			var signb = beginTime.split (":"),
				signe = endTime.split (":"),
				thisb = bTime.split (":"),
			  	thise = eTime.split(":");
 
			var b = new Date ();
			var e = new Date ();
			var tb = new Date ();
			var te = new Date ();
			 
			b.setHours (signb[0],signb[1]);
			e.setHours (signe[0],signe[1]);
			tb.setHours (thisb[0],thisb[1]);
			te.setHours (thise[0],thise[1]);
			  /*
		        * kq-00 上下午都异常
				* kq-01 上午正常  下午异常
				* kq-10上午异常 下午正常
				* kq-11  上下午都正常
			 */
			  //时长
			var ws = te.getTime()-tb.getTime(); 
			var wh = ws/60/60/1000;
			if(wh < workHours){//时长不够
				  result = "kq-00";
			}else{
				  if(tb.getTime() > b.getTime()){//上午迟到
					  if(te.getTime() < e.getTime() || wh < workHours){//早退,时长不够
						  result = "kq-00" ;
					  }else{						  
						  result = "kq-01" ;
					  }
				  }else{//上午正常
					  if(te.getTime() < e.getTime() || wh < workHours){//早退,时长不够
						  result = "kq-10" ;
					  }else{
						  result = "kq-11" ;
					  }
				  }
			} 
		}else if(bTime == "" || eTime == ""){//忘记打卡，全天异常
			result = "kq-00" ;
		}
	    return result;
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
                		
                		//flexible弹性签到
                		var cld = setDay(kqDate,sTime,eTime);
                		var kqType = checkTime(sTime,eTime);
                		$("#"+cld).addClass(kqType);
                		
                    }
            	}
            },
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                alert(JSON.stringify(errorThrown));
            }
        });
    }
	
});
