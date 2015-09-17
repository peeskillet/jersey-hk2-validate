/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.underdog.jersey.hk2.validate;

import java.lang.reflect.Parameter;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ValidatingMethodInterceptor implements MethodInterceptor {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        for (int i = 0; i < args.length; i++) {
            // Java 8
            Parameter parameter = invocation.getMethod().getParameters()[i];
            if (parameter.getAnnotation(Valid.class) != null) {
                handleValidation(args[i]);
            }
        }
        return invocation.proceed();
    }

    private void handleValidation(Object arg) {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(arg);

        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
