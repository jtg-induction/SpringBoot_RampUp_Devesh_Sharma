package com.joshtechnologygroup.minisocial.dao;

import com.joshtechnologygroup.minisocial.bean.User;
import com.joshtechnologygroup.minisocial.dto.user.ActiveUserDTO;
import com.joshtechnologygroup.minisocial.dto.user.PopulatedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u, ud, rd, od FROM User u
            LEFT JOIN FETCH UserDetail ud on ud.userId = u.id
            LEFT JOIN FETCH ResidentialDetail rd ON rd.userId = u.id
            LEFT JOIN FETCH OfficialDetail od ON od.userId = u.id
            WHERE u.id = :id
            """)
    Optional<PopulatedUser> findUserPopulated(@Param("id") Long id);

    @Query("""
            SELECT id as userId, email FROM User WHERE active = true
            """)
    List<ActiveUserDTO> findActiveUsers();

    @Query("""
            SELECT DISTINCT u.id FROM User u WHERE u.id IN :ids
            """)
    List<Long> findExistingUserIds(@Param("ids") List<Long> ids);
}
