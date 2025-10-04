package com.roqia.Drive_demo.security.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roqia.Drive_demo.error.customExceptions.RecordNotFoundException;
import com.roqia.Drive_demo.model.Provider;
import com.roqia.Drive_demo.repo.ProviderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleService {
@Autowired
private ProviderRepo providerRepo;
@Value("${spring.security.oauth2.client.registration.google.client-id}")
private  String clientId;
@Value("${spring.security.oauth2.client.registration.google.client-secret}")
private  String clientSecret;
    public void sendMail(String to, String subject, String text, String accessToken) throws JsonProcessingException {

        Provider provider = providerRepo.findByAccessToken(accessToken).orElseThrow(()->new RecordNotFoundException("No such provider found with this token"));
         if (Instant.now().isAfter(provider.getExpiryDate())){
            Map<String,Object>map = refreshAccessToken(clientId,clientSecret,provider.getRefreshAccessToken());
            provider.setAccessToken((String) map.get("access_token"));
            provider.setExpiryDate(Instant.now().plusSeconds((Long) map.get("expires_in")));
            providerRepo.save(provider);
         }
         String savedAccessToken= provider.getAccessToken();
        String rawMessage = "To: " + to + "\r\n" +
                "Subject: " + subject + "\r\n" +
                "Content-Type: text/html; charset=UTF-8" + "\r\n\r\n" +
                text;

        String encodedMessage = Base64.getUrlEncoder().withoutPadding().encodeToString(rawMessage.getBytes(StandardCharsets.UTF_8));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> body = new HashMap<>();
        body.put("raw", encodedMessage);
        String json = mapper.writeValueAsString(body);

        WebClient webClient = WebClient.builder().baseUrl("https://gmail.googleapis.com").
                defaultHeader("Authorization", "Bearer " + savedAccessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();

        String response = webClient.post()
                .uri("/gmail/v1/users/me/messages/send")
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
    public Map<String,Object> refreshAccessToken(String clientId,String clientSecret,String refreshToken) throws JsonProcessingException {

        Map<String,String>body = new HashMap<>();
        body.put("client_id",clientId);
        body.put("client_secret",clientSecret);
        body.put("refresh_token",refreshToken);
        body.put("grant_type","refresh_token");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(body);

        WebClient webClient = WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com/token")
                .build();

        String response = webClient.post()
                .header("Content-Type","application/json")
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper responseMapper = new ObjectMapper();
        JsonNode responseJson = responseMapper.readTree(response);
        Map<String,Object>responseMap=new HashMap<>();
       responseMap.put("access_token",responseJson.get("access_token").asText());
       responseMap.put("expires_in",responseJson.get("expires_in").asLong());
       return responseMap;
    }
}
