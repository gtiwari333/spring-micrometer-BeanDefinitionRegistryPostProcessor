package beanns;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

@Configuration
class Config {

    @Bean
    static BeanRegistry reg(ApplicationContext ctx) {
        return new BeanRegistry(ctx);
    }
}


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
    final BBean bBean;


    @Scheduled(fixedDelay = 4000)
    void x() {
        log.info(app.name);
        ResponseEntity<String> resp = rest.getForEntity("http://localhost:8080/test/scheduled-job", String.class);
        log.info(resp.getStatusCode() + " " + resp.getBody());
        aBean.doSth();
        bBean.doSth();
        log.info("");
    }
}

@RequiredArgsConstructor
@Slf4j
class BeanRegistry implements BeanDefinitionRegistryPostProcessor {

    private final ApplicationContext ctx;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        BindResult<App> result = Binder.get(ctx.getEnvironment())
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
                            RestTemplate restTemplate = ctx.getBean(RestTemplate.class);

                            Object obj = c.newInstance();
                            c.getMethod("setRestTemplate", RestTemplate.class).invoke(obj, restTemplate);


                            return obj;
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
@RequiredArgsConstructor
class Ctrl {

    final RestTemplate restTemplate;

    @GetMapping("/test/{source}")
    String test(@PathVariable String source) {
        log.info("Test called from {} ", source);
        return "Hello";
    }

}

@Slf4j
@Setter
class ABean {

    RestTemplate restTemplate;

    void doSth() {
        log.info("Called A Bean method");
        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:8080/test/A-Bean", String.class);
        log.info(resp.getStatusCode() + " " + resp.getBody());
    }

}

@Slf4j
@Setter
class BBean {

    RestTemplate restTemplate;

    void doSth() {
        log.info("Called B Bean method");
        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:8080/test/C-Bean", String.class);
        log.info(resp.getStatusCode() + " " + resp.getBody());
    }

}

@Slf4j
@Setter
class CBean {

    RestTemplate restTemplate;

    void doSth() {
        log.info("Called");

    }

}

@Slf4j
@Setter
class DBean {

    RestTemplate restTemplate;

    void doSth() {
        log.info("Called");

    }

}