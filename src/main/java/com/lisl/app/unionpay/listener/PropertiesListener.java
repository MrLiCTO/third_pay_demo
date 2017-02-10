package com.lisl.app.unionpay.listener;

import com.lisl.app.unionpay.sdk.SDKConfig;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * 监听器，启动加载properties文件内容
 * <p>
 * 创建日期：2016年5月17日 下午5:36:25
 * 操作用户：zhoubang
 */
public class PropertiesListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            SDKConfig.getConfig().loadPropertiesFromSrc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



