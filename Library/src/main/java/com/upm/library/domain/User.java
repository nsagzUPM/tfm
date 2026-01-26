package com.upm.library.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    protected User() {}

    public User(String externalId, Role role) {
        this.externalId = externalId;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getExternalId() { return externalId; }
    public Role getRol() { return role; }
}
