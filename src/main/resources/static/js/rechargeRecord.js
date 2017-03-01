$(function(){
	var re_startTime;
	var re_endTime;
	var temp_arr = [];
	var rechargeRecord = new Vue({
		el:"#rechargeRecord",
		data:{
			recordList:"",
			showData:false,
			noData:true
		},
		beforeCreate:function(){
			var _this = this;
			$.ajax({
				type:"POST",
				url:rechargeRecord_url+"?"+Math.random(),
				data:{
					pageNo:1,
					pageSize:10
				},
				success:function(data){
					if(data.code == 200){
						if($.isEmptyObject(data.data['pageData'])){
							_this.showData = false;
							_this.noData = true;
						}else{
						    _this.showData = true;
						    _this.noData = false;
							_this.recordList = data.data['pageData'];
							changeShowState(_this.recordList);
							var pageNum = Math.ceil(data.data['totalCount']/10);
							if(pageNum<=1){
								return;
							}else{
								$(".paginList").createPage({
							        pageCount:pageNum,
							        current:1,
							        backFn:function(p){
							        	$.ajax({
							        		type:"POST",
											url:rechargeRecord_url+"?"+Math.random(),
											data:{
												startDate:re_startTime,
												endDate:re_endTime,
												pageNo:p,
												pageSize:10
											},
											success:function(data){
												temp_arr = [];
												_this.recordList = data.data['pageData'];
												changeShowState(_this.recordList);
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
							    });	
							}
						}
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
			recordInquire:function(){
				if($('#re_startTime').val()==''||$('#re_endTime').val()==''){
					return;
				}
				re_startTime = $('#re_startTime').val();
				re_endTime = $('#re_endTime').val();
				var _this = this;
				$.ajax({
					type:"POST",
					url:rechargeRecord_url+"?"+Math.random(),
					data:{
						startDate:re_startTime,
						endDate:re_endTime,
						pageNo:1,
						pageSize:10
					},
					success:function(data){
						if(data.code == 200){
							if($.isEmptyObject(data.data['pageData'])){
								_this.showData = false;
								_this.noData = true;
							}else{
							    _this.showData = true;
							    _this.noData = false;
								_this.recordList = data.data['pageData'];
								changeShowState(_this.recordList);
								var pageNum = Math.ceil(data.data['totalCount']/10);
								if(pageNum<=1){
									return;
								}else{
									$(".paginList").createPage({
								        pageCount:pageNum,
								        current:1,
								        backFn:function(p){
								        	$.ajax({
								        		type:"POST",
												url:rechargeRecord_url+"?"+Math.random(),
												data:{
													startDate:re_startTime,
													endDate:re_endTime,
													pageNo:p,
													pageSize:10
												},
												success:function(data){
													temp_arr = [];
													_this.recordList = data.data['pageData'];
													changeShowState(_this.recordList);
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
								    });	
								}
							}
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
		},
		updated:function(){
			$('.payState a').each(function(index){
				this.href = 'javascript:;';
				this.style.cursor = 'default';
			});
			$('.unPaid').each(function(index){
				this.href = unPaid_url+'?unioncloudOrderId='+temp_arr[index];
				this.style.cursor = 'pointer';
			});
		}
	});
	function changeShowState(obj){
		for(var i = 0; i < obj.length;i++){
			switch(Number(obj[i].state)){
				case 0:
					obj[i].state = '支付失败';
					break;
				case 1:
					obj[i].state = '支付成功';
					break;
				case 2:
					obj[i].state = '未支付 >>';
					temp_arr.push(obj[i].unioncloudOrderId);
					break;
				case 3:
					obj[i].state = '过期';
					break;
			}
			switch (Number(obj[i].paymentOrderId)) {
				case 0:
					obj[i].paymentOrderId = "支付宝";
					break;
				case 1:
					obj[i].paymentOrderId = "微信";
					break;
				case 2:
					obj[i].paymentOrderId = "银联";
					break;
			}
		}
	}
	//日期选择
	var checkin = $("#re_startTime").fdatepicker({
		format: "yyyy-mm-dd",
		onRender: function (date) {
			var newDate = new Date();
			var now = newDate.getTime();
			date.valueOf()<=now;
			if(date.valueOf()>now){
				return "disabled";
			}else{
				return "";
			}
		}
	}).on("changeDate", function (ev) {
		if (ev.date.valueOf() > checkout.date.valueOf()) {
			var newDate = new Date(ev.date);
			newDate.setDate(newDate.getDate());
			checkout.update(newDate);
		}
		checkin.hide();
		$("#re_endTime").focus();
	}).data("datepicker");
	var checkout = $("#re_endTime").fdatepicker({
		format: "yyyy-mm-dd",
		onRender: function (date) {
			var newDate = new Date();
			var now = newDate.getTime();
			date.valueOf()<=now;
			if(date.valueOf()<checkin.date.valueOf()||date.valueOf()>now){
				return "disabled";
			}else{
				return "";
			}
		}
	}).on("changeDate", function (ev) {
		checkout.hide();
	}).data("datepicker");
});