package com.bai.practice.dao;

import com.bai.practice.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts (int userId,int offset,int limit);

    // @Parm 给参数取别名，如果这个方法只有一个参数，并且在sql中使用<if>标签的，就必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById (int id);

    // 更新帖子的回帖数量
    int updateCommentCount(int id,int commentCount);
}
