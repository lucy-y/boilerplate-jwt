package com.lucy.jwt.study.repository;

import com.lucy.jwt.study.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
