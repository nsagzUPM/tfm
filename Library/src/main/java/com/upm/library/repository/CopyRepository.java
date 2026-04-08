package com.upm.library.repository;

import com.upm.library.domain.Copy;
import com.upm.library.domain.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CopyRepository extends JpaRepository<Copy, Long> {
    List<Copy> findByBookId(Long bookId);
}

