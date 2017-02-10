package com.lisl.app;

import com.lisl.app.unionpay.listener.PropertiesListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayDemoApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(PayDemoApplication.class);
		application.addListeners(new PropertiesListener());
		application.run(args);
	}
}
