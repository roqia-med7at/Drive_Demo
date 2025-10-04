package com.roqia.Drive_demo.security.oauth2.model;

import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.security.jwt.service.JwtService;
import com.roqia.Drive_demo.security.jwt.service.TokenService;
import com.roqia.Drive_demo.security.oauth2.service.Oauth2UserService;
import com.roqia.Drive_demo.service.HandleUserLoginService;
import com.roqia.Drive_demo.service.StorageService;;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final Oauth2UserService oauth2UserService;
    private final TokenService tokenService;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final HandleUserLoginService loginService;
    public Oauth2SuccessHandler(JwtService jwtService, Oauth2UserService oauth2UserService, TokenService tokenService, OAuth2AuthorizedClientService oAuth2AuthorizedClientService, HandleUserLoginService loginService) {
        this.jwtService = jwtService;
        this.oauth2UserService = oauth2UserService;
        this.tokenService = tokenService;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.loginService = loginService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       OAuth2User principal = (OAuth2User) authentication.getPrincipal();
       CustomOauth2User oauth2User;

        OAuth2AuthenticationToken  oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
        String accessToken = client.getAccessToken().getTokenValue();
        String oauth2RefreshToken = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null;
        Instant expiryDate = client.getAccessToken().getExpiresAt();

       if (principal instanceof CustomOauth2User)
         oauth2User = (CustomOauth2User)principal;
       else {
           String providerName = oauthToken.getAuthorizedClientRegistrationId();
           oauth2User = new CustomOauth2User(principal, providerName);
       }
           User user = oauth2UserService.findOrCreateUser(oauth2User,accessToken,oauth2RefreshToken,expiryDate);

       Map<String,String>map = loginService.handleTokens(user.getId());
       int parentId = loginService.createRootFolder(user.getId());
        loginService.createSharedFolder(user.getId(),parentId,"shared");
          response.setContentType("application/json");
           response.getWriter().write("{\"token\":\""+map.get("token")+"\"}"+ "\n{\"refresh-token\":\"\""+map.get("refreshToken")+"\"}");
           response.getWriter().flush();


       }
    }

