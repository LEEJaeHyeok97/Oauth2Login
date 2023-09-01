package com.example.demo.domain.auth.presentation;

import com.example.demo.domain.auth.dto.OAuthToken;
import com.example.demo.domain.auth.dto.KakaoProfile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoController {

    @ResponseBody
    @GetMapping("/login/oauth2/code/kakao")
    public String kakaoCallback(@RequestParam("code") String code) {

        // Post 요청 라이브러리
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // http 바디 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "4fdffd9fe5c823d1281774537a737c40");
        params.add("redirect_uri", "http://localhost:9000/login/oauth2/code/kakao");
        params.add("code", code);

        // httpHeader와 httpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // 실제 요청 Http post 방식 그리고 response 변수에 응답 받는다
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();

        OAuthToken oauthToken = null;

        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("카카오 엑세스 토큰:" + oauthToken.getAccess_token());


        RestTemplate rt2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // httpHeader와 httpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        // 실제 요청 Http post 방식 그리고 response 변수에 응답 받는다
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );


        ObjectMapper objectMapper2 = new ObjectMapper();

        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        System.out.println("카카오 아이디(번호):" + kakaoProfile.getId());
        System.out.println("카카오 이메일:" + kakaoProfile.getKakaoAccount().getEmail());


        return response2.getBody();
    }

    @GetMapping("/")
    public String index() {
        return "index"; // index.html 파일의 이름 (확장자 제외)을 리턴합니다.
    }
}

