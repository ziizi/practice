package com.bai.practice;

import com.bai.practice.dao.HelloDao;
import com.bai.practice.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
class PracticeApplicationTests implements ApplicationContextAware {
	private  ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}


	@Test
	void contextLoads() {
		System.out.println(this.applicationContext);

		// 根据接口类型获取，获取@Primary优先的实现类
		HelloDao helloDao = applicationContext.getBean(HelloDao.class);
		System.out.println(helloDao.select());

		// 根据名字获取bean，将获取的对象转化为HelloDao.class
		helloDao = applicationContext.getBean("Hiber",HelloDao.class);
		System.out.println(helloDao.select());

	}


	@Test
	void beanLife() {
		HelloService helloService = applicationContext.getBean(HelloService.class);
		System.out.println(helloService);
		HelloService helloService2 = applicationContext.getBean(HelloService.class);
		System.out.println(helloService2);
	}

	@Test
	void beanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}


	@Autowired
	@Qualifier("Hiber")
	public HelloDao helloDao;

	@Autowired
	public SimpleDateFormat simpleDateFormat;
	@Autowired
	public HelloService helloService;

	@Test
	void testDI() {
		System.out.println(simpleDateFormat.format(new Date()));
		System.out.println(helloService);
		System.out.println(helloDao.select());

	}

}
