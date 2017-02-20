package com.lisl.app.beanPost;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/2/20.
 */
@Configuration
public class TestConfig {
    @Bean(initMethod = "init")
    public People people(){
        return new People();
    }
}
