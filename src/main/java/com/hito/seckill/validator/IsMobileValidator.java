package com.hito.seckill.validator;

import com.hito.seckill.common.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author imyzt
 * @date 2019/3/9 15:05
 * @description IsMobileValidator
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            return ValidatorUtil.isMobile(value);
        } else {
            return !StringUtils.isEmpty(value) && ValidatorUtil.isMobile(value);
        }
    }
}
