package org.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pojo.User;
import org.example.service.IUserService;
import org.example.utils.JwtUtil;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private IUserService userService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
		String token = request.getHeader("Authorization");
        try{
			Map<String, Object> claims = JwtUtil.parseToken(token);
			if(claims != null){
				User user = userService.findByUserName((String) claims.get("username"));
				ThreadLocalUtil.set(user);
			}
			return true;
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
					throws Exception {
		ThreadLocalUtil.remove();
	}
}
