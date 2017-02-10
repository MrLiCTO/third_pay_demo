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
 * 银联PC端退款
 *      交易：退货交易：后台资金类交易，有同步应答和后台通知应答
 *      
 *      交易说明：
 *          1）以后台通知或交易状态查询交易（Form_6_5_Query）确定交易成功，建议发起查询交易的机制：可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05继续查询，否则终止查询）
 *          2）退货金额不超过总金额，可以进行多次退货
 *          3）退货能对11个月内的消费做（包括当清算日），支持部分退货或全额退货，到账时间较长，一般1-10个清算日（多数发卡行5天内，但工行可能会10天），所有银行都支持
 *      
 *      
 * 创建日期：2016年5月18日 上午11:30:42
 * 操作用户：zhoubang
 * 
 */

@Controller
@RequestMapping("pc")
public class PCRefundController {

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
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public void pcpay(HttpServletRequest request, HttpServletResponse response,String origQryId,String txnAmt) throws IOException {
        logger.debug(MessageFormat.format("银联PC端发起退款,交易号：{0}，退款金额(分)：{1}", origQryId,txnAmt));
        
        Map<String, String> data = new HashMap<String, String>();
        
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", BaseController.version);               //版本号
        data.put("encoding", BaseController.encoding_UTF8);        //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", "01");                        //签名方法 目前只支持01-RSA方式证书加密
        data.put("txnType", "04");                           //交易类型 04-退货       
        data.put("txnSubType", "00");                        //交易子类型  默认00      
        data.put("bizType", "000201");                       //业务类型 B2C网关支付，手机wap支付 
        data.put("channelType", "07");                       //渠道类型，07-PC，08-手机     
        
        /***商户接入参数***/
        data.put("merId", SDKConfig.getConfig().getMerId());                //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", "0");                         //接入类型，商户接入固定填0，不需修改       
        data.put("orderId", BaseController.getOrderId());          //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费      
        data.put("txnTime", BaseController.getCurrentTime());      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效       
        data.put("currencyCode", "156");                     //交易币种（境内商户一般是156 人民币）     
        data.put("txnAmt", txnAmt);                          //****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额     
        //data.put("reqReserved", "透传信息");                  //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节     
        data.put("backUrl", BaseController.backUrl);               //后台通知地址，后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 退货交易 商户通知,其他说明同消费交易的后台通知
        
        /***要调通交易以下字段必须修改***/
        data.put("origQryId", origQryId);      //****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取
        
        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
        Map<String, String> reqData  = AcpService.sign(data,BaseController.encoding_UTF8);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url = SDKConfig.getConfig().getBackRequestUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl

         Map<String, String> rspData = AcpService.post(reqData,url,BaseController.encoding_UTF8);//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        
        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, BaseController.encoding_UTF8)){
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if("00".equals(respCode)){
                    //交易已受理，等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
                    LogUtil.debug("交易已受理，等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。");
                }else if("03".equals(respCode)|| 
                         "04".equals(respCode)||
                         "05".equals(respCode)){
                    //后续需发起交易状态查询交易确定交易状态
                    LogUtil.debug("后续需发起交易状态查询交易确定交易状态");
                }else{
                    //其他应答码为失败请排查原因
                    LogUtil.debug("其他应答码为失败请排查原因");
                }
            }else{
                LogUtil.writeErrorLog("验证签名失败");
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



