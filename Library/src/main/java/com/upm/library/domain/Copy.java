package com.upm.library.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "copies")
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;

    @Column(nullable = false)
    private boolean referenceOnly = false;

    private String location;

    protected Copy() {}

    public Copy(Book book, boolean referenceOnly, String location) {
        this.book = book;
        this.referenceOnly = referenceOnly;
        this.location = location;
    }

    public Long getId() { return id; }
    public Book getBook() { return book; }
    public CopyStatus getStatus() { return status; }
    public boolean isReferenceOnly() { return referenceOnly; }
    public String getLocation() { return location; }

    public void markAsReserved() { this.status = CopyStatus.RESERVED; }
    public void markAsLoaned() { this.status = CopyStatus.LOANED; }
    public void markAsAvailable() { this.status = CopyStatus.AVAILABLE; }
}
