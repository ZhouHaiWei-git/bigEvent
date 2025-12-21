package org.example.service;

import lombok.NonNull;
import org.example.pojo.User;

public interface IUserService {
     User findByUserName(String username);
     void register(String username, String password);

     void update(User user);

     void updateAvatar(String avatarUrl);

     void updatePwd(@NonNull Integer id, String md5String);
}
