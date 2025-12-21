package org.example.service.impl;

import lombok.NonNull;
import org.example.mapper.UserMapper;
import org.example.utils.Md5Util;
import org.example.pojo.User;
import org.example.service.IUserService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {
        User u = userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password) {
        String md5Password = Md5Util.getMD5String(password);
        userMapper.add(username,md5Password);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        User user = (User) ThreadLocalUtil.get();
        userMapper.updateAvatar(user.getId(),avatarUrl);
    }

    @Override
    public void updatePwd(@NonNull Integer id, String md5Pwd) {
        userMapper.updatePwd(id,md5Pwd);
    }
}
