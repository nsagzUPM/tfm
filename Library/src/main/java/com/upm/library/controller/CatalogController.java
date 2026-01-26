package com.upm.library.controller;

import com.upm.library.domain.Copy;
import com.upm.library.dto.catalog.BookSummaryDto;
import com.upm.library.dto.catalog.CopyDetailsDto;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.CopyRepository;
import com.upm.library.service.CatalogService;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog")
class CatalogController {
    private final CatalogService catalogService;
    private final CopyRepository copyRepository;

    public CatalogController(CatalogService catalogService, CopyRepository copyRepository) {
        this.catalogService = catalogService;
        this.copyRepository = copyRepository;
    }

    @GetMapping("")
    public List<BookSummaryDto> search(@RequestParam(required = false) String query) {
        return catalogService.search(query).stream()
                .map(BookSummaryDto::from)
                .toList();
    }

    @GetMapping("/copies/{id}")
    public CopyDetailsDto getCopyDetails(@PathVariable Long id) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Copy not found."));
        return CopyDetailsDto.from(copy);
    }
}
