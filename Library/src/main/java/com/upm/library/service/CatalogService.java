package com.upm.library.service;

import com.upm.library.domain.Book;
import com.upm.library.repository.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {
    private final BookRepository bookRepository;

    public CatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> search(String query) {
        if (query == null || query.isBlank()) {
            return bookRepository.findAll();
        }

        String q = "%" + query.trim().toLowerCase() + "%";

        Specification<Book> spec = (root, cq, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("title")), q),
                        cb.like(cb.lower(root.get("author")), q),
                        cb.like(cb.lower(root.get("subject")), q)
                );

        return bookRepository.findAll(spec);
    }
}
