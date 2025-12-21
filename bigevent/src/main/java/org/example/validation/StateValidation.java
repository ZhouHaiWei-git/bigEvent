package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.anno.State;

public class StateValidation implements ConstraintValidator<State, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		//提供校验规则
		if(value == null)
			return false;
		if("已发布".equals(value) || "草稿".equals(value))
			return true;

		return false;
	}
}
