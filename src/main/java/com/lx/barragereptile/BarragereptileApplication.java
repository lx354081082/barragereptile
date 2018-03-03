package com.lx.barragereptile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
//@EnableTransactionManagement事物注解
@EnableTransactionManagement
@SpringBootApplication
public class BarragereptileApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarragereptileApplication.class, args);
	}
}
