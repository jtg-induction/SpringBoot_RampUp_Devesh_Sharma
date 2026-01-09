package com.joshtechnologygroup.minisocial.dao;

import com.joshtechnologygroup.minisocial.bean.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
}
