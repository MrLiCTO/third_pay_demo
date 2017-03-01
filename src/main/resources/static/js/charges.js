$(function(){
	var charges = new Vue({
		el:"#charges",
		data:{
			pstn:"",
			voip:"",
			messages:""
		},
		//获取资费配置信息
		beforeCreate:function(){
			var _self = this;
			$.ajax({
				type : "POST",
				url : charges_url+'?'+Math.random(),
				dataType : "json",
				success : function(data){
					if(data.code == 200){
						_self.pstn = data.data["pstn"];
						_self.messages = data.data["messages"];
						for(var i = 0; i<_self.pstn.length; i++){
							switch(Number(data.data["pstn"][i].name)){
								case 1:
									_self.pstn[i].name = "单向外呼";
									break;
								case 2:
									_self.pstn[i].name = "双向外呼";
									break;
							}
							switch(Number(data.data["pstn"][i].payType)){
								case 1:
									_self.pstn[i].payType = "时长";
									break;
								case 2:
									_self.pstn[i].payType = "条数";
									break;
							}	
						}
						for(var i = 0; i<_self.messages.length; i++){
							switch(Number(data.data["messages"][i].name)){
								case 3:
									_self.messages[i].name = "短信";
									break;
								case 4:
									_self.messages[i].name = "语音验证码";
									break;
							}	
							switch(Number(data.data["messages"][i].payType)){
								case 1:
									_self.messages[i].payType = "时长";
									break;
								case 2:
									_self.messages[i].payType = "条数";
									break;
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
	});
});
