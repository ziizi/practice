package com.bai.practice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect // 是个切面注解
public class HelloAspect {

    // 第一个* 所有的类型的返回值，*.* 所有类中的所有方法，参数不限
    @Pointcut("execution(* com.bai.practice.service.*.*(..))") // 切点
    public void pointcut () {

    }

    @Before("pointcut()") // 前置通知
    public void before (){
        System.out.println("before");
    }

    @After("pointcut()") // 前置通知
    public void after (){
        System.out.println("After");
    }

    @AfterReturning("pointcut()") // 前置通知
    public void afterReturn (){
        System.out.println("AfterReturning");
    }

    @AfterThrowing("pointcut()") // 前置通知
    public void afterThrow (){
        System.out.println("afterThrow");
    }

    @Around("pointcut()") // 前置通知
    public Object around (ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = joinPoint.proceed(); // 目标方法
        System.out.println("around after");
        return obj;
    }
}
