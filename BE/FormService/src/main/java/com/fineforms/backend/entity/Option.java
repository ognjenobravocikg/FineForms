package com.fineforms.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tekst opcije (za text-based answer)
    @Column(nullable = false)
    private String text;

    // Redosled prikazivanja
    @Column(name = "ord")
    private int order;

    // Da li je tačan odgovor (koristi se kod kviz pitanja)
    @Column(name = "is_correct")
    private boolean isCorrect;

    // Ako je opcija tipa slika
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;
}
