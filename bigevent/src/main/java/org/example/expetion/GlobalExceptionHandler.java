package org.example.expetion;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.pojo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// -------------------------- 业务常见异常 --------------------------
	/**
	 * 处理请求体参数校验失败（@RequestBody + @Valid/@Validated）
	 * 场景：POST/PUT 请求中，JSON 数据绑定到实体类时的字段校验失败
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 状态码
	public Result handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		e.printStackTrace();
		// 获取所有字段错误信息
		List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		if (CollectionUtils.isEmpty(Collections.singleton(fieldErrors))) {
			return Result.error("请求参数格式错误");
		}

		// 拼接错误信息（格式：字段名: 错误提示）
		String errorMsg = fieldErrors.stream()
				.map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
				.collect(Collectors.joining("; "));

		return Result.error(errorMsg);
	}

	/**
	 * 处理请求参数/路径变量校验失败（@RequestParam/@PathVariable + 校验注解）
	 * 场景：GET 请求中，单个参数的校验失败（如 @Min(1) Long id）
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 状态码
	public Result handleConstraintViolation(ConstraintViolationException e) {
		 e.printStackTrace();
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		if (CollectionUtils.isEmpty(violations)) {
			return Result.error("请求参数校验失败");
		}

		// 拼接错误信息（格式：参数路径: 错误提示）
		String errorMsg = violations.stream()
				.map(violation ->  violation.getMessage())
				.collect(Collectors.joining("; "));

		return Result.error(errorMsg);
	}

	/**
	 * 处理缺少 @RequestParam 参数的异常（前端完全不传id时）
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result handleMissingParamsException(MissingServletRequestParameterException e) {
		// 这里可以直接返回“id不能为空”，也可以动态拼接参数名
		String errorMsg = "缺少必填参数：" + e.getParameterName();
		return Result.error(errorMsg);
	}


	// -------------------------- 系统通用异常 --------------------------

	/**
	 * 兜底处理所有未捕获的异常
	 * 注意：尽量避免直接捕获 Exception，建议细分异常类型，此处仅作为兜底
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 返回 500 状态码
	public Result handleGlobalException(Exception e) {

		// 生产环境建议返回通用提示，避免暴露敏感信息
		String errorMsg = "服务器内部错误，请稍后重试";
		e.printStackTrace();
		return Result.error(errorMsg);
	}
}
