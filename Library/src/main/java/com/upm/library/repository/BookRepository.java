package com.upm.library.repository;

import com.upm.library.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<com.upm.library.domain.Book, Long> , JpaSpecificationExecutor<Book> {
}
