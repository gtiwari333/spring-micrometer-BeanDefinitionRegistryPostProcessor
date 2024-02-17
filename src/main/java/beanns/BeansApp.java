package beanns;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(App.class)
public class BeansApp {
    public static void main(String[] args) {
        SpringApplication.run(BeansApp.class, args);
    }

    @Bean
    RestTemplate rest(RestTemplateBuilder b) {
        return b.build();
    }
}

@AutoConfigureAfter(value = {ObservationAutoConfiguration.class, RestTemplateAutoConfiguration.class, JacksonAutoConfiguration.class
        , GsonAutoConfiguration.class})
//@AutoConfigureBefore()
class Con {

    @Bean
//    static BeanRegistry reg(RestTemplate t) { //Issue 1: the trace ids doesn't print on log
    static DynamicBeanDefinitionRegistrar reg(Environment env, ObservationRegistry tr, Tracer tt) { //Issue 2: the trace ids doesn't print on log
//    static BeanRegistry reg() { // NON ISSUE: trace ids print on log
        return new DynamicBeanDefinitionRegistrar(env);//TODO: pass RestTemplate and Tracer to the dynamically created bean
    }
}


//@RequiredArgsConstructor
//class BeanRegistry implements BeanDefinitionRegistryPostProcessor {
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry reg) throws BeansException {
//    }
//}

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Data
class App {
    String name;
    List<Bean> beans;


    @Data
    static final class Bean {
        String name;
        List<String> classes;
    }
}

@Service
@RequiredArgsConstructor
@Slf4j
class AService {

    final RestTemplate rest;
    final App app;
    final ABean aBean;

    @Scheduled(fixedDelay = 1000)
    void x() {
        log.info(app.name);
        ResponseEntity<String> resp = rest.getForEntity("http://localhost:8080/test", String.class);
        log.info(resp.getStatusCode() + " " + resp.getBody());
        aBean.doSth();
    }
}

@RequiredArgsConstructor
class DynamicBeanDefinitionRegistrar implements BeanDefinitionRegistryPostProcessor {

    private final Environment env;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        BindResult<App> result = Binder.get(env)
                .bind("app", App.class);
        App app = result.get();

        app.beans.forEach(
                bn -> bn.classes.forEach(cls -> {
                    final Class c;
                    try {
                        c = Class.forName(cls);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                    beanDefinition.setBeanClass(c);
                    beanDefinition.setInstanceSupplier(() -> {
                        try {
                            return c.newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    System.out.println(cls + " " + beanDefinition);
                    registry.registerBeanDefinition(cls, beanDefinition);
                }));
    }


}

@RestController
@Slf4j
class Ctrl {

    @GetMapping("/test")
    String test() {
        log.info("Test ..");
        return "Hello";
    }

}

@Slf4j
class ABean {

    void doSth() {
        log.info("Called A Bean method");
    }

}

@Slf4j
class BBean {

    void doSth() {
        log.info("Called");

    }

}

@Slf4j
class CBean {

    void doSth() {
        log.info("Called");

    }

}

@Slf4j
class DBean {

    void doSth() {
        log.info("Called");

    }

}