package com.upm.library.dto.catalog;

import com.upm.library.domain.Book;

public record BookSummaryDto(Long id, String title, String author, String subject) {
    public static BookSummaryDto from(Book b) {
        return new BookSummaryDto(b.getId(), b.getTitle(), b.getAuthor(), b.getSubject());
    }
}
