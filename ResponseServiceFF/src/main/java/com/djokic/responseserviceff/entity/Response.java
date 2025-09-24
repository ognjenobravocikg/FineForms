package com.djokic.responseserviceff.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
public class Response {
    // TODO: ADD ID, Form, Author, List of Answers for Questions. Also implement Answer class that will have Response ID, Question ID and other data related to the response on a specific question.
}
