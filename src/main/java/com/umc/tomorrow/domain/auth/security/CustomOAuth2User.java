package com.umc.tomorrow.domain.auth.security;

import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserResponseDTO userResponseDTO;

    public CustomOAuth2User(UserResponseDTO userResponseDTO) {

        this.userResponseDTO = userResponseDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userResponseDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userResponseDTO.getName();
    }

    public UserResponseDTO getUserResponseDTO() {
        return userResponseDTO;
    }

    public String getUsername() {
        return userResponseDTO.getUsername();
    }

    public UserResponseDTO getUser() {
        return this.userResponseDTO;
    }
}