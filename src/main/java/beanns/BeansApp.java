package beanns;

import io.micrometer.tracing.Tracer;
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
    BeanRegistry reg(RestTemplate t) { //Issue 1: the trace ids doesn't print on log
//    BeanRegistry reg(Tracer tr) { //Issue 2: the trace ids doesn't print on log
//    BeanRegistry reg() { // NON ISSUE: trace ids print on log
        return new BeanRegistry();//TODO: pass RestTemplate and Tracer to the dynamically created bean
    }
}


@RequiredArgsConstructor
class BeanRegistry implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry reg) throws BeansException {
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


