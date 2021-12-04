package com.example.demo.tody5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
@Slf4j
public class RemoteService {

    @RestController
    public static class RemoteController {
        @GetMapping("/service1")
        public String service1(String req) throws InterruptedException {

            log.info("called");
            System.out.println(System.getProperty("server.tomcat.max-threads"));

            Thread.sleep(2000);
            return req + "/service1";
        }



        @GetMapping("/service2")
        public String service2(String req) throws InterruptedException {
            Thread.sleep(2000);
            return req + "/service2";  // html/text
        }
    }


    public static void main(String[] args) {
        // 하나의 프로젝트에서 2개의 스프링 애플리케이션을 띄우기 위해 외부 서비스 역할을 하는 RemoteApplication은
        // application.properties가 아닌 별도의 프로퍼티를 이용하도록 직접 설정한다.
        System.setProperty("server.port", "8081");
        System.setProperty("server.tomcat.max-threads", "1000");

        SpringApplication.run(RemoteService.class, args);
    }
}
