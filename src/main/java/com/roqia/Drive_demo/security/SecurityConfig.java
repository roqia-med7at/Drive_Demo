package com.roqia.Drive_demo.security;

import com.roqia.Drive_demo.security.jwt.config.JwtFilter;
import com.roqia.Drive_demo.security.jwt.service.MyUserDetailsService;
import com.roqia.Drive_demo.security.oauth2.model.Oauth2SuccessHandler;
import com.roqia.Drive_demo.security.oauth2.service.CustomOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private Oauth2SuccessHandler oauth2LoginSuccessHandler;
    @Autowired
    private CustomOauth2UserService customOauth2UserService;
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
  public AuthenticationProvider authenticationProvider (){
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(myUserDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
}
    @Bean
    public DefaultSecurityFilterChain filterChain (HttpSecurity http) throws Exception {

      http.csrf(customizer->customizer.disable())
              .authorizeHttpRequests(request->request
                      .requestMatchers("api/auth/register","api/auth/login","api/auth/refresh","api/auth/logout").permitAll()
                      .anyRequest().authenticated()
              ).httpBasic(Customizer.withDefaults())
              .oauth2Login(outh2->outh2
                      .userInfoEndpoint(userInfo->userInfo.userService(customOauth2UserService))
                      .successHandler(oauth2LoginSuccessHandler)
              )
              .authenticationProvider(authenticationProvider())
              .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();

    }
    @Bean
    public PasswordEncoder passwordEncoder (){
    return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
