$(function(){
	//支付方式选择
	var payType = null;
	var payTypeId;
	$(".rechargeType a").on("click",function(){
		$(".rechargeType a").each(function(){
			$(this).css("borderColor","#d9d9d9");
			$(".rechargeType a i").hide();	
		});
		$(this).css("borderColor","#3e96c9");
		$(".rechargeType a i").eq($(this).index()-1).show();
		payType = $(this).attr("title");
		switch(payType){
			case "支付宝":
				payTypeId = 0;
				break;
			case "微信":
				payTypeId = 1;
				break;
			case "银联":
				payTypeId = 2;
				break;
		}
	});
	function isPirce(s){
	    var p =/^[1-9](\d+(\.\d{1,2})?)?$/; 
	    var p1=/^[0-9](\.\d{1,2})?$/;
	    return p.test(s) || p1.test(s);
	}
	//获取账户余额
	$.ajax({
		type:"POST",
		url:currerNumber_url+"?"+Math.random(),
		dataType: "json",
		success:function(data){
			$(".currerNumber .num").html(data.data["totalAccount"]);
		},
        error : function(XMLHttpRequest,status){    
            var sessionstatus = XMLHttpRequest.getResponseHeader("sessionStatus");  
            if (sessionstatus == "timeout") {
               top.location = projectName + "/a/login?status=timeout";
            }else{
                alert("数据获取失败，请重试！");
            }   
        }
	});
	//数据提交
	$(".rechargeBtn").on("click",function(){
		//检测金额输入
		var BuyCash = $("#rechargeNum").val();
		if(isPirce(BuyCash)&&BuyCash>0&&BuyCash<=1000000){
			$(".errorInfo").hide();
			$("#rechargeNum").css("borderColor","#3e96c9");
		}else{
			$(".errorInfo").show();
			$("#rechargeNum").css("borderColor","#d9d9d9");
		};
		var payNum = $("#rechargeNum").val();
		if($("#rechargeNum").val()==""||payType==null||$(".errorInfo").css("display")=="block"){
			return;
		}	
		$.ajax({
			type:"POST",
			url:recharge_url+"?"+Math.random(),
			data: {
				txnAmt : payNum,
				paymentOrderId : payTypeId
			},
			success:function(data){
				if(data.code == 200){
					location.href = submit_url+'?unioncloudOrderId='+data.data.unioncloudOrderId;
				}
			},
	        error : function(XMLHttpRequest,status){    
	            var sessionstatus = XMLHttpRequest.getResponseHeader("sessionStatus");  
	            if (sessionstatus == "timeout") {
	               top.location = projectName + "/a/login?status=timeout";
	            }else{
	                alert("数据获取失败，请重试！");
	            }   
	        }
		});	
	});	
});

