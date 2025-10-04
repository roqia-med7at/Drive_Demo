package com.roqia.Drive_demo.security.oauth2.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class CustomOauth2User implements OAuth2User {
    private final OAuth2User oAuth2User;
    private final String providerName;

    public CustomOauth2User(OAuth2User oAuth2User, String providerName) {
        this.oAuth2User = oAuth2User;
        this.providerName = providerName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
           return oAuth2User.getName();
       }
       public String getEmail(){
        return oAuth2User.getAttribute("email");
    }
    public String getProviderId(){
        if("google".equalsIgnoreCase(providerName)){
            return oAuth2User.getAttribute("sub");
        } else if ("github".equalsIgnoreCase(providerName)) {
            return  String.valueOf(oAuth2User.getAttribute("id"));
        }else {
            return getName();
        }
    }
    public String getUserName(){
        return oAuth2User.getAttribute("name");
    }
}

