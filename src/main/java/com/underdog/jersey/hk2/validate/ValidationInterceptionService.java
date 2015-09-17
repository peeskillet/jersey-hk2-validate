
package com.underdog.jersey.hk2.validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;

public class ValidationInterceptionService implements InterceptionService {

    private final static MethodInterceptor METHOD_INTERCEPTOR = new ValidatingMethodInterceptor();
    private final static List<MethodInterceptor> METHOD_LIST = Collections.singletonList(METHOD_INTERCEPTOR);

    @Override
    public Filter getDescriptorFilter() {
        return BuilderHelper.createContractFilter(Validatable.class.getCanonicalName());
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        for (Parameter parameter: method.getParameters()) {
            if (parameter.isAnnotationPresent(Valid.class)) {
                return METHOD_LIST;
            }
        }
        return null;
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> c) {
        return null;
    }
}
