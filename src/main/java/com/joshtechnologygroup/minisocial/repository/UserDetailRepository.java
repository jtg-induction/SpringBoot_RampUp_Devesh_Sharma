package com.joshtechnologygroup.minisocial.repository;

import com.joshtechnologygroup.minisocial.bean.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
}
