package com.chx.claims.identity.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="users") public class User { @Id private UUID id; @Column(unique=true,nullable=false) private String email; private String passwordHash,fullName; @Enumerated(EnumType.STRING) private Role role; private Instant createdAt; protected User(){} public UUID getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getFullName(){return fullName;} public Role getRole(){return role;} }
