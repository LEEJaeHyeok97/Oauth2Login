package com.example.demo.domain.auth.presentation;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Controller
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

        return response.getBody();
    }

    @GetMapping("/")
    public String index() {
        return "index"; // index.html 파일의 이름 (확장자 제외)을 리턴합니다.
    }
}

