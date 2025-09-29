package com.djokic.userserviceff.entity;

import com.djokic.userserviceff.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,
            name = "email",
            nullable = false )
    private String email;

    @Column(name = "password",
            nullable = false)
    private String password;

    @Column(name = "first_name",
            nullable = false)
    private String firstName;

    @Column(name = "last_name",
            nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",
            nullable = false)
    private Role role;
}