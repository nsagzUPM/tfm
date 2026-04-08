package com.upm.library.dto.catalog;

import com.upm.library.domain.Copy;

public record CopyDetailsDto(
        Long id,
        Long bookId,
        String title,
        String author,
        String status,
        boolean referenceOnly,
        String location

) {
    public static CopyDetailsDto from(Copy c) {

        return new CopyDetailsDto(
                c.getId(),
                c.getBook().getId(),
                c.getBook().getTitle(),
                c.getBook().getAuthor(),
                c.getStatus().name(),
                c.isReferenceOnly(),
                c.getLocation()
        );
    }
}