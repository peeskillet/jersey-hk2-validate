
jersey-hk2-validate
===

####Please take a look at the tests for this project for two complete examples usages. One of the examples uses JPA.
There is the `ValidationFeatureTest` and the `ValidationFeatureJpaTest` that you can run an JUnit tests.

* [Problem](#problem)
* [Implementation Details](#implementation)
* [Usage](#usage)

<a name="problem"></a>
###Problem

This project was inspired by Stack Overflow question [@Valid not throwing exception][1].

The OP (original poster) is attempting to use `@Valid` for bean validation in a Jersey 2.x application.
The problem is that `@Valid` is being used in an arbitrary service method. 

    public class SomeService {
        public void save(@Valid Model model) {
            ...
        }
    }

This is not supported. What is supported is validation on services that are controlled by Jersey,
e.g. resource classes and the methods accepting the incoming DTO. For example

    @Path("models")
    public class ModelResource {

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response post(@Valid Model model) {
            ...
        }
    }


<a name="implementation"></a>
###Implementation

The solution that this project proposes, is to take advantage Jersey's DI implementation [HK2][2].
What HK2 offers is an [AOP implementation][3]. 

What we will do intercept all calls to methods annotated with `@Valid` and perform explicit
validation in the interceptor. Here is what it looks like.

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

The other two things we will need to do is register the interceptor through an implementation
of HK2's `InterceptionService`. Here's the implementation

    public class ValidationInterceptionService implements InterceptionService {

        private final static MethodInterceptor METHOD_INTERCEPTOR = new ValidatingMethodInterceptor();
        private final static List<MethodInterceptor> METHOD_LIST = Collections.singletonList(METHOD_INTERCEPTOR);

        @Override
        public Filter getDescriptorFilter() {
            return BuilderHelper.createContractFilter(Validatable.class.getCanonicalName());
        }

        @Override
        public List<MethodInterceptor> getMethodInterceptors(Method method) {
            if (method.isAnnotationPresent(Validated.class)) {
                return METHOD_LIST;
            }
            return null;
        }

        @Override
        public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> c) {
            return null;
        }
    }

What the `getDescriptorFilter()` does is tell HK2 what services should it intercept. In the HK2
documentation, you will see the example return `BuilderHelper.allFilter()`. The problem with this
is that HK2 manages a bunch of services in the Jersey space, so having it filter all services
will be a performance hit. 

What is introduced in this project is the `Validatable` marker interface. In the 
`getDescriptorFilter()` method, we are saying that any service with an advertised
contract of `Validatable` will be intercepted. So two things we need to do is

1. Make our services implement `Validatable`

        public class SeriviceImpl implements Service, Validatable {
            ...
        }

2. Then when we bind the service to HK2, we should bind the implementation to both
`Service` and `Validatable`

        bind(ServiceImpl.class).to(Service.class).to(Validatable.class).in(Singleton.class);

If you look back at the `ValidationInterceptionService`, you will also see the
`getMethodInterceptors` method. Here we are telling HK2, that only methods annotated with
`@Validated` (which is included in this project) should be intercepted. So the implementation
of the service will finally end up looking like this

        public class SeriviceImpl implements Service, Validatable {
            
            @Override
            @Validated
            public void Save(@Valid Model model) {}
        }

------

<a name="usage"></a>
###Usage

Make sure you have Maven, and Java 8 (this project makes use of `java.util.reflect.Parameter`)

Get the project

    git clone <project-url>

Build it

    cd jersey-hk2-validate
    maven clean install

Then include it in your project

    <dependency>
        <groupId>com.underdog</groupId>
        <artifactId>jersey-hk2-validate</artifactId>
        <version>0.0.1</version>
    </dependency>

To make use of this project, I created a `Feature` implementation in `ValidationFeature`
which takes care of registering all components above. What the user should take care of is:

1. Make sure the service they want to bind implements `Validatable`
2. Make sure the service method is annotated with `Validated`
3. Make sure the method parameter id annotated with `@Valid`

Then with the `ValidationFeature`, you can register your services classes. There are three
different APIs you can use to register class. You will first create the `Builder`, which is the only
way to create the feature

    Builder builder = new ValidationFeature.Builder();

Then you have the following APIs to register services

    // registers singleton services by class
    builder.addSingletonClass(Implementation.class, ContractClass.class);

    // registers singleton services by implementation 
    builder.addSingletonInstance(instance, ContractClass.class);

    // registers request scoped services by class
    builder.addRequestScopeClass(Implementation.class, ContractClass.class);

When you are done registering, just call `build()` on the builder to get back instance
of `ValidationFeature`, which you can register with your application (`ResourceConfig`).

    ValidationFeature feature = new ValidationFeature.Builder()
            .addSingletonClass(SomeService.class, Service.class).build();
    ResourceConfig config = new ResourceConfig();
    config.register(feature);

-----



[1]: http://stackoverflow.com/q/32611262/2587435
[2]: https://hk2.java.net/2.4.0-b07/
[3]: https://hk2.java.net/2.4.0-b07/aop-example.html
