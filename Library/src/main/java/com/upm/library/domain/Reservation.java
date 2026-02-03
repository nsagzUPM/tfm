package com.upm.library.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Copy copy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private LocalDate deadline = LocalDate.parse("2025-10-30");

    protected Reservation() {
    }

    public Reservation(User user, Copy copy) {
        this.user = user;
        this.copy = copy;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Copy getCopy() {
        return copy;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void fulfill() {
        this.status = ReservationStatus.FULFILLED;
    }
}
