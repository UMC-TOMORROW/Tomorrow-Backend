package com.umc.tomorrow.domain.member.repository;

import com.umc.tomorrow.domain.member.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}
