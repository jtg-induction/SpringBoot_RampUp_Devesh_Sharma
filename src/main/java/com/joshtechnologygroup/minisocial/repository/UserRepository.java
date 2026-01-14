package com.joshtechnologygroup.minisocial.repository;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT id as userId, email FROM User WHERE active = true
            """)
    List<ActiveUserDTO> findActiveUsers();

    @Query("""
            SELECT u.id FROM User u WHERE u.id IN :ids
            """)
    List<Long> findExistingUserIds(@Param("ids") List<Long> ids);
}
