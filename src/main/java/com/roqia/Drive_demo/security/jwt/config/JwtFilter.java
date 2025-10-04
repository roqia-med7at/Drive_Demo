package com.roqia.Drive_demo.security.jwt.config;

import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import com.roqia.Drive_demo.security.jwt.service.JwtService;
import com.roqia.Drive_demo.security.jwt.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Configuration
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if(path.equals("api/auth/login")||path.equals("api/auth/register")||path.equals("api/auth/refresh")||path.equals("api/auth/logout")){
            filterChain.doFilter(request,response);
            return;
        }
        String token=null;
        String username=null;
        String authHeader = request.getHeader("Authorization");
        if(authHeader!=null&&authHeader.startsWith("Bearer ")){
            token=authHeader.substring(7);
            username=jwtService.extract_userEmail(token);

        }
        if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
         UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(username);
         if(jwtService.validate_token(token,userPrincipal)){
             UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal,null,userPrincipal.getAuthorities());
             SecurityContextHolder.getContext().setAuthentication(auth);
         }

        }
        filterChain.doFilter(request,response);

    }
}
