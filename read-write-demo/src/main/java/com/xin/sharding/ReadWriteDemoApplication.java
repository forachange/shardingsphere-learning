package com.xin.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;

import com.xin.sharding.service.UserServiceImpl;

@SpringBootApplication(exclude = JtaAutoConfiguration.class)
public class ReadWriteDemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ReadWriteDemoApplication.class, args);
	}
	
	@Autowired
	UserServiceImpl userServiceImpl;

	@Override
	public void run(String... args) throws Exception {
		try {
			userServiceImpl.initEnvironment();
			userServiceImpl.processSuccess();
        } finally {
        	userServiceImpl.cleanEnvironment();
        }
	}

}
