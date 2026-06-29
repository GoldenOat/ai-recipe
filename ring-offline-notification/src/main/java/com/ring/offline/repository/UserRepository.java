package com.ring.offline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ring.offline.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

}
