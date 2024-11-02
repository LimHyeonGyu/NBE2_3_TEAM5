package edu.example.dev_3_5_cc.api_controller

import com.fasterxml.jackson.databind.ObjectMapper
import edu.example.dev_3_5_cc.entity.KakaoProfile
import edu.example.dev_3_5_cc.entity.OAuthToken
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
class AuthController {
    @GetMapping("auth/kakao/callback")
    @ResponseBody
    fun kakaoCallback(code: String): String {
        //POST방식으로 key=value 데이터를 요청 (카카오쪽으로)
        val rt: RestTemplate = RestTemplate()

        //HttpHeader 오브젝트 생성
        val headers : HttpHeaders = HttpHeaders()
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")

        //HttpBody 오브젝트 생성
        val params = LinkedMultiValueMap<String,String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", "67b872e5a24973543b3859c3985a73f6")
            add("redirect_uri", "http://localhost:8080/auth/kakao/callback")
            add("code", code)
        }

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        val kakaoTokenRequest = HttpEntity(params,headers)

        //Http 요청하기 - POST방식으로 - Response변수의 응답 받음
        val response: ResponseEntity<String> = rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoTokenRequest,
            String::class.java
        )

        //Gson, Json Simple, ObjectMapper
        val objectMapper = ObjectMapper()
        val oauthToken : OAuthToken = objectMapper.readValue(response.body, OAuthToken::class.java)

        println("카카오 엑세스 토큰: ${oauthToken.access_token}")

        val rt2: RestTemplate = RestTemplate()

        //HttpHeader 오브젝트 생성
        val headers2 : HttpHeaders = HttpHeaders()
        headers2.add("Authorization", "Bearer " + oauthToken.access_token)
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        val kakaoProfileRequest = HttpEntity(params,headers2)

        //Http 요청하기 - POST방식으로 - Response변수의 응답 받음
        val response2: ResponseEntity<String> = rt2.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoProfileRequest,
            String::class.java
        )
        val objectMapper2 = ObjectMapper()
        val kakaoProfile : KakaoProfile = objectMapper2.readValue(response2.body, KakaoProfile::class.java)

        println("카카오 아이디(번호): ${kakaoProfile.id}")

        return "응답: ${response2.body}"
    }
}