package org.example.colltroller;

import jakarta.validation.constraints.Pattern;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.IUserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.example.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
	@Autowired
	private IUserService userService;

	@PostMapping("/register")
	public Result register(@Pattern(regexp = "^[a-zA-Z0-9_-]{5,16}$", message = "用户名格式错误") String username, @Pattern(regexp = "^[a-zA-Z0-9_-]{6,16}$", message = "密码格式错误") String password) {

		User user = userService.findByUserName(username);
		if (user == null) {
			userService.register(username, password);
			return Result.success();
		} else {
			return Result.error("用户名已经被占用");
		}
	}

	@PostMapping("/login")
	public Result login(@Pattern(regexp = "^[a-zA-Z0-9_-]{5,16}$", message = "用户名格式错误") String username, @Pattern(regexp = "^[a-zA-Z0-9_-]{6,16}$", message = "密码格式错误") String password) {
		User user = userService.findByUserName(username);
		if (user == null) {
			return Result.error("用户名不存在");
		} else {
			if (Md5Util.getMD5String(password).equals(user.getPassword())){
				Map<String, Object> claims = new HashMap<>();
				claims.put("id", user.getId());
				claims.put("username", user.getUsername());
				return Result.success(JwtUtil.genToken( claims));
			}

		}
		return Result.error("密码错误");
	}

	@GetMapping("/userInfo")
	public Result<User> getUserInfo() {
		User user = (User)ThreadLocalUtil.get();
		return Result.success(user);
	}

	@PutMapping("/update")
	public Result update(@RequestBody @Validated User user) {
		userService.update(user);
		return Result.success();
	}

	@PatchMapping("/updateAvatar")
	public Result updateAvatar(@RequestParam @URL String avatarUrl) {
		userService.updateAvatar(avatarUrl);
		return Result.success();
	}

	@PatchMapping("/updatePwd")
	public Result updatePwd(@RequestBody Map<String, String> params) {
      //1 校验参数
		String oldPwd = params.get("oldPwd");
		String newPwd = params.get("newPwd");
		String rePwd = params.get("rePwd");
		if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd))
			return Result.error("参数错误");
		if(!newPwd.equals(rePwd))
			return Result.error("两次密码不一致");
		User user = (User)ThreadLocalUtil.get();
		if(!Md5Util.getMD5String(oldPwd).equals(user.getPassword()))
			return Result.error("旧密码错误");
		userService.updatePwd(user.getId(),Md5Util.getMD5String(newPwd));
		return Result.success();
	}
}
