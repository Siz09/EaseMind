package com.mindease.backend.repository;

import com.mindease.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void deleteByFirebaseUid(String uid);
}
