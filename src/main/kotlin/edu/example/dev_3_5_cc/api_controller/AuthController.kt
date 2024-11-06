package edu.example.dev_3_5_cc.api_controller

import com.fasterxml.jackson.databind.ObjectMapper
import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.entity.KakaoProfile
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.OAuthToken
import edu.example.dev_3_5_cc.jwt.JWTUtil
import edu.example.dev_3_5_cc.service.MemberService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.*

@Controller
class AuthController (
    val memberService: MemberService,
    val jwtUtil: JWTUtil,
    val responseHeader: HttpServletResponse,
    private val modelMapper: ModelMapper,
    private val authenticationManager: AuthenticationManager,

    ){

    @GetMapping("auth/kakao/callback")
    fun kakaoCallback(code: String) : String { // ResponseEntity<Map<String, String>> {

        //-------------------------------------------------카카오 api 서버에 accessToken 요청하는 단계

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

        //ObjectMapper
        val objectMapper = ObjectMapper()
        val oauthToken : OAuthToken = objectMapper.readValue(response.body, OAuthToken::class.java)

        println("카카오 엑세스 토큰: ${oauthToken.access_token}")

        //-------------------------------------------------카카오 api 서버에 accessToken으로 사용자 정보 요청하는 단계

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
            HttpMethod.GET,
            kakaoProfileRequest,
            String::class.java
        )
        val objectMapper2 = ObjectMapper()
        val kakaoProfile : KakaoProfile = objectMapper2.readValue(response2.body, KakaoProfile::class.java)
        println("카카오 프로필: ${kakaoProfile}")

        //-------------------------------------------------요청 받은 사용자 정보로 자동 회원 등록 및 로그인 단계

        val newKakaoProfile = memberService.findOrRegisterKakaoMember(kakaoProfile)

        val authentication = UsernamePasswordAuthenticationToken(newKakaoProfile, null, listOf(SimpleGrantedAuthority("ROLE_USER")))
        SecurityContextHolder.getContext().authentication = authentication

        println("New Kakao Profile: ${newKakaoProfile}")

        val jwtToken = jwtUtil.createAccessToken(newKakaoProfile.memberId!!,"ROLE_USER",1000 * 60 * 60 * 10L)

        // 쿠키 생성
        val cookie = Cookie("jwtToken", jwtToken).apply {
            path = "/"
            isHttpOnly = false // 보안 상 HttpOnly로 설정하여 JavaScript에서 접근할 수 없도록 함
            maxAge = (60 * 60 * 10) // 쿠키 유효 시간 설정 (초 단위)
        }

        // 로그로 쿠키 정보 출력
        println("Generated JWT Cookie: Name=${cookie.name}, Value=${cookie.value}, Path=${cookie.path}, HttpOnly=${cookie.isHttpOnly}, MaxAge=${cookie.maxAge}")

        // 응답 헤더에 쿠키 추가
        responseHeader.addCookie(cookie)

        // return ResponseEntity.ok(mapOf("token" to token))
        return "redirect:/";
    }
}