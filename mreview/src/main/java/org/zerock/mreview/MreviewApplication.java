package org.zerock.mreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableJpaAuditing
@SpringBootApplication
public class MreviewApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(MreviewApplication.class, args);
    }

    // 디자인적인 페이지 (백엔드연동 필요없는)
    // 퍼블리셔가 작업
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/member/login").setViewName("new/member/login");
    }

}
