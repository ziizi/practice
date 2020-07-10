package com.bai.practice.actuator;

import com.bai.practice.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 自定义端点
 */
@Component
@Endpoint(id = "database")
public class DataBaseEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseEndpoint.class);

    //获取一个数据库连接
    @Autowired
    private DataSource dataSource;

    @ReadOperation // 表示get请求
    public String checkCon (){
        try(Connection con = dataSource.getConnection()){
            logger.info("数据库连接出错");
            return CommunityUtil.getJSONString(0,"连接正常");
        }catch (Exception e){
            logger.info("数据库连接出错");
            return CommunityUtil.getJSONString(0,"连接正常");
        }

    }
}
