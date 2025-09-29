package com.djokic.responseserviceff.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {

    private Long id;
    private String text;
    private int order;
    private boolean isCorrect;

    private String imageUrl;

    private Question question;
}