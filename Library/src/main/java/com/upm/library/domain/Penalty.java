package com.upm.library.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active = true;

    private String reason;

    protected Penalty() {}

    public Penalty(User user, LocalDate startDate, LocalDate endDate, String reason) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.active = true;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return active; }
    public String getReason() { return reason; }

    public void deactivate() { this.active = false; }
}
