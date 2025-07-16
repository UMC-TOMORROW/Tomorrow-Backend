package com.umc.tomorrow.domain.auth.service;

import com.umc.tomorrow.domain.auth.dto.*;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.dto.UserDTO;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    //오버라이드 전까지 코드 추가함 ->3번에서
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if(registrationId.equals("kakao")){

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인 방식입니다.");
        }

        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();

        UserDTO userDTO = UserDTO.builder()
            .name(oAuth2Response.getName())
            .provider(oAuth2Response.getProvider())
            .providerUserId(oAuth2Response.getProviderId())
            .email(oAuth2Response.getEmail())
            // .role("ROLE_USER") // UserDTO에 role 필드가 없으므로 주석 처리 또는 필요시 추가
            .build();

        return new CustomOAuth2User(userDTO);

    }

}
