package com.lisl.app.unionpay.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lisl.app.unionpay.sdk.AcpService;
import com.lisl.app.unionpay.sdk.LogUtil;
import com.lisl.app.unionpay.sdk.SDKConfig;
import com.lisl.app.unionpay.sdk.SDKConstants;
import com.lisl.app.unionpay.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 银联PC端网关支付
 *      交易：消费：前台跳转，有前台通知应答和后台通知应答
 * 
 * 创建日期：2016年5月18日 上午11:30:42
 * 操作用户：zhoubang
 * 
 */

@Controller
@RequestMapping("pc")
public class PCPayController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 银联PC端发起支付
     * 
     * 创建日期：2016年5月18日 上午11:37:21
     * 操作用户：zhoubang
     * 
     * @param request
     * @param response
     * @param merId 商户号
     * @param txnAmt 交易金额(单位为分)
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)//测试方便,改成了get请求
    public void pcpay(HttpServletRequest request, HttpServletResponse response,String merId,String txnAmt) throws IOException {
        merId="777290058120760";
        txnAmt="1";
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        logger.debug(MessageFormat.format("银联PC端发起支付,商户号：{0}，支付金额(分)：{1}", merId,txnAmt));
        
        Map<String, String> requestData = new HashMap<String, String>();
        
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        requestData.put("version", BaseController.version);                 //版本号，全渠道默认值
        requestData.put("encoding", BaseController.encoding_UTF8);          //字符集编码，可以使用UTF-8,GBK两种方式
        requestData.put("signMethod", "01");                          //签名方法，只支持 01：RSA方式证书加密
        requestData.put("txnType", "01");                             //交易类型 ，01：消费
        requestData.put("txnSubType", "01");                          //交易子类型， 01：自助消费
        requestData.put("bizType", "000201");                         //业务类型，B2C网关支付，手机wap支付
        requestData.put("channelType", "07");                         //渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板  08：手机
        
        /***商户接入参数***/
        requestData.put("merId", merId);                              //商户号，测试阶段使用测试商户号
        requestData.put("accessType", "0");                           //接入类型，0：直连商户 
        requestData.put("orderId",BaseController.getOrderId());             //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则     
        requestData.put("txnTime", BaseController.getCurrentTime());        //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        requestData.put("currencyCode", "156");                       //交易币种（境内商户一般是156 人民币）        
        requestData.put("txnAmt", txnAmt);                            //交易金额，单位分，不要带小数点
        //requestData.put("reqReserved", "透传字段");                  //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节       
        
        //前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
        //如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
        //异步通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
        requestData.put("frontUrl", BaseController.frontUrl);
        
        //后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
        //后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
        //注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
        //    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
        //    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
        requestData.put("backUrl", BaseController.backUrl);
        
        //////////////////////////////////////////////////
        //
        //       报文中特殊用法请查看 PCwap网关跳转支付特殊用法.txt
        //
        //////////////////////////////////////////////////
        
        /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        Map<String, String> submitFromData = AcpService.sign(requestData,BaseController.encoding_UTF8);  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        
        String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
        String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,BaseController.encoding_UTF8);   //生成自动跳转的Html表单
        
        LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据："+html);
        //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
        response.getWriter().write(html);
    }
    
    
    /**
     * 支付成功后，同步通知处理。
     *      银联通过post请求。
     * 
     * 创建日期：2016年5月18日 下午2:21:05
     * 操作用户：zhoubang
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping(value = "/frontRcvResponse", method = RequestMethod.POST)
    public void frontRcvResponse(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.debug("支付成功后，frontRcvResponse前台接收报文返回开始！");
        
        String encoding = request.getParameter(SDKConstants.param_encoding);
        logger.debug("返回报文中encoding=[" + encoding + "]");

        String pageResult = "";
        if (BaseController.encoding_UTF8.equalsIgnoreCase(encoding)) {
            pageResult = "/utf8_result.jsp";
        } else {
            pageResult = "/gbk_result.jsp";
        }
        Map<String, String> respParam = getAllRequestParam(request);

        // 打印请求报文
        logger.debug(JsonUtils.toJson(respParam));

        Map<String, String> valideData = null;
        StringBuffer page = new StringBuffer();
        if (null != respParam && !respParam.isEmpty()) {
            Iterator<Entry<String, String>> it = respParam.entrySet().iterator();
            valideData = new HashMap<String, String>(respParam.size());
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                value = new String(value.getBytes(encoding), encoding);
                page.append("<tr><td width=\"30%\" align=\"right\">" + key + "(" + key + ")</td><td>" + value + "</td></tr>");
                valideData.put(key, value);
            }
        }
        if (!AcpService.validate(valideData, encoding)) {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
            logger.debug("验证签名结果[失败].");
        } else {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
            logger.debug("验证签名结果[成功].");
            logger.debug("orderId：" + valideData.get("orderId")); //其他字段也可用类似方式获取
        }
        request.setAttribute("result", page.toString());
        request.getRequestDispatcher(pageResult).forward(request, response);

        logger.debug("支付成功后，frontRcvResponse前台接收报文返回结束！");
    }
    
    
    /**
     * 获取请求参数中所有的信息
     * 
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    // System.out.println("======为空的字段名===="+en);
                    res.remove(en);
                }
            }
        }
        return res;
    }
}



