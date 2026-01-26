package org.example.interceptor;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.pojo.User;
import org.example.service.IUserService;
import org.example.utils.JwtUtil;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("^/files/(\\d+)/(download|preview)$");

    @Autowired
    private IUserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
		String token = request.getHeader("Authorization");
        try{
			ValueOperations<String, String> ops = redisTemplate.opsForValue();
			if (token == null || StringUtils.isBlank(token)) {
				String uri = request.getRequestURI();
				Matcher matcher = FILE_URI_PATTERN.matcher(uri);
				if (matcher.matches()) {
					String fileId = matcher.group(1);
					String fileToken = request.getParameter("fileToken");
					if (fileToken != null && ! StringUtils.isBlank(fileToken)) {
						String bindValue = ops.get("file:token:" + fileToken);
						if (bindValue != null && !StringUtils.isBlank(bindValue)) {
							String bindId = bindValue;
							Long expAt = null;
							int idx = bindValue.indexOf(':');
							if (idx > 0 && idx < bindValue.length() - 1) {
								bindId = bindValue.substring(0, idx);
								try {
									expAt = Long.parseLong(bindValue.substring(idx + 1));
								} catch (Exception ignored) {
									expAt = null;
								}
							}
							if (bindId.equals(fileId)) {
								if (expAt == null || Instant.now().getEpochSecond() <= expAt) {
									return true;
								}
							}
						}
					}
				}
				throw new RuntimeException("用户未登录");
			}

			String redisToken = ops.get("token:" + token);
			if (redisToken == null){
				throw new RuntimeException("用户未登录");
			}
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
