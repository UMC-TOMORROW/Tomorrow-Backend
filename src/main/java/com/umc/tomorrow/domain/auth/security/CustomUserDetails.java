package com.umc.tomorrow.domain.auth.security;

import com.umc.tomorrow.domain.member.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 또는 Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인이라면 null
    }

    @Override
    public String getUsername() {
        return user.getProvider() + " " + user.getProviderUserId(); // JWT에서 username으로 넣은 값과 같게
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
