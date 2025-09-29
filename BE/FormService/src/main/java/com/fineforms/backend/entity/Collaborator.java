package com.fineforms.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fineforms.backend.enums.CollaboratorRole;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "collaborators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    @JsonIgnore
    private Form form;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaboratorRole role;
}
