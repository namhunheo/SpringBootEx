package org.zerock.club.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Security 활성화
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public InMemoryUserDetailsManager  userDetailsService() {
//        // UserDetails 객체가 DB에서 조회한 객체
//        UserDetails user = User.builder()
//                .username("user1")
//                .password(passwordEncoder().encode("1111"))
//                .roles("USER")
//                .build();
//        // 이 이후에 시큐리티에서 로그인 페이지에서 전송한 username, password랑 이 객체(UserDetails)의  username, password 랑 비교해서 로그인 처리
//        return  new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth)->{
            auth
                    .requestMatchers("/sample/all").permitAll() // 모두 허용(인증없이 가능)
//                   .requestMatchers("/sample/member").hasRole("USER") //USER 권한이 있는 사용자만 허용(인가)
                    .requestMatchers("/member/mypage").authenticated() // 로그인후 가능(인증 필요)
                    .anyRequest().permitAll(); // 나머지 모든 URL 인증없이 허용
        });

        // 로그인 설정(커스터 마이징) : 로그인페이지 경로, 파라미터....
        http.formLogin(login ->{
//            login.loginPage("/sample/member"); // 로그인페이지(URL)설정
        });

        // csrf 비활성화
        http.csrf(csrf->{
            csrf.disable();
        });

        // 로그아웃 설정
        http.logout(logout ->{

        });
        // OAuth 설정
//        http.oauth2Login(oauth2->{
//            oauth2.defaultSuccessUrl("/sample/member", true);
//            oauth2.userInfoEndpoint(userInfo ->
//                    userInfo.userService(clubOAuth2UserDetailsService)
//            );
//        });

        return  http.build();
    }
}
