package com.example.file.config.security;

import com.example.file.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 * <p>
 * Security 配置
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 16:46
 */
@Configuration
@EnableWebSecurity
@Slf4j
@EnableConfigurationProperties(CustomConfig.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private UserAuthAccessDeniedHandler userAuthAccessDeniedHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable(); // 关闭自带的登陆界面
        http.cors()
                // 关闭 CSRF
                .and().csrf().disable()
                // 登录行为由自己实现，参考 AuthController#login
                .formLogin().disable()
                .httpBasic().disable()
                // 认证请求
                .authorizeRequests()
//                .antMatchers("/file/**").anonymous()
                .antMatchers("/share/info").anonymous()
                .antMatchers("/share/vaild").anonymous()
                .antMatchers("/share/download").anonymous()
                .antMatchers("/user/login").anonymous()
                .antMatchers("/user/register").anonymous()
                // 所有请求都需要登录访问
                .anyRequest()
                .authenticated()
                // RBAC 动态 url 认证
//                .anyRequest()
//                .access("@rbacAuthorityService.hasPermission(request,authentication)")

                // 登出行为由自己实现，参考 AuthController#logout
                .and().logout().disable()
                // Session 管理 // 因为使用了JWT，所以这里不管理Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 认证不通过和权限不足的返回
                .and().exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(userAuthAccessDeniedHandler);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
