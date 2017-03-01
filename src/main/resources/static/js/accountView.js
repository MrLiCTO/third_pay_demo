$(function(){
    var trendDataDay = [];
    var trendDataNum = [];
    var surveyData = [];
    //获取账户消费信息
    $.ajax({
        type : "POST",
        url : getAccountAmount_url+'?'+Math.random(),
        dataType : "json",
        success : function(data){
            if(data.code == 200){
                var rech = data.data['primaryAmount']&&data.data['primaryAmount'].toFixed(2)?data.data['primaryAmount'].toFixed(2):0;
                var disAc = data.data['deputeAmount']&&data.data['deputeAmount'].toFixed(2)?data.data['deputeAmount'].toFixed(2):0;
                var tolAc = rech&&disAc&&(Number(rech)+Number(disAc)).toFixed(2);
                var consum_yes = data.data['consumptionYesterday']&&data.data['consumptionYesterday'].toFixed(2)?data.data['consumptionYesterday'].toFixed(2):0;
                var consum_mon = data.data['consumptionMonth']&&data.data['consumptionMonth'].toFixed(2)?data.data['consumptionMonth'].toFixed(2):0;
                $('.totalAccount').html(tolAc);//账户总余额
                $('.rechargeAccount').html(rech+'元');//充值账户余额
                $('.disAccount').html(disAc+'元');//优惠账户余额
                $('.consum_yes').html(consum_yes);//昨日消费
                $('.consum_mon').html(consum_mon);//本月消费
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
    //获取消费走势信息
    $.ajax({
        type : "POST",
        url : getConsumptionTrend_url+'?'+Math.random(),
        dataType : "json",
        success : function(data){
            if(data.code == 200){
                var temp_data1 = data.data;
                for(var i = 0; i < temp_data1.length; i++){
                    trendDataDay.push(temp_data1[i].days+'号');
                    trendDataNum.push(temp_data1[i].amount);
                }
                //绘制消费走势折线图
                var accountTrend = echarts.init($('#lineChart')[0]);
                option1 = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis:  {
                        type: 'category',
                        boundaryGap: false,
                        data: trendDataDay
                    },
                    yAxis: {
                        type: 'value',
                        axisLabel: {
                            formatter: '{value} 元'
                        }
                    },
                    series: {
                        name:'消费金额',
                        type:'line',
                        data:trendDataNum
                    } 
                };
                accountTrend.setOption(option1);
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
    //获取消费概况信息
    $.ajax({
        type : "POST",
        url : getConsumerOverview_url+'?'+Math.random(),
        dataType : "json",
        success : function(data){
            if(data.code == 200){ 
                var temp_data2 = data.data;
                for(var i = 0; i < temp_data2.length; i++){
                    var temp_json = {};
                    switch(Number(temp_data2[i].proType)){
                        case 1:
                            temp_json.name = '单向外呼';
                            break;
                        case 2:
                            temp_json.name = '双向外呼';
                            break;
                        case 3:
                            temp_json.name = '短信';
                            break;
                        case 4:
                            temp_json.name = '语音验证码';
                            break;
                    }
                    temp_json.value = temp_data2[i].result;
                    surveyData.push(temp_json);
                }
                //绘制消费概况饼状图
                var accountSurvey = echarts.init($('#pieChart')[0]);
                option2 = {
                    tooltip: {
                        trigger: 'item',
                        formatter: "{b}:{c}"
                    },
                    legend: {
                        orient: 'vertical',
                        bottom: '35',
                        data:['单向外呼','双向外呼','短信','语音验证码']
                    },
                    series: [
                        {
                            type:'pie',
                            radius: ['40%', '65%'],
                            avoidLabelOverlap: false,
                            label: {
                                normal: {
                                    show: false,
                                    position: 'center'
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: false
                                }
                            },
                            data:surveyData
                        }
                    ]
                };
                // 使用刚指定的配置项和数据显示图表。
                accountSurvey.setOption(option2);
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