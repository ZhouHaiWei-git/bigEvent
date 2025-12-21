package org.example.mapper;

import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.pojo.User;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface UserMapper {
    //根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User findByUserName(String username);

    //添加用户
    @Insert("insert into user(username,password,create_time,update_time)" +
            " values(#{username},#{md5Password},now(),now())")
    void add(String username, String md5Password);

    //更新用户信息
    @Update("update user set nickname = #{nickname},email = #{email},update_time = now() where id = #{id}")
    void update(User user);

    //更新用户头像
    @Update("update user set user_pic = #{avatarUrl},update_time = now() where id = #{id}")
    void updateAvatar(@NonNull Integer id, String avatarUrl);

    //更新用户密码
    @Update("update user set password = #{md5Pwd},update_time = now() where id = #{id}")
    void updatePwd(@NonNull Integer id, String md5Pwd);
}
