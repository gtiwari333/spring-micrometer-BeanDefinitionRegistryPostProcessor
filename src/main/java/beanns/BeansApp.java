package beanns;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class BeansApp {
    public static void main(String[] args) {
        SpringApplication.run(BeansApp.class, args);
    }

    @Bean
    RestTemplate rest(RestTemplateBuilder b) {
        return b.build();
    }

    @Bean
//    DynamicBeanDefinitionRegistrar r(RestTemplate t) { // the trace ids doesn't print on log
        DynamicBeanDefinitionRegistrar r() { //trace ids print on log
        return new DynamicBeanDefinitionRegistrar();//TODO: pass RestTemplate to the dynamically created bean
    }
}


@RequiredArgsConstructor
class DynamicBeanDefinitionRegistrar implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry reg) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}

@RestController
@Slf4j
@RequiredArgsConstructor
class Ctrl {

    final RestTemplate rest;

    @GetMapping("/test")
    String test() {
        log.info("Test called ..");
        return "Hello";
    }

    @Scheduled(fixedDelay = 4000)
    void x() {
        var resp = rest.getForEntity("http://localhost:8080/test", String.class);
        log.info(resp.getStatusCode() + " " + resp.getBody());
    }

}


