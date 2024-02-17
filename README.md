Showcase project to demonstrate a potential bug.

## Background:

- App uses micrometer tracing to print trace/span ids to log
- `BeanRegistry` is an instance of `BeanDefinitionRegistryPostProcessor` where I will be creating Beans dynamically and
  pass RestTemplate and Tracer to those beans.
- It works when I inject none of the RestTemplate or Tracer bean to DynamicBeanDefinitionRegistrar creation

## Non-Issue:

```java

@Bean
DynamicBeanDefinitionRegistrar r() { //trace ids print on log
    return new DynamicBeanDefinitionRegistrar();
}
```

# Issue 1: the trace ids doesn't print on log after I try to inject RestTemplate to `reg` method

```java

@Bean
DynamicBeanDefinitionRegistrar r(RestTemplate t) { // 
    return new DynamicBeanDefinitionRegistrar();
}
```

# Issue 2: It throws `java.lang.NoSuchMethodException: org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage.<init>()` when I try to inject `io.micrometer.tracing.Tracer` bean

It appears its trying to create a bean of `BravePropagationConfigurations$PropagationWithBaggage` by calling empty
constructor, and it doesn't have the default constructor.

```java

@Bean
DynamicBeanDefinitionRegistrar r(Tracer tr) { // 
    return new DynamicBeanDefinitionRegistrar();
}
```

Stacktrace - Issue 2

``` 

org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'reg' defined in beanns.BeansApp: Unsatisfied dependency expressed through method 'reg' parameter 0: Error creating bean with name 'braveTracerBridge' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracerBridge' parameter 0: Error creating bean with name 'braveTracer' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracer' parameter 0: Error creating bean with name 'braveTracing' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracing' parameter 4: Error creating bean with name 'braveCurrentTraceContext' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveCurrentTraceContext' parameter 0: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage': Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:798) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:542) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:204) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:141) ~[spring-context-6.1.3.jar:6.1.3]
	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:788) ~[spring-context-6.1.3.jar:6.1.3]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:606) ~[spring-context-6.1.3.jar:6.1.3]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.2.2.jar:3.2.2]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[spring-boot-3.2.2.jar:3.2.2]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456) ~[spring-boot-3.2.2.jar:3.2.2]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:334) ~[spring-boot-3.2.2.jar:3.2.2]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1354) ~[spring-boot-3.2.2.jar:3.2.2]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343) ~[spring-boot-3.2.2.jar:3.2.2]
	at beanns.BeansApp.main(BeansApp.java:24) ~[classes/:na]
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'braveTracerBridge' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracerBridge' parameter 0: Error creating bean with name 'braveTracer' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracer' parameter 0: Error creating bean with name 'braveTracing' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracing' parameter 4: Error creating bean with name 'braveCurrentTraceContext' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveCurrentTraceContext' parameter 0: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage': Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:798) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:542) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:907) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:785) ~[spring-beans-6.1.3.jar:6.1.3]
	... 19 common frames omitted
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'braveTracer' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracer' parameter 0: Error creating bean with name 'braveTracing' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracing' parameter 4: Error creating bean with name 'braveCurrentTraceContext' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveCurrentTraceContext' parameter 0: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage': Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:798) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:542) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:907) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:785) ~[spring-beans-6.1.3.jar:6.1.3]
	... 33 common frames omitted
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'braveTracing' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveTracing' parameter 4: Error creating bean with name 'braveCurrentTraceContext' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveCurrentTraceContext' parameter 0: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage': Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:798) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:542) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:907) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:785) ~[spring-beans-6.1.3.jar:6.1.3]
	... 47 common frames omitted
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'braveCurrentTraceContext' defined in class path resource [org/springframework/boot/actuate/autoconfigure/tracing/BraveAutoConfiguration.class]: Unsatisfied dependency expressed through method 'braveCurrentTraceContext' parameter 0: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage': Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:798) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:542) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:907) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:785) ~[spring-beans-6.1.3.jar:6.1.3]
	... 61 common frames omitted
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage': Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1316) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1201) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:409) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1164) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:561) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:521) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:325) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:323) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.addCandidateEntry(DefaultListableBeanFactory.java:1689) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.findAutowireCandidates(DefaultListableBeanFactory.java:1653) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveMultipleBeanCollection(DefaultListableBeanFactory.java:1543) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveMultipleBeans(DefaultListableBeanFactory.java:1511) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1392) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:907) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:785) ~[spring-beans-6.1.3.jar:6.1.3]
	... 75 common frames omitted
Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage]: No default constructor found
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:84) ~[spring-beans-6.1.3.jar:6.1.3]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1310) ~[spring-beans-6.1.3.jar:6.1.3]
	... 100 common frames omitted
Caused by: java.lang.NoSuchMethodException: org.springframework.boot.actuate.autoconfigure.tracing.BravePropagationConfigurations$PropagationWithBaggage.<init>()
	at java.base/java.lang.Class.getConstructor0(Class.java:3761) ~[na:na]
	at java.base/java.lang.Class.getDeclaredConstructor(Class.java:2930) ~[na:na]
	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:80) ~[spring-beans-6.1.3.jar:6.1.3]
	... 101 common frames omitted
```
