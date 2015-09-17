
package com.underdog.jersey.hk2.validate;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Singleton;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;


public final class ValidationFeature implements Feature {

    private final Map<Class<?>, Class<?>> services;
    private final Map<Class<?>, Object> singletonServices;
    private final Map<Class<?>, Class<?>> requestServices;
    
    ValidationFeature(Map<Class<?>, Class<?>> services, 
                      Map<Class<?>, Object> singletonServices,
                      Map<Class<?>, Class<?>> requestServices) {
        this.services = services;
        this.singletonServices = singletonServices;
        this.requestServices = requestServices;
    }

    @Override
    public boolean configure(FeatureContext context) {
        context.register(new Binder());
        return true;
    }
    
    private class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bindClasses(services, Singleton.class);
            bindClasses(requestServices, RequestScoped.class);
            
            for (Map.Entry<Class<?>, Object> entry: singletonServices.entrySet()) {
                Object impl = entry.getValue();
                Class contract = entry.getKey();
                bind(impl).to(contract).to(Validatable.class);
            }
            
            bind(ValidationInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
        } 
        
        private void bindClasses(Map<Class<?>, Class<?>> services, Class<? extends Annotation> scope) {
            for (Map.Entry<Class<?>, Class<?>> entry: services.entrySet()) {
                Class<?> impl = entry.getKey();
                Class<?> contract = entry.getValue();
                bind(impl).to(contract).to(Validatable.class).in(scope);
            }
        }
    }

    public static class Builder {

        private final Map<Class<?>, Class<?>> services = new HashMap<>();
        private final Map<Class<?>, Object> singletonServices = new HashMap<>();
        private final Map<Class<?>, Class<?>> requestServices = new HashMap<>();

        public <T extends Validatable> Builder addSingletonClass(Class<T> impl, Class<? super T> contract) {
            Objects.requireNonNull(impl, "Implementation cannot be null");
            Objects.requireNonNull(impl, "Contract class cannot be null");
            services.put(impl, contract);
            return this;
        }

        public <T extends Validatable> Builder addSingletonInstance(T impl, Class<? super T> contract) {
            Objects.requireNonNull(impl, "Implementation cannot be null");
            Objects.requireNonNull(impl, "Contract class cannot be null");
            singletonServices.put(contract, impl);
            return this;
        }
     
        public <T extends Validatable> Builder addRequestScopeClass(Class<T> impl, Class<? super T> contract) {
            Objects.requireNonNull(impl, "Implementation cannot be null");
            Objects.requireNonNull(contract, "Contract class cannot be null");
            requestServices.put(impl, contract);
            return this;
        }
        
        public ValidationFeature build() {
            return new ValidationFeature(this.services, this.singletonServices, this.requestServices);
        }
    }
}
