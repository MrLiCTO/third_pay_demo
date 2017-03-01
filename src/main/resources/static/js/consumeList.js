$(function(){
	var consumeList = new Vue({
		el:"#consumeList",
		data:{
			select1:"",
			select2:"所有",
			application:"",
			dataList:"",
			pageNow:1
		},
		beforeCreate:function(){
			var oDate = new Date();
			var y = oDate.getFullYear();
			var m = oDate.getMonth()+1;
			var d = oDate.getDate();
			function add0(n){
				return n<10?'0'+n:''+n;
			}
			var today = y+'-'+add0(m)+'-'+add0(d);			
			var _this = this;
			//获取应用列表
			$.ajax({
				type:"POST",
				url:getAppList_url2+'?'+Math.random(),
				success:function(data){
					if(data.code == 200){
						$('.dateBar').val(today);
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
			//showList(0);
		},
		methods:{
			chooseList:function(index){
				var _this = this;
				showList(index);
			}
		}
	});
	$('.consumeItems li').on('click',function(){
		$(this).addClass('liChecked').siblings().removeClass('liChecked');
		$('.consumeListTable').eq($(this).index()).addClass('showTableList').siblings().removeClass('showTableList');
	});

	//日期选择
	var checkin = $("#startTime2").fdatepicker({
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
		$("#endTime2").focus();
	}).data("datepicker");
	var checkout = $("#endTime2").fdatepicker({
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

	function showList(index){
		var con_startTime = $('#startTime2').val();
		var con_endTime = $('#endTime2').val();
		var keyWordTxt = $('.keyWordTxt').val();
		var curState = $('.curState').val();
		var app_id = 1;
		$.ajax({
			type : "POST",
			url : getConsumList_url+'?'+Math.random(),
			dataType : "json",
			data:{
				appId:app_id,
				startDate:con_startTime,
				endDate:con_endTime,
				keyWord:keyWordTxt,
				state:curState
			},
			success : function(data){
				if(data.code == 200){
					if($.isEmptyObject(data.data)){
						$('.no_data').show();
						$('.pagin').hide();
					}else{
						$('.no_data').hide();
						$('.pagin').show();
						consumeList.dataList = data.data[index+1];
						$('.consumeListTable').eq(index).show().siblings('.consumeListTable').hide();
						var totalPage = Math.ceil(consumeList.dataList['total']/5);
						$(".paginList").createPage({
					        pageCount:totalPage,
					        current:1,
					        backFn:function(p){
					           consumeList.pageNow = p;
					           $.ajax({
									type : "POST",
									url : getConsumList_url+'?'+Math.random(),
									dataType : "json",
									data:{
										appId:app_id,
										startDate:con_startTime,
										endDate:con_endTime,
										pageSize:5,
										pageNo:p
									},
									success:function(data){
										consumeList.dataList = data.data[index+1];
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
