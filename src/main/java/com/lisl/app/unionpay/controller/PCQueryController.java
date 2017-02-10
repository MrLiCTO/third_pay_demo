package com.lisl.app.unionpay.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lisl.app.unionpay.sdk.AcpService;
import com.lisl.app.unionpay.sdk.LogUtil;
import com.lisl.app.unionpay.sdk.SDKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



/**
 * 银联PC端交易查询
 *      交易状态查询交易：只有同步应答
 *      
 *      交易说明：
 *          1）对前台交易发起交易状态查询：前台类交易建议间隔（5分、10分、30分、60分、120分）发起交易查询，如果查询到结果成功，则不用再查询。（失败，处理中，查询不到订单均可能为中间状态）。也可以建议商户使用payTimeout（支付超时时间），过了这个时间点查询，得到的结果为最终结果。
 *          2）对后台交易发起交易状态查询：后台类资金类交易同步返回00，成功银联有后台通知，商户也可以发起 查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05继续查询，否则终止查询）。
 *                                   后台类资金类同步返03 04 05响应码及未得到银联响应（读超时）需发起查询交易，可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05继续查询，否则终止查询）。
 *      
 *      
 * 创建日期：2016年5月18日 上午11:30:42
 * 操作用户：zhoubang
 * 
 */

@Controller
@RequestMapping("pc")
public class PCQueryController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 银联PC端发起退款
     * 
     * 创建日期：2016年5月18日 上午11:37:21
     * 操作用户：zhoubang
     * 
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public void pcpay(HttpServletRequest request, HttpServletResponse response,String orderId,String txnTime) throws IOException {
        logger.debug(MessageFormat.format("银联PC端发起交易查询,订单id{0}，交易日期(分)：{1}", orderId,txnTime));
        
        Map<String, String> data = new HashMap<String, String>();
        
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", BaseController.version);                 //版本号
        data.put("encoding", BaseController.encoding_UTF8);          //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", "01");                          //签名方法 目前只支持01-RSA方式证书加密
        data.put("txnType", "00");                             //交易类型 00-默认
        data.put("txnSubType", "00");                          //交易子类型  默认00
        data.put("bizType", "000201");                         //业务类型 B2C网关支付，手机wap支付
        
        /***商户接入参数***/
        data.put("merId", SDKConfig.getConfig().getMerId());                  //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改
        
        /***要调通交易以下字段必须修改***/
        data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
        data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
        
        Map<String, String> reqData = AcpService.sign(data,BaseController.encoding_UTF8);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        
        String url = SDKConfig.getConfig().getSingleQueryUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
        //这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        Map<String, String> rspData = AcpService.post(reqData,url,BaseController.encoding_UTF8);
        
        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, BaseController.encoding_UTF8)){
                LogUtil.writeLog("验证签名成功");
                if("00".equals(rspData.get("respCode"))){//如果查询交易成功
                    //处理被查询交易的应答码逻辑
                    String origRespCode = rspData.get("origRespCode");
                    if("00".equals(origRespCode)){
                        //交易成功，更新商户订单状态
                        LogUtil.debug("交易成功，更新商户订单状态");
                    }else if("03".equals(origRespCode) ||
                             "04".equals(origRespCode) ||
                             "05".equals(origRespCode)){
                        //需再次发起交易状态查询交易 
                        LogUtil.debug("需再次发起交易状态查询交易 ");
                    }else{
                        //其他应答码为失败请排查原因
                        LogUtil.debug("其他应答码为失败请排查原因 ");
                    }
                }else{//查询交易本身失败，或者未查到原交易，检查查询交易报文要素
                    LogUtil.debug("查询交易本身失败，或者未查到原交易，检查查询交易报文要素 ");
                }
            }else{
                LogUtil.writeErrorLog("验证签名失败");
                LogUtil.debug("检查验证签名失败的原因");
            }
        }else{
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        String reqMessage = BaseController.genHtmlResult(reqData);
        String rspMessage = BaseController.genHtmlResult(rspData);
        response.getWriter().write("</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"");
    }
}



