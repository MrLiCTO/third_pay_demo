$(function () {
    $('.payErrorBtn').attr('href', backRechargeRecord_url);
    $('.paySuccBtn').attr('href', backRechargeRecord_url);
    var str = location.href;
    var orderId = str.substring(str.indexOf('=') + 1);
    var confirmOrder = new Vue({
        el: "#confirmOrder",
        data: {
            orderInfo: "",
            orderContinue: "",
            orderCancel: "",
            unioncloudOrderId: ""
        },
        //获取当前订单信息
        beforeCreate: function () {
            var _self = this;
            $.ajax({
                type: "POST",
                url: confirmOrder_url + '?' + Math.random(),
                dataType: "json",
                data: {
                    unioncloudOrderId: orderId
                },
                success: function (data) {
                    if (data.code == 200) {
                        _self.orderInfo = data.data;
                        _self.unioncloudOrderId = orderId;
                        switch (Number(data.data["paymentOrderId"])) {
                            case 0:
                                _self.orderInfo["paymentOrderId"] = "支付宝";
                                break;
                            case 1:
                                _self.orderInfo["paymentOrderId"] = "微信";
                                break;
                            case 2:
                                _self.orderInfo["paymentOrderId"] = "银联";
                                break;
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
        methods: {
            //确认订单并提交后台支付
            confOrder: function () {
                var _this = this;
                $.ajax({
                    type: "POST",
                    url: confirmOrder_url + '?' + Math.random(),
                    dataType: "json",
                    data: {
                        unioncloudOrderId: orderId
                    },
                    success: function (data) {
                        if (Number(data.data.state) == 2) {
                            _this.orderContinue = true;
                            _this.orderCancel = false;
                            var type = _this.orderInfo["paymentOrderId"];
                            var submit_url;
                            switch (type) {
                                case "支付宝":
                                    submit_url = alipay_url;
                                    $('.submit').attr('action',submit_url+'?unioncloudOrderId='+orderId);
                                    $('.submit').submit();
                                    $('.payResTip').show();
                                    break;
                                case "微信":
                                	alert(orderId)
                                    $.ajax({
                                        type: "POST",
                                        url: wechat_url + '?' + Math.random(),
                                        dataType: "json",
                                        data: {
                                            unioncloudOrderId: orderId
                                        },
                                        success:function(data){
                                            $('.weChatCode').attr('src',data.data);
                                        }
                                    });
                                    
                                    $('.weChatTitle').show();
                                    $('.weChatPay').show();
                                    break;
                                case "银联":
                                    submit_url = unionpay_url;
                                    break;
                            }
                            $('.popupMasker').show();
                            $('.payResult').show();
                        } else {
                            _this.orderContinue = false;
                            _this.orderCancel = true;
                            //失效后重定向到充值页面
                            //location.href = backRecharge_url;
                            $('.confirmHref').attr('href', backRecharge_url);
                            $('.confirmBtn').val('重新下单');
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
            closeMasker: function () {
                //关闭弹窗
                $('.popupMasker').hide();
                $('.payResult').hide();
            }
        }
    });
});

