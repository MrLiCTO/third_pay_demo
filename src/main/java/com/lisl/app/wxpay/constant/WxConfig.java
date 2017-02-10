package com.lisl.app.wxpay.constant;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

public class WxConfig {
    static{
        try {
            Configuration configs = new PropertiesConfiguration("wxpay.properties");
            pay_url = configs.getString("wxpay.pay_url");
            refund_url = configs.getString("wxpay.refund_url");
            qurey_url = configs.getString("wxpay.qurey_url");
            close_url = configs.getString("wxpay.close_url");
            qurey_refund_url = configs.getString("wxpay.qurey_refund_url");
            appid = configs.getString("wxpay.appid");
            appsecret = configs.getString("wxpay.appsecret");
            mch_id = configs.getString("wxpay.mch_id");
            trade_type = configs.getString("wxpay.trade_type");
            notify_url = configs.getString("wxpay.notify_url");
        } catch (Exception e) {
            System.out.println("wxpay.properties初始化失败！");
            e.printStackTrace();
        }
    }

    public static String pay_url;//统一下单
    public static String refund_url;//退款
    public static String qurey_url;//查询
    public static String close_url;//关闭订单
    public static String qurey_refund_url;//查询退款

    public static String notify_url;//回调url

    public static String appid;
    public static String appsecret;
    public static String mch_id;
    public static String trade_type;
    //这个就是自己要保管好的私有Key了（切记只能放在自己的后台代码里，不能放在任何可能被看到源代码的客户端程序中）
    // 每次自己Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
    // 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改
    public static String key="";


}
