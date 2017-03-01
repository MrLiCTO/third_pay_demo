$(function(){
	//初始化应用消费数据
	var initData = [{
		"name": "PSTN语音",
      	"total": 0,
		"items": [
	        {
	          "name": "单向外呼",
	          "payType": "时长",
	          "price": 0,
	          "number": 0
	        },
	        {
	          "name": "双向外呼",
	          "payType": "时长",
	          "price": 0,
	          "number": 0
	        }
	    ]
	},{
		"name": "短信、语音验证",
      	"total": 0,
		"items": [
	        {
	          "name": "短信",
	          "payType": "条数",
	          "price": 0,
	          "number": 0
	        },
	        {
	          "name": "语音验证码",
	          "payType": "条数",
	          "price": 0,
	          "number": 0
	        }
	    ]
	}]
	var chooseMonth;
	var monthlyBill = new Vue({
		el:"#monthlyBill",
		data:{
			accountInfo:"",
			pstn: initData[0],
			messages:initData[1],
			application:"",
			appName:"全部"
		},
		//初始化当前月账单
		beforeCreate:function(){
			var oDate = new Date();
			var y = oDate.getFullYear();
			var m = oDate.getMonth();
			function add0(n){
				return n<10?'0'+n:''+n;
			}
			if(m == 0){
				m=12;
				y--;
			}
			var curMon = y+'-'+add0(m);
			var _this = this;
			//获取月份消费账单
			showMonthlyBill(_this,curMon);
			//获取应用显示消费信息
			getAppConsum(_this,curMon,0);
			//获取应用列表
			$.ajax({
				type:"POST",
				url:getAppList_url+'?'+Math.random(),
				success:function(data){
					if(data.code == 200){
						_this.application = data.data;
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
		},
		methods:{
			//根据应用显示消费信息
			showAppList:function(){
				this.pstn = initData[0];
				this.messages = initData[1];
				var _this = this;
				chooseMonth = $("#monthPicker").val();
				getAppConsum(_this,chooseMonth,0);
					
			},
			//点击下载详单后显示弹框
			showPop:function(){
				$('.popMask').show();
				$('.popUp').show();	
			},
			//关闭弹窗
			closePop:function(){	
				$('.popMask').hide();
				$('.popUp').hide();
			},
			//选择应用下载详单
			downExcel:function(){
				chooseMonth = $("#monthPicker").val();
				var checkedAppId = $('.cont li.liChecked').html();
				if(app_id == null){
					return;
				}
				$.ajax({
					type:"POST",
					url:downExcel_url,
					data:{
						accountId:'',
						month:chooseMonth,
						appId:app_id
					},
					success:function(){		
						$('.popMask').hide();
						$('.popUp').hide();
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
			}
		}
	});
	//月份选择
	$("#monthPicker").fdatepicker({
		format: "yyyy-mm"
	}).on('change',function(){
		//选择月份账单
		chooseMonth = $(this).val();
		//获取月份消费账单
		showMonthlyBill(monthlyBill,chooseMonth);
	});
	var app_id = null;
	$('.cont').on('click','li',function(){
		$(this).addClass("liChecked").siblings().removeClass("liChecked");
		if($(this).index() == 0){
			app_id = 0;
		}else{
			app_id = monthlyBill.application[$(this).index()-1].id;
		}
	});
});
//获取月份消费账单
function showMonthlyBill(obj,month){
	$.ajax({
		type:"POST",
		url:getAccountInfo_url+'?'+Math.random(),
		data:{
			month:month
		},
		success:function(data){
			if(data.code == 200){
				$("#monthPicker").val(month);
				obj.accountInfo = data.data;
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
}
//获取应用消费信息
function getAppConsum(obj,month,id){
	$.ajax({
		type:"POST",
		url:getConsumerInfo_url+'?'+Math.random(),
		data:{
			month:month,
			appId:id
		},
		success:function(data){
			if(data.code == 200){
				var len1 = data.data[0].items.length;
				var len2 = data.data[1].items.length;
				switch(len1){
					case 0:
						break;
					case 1:
						if(data.data[0].items[0]['name'] == 1){
							obj.pstn['items'][0] = data.data[0].items[0];
							obj.pstn['total'] = data.data[0].total;
						}else if(data.data[0].items[0]['name'] == 2){
							obj.pstn['items'][1] = data.data[0].items[0];
							obj.pstn['total'] = data.data[0].total;
						}
						break;
					case 2:
						obj.pstn = data.data[0];
						break;
				}
				switch(len2){
					case 0:
						break;
					case 1:
						if(data.data[1].items[0]['name'] == 3){
							obj.messages['items'][0] = data.data[1].items[0];
							obj.messages['total'] = data.data[1].total;
						}else if(data.data[1].items[0]['name'] == 4){
							obj.messages['items'][1] = data.data[1].items[0];
							obj.messages['total'] = data.data[1].total;
						}
						break;
					case 2:
						obj.messages = data.data[1];
						break;
				}
				var pstnItmes = obj.pstn.items;
				var messagesItems = obj.messages.items;
				changeShow(pstnItmes,messagesItems,data);
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
}
function changeShow(obj1,obj2,data){
	for(var i = 0; i<obj1.length; i++){
		switch(Number(obj1[i].name)){
			case 1:
				obj1[i].name = "单向外呼";
				break;
			case 2:
				obj1[i].name = "双向外呼";
				break;
		}
		switch(Number(obj1[i].payType)){
			case 1:
				obj1[i].payType = "时长";
				break;
			case 2:
				obj1[i].payType = "条数";
				break;
		}	
	}
	for(var i = 0; i<obj2.length; i++){
		switch(Number(obj2[i].name)){
			case 3:
				obj2[i].name = "短信";
				break;
			case 4:
				obj2[i].name = "语音验证码";
				break;
		}	
		switch(Number(obj2[i].payType)){
			case 1:
				obj2[i].payType = "时长";
				break;
			case 2:
				obj2[i].payType = "条数";
				break;
		}	
	}
}
	
	
	
