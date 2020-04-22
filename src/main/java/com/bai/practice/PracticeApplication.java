package com.bai.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PracticeApplication {

	/**
	 * 解决netty启动冲突的
	 * see Netty4Utils 中这个setAvailableProcessors方法
	 */
	@PostConstruct
	public void init () {
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(PracticeApplication.class, args);
	}

}
