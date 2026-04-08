package com.upm.library.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Copy copy;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column
    private LocalDate returnDate;

    @Column(nullable = false)
    private int renewals = 0;

    @Column(nullable = false)
    private boolean closed = false;

    protected Loan() {}

    public Loan(User user, Copy copy, LocalDate startDate, LocalDate dueDate) {
        this.user = user;
        this.copy = copy;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Copy getCopy() { return copy; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getDueDate() { return dueDate; }
    public int getRenewals() { return renewals; }
    public boolean isClosed() { return closed; }
    public void renew(LocalDate newDueDate) {
        this.dueDate = newDueDate;
        this.renewals++;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void close() { this.closed = true; }
}
