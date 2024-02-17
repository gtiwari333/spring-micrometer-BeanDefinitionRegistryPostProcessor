Showcase project to demonstrate a potential bug.

What's the issue:

```
    @Bean
//    DynamicBeanDefinitionRegistrar r(RestTemplate t) { // the trace ids doesn't print on log
        DynamicBeanDefinitionRegistrar r() { //trace ids print on log
        return new DynamicBeanDefinitionRegistrar(); 
    }
```

It doesn't print the trace ids on log once i try to inject RestTemplate to the DynamicBeanDefinitionRegistrar bean creation.

  
