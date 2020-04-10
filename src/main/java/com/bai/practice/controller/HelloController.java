package com.bai.practice.controller;

import com.bai.practice.service.HelloService;
import com.bai.practice.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/class")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/hello")
    @ResponseBody // 告诉浏览器返回的是个字符串
    public String sayHello () {
        return "hello spring boot";
    }


    @RequestMapping("/getData")
    @ResponseBody
    public String getData () {
        return helloService.find();
    }

    @RequestMapping("/http")
    public void  http (HttpServletRequest request, HttpServletResponse response) {
        // 获取请求的方式
        System.out.println("请求的方式:" + request.getMethod());
        System.out.println("请求的路径:" + request.getServletPath());

        Enumeration<String> enumeration = request.getHeaderNames(); // 以key-value的形式存的
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println("name:value=" + name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应的数据
        response.setContentType("text/html;charset=utf-8");
        try ( // 新写法，printWriter必须要有close() 方法
                PrintWriter printWriter = response.getWriter();
                ){
            printWriter.write("<h1> hello request </h1>");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 处理GET请求，默认就是GET请求，参数通过? 符号隔开
    // /getStudent?current=2&limit=20 第2页，每页20条数据
    @RequestMapping(path = "/getStudents",method = RequestMethod.GET)
    @ResponseBody
   // public String getStudents (int current,int limit) {  //(int current,int limit) 这种dispatchservlet会自动把参数赋值。

    public String getStudents (// 也可以通过注解@RequestParm来实现,是否必输和默认值
            @RequestParam(name = "current",required = false , defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false, defaultValue = "10") int limit) {
        System.out.println("current:" + current + ",limit:" + limit);
        return "current:" + current + ",limit:" + limit;
    }


    // 如果参数在访问的路径上，可以通过@PathVariable 注解实现获取参数
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent (@PathVariable("id") int id) {
        return "id:"+id;
    }


    @RequestMapping(path = "/addStudent",method = RequestMethod.POST)
    @ResponseBody
    public String addStudent (String name, int age) {
        System.out.println("addStudent");
        return "name:"+name + "age:"+ age;
    }


    // 给浏览器返回html数据,第一种办法
    @RequestMapping (path = "/getTeacher",method = RequestMethod.GET)
    public ModelAndView getTeacher () {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","teacher");
        modelAndView.addObject("age",18);
        modelAndView.setViewName("/view/teacher");
        return modelAndView;
    }
    // 给浏览器返回html数据,第二种办法
    @RequestMapping(path = "/getSchool",method = RequestMethod.GET)
    public String getSchool (Model model) {
        model.addAttribute("name","beijing university");
        model.addAttribute("age",80);
        return "/view/teacher";
    }

    // 给浏览器响应json数据，网页没有刷新，局部刷新，注册时判断名字是否被用了。
    @RequestMapping(path = "/emplyee",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp () {
        Map map = new HashMap<String,Object>();
        map.put("name","emplyee");
        map.put("age",16);
        map.put("salary",5222);
        return map;
    }


    @RequestMapping(path = "/emplyeeAll",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmpAll () {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map map = new HashMap<String,Object>();
        map.put("name","emplyee");
        map.put("age",16);
        map.put("salary",5222);
        list.add(map);

        Map map2 = new HashMap<String,Object>();
        map2.put("name","emplyee2");
        map2.put("age",18);
        map2.put("salary",50000);
        list.add(map2);
        return list;
    }


    // cookie 示例
    @RequestMapping(path = "/cookie/set" ,method = RequestMethod.GET)
    @ResponseBody
    public String setCookie (HttpServletResponse response) {
        // 生成cookie
        Cookie cookie = new Cookie("code", CommunityUtil.genUUID());
        // 设置需要带上cookie的路径
        cookie.setPath("/practice/class");
        // 设置生效时间
        cookie.setMaxAge(60 * 10);

        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping (path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie (@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }


    @RequestMapping(path = "/session/set" ,method = RequestMethod.GET)
    @ResponseBody
    public String setSession (HttpSession session) {
        // 生成cookie
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session";
    }

    @RequestMapping(path = "/session/get" ,method = RequestMethod.GET)
    @ResponseBody
    public String getSession (HttpSession session) {
        // 生成cookie
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }
}
