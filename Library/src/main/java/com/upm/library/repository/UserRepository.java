package com.upm.library.repository;

import java.util.Optional;

import com.upm.library.domain.Book;
import com.upm.library.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByExternalId(String externalId);
}