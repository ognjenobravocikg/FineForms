package com.djokic.responseserviceff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long formId;

    @Column(nullable = false)//anonymous users have id = 0
    private Long userId;

    private String userEmail;

    @Column(columnDefinition = "TEXT")
    private String answers;

    private LocalDateTime submittedAt;

    private Boolean isAuthenticated;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (this.isAuthenticated == null) {
            this.isAuthenticated = this.userId != null && this.userId > 0;
        }
    }
}