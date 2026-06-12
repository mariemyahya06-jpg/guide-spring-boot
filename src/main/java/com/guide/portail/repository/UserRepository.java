package com.guide.portail.repository;

import com.guide.portail.entity.Role;
import com.guide.portail.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Page<User> findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String fullName, String username, Pageable pageable);
}
