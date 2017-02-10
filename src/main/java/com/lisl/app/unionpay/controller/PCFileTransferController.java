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
 * 银联PC端对账文件下载交易
 *      文件传输类接口：后台获取对账文件交易，只有同步应答
 *      
 *      请使用700000000000001商户号发起文件传输案例测试 清算日期：0119
 *      
 *      交易说明：
 *          对账文件的格式请参考《全渠道平台接入接口规范 第3部分 文件接口》
 *          对账文件示例见目录【对账文件样例】下的 802310048993424_20150905.zip
 *          解析落地后的对账文件可以参考BaseController.java中的parseZMFile();parseZMEFile();方法
 *      
 *      
 * 创建日期：2016年5月18日 上午11:30:42
 * 操作用户：zhoubang
 * 
 */

@Controller
@RequestMapping("pc")
public class PCFileTransferController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 对账文件下载交易
     * 
     * 创建日期：2016年5月20日 上午10:52:27
     * 操作用户：zhoubang
     * 
     * @param request
     * @param response
     * @param merId 商户号
     * @param settleDate 清算日期
     * @throws IOException
     */
    @RequestMapping(value = "/fileTransfer", method = RequestMethod.POST)
    public void pcpay(HttpServletRequest request, HttpServletResponse response,String merId,String settleDate) throws IOException {
        logger.debug(MessageFormat.format("银联PC端发起对账文件下载交易,商户号merId{0}，清算日期settleDate：{1}", merId,settleDate));
        Map<String, String> data = new HashMap<String, String>();
        
        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
        data.put("version", BaseController.version);               //版本号 全渠道默认值
        data.put("encoding", BaseController.encoding_UTF8);             //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", "01");                        //签名方法 目前只支持01-RSA方式证书加密
        data.put("txnType", "76");                           //交易类型 76-对账文件下载
        data.put("txnSubType", "01");                        //交易子类型 01-对账文件下载
        data.put("bizType", "000000");                       //业务类型，固定
        
        /***商户接入参数***/
        data.put("accessType", "0");                         //接入类型，商户接入填0，不需修改
        data.put("merId", merId);                            //商户代码，请替换正式商户号测试，如使用的是自助化平台注册的777开头的商户号，该商户号没有权限测文件下载接口的，请使用测试参数里写的文件下载的商户号和日期测。如需777商户号的真实交易的对账文件，请使用自助化平台下载文件。
        data.put("settleDate", settleDate);                  //清算日期，如果使用正式商户号测试则要修改成自己想要获取对账文件的日期， 测试环境如果使用700000000000001商户号则固定填写0119
        data.put("txnTime",BaseController.getCurrentTime());       //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("fileType", "00");                          //文件类型，一般商户填写00即可
        
        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
        
        Map<String, String> reqData = AcpService.sign(data,BaseController.encoding_UTF8);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url = SDKConfig.getConfig().getFileTransUrl();//获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.fileTransUrl
        Map<String, String> rspData =  AcpService.post(reqData,url,BaseController.encoding_UTF8);
        logger.debug("对账单下载请求返回结果rspData：" + rspData);
        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, BaseController.encoding_UTF8)){
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if("00".equals(respCode)){
                    LogUtil.debug("交易成功，解析返回报文中的fileContent并落地");
                    //交易成功，解析返回报文中的fileContent并落地
                    AcpService.deCodeFileContent(rspData,"D:\\",BaseController.encoding_UTF8);
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



