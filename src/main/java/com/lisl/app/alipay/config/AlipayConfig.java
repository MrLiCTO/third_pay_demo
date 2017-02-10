package com.lisl.app.alipay.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class AlipayConfig {
    static {
        try {
            Configuration configs = new PropertiesConfiguration("alipay.properties");
            partner = configs.getString("alipay.partner");
            seller_id = configs.getString("alipay.seller_id");
            key = configs.getString("alipay.key");
            notify_url = configs.getString("alipay.notify_url");
            return_url = configs.getString("alipay.return_url");
            sign_type = configs.getString("alipay.sign_type");
            log_path = configs.getString("alipay.log_path");
            input_charset = configs.getString("alipay.input_charset");
            payment_type = configs.getString("alipay.payment_type");
            service = configs.getString("alipay.service");
            anti_phishing_key = configs.getString("alipay.anti_phishing_key");
            exter_invoke_ip = configs.getString("alipay.exter_invoke_ip");
        } catch (Exception e) {
            System.out.println("alipay.properties初始化失败！");
            e.printStackTrace();
        }
    }

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String partner;

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public static String seller_id;

    // MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String key;

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url;

    // 签名方式
    public static String sign_type;

    // 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
    public static String log_path;

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset;

    // 支付类型 ，无需修改
    public static String payment_type;

    // 调用的接口名，无需修改
    public static String service;


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

//↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public static String anti_phishing_key;

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public static String exter_invoke_ip;

//↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
}

