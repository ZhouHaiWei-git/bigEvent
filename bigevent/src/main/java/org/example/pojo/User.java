package org.example.pojo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class User {
    @NonNull
    private Integer id;//主键ID
    @NotEmpty
    private String username;//用户名
    @JsonIgnore
    private String password;//密码
    @Pattern(regexp = "^[a-zA-Z0-9_-]{1,16}$", message = "昵称格式错误")
    private String nickname;//昵称
    @NotEmpty
    @Email
    private String email;//邮箱
    private String userPic;//用户头像地址
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}
