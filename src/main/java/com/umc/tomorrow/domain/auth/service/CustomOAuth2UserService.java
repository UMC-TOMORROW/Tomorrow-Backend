package com.umc.tomorrow.domain.auth.service;

import com.umc.tomorrow.domain.auth.dto.*;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

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

        UserResponseDTO userResponseDTO = new UserResponseDTO(
            null, // id
            null, // role
            null, // username
            oAuth2Response.getEmail(),
            oAuth2Response.getName(),
            null, // gender
            null, // phoneNumber
            null, // address
            null, // status
            null, // inactiveAt
            null, // isOnboarded
            oAuth2Response.getProvider(),
            oAuth2Response.getProviderId(),
            null, // resumeId
            null  // profileImageUrl
        );

        return new CustomOAuth2User(userResponseDTO);

    }

}
