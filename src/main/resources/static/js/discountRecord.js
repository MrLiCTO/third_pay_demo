$(function(){
	var dis_startTime;
	var dis_endTime;
	var discountRecord = new Vue({
		el:"#discountRecord",
		data:{
			disRecordList:"",
			showData:false,
			noData:true
		},
		beforeCreate:function(){
			var _this = this;
			$.ajax({
				type:"POST",
				url:discountRecord_url+"?"+Math.random(),
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
							_this.disRecordList = data.data['pageData'];
							changeShowState(_this.disRecordList);
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
											url:discountRecord_url+"?"+Math.random(),
											data:{
												startDate:dis_startTime,
												endDate:dis_endTime,
												pageNo:p,
												pageSize:10
											},
											success:function(data){
												_this.recordList = data.data['pageData'];
												changeShowState(_this.disRecordList);
											},
											error: function(XMLHttpRequest,status){    
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
			disRecordInquire:function(){
				if($('#dis_startTime').val()==''||$('#dis_endTime').val()==''){
					return;
				}
				dis_startTime = $('#dis_startTime').val();
				dis_endTime = $('#dis_endTime').val();
				var _this = this;
				$.ajax({
					type:"POST",
					url:discountRecord_url+"?"+Math.random(),
					data:{
						startDate:dis_startTime,
						endDate:dis_endTime,
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
								_this.disRecordList = data.data['pageData'];
								changeShowState(_this.disRecordList);
								var pageNum = Math.ceil(data.data['totalCount'])/10;
								if(pageNum<=1){
									return;
								}else{
									$(".paginList").createPage({
								        pageCount:pageNum,
								        current:1,
								        backFn:function(p){
								            $.ajax({
								        		type:"POST",
												url:discountRecord_url+"?"+Math.random(),
												data:{
													startDate:dis_startTime,
													endDate:dis_endTime,
													pageNo:p,
													pageSize:10
												},
												success:function(data){
													_this.recordList = data.data['pageData'];
													changeShowState(_this.disRecordList);
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
					error: function(XMLHttpRequest,status){    
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
	//返回状态数字改成文本显示
	function changeShowState(obj){
		for(var i = 0; i < obj.length;i++){
			switch(Number(obj[i].state)){
				case 0:
					obj[i].state = '未生效';
					break;
				case 1:
					obj[i].state = '已生效';
					break;
				case 2:
					obj[i].state = '已过期';
					break;
			}	
		}
	}
	//日期选择
	var checkin = $("#dis_startTime").fdatepicker({
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
		$("#dis_endTime").focus();
	}).data("datepicker");
	var checkout = $("#dis_endTime").fdatepicker({
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
