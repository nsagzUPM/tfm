package com.upm.library.controller;

import com.upm.library.domain.Book;
import com.upm.library.dto.catalog.BookSummaryDto;
import com.upm.library.dto.catalog.CopyDetailsDto;
import com.upm.library.repository.BookRepository;
import com.upm.library.repository.CopyRepository;
import com.upm.library.service.CatalogService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/catalog")
class CatalogController {
    private final CatalogService catalogService;
    private final CopyRepository copyRepository;
    private final BookRepository bookRepository;

    public CatalogController(CatalogService catalogService, CopyRepository copyRepository, BookRepository bookRepository) {
        this.catalogService = catalogService;
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String search(@RequestParam(required = false, name = "q") String q, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("books", (q == null || q.isBlank()) ? List.of() : catalogService.search(q)
                .stream().map(BookSummaryDto::from).toList());
        return "catalog";
    }

    @GetMapping("/copies/{id}")
    public String getCopyDetails(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("book", book);
        model.addAttribute("copies", copyRepository.findByBookId(id)
                .stream().map(CopyDetailsDto::from).toList());
        return "catalog-copies";
    }
}
