package com.bai.practice.controller;

import com.bai.practice.entity.Event;
import com.bai.practice.event.EventProducer;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunitConstant {
    // 生成图片异步实现
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkStoragePath;

    @RequestMapping(path = "/share",method = RequestMethod.GET)
    @ResponseBody
    public String share (String htmlUrl) {
        // 文件名
        String fileName = CommunityUtil.genUUID();
        // 异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);

        // 返回访问路径
        Map<String,Object> map = new HashMap<>();
        map.put("shareUrl",domain + contextPath + "/share/image" + fileName);
        return CommunityUtil.getJSONString(0,null,map);
    }

    // 获取长图
    @RequestMapping(value = "/share/image/{fileName}",method = RequestMethod.GET)
    public void getShareImage (@PathVariable(name = "fileName")String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        response.setContentType("image/png");
        File file = new File(wkStoragePath + "/" + fileName + ".png");
        try {
            OutputStream stream = response.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int b = 0; // 游标
            while ((b = fileInputStream.read(bytes)) != -1) {
                stream.write(bytes,0,b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败"+e.getMessage());
        }

    }

}
