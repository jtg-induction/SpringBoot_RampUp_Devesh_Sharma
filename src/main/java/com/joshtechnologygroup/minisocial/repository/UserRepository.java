package com.joshtechnologygroup.minisocial.repository;

import com.joshtechnologygroup.minisocial.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u.id FROM User u WHERE u.id IN :ids
            """)
    List<Long> findExistingUserIds(@Param("ids") List<Long> ids);
}
